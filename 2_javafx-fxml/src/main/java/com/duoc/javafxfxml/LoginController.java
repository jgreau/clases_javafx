package com.duoc.javafxfxml;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    // Usuario y clave fijos: solo para practicar (en un sistema real se validaría contra una BD)
    private static final String USUARIO_VALIDO = "admin";
    private static final String CLAVE_VALIDA = "1234";

    @FXML
    private TextField usuarioField;

    @FXML
    private PasswordField claveField;

    @FXML
    private Label errorLabel;

    @FXML
    private void onIngresarClick() throws IOException {
        String usuario = usuarioField.getText().trim();
        String clave = claveField.getText();

        if (usuario.isEmpty() || clave.isEmpty()) {
            errorLabel.setText("Debes ingresar usuario y clave.");
            return;
        }

        if (USUARIO_VALIDO.equals(usuario) && CLAVE_VALIDA.equals(clave)) {
            abrirBienvenida(usuario);
        } else {
            errorLabel.setText("Usuario o clave incorrectos.");
            claveField.clear();
        }
    }

    private void abrirBienvenida(String usuario) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("bienvenida-view.fxml"));
        Parent raiz = loader.load();

        // Podemos hablar con el otro controlador para pasarle datos
        BienvenidaController controlador = loader.getController();
        controlador.setUsuario(usuario);

        // La ventana actual se obtiene desde cualquier componente de la escena
        Stage stage = (Stage) usuarioField.getScene().getWindow();
        stage.setTitle("Bienvenida");
        stage.setScene(new Scene(raiz, 360, 280));
    }
}
