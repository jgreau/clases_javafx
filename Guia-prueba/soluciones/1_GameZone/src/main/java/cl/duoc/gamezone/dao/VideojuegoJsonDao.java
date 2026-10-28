package cl.duoc.gamezone.dao;

import cl.duoc.gamezone.model.JuegoDigital;
import cl.duoc.gamezone.model.JuegoFisico;
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
 * Implementación de {@link VideojuegoDao} que guarda cada subtipo en un archivo JSON usando Gson.
 */
public class VideojuegoJsonDao implements VideojuegoDao {

    // Gson convierte objetos Java a JSON y al revés. setPrettyPrinting deja el archivo ordenado y legible.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Los dos métodos cargar funcionan igual; solo cambian el archivo y el subtipo.
    @Override
    public List<JuegoDigital> cargarJuegosDigitales() {
        Path archivo = Path.of("juegos_digitales.json");
        // Primera ejecución: si el archivo aún no existe, se parte con una lista vacía (sin error).
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        // try-with-resources: cierra el archivo automáticamente al terminar, haya error o no.
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            // Gson lee el JSON (una lista de objetos) como un arreglo del subtipo, usando sus constructores vacíos.
            JuegoDigital[] leidos = gson.fromJson(lector, JuegoDigital[].class);
            // Si el archivo está vacío, fromJson devuelve null.
            if (leidos == null) {
                return new ArrayList<>();
            }
            // Arrays.asList devuelve una lista de tamaño fijo; se copia a un ArrayList para poder agregar y quitar.
            return new ArrayList<>(Arrays.asList(leidos));
        // IOException: falla del archivo. JsonParseException: el contenido no es un JSON válido.
        // Se convierte en PersistenciaException para que el controlador muestre el mensaje.
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer el archivo juegos_digitales.json.", e);
        }
    }

    @Override
    public void guardarJuegosDigitales(List<JuegoDigital> datos) {
        escribir("juegos_digitales.json", datos);
    }

    @Override
    public List<JuegoFisico> cargarJuegosFisicos() {
        Path archivo = Path.of("juegos_fisicos.json");
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            JuegoFisico[] leidos = gson.fromJson(lector, JuegoFisico[].class);
            if (leidos == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(leidos));
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer el archivo juegos_fisicos.json.", e);
        }
    }

    @Override
    public void guardarJuegosFisicos(List<JuegoFisico> datos) {
        escribir("juegos_fisicos.json", datos);
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
