package cl.duoc.costaserena.model;

/**
 * Contrato que cumplen las habitaciones que participan en el servicio de salida tardía del hotel.
 */
public interface ConLateCheckout {

    /**
     * Indica si la habitación tiene late checkout activo.
     *
     * @return true si corresponde
     */
    boolean tieneLateCheckout();

    /**
     * Activa el late checkout de la habitación.
     */
    void activarLateCheckout();
}
