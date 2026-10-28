package cl.duoc.gamezone.dao;

/**
 * Error al leer o escribir el medio de almacenamiento.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
