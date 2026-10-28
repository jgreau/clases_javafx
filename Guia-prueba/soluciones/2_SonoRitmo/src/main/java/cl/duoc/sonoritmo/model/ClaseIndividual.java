package cl.duoc.sonoritmo.model;

/**
 * La clase individual del sistema.
 * Costo: base $60.000; si el estudiante no rindió evaluación de nivel, recargo del 15%.
 */
public class ClaseIndividual extends ClaseMusical implements ConSalaPrivada {

    private static final double COSTO_BASE = 60000;
    private static final double RECARGO = 0.15;

    private String instrumento;
    private boolean evaluacionNivel;
    private boolean salaPrivada;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public ClaseIndividual() {
    }

    public ClaseIndividual(String codigoClase, int duracionMeses, int cupoMaximo, String instrumento, boolean evaluacionNivel) {
        super(codigoClase, duracionMeses, cupoMaximo);
        setInstrumento(instrumento);
        setEvaluacionNivel(evaluacionNivel);
        this.salaPrivada = false;
    }

    public String getInstrumento() {
        return instrumento;
    }

    public void setInstrumento(String instrumento) {
        this.instrumento = instrumento;
    }

    public boolean isEvaluacionNivel() {
        return evaluacionNivel;
    }

    public void setEvaluacionNivel(boolean evaluacionNivel) {
        this.evaluacionNivel = evaluacionNivel;
    }

    @Override
    public boolean tieneSalaPrivada() {
        return salaPrivada;
    }

    @Override
    public void asignarSalaPrivada() {
        this.salaPrivada = true;
    }

    @Override
    public double calcularCosto() {
        if (!evaluacionNivel) {
            return COSTO_BASE + COSTO_BASE * RECARGO;
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return "Clase Individual";
    }
}
