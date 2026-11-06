package cl.duoc.capacitacion.model;

/**
 * Curso certificado dictado por la empresa. Participa en el proceso de emisión de diplomas.
 * Costo del curso: base $85.000; si la evaluación final no está al día, recargo del 20%.
 */
public class CursoCertificado extends Curso implements ConDiploma {

    private static final double COSTO_BASE = 85000;
    private static final double RECARGO_EVALUACION_PENDIENTE = 0.20;

    private String entidadCertificadora;
    private boolean evaluacionAlDia;
    private boolean diplomaEmitido;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public CursoCertificado() {
    }

    public CursoCertificado(String codigo, int duracionHoras, int cupoMaximo, String entidadCertificadora, boolean evaluacionAlDia) {
        super(codigo, duracionHoras, cupoMaximo);
        setEntidadCertificadora(entidadCertificadora);
        setEvaluacionAlDia(evaluacionAlDia);
        this.diplomaEmitido = false;
    }

    public String getEntidadCertificadora() {
        return entidadCertificadora;
    }

    public void setEntidadCertificadora(String entidadCertificadora) {
        this.entidadCertificadora = entidadCertificadora;
    }

    public boolean isEvaluacionAlDia() {
        return evaluacionAlDia;
    }

    public void setEvaluacionAlDia(boolean evaluacionAlDia) {
        this.evaluacionAlDia = evaluacionAlDia;
    }

    @Override
    public boolean tieneDiplomaEmitido() {
        return diplomaEmitido;
    }

    @Override
    public void emitirDiploma() {
        this.diplomaEmitido = true;
    }

    @Override
    public double calcularCosto() {
        if (evaluacionAlDia) {
            return COSTO_BASE;
        }
        return COSTO_BASE + COSTO_BASE * RECARGO_EVALUACION_PENDIENTE;
    }

    @Override
    public String getTipo() {
        return "Curso Certificado";
    }
}
