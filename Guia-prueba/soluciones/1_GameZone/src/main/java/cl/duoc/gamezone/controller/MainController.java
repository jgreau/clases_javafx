package cl.duoc.gamezone.controller;

import cl.duoc.gamezone.dao.JsonDao;
import cl.duoc.gamezone.dao.PersistenciaException;
import cl.duoc.gamezone.model.Videojuego;
import cl.duoc.gamezone.model.JuegoDigital;
import cl.duoc.gamezone.model.JuegoFisico;
import cl.duoc.gamezone.repository.VideojuegoRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


/**
 * Controlador de la vista principal: coordina el formulario, la tabla y los mensajes.
 * La lógica de negocio vive en el modelo y el acceso a datos en el repositorio.
 */
public class MainController {

    private static final String TIPO_JUEGO_DIGITAL = "Juego Digital";
    private static final String TIPO_JUEGO_FISICO = "Juego Físico";
    private static final Locale CHILE = Locale.forLanguageTag("es-CL");
    private static final DecimalFormat FORMATO_MONEDA = new DecimalFormat("$#,##0", DecimalFormatSymbols.getInstance(CHILE));
    private static final String COLOR_OK = "-fx-text-fill: #1b5e20;";
    private static final String COLOR_ERROR = "-fx-text-fill: #b71c1c;";

    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAnioLanzamiento;
    @FXML private TextField txtTamanioGb;
    @FXML private TextField txtPlataforma;
    @FXML private CheckBox chkParcheDiaUno;
    @FXML private CheckBox chkAccesoAnticipado;
    @FXML private TextField txtDlcIncluidos;
    @FXML private TableView<Videojuego> tabla;
    @FXML private TableColumn<Videojuego, String> colTipo;
    @FXML private TableColumn<Videojuego, String> colTitulo;
    @FXML private TableColumn<Videojuego, String> colAnioLanzamiento;
    @FXML private TableColumn<Videojuego, String> colTamanioGb;
    @FXML private TableColumn<Videojuego, String> colCosto;
    @FXML private Label lblMensaje;

    private final VideojuegoRepository repositorio = new VideojuegoRepository(
            new JsonDao<>("juegos_digitales.json", JuegoDigital.class),
            new JsonDao<>("juegos_fisicos.json", JuegoFisico.class));
    private final ObservableList<Videojuego> datosTabla = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // El selector muestra el texto de ayuda también cuando no hay selección.
        cbTipo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio || item == null ? cbTipo.getPromptText() : item);
            }
        });
        cbTipo.valueProperty().addListener((obs, anterior, nuevo) -> actualizarCamposPorTipo());

        colTipo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getTipo()));
        colTitulo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getTitulo()));
        colAnioLanzamiento.setCellValueFactory(celda -> new SimpleStringProperty(String.valueOf(celda.getValue().getAnioLanzamiento())));
        colTamanioGb.setCellValueFactory(celda -> new SimpleStringProperty(String.format(CHILE, "%.1f", celda.getValue().getTamanioGb())));
        colCosto.setCellValueFactory(celda -> new SimpleStringProperty(
                FORMATO_MONEDA.format(celda.getValue().calcularPrecio())));
        tabla.setItems(datosTabla);

        actualizarCamposPorTipo();
        try {
            repositorio.cargar();
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
        refrescarTabla();
    }

    @FXML
    private void onRegistrar() {
        try {
            Videojuego nuevo = construirDesdeFormulario();
            repositorio.agregar(nuevo);
            refrescarTabla();
            mostrarExito(nuevo.getTitulo() + " (" + nuevo.getTipo() + ") registrado correctamente.");
            limpiarFormulario();
        } catch (IllegalArgumentException | PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Videojuego seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Debe seleccionar un videojuego de la tabla para eliminarlo.");
            return;
        }
        try {
            repositorio.eliminar(seleccionado);
            refrescarTabla();
            mostrarExito(seleccionado.getTitulo() + " (" + seleccionado.getTipo() + ") eliminado correctamente.");
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Valida el formulario y construye el objeto del tipo elegido. Lanza
     * IllegalArgumentException con el campo y el motivo si algún dato no es válido.
     */
    private Videojuego construirDesdeFormulario() {
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo: debe seleccionar un tipo de videojuego.");
        }
        String titulo = leerTexto(txtTitulo, "Título");
        int anioLanzamiento = leerEntero(txtAnioLanzamiento, "Año de lanzamiento", 1990, 2026, "debe estar entre 1990 y 2026.");
        double tamanioGb = leerDecimalPositivo(txtTamanioGb, "Tamaño (GB)");

        if (TIPO_JUEGO_DIGITAL.equals(tipo)) {
            String plataforma = leerTexto(txtPlataforma, "Plataforma");
            JuegoDigital juegoDigital = new JuegoDigital(titulo, anioLanzamiento, tamanioGb, plataforma, chkParcheDiaUno.isSelected());
            if (chkAccesoAnticipado.isSelected()) {
                juegoDigital.activarAccesoAnticipado();
            }
            return juegoDigital;
        }

        // Segundo tipo: Juego Físico
        int dlcIncluidos = leerEntero(txtDlcIncluidos, "DLC incluidos", 0, Integer.MAX_VALUE, "debe ser mayor o igual que cero.");
        JuegoFisico juegoFisico = new JuegoFisico(titulo, anioLanzamiento, tamanioGb, dlcIncluidos);
        return juegoFisico;
    }

    private String leerTexto(TextField campo, String nombre) {
        String valor = campo.getText() == null ? "" : campo.getText().trim();
        if (valor.isEmpty()) {
            throw new IllegalArgumentException(nombre + ": no puede estar vacío ni contener solo espacios.");
        }
        return valor;
    }

    private int leerEntero(TextField campo, String nombre, int minimo, int maximo, String regla) {
        int valor;
        try {
            valor = Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombre + ": debe ser un número entero.");
        }
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException(nombre + ": " + regla);
        }
        return valor;
    }

    private double leerDecimalPositivo(TextField campo, String nombre) {
        double valor;
        try {
            valor = Double.parseDouble(campo.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombre + ": debe ser un número (se aceptan decimales).");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException(nombre + ": debe ser mayor que cero.");
        }
        return valor;
    }

    /** Habilita solo los datos propios del tipo elegido; los demás se deshabilitan y se vacían. */
    private void actualizarCamposPorTipo() {
        String tipo = cbTipo.getValue();
        boolean esJuegoDigital = TIPO_JUEGO_DIGITAL.equals(tipo);
        boolean esJuegoFisico = TIPO_JUEGO_FISICO.equals(tipo);
        txtPlataforma.setDisable(!esJuegoDigital);
        if (!esJuegoDigital) {
            txtPlataforma.clear();
        }
        chkParcheDiaUno.setDisable(!esJuegoDigital);
        if (!esJuegoDigital) {
            chkParcheDiaUno.setSelected(false);
        }
        chkAccesoAnticipado.setDisable(!esJuegoDigital);
        if (!esJuegoDigital) {
            chkAccesoAnticipado.setSelected(false);
        }
        txtDlcIncluidos.setDisable(!esJuegoFisico);
        if (!esJuegoFisico) {
            txtDlcIncluidos.clear();
        }
    }

    private void limpiarFormulario() {
        txtTitulo.clear();
        txtAnioLanzamiento.clear();
        txtTamanioGb.clear();
        cbTipo.setValue(null);
        actualizarCamposPorTipo();
    }

    private void refrescarTabla() {
        datosTabla.setAll(repositorio.listar());
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setStyle(COLOR_OK);
        lblMensaje.setText(mensaje);
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setStyle(COLOR_ERROR);
        lblMensaje.setText(mensaje);
    }
}
