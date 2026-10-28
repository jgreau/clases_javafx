package cl.duoc.sonoritmo.model;

/**
 * La clase grupal del sistema.
 * Costo: base $25.000; si la cantidad de integrantes supera 6, recargo del 10%.
 */
public class ClaseGrupal extends ClaseMusical {

    private static final double COSTO_BASE = 25000;
    private static final double RECARGO = 0.1;
    private static final int LIMITE_INTEGRANTES = 6;

    private int integrantes;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public ClaseGrupal() {
    }

    public ClaseGrupal(String codigoClase, int duracionMeses, int cupoMaximo, int integrantes) {
        super(codigoClase, duracionMeses, cupoMaximo);
        setIntegrantes(integrantes);
    }

    public int getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(int integrantes) {
        this.integrantes = integrantes;
    }

    @Override
    public double calcularCosto() {
        if (integrantes > LIMITE_INTEGRANTES) {
            return COSTO_BASE + COSTO_BASE * RECARGO;
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return "Clase Grupal";
    }
}
