package cl.duoc.sonoritmo.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de {@link Dao} que guarda los objetos en un archivo JSON usando Gson.
 *
 * @param <T> tipo de objeto que se almacena
 */
public class JsonDao<T> implements Dao<T> {

    private final Path archivo;
    private final Class<T> tipo;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonDao(String nombreArchivo, Class<T> tipo) {
        this.archivo = Path.of(nombreArchivo);
        this.tipo = tipo;
    }

    @Override
    public List<T> cargar() {
        if (!Files.exists(archivo)) {
            return new ArrayList<>();
        }
        try (Reader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            List<T> datos = gson.fromJson(lector, TypeToken.getParameterized(List.class, tipo).getType());
            return datos == null ? new ArrayList<>() : datos;
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo leer el archivo " + archivo.getFileName() + ".", e);
        }
    }

    @Override
    public void guardar(List<T> datos) {
        try (Writer escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            gson.toJson(datos, TypeToken.getParameterized(List.class, tipo).getType(), escritor);
        } catch (IOException | JsonParseException e) {
            throw new PersistenciaException("No se pudo guardar el archivo " + archivo.getFileName() + ".", e);
        }
    }
}
