package cl.duoc.costaserena.model;

/**
 * Representa a cualquier habitación del sistema de Costa Serena.
 * Define los datos comunes y delega en cada tipo el cálculo de la tarifa por noche.
 */
public abstract class Habitacion {

    private String numero;
    private int piso;
    private int capacidad;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    protected Habitacion() {
    }

    public Habitacion(String numero, int piso, int capacidad) {
        setNumero(numero);
        setPiso(piso);
        setCapacidad(capacidad);
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de habitación no puede ser nulo ni vacío.");
        }
        this.numero = numero.trim();
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        if (piso < 1 || piso > 20) {
            throw new IllegalArgumentException("El piso debe estar entre 1 y 20.");
        }
        this.piso = piso;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("La capacidad de huéspedes debe ser mayor que cero.");
        }
        this.capacidad = capacidad;
    }

    /**
     * Calcula la tarifa por noche según las reglas propias de cada tipo.
     *
     * @return tarifa en pesos
     */
    public abstract double calcularTarifa();

    /**
     * Retorna el nombre del tipo (por ejemplo, "Habitación Suite" o "Habitación Estándar").
     *
     * @return tipo
     */
    public abstract String getTipo();

    @Override
    public String toString() {
        return "Habitación: " + numero + " | Piso: " + piso;
    }
}
