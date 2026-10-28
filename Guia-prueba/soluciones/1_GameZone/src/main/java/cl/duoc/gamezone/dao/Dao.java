package cl.duoc.gamezone.dao;

import java.util.List;

/**
 * Contrato de acceso a datos: lectura y escritura de una colección de objetos de dominio.
 *
 * @param <T> tipo de objeto que se almacena
 */
public interface Dao<T> {

    /**
     * Lee todos los objetos almacenados.
     *
     * @return lista de objetos; vacía si aún no hay datos
     * @throws PersistenciaException si el medio de almacenamiento no se puede leer
     */
    List<T> cargar();

    /**
     * Reemplaza el contenido almacenado por la lista recibida.
     *
     * @param datos objetos que deben quedar almacenados
     * @throws PersistenciaException si el medio de almacenamiento no se puede escribir
     */
    void guardar(List<T> datos);
}
