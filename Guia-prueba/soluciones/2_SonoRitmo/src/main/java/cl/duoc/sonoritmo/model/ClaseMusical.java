package cl.duoc.sonoritmo.model;

/**
 * Representa a cualquier clase del sistema de SonoRitmo.
 * Define los datos comunes y delega en cada tipo el cálculo de el costo.
 */
public abstract class ClaseMusical {

    private String codigoClase;
    private int duracionMeses;
    private int cupoMaximo;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    protected ClaseMusical() {
    }

    public ClaseMusical(String codigoClase, int duracionMeses, int cupoMaximo) {
        setCodigoClase(codigoClase);
        setDuracionMeses(duracionMeses);
        setCupoMaximo(cupoMaximo);
    }

    public String getCodigoClase() {
        return codigoClase;
    }

    public void setCodigoClase(String codigoClase) {
        if (codigoClase == null || codigoClase.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de clase no puede ser nulo ni vacío.");
        }
        this.codigoClase = codigoClase.trim();
    }

    public int getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(int duracionMeses) {
        if (duracionMeses < 1 || duracionMeses > 12) {
            throw new IllegalArgumentException("La duración debe estar entre 1 y 12 meses.");
        }
        this.duracionMeses = duracionMeses;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        if (cupoMaximo < 1) {
            throw new IllegalArgumentException("El cupo máximo debe ser mayor que cero.");
        }
        this.cupoMaximo = cupoMaximo;
    }

    /**
     * Calcula el costo según las reglas propias de cada tipo.
     *
     * @return costo en pesos
     */
    public abstract double calcularCosto();

    /**
     * Retorna el nombre del tipo (por ejemplo, "Clase Individual" o "Clase Grupal").
     *
     * @return tipo
     */
    public abstract String getTipo();

    @Override
    public String toString() {
        return codigoClase + " (" + duracionMeses + " meses)";
    }
}
