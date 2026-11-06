package cl.duoc.capacitacion.dao;

import cl.duoc.capacitacion.model.CursoCertificado;
import cl.duoc.capacitacion.model.CursoLibre;
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
 * Implementación de {@link CursoDao} que guarda cada subtipo en un archivo JSON usando Gson.
 */
public class CursoJsonDao implements CursoDao {

    // Gson convierte objetos Java a JSON y al revés. setPrettyPrinting deja el archivo ordenado y legible.
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Los dos métodos cargar funcionan igual; solo cambian el archivo y el subtipo.
    @Override
    public List<CursoCertificado> cargarCursosCertificados() {
        Path archivo = Path.of("cursos_certificados.json");
        // Primera ejecución: si el archivo aún no existe, se parte con una lista vacía (sin error).
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        // try-with-resources: cierra el archivo automáticamente al terminar, haya error o no.
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            // Gson lee el JSON (una lista de objetos) como un arreglo del subtipo, usando sus constructores vacíos.
            CursoCertificado[] leidos = gson.fromJson(lector, CursoCertificado[].class);
            // Si el archivo está vacío, fromJson devuelve null.
            if (leidos == null) {
                return new ArrayList<>();
            }
            // Arrays.asList devuelve una lista de tamaño fijo; se copia a un ArrayList para poder agregar y quitar.
            return new ArrayList<>(Arrays.asList(leidos));
        // IOException: falla del archivo. JsonParseException: el contenido no es un JSON válido.
        // Se convierte en PersistenciaException para que el controlador muestre el mensaje.
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer el archivo cursos_certificados.json.", e);
        }
    }

    @Override
    public void guardarCursosCertificados(List<CursoCertificado> datos) {
        escribir("cursos_certificados.json", datos);
    }

    @Override
    public List<CursoLibre> cargarCursosLibres() {
        Path archivo = Path.of("cursos_libres.json");
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            CursoLibre[] leidos = gson.fromJson(lector, CursoLibre[].class);
            if (leidos == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(leidos));
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer el archivo cursos_libres.json.", e);
        }
    }

    @Override
    public void guardarCursosLibres(List<CursoLibre> datos) {
        escribir("cursos_libres.json", datos);
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
