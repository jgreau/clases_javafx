package cl.duoc.capacitacion.model;

/**
 * Curso libre dictado por la empresa.
 * Costo del curso: base $45.000; si la cantidad de inscritos supera 20, recargo del 10%.
 */
public class CursoLibre extends Curso {

    private static final double COSTO_BASE = 45000;
    private static final double RECARGO_INSCRITOS = 0.10;
    private static final int LIMITE_INSCRITOS = 20;

    private int inscritos;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public CursoLibre() {
    }

    public CursoLibre(String codigo, int duracionHoras, int cupoMaximo, int inscritos) {
        super(codigo, duracionHoras, cupoMaximo);
        setInscritos(inscritos);
    }

    public int getInscritos() {
        return inscritos;
    }

    public void setInscritos(int inscritos) {
        this.inscritos = inscritos;
    }

    @Override
    public double calcularCosto() {
        if (inscritos > LIMITE_INSCRITOS) {
            return COSTO_BASE + COSTO_BASE * RECARGO_INSCRITOS;
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return "Curso Libre";
    }
}
