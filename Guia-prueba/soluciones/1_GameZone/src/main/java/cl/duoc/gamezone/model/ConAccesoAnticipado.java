package cl.duoc.gamezone.model;

/**
 * Contrato que cumplen los videojuegos que participan en el programa de acceso anticipado de la plataforma.
 */
public interface ConAccesoAnticipado {

    /**
     * Indica si el videojuego tiene acceso anticipado activo.
     *
     * @return true si corresponde
     */
    boolean tieneAccesoAnticipado();

    /**
     * Activa el acceso anticipado del videojuego.
     */
    void activarAccesoAnticipado();
}
