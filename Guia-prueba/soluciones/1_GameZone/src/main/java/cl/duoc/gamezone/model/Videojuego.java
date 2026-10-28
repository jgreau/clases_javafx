package cl.duoc.gamezone.model;

/**
 * Representa a cualquier videojuego del sistema de GameZone.
 * Define los datos comunes y delega en cada tipo el cálculo de el precio.
 */
public abstract class Videojuego {

    private String titulo;
    private int anioLanzamiento;
    private double tamanioGb;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    protected Videojuego() {
    }

    public Videojuego(String titulo, int anioLanzamiento, double tamanioGb) {
        setTitulo(titulo);
        setAnioLanzamiento(anioLanzamiento);
        setTamanioGb(tamanioGb);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede ser nulo ni vacío.");
        }
        this.titulo = titulo.trim();
    }

    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public void setAnioLanzamiento(int anioLanzamiento) {
        if (anioLanzamiento < 1990 || anioLanzamiento > 2026) {
            throw new IllegalArgumentException("El año de lanzamiento debe estar entre 1990 y 2026.");
        }
        this.anioLanzamiento = anioLanzamiento;
    }

    public double getTamanioGb() {
        return tamanioGb;
    }

    public void setTamanioGb(double tamanioGb) {
        if (tamanioGb <= 0) {
            throw new IllegalArgumentException("El tamaño debe ser mayor que cero.");
        }
        this.tamanioGb = tamanioGb;
    }

    /**
     * Calcula el precio según las reglas propias de cada tipo.
     *
     * @return precio en pesos
     */
    public abstract double calcularPrecio();

    /**
     * Retorna el nombre del tipo (por ejemplo, "Juego Digital" o "Juego Físico").
     *
     * @return tipo
     */
    public abstract String getTipo();

    @Override
    public String toString() {
        return "Título: " + titulo + " | Año: " + anioLanzamiento;
    }
}
