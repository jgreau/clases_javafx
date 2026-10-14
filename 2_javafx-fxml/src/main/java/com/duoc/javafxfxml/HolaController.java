package com.duoc.javafxfxml;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HolaController {

    @FXML
    private Label mensajeLabel;

    private int clics = 0;

    @FXML
    private void onSaludarClick() {
        clics++;
        mensajeLabel.setText("¡Hola desde el controlador! Clics: " + clics);
    }
}
