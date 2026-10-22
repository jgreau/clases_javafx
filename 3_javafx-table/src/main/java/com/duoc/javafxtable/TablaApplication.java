package com.duoc.javafxtable;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Aplicación de contactos: la ventana parte mostrando contacto-view.fxml. */
public class TablaApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("contacto-view.fxml"));
        Parent raiz = loader.load();

        stage.setTitle("Contactos");
        stage.setScene(new Scene(raiz, 640, 520));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
