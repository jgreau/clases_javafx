package com.duoc.javafxfxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Aplicación de login: la ventana parte mostrando login-view.fxml. */
public class LoginApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Parent raiz = loader.load();

        stage.setTitle("Login");
        stage.setScene(new Scene(raiz, 360, 280));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
