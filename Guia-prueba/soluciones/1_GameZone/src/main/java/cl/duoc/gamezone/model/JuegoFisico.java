package cl.duoc.gamezone.model;

/**
 * El juego físico del sistema.
 * Precio: base $29.990; si la cantidad de DLC incluidos supera 3, recargo del 10%.
 */
public class JuegoFisico extends Videojuego {

    private static final double PRECIO_BASE = 29990;
    private static final double RECARGO = 0.1;
    private static final int LIMITE_DLC = 3;

    private int dlcIncluidos;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public JuegoFisico() {
    }

    public JuegoFisico(String titulo, int anioLanzamiento, double tamanioGb, int dlcIncluidos) {
        super(titulo, anioLanzamiento, tamanioGb);
        setDlcIncluidos(dlcIncluidos);
    }

    public int getDlcIncluidos() {
        return dlcIncluidos;
    }

    public void setDlcIncluidos(int dlcIncluidos) {
        this.dlcIncluidos = dlcIncluidos;
    }

    @Override
    public double calcularPrecio() {
        if (dlcIncluidos > LIMITE_DLC) {
            return PRECIO_BASE + PRECIO_BASE * RECARGO;
        }
        return PRECIO_BASE;
    }

    @Override
    public String getTipo() {
        return "Juego Físico";
    }
}
