package cl.duoc.costaserena.model;

/**
 * La habitación estándar del sistema.
 * Tarifa: base $55.000; si la cantidad de camas adicionales supera 1, recargo del 20%.
 */
public class HabitacionEstandar extends Habitacion {

    private static final double TARIFA_BASE = 55000;
    private static final double RECARGO = 0.2;
    private static final int LIMITE_CAMAS = 1;

    private int camasAdicionales;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public HabitacionEstandar() {
    }

    public HabitacionEstandar(String numero, int piso, int capacidad, int camasAdicionales) {
        super(numero, piso, capacidad);
        setCamasAdicionales(camasAdicionales);
    }

    public int getCamasAdicionales() {
        return camasAdicionales;
    }

    public void setCamasAdicionales(int camasAdicionales) {
        this.camasAdicionales = camasAdicionales;
    }

    @Override
    public double calcularTarifa() {
        if (camasAdicionales > LIMITE_CAMAS) {
            return TARIFA_BASE + TARIFA_BASE * RECARGO;
        }
        return TARIFA_BASE;
    }

    @Override
    public String getTipo() {
        return "Habitación Estándar";
    }
}
