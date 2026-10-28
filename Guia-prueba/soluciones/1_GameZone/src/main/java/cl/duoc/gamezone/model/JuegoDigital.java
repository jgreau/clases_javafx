package cl.duoc.gamezone.model;

/**
 * El juego digital del sistema.
 * Precio: base $19.990; si el juego no cuenta con el parche del día uno instalado, recargo del 10%.
 */
public class JuegoDigital extends Videojuego implements ConAccesoAnticipado {

    private static final double PRECIO_BASE = 19990;
    private static final double RECARGO = 0.1;

    private String plataforma;
    private boolean parcheDiaUno;
    private boolean accesoAnticipado;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public JuegoDigital() {
    }

    public JuegoDigital(String titulo, int anioLanzamiento, double tamanioGb, String plataforma, boolean parcheDiaUno) {
        super(titulo, anioLanzamiento, tamanioGb);
        setPlataforma(plataforma);
        setParcheDiaUno(parcheDiaUno);
        this.accesoAnticipado = false;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public boolean isParcheDiaUno() {
        return parcheDiaUno;
    }

    public void setParcheDiaUno(boolean parcheDiaUno) {
        this.parcheDiaUno = parcheDiaUno;
    }

    @Override
    public boolean tieneAccesoAnticipado() {
        return accesoAnticipado;
    }

    @Override
    public void activarAccesoAnticipado() {
        this.accesoAnticipado = true;
    }

    @Override
    public double calcularPrecio() {
        if (!parcheDiaUno) {
            return PRECIO_BASE + PRECIO_BASE * RECARGO;
        }
        return PRECIO_BASE;
    }

    @Override
    public String getTipo() {
        return "Juego Digital";
    }
}
