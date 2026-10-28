package cl.duoc.sonoritmo;

import javafx.application.Application;

/**
 * Punto de entrada del programa. Ejecuta esta clase para iniciar la aplicación.
 * <p>
 * Existe como clase separada de {@link App} para que la aplicación JavaFX
 * arranque correctamente desde IntelliJ IDEA sin configuración adicional.
 * No es necesario modificarla.
 */
public class Launcher {

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
