package cl.duoc.capacitacion.model;

/**
 * Representa a cualquier curso dictado por la empresa de capacitación.
 * Define los datos comunes y delega en cada tipo de curso el cálculo del
 * costo del curso.
 */
public abstract class Curso {

    private String codigo;
    private int duracionHoras;
    private int cupoMaximo;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    protected Curso() {
    }

    public Curso(String codigo, int duracionHoras, int cupoMaximo) {
        setCodigo(codigo);
        setDuracionHoras(duracionHoras);
        setCupoMaximo(cupoMaximo);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede ser nulo ni vacío.");
        }
        this.codigo = codigo.trim();
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(int duracionHoras) {
        if (duracionHoras < 4 || duracionHoras > 200) {
            throw new IllegalArgumentException("La duración debe estar entre 4 y 200 horas.");
        }
        this.duracionHoras = duracionHoras;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        if (cupoMaximo <= 0) {
            throw new IllegalArgumentException("El cupo máximo debe ser mayor que cero.");
        }
        this.cupoMaximo = cupoMaximo;
    }

    /**
     * Calcula el costo del curso según las reglas propias de cada tipo de curso.
     *
     * @return costo del curso en pesos
     */
    public abstract double calcularCosto();

    /**
     * Retorna el nombre del tipo de curso (por ejemplo, "Curso Certificado" o "Curso Libre").
     *
     * @return tipo de curso
     */
    public abstract String getTipo();

    @Override
    public String toString() {
        return "Código: " + codigo + " | Duración: " + duracionHoras + " horas";
    }
}
