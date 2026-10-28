package cl.duoc.gamezone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Aplicación JavaFX. Carga la vista principal y la muestra en la ventana.
 * <p>
 * La vista principal debe crearse con Scene Builder en:
 * src/main/resources/cl/duoc/gamezone/view/main-view.fxml
 */
public class App extends Application {

    /** Ubicación de la vista principal dentro de src/main/resources. */
    private static final String VISTA_PRINCIPAL = "/cl/duoc/gamezone/view/main-view.fxml";

    @Override
    public void start(Stage stage) throws Exception {
        URL vista = App.class.getResource(VISTA_PRINCIPAL);
        if (vista == null) {
            throw new IllegalStateException("No se encontró la vista principal: " + VISTA_PRINCIPAL);
        }
        Parent root = FXMLLoader.load(vista);
        stage.setTitle("GameZone - Catálogo de videojuegos");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
