package cl.duoc.sonoritmo.dao;

import cl.duoc.sonoritmo.model.ClaseIndividual;
import cl.duoc.sonoritmo.model.ClaseGrupal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementación de {@link ClaseMusicalDao} que guarda cada subtipo en un archivo JSON usando Gson.
 */
public class ClaseMusicalJsonDao implements ClaseMusicalDao {

    // Gson convierte objetos Java a JSON y al revés. setPrettyPrinting deja el archivo ordenado y legible.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Los dos métodos cargar funcionan igual; solo cambian el archivo y el subtipo.
    @Override
    public List<ClaseIndividual> cargarClasesIndividuales() {
        Path archivo = Path.of("clases_individuales.json");
        // Primera ejecución: si el archivo aún no existe, se parte con una lista vacía (sin error).
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        // try-with-resources: cierra el archivo automáticamente al terminar, haya error o no.
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            // Gson lee el JSON (una lista de objetos) como un arreglo del subtipo, usando sus constructores vacíos.
            ClaseIndividual[] leidos = gson.fromJson(lector, ClaseIndividual[].class);
            // Si el archivo está vacío, fromJson devuelve null.
            if (leidos == null) {
                return new ArrayList<>();
            }
            // Arrays.asList devuelve una lista de tamaño fijo; se copia a un ArrayList para poder agregar y quitar.
            return new ArrayList<>(Arrays.asList(leidos));
        // IOException: falla del archivo. JsonParseException: el contenido no es un JSON válido.
        // Se convierte en PersistenciaException para que el controlador muestre el mensaje.
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer el archivo clases_individuales.json.", e);
        }
    }

    @Override
    public void guardarClasesIndividuales(List<ClaseIndividual> datos) {
        escribir("clases_individuales.json", datos);
    }

    @Override
    public List<ClaseGrupal> cargarClasesGrupales() {
        Path archivo = Path.of("clases_grupales.json");
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            ClaseGrupal[] leidos = gson.fromJson(lector, ClaseGrupal[].class);
            if (leidos == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(leidos));
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer el archivo clases_grupales.json.", e);
        }
    }

    @Override
    public void guardarClasesGrupales(List<ClaseGrupal> datos) {
        escribir("clases_grupales.json", datos);
    }

    // Escribe la lista completa en el archivo (reemplaza lo que había). List<?> acepta una lista de cualquier subtipo.
    private void escribir(String nombreArchivo, List<?> datos) {
        try (Writer escritor = Files.newBufferedWriter(Path.of(nombreArchivo), StandardCharsets.UTF_8)) {
            // toJson convierte la lista a texto JSON y lo escribe en el archivo.
            gson.toJson(datos, escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo guardar el archivo " + nombreArchivo + ".", e);
        }
    }
}
