package com.duoc.javafxfxml;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class BienvenidaController {

    @FXML
    private Label saludoLabel;

    public void setUsuario(String usuario) {
        saludoLabel.setText("¡Bienvenido, " + usuario + "!");
    }

    @FXML
    private void onCerrarSesionClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Parent raiz = loader.load();

        Stage stage = (Stage) saludoLabel.getScene().getWindow();
        stage.setTitle("Login");
        stage.setScene(new Scene(raiz, 360, 280));
    }
}
