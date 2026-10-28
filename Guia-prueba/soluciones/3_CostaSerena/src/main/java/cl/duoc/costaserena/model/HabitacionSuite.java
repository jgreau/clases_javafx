package cl.duoc.costaserena.model;

/**
 * La habitación suite del sistema.
 * Tarifa: base $95.000; si no incluye desayuno buffet, recargo del 10%.
 */
public class HabitacionSuite extends Habitacion implements ConLateCheckout {

    private static final double TARIFA_BASE = 95000;
    private static final double RECARGO = 0.1;

    private String vista;
    private boolean desayunoBuffet;
    private boolean lateCheckout;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public HabitacionSuite() {
    }

    public HabitacionSuite(String numero, int piso, int capacidad, String vista, boolean desayunoBuffet) {
        super(numero, piso, capacidad);
        setVista(vista);
        setDesayunoBuffet(desayunoBuffet);
        this.lateCheckout = false;
    }

    public String getVista() {
        return vista;
    }

    public void setVista(String vista) {
        this.vista = vista;
    }

    public boolean isDesayunoBuffet() {
        return desayunoBuffet;
    }

    public void setDesayunoBuffet(boolean desayunoBuffet) {
        this.desayunoBuffet = desayunoBuffet;
    }

    @Override
    public boolean tieneLateCheckout() {
        return lateCheckout;
    }

    @Override
    public void activarLateCheckout() {
        this.lateCheckout = true;
    }

    @Override
    public double calcularTarifa() {
        if (!desayunoBuffet) {
            return TARIFA_BASE + TARIFA_BASE * RECARGO;
        }
        return TARIFA_BASE;
    }

    @Override
    public String getTipo() {
        return "Habitación Suite";
    }
}
