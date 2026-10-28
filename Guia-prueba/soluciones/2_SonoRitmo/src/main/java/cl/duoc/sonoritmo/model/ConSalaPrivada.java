package cl.duoc.sonoritmo.model;

/**
 * Contrato que cumplen las clases que participan en el servicio de sala de ensayo privada de la academia.
 */
public interface ConSalaPrivada {

    /**
     * Indica si la clase tiene una sala privada asignada.
     *
     * @return true si corresponde
     */
    boolean tieneSalaPrivada();

    /**
     * Asigna una sala de ensayo privada a la clase.
     */
    void asignarSalaPrivada();
}
