package cl.duoc.sonoritmo.controller;

import cl.duoc.sonoritmo.dao.JsonDao;
import cl.duoc.sonoritmo.dao.PersistenciaException;
import cl.duoc.sonoritmo.model.ClaseMusical;
import cl.duoc.sonoritmo.model.ClaseIndividual;
import cl.duoc.sonoritmo.model.ClaseGrupal;
import cl.duoc.sonoritmo.repository.ClaseMusicalRepository;
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

    private static final String TIPO_CLASE_INDIVIDUAL = "Clase Individual";
    private static final String TIPO_CLASE_GRUPAL = "Clase Grupal";
    private static final Locale CHILE = Locale.forLanguageTag("es-CL");
    private static final DecimalFormat FORMATO_MONEDA = new DecimalFormat("$#,##0", DecimalFormatSymbols.getInstance(CHILE));
    private static final String COLOR_OK = "-fx-text-fill: #1b5e20;";
    private static final String COLOR_ERROR = "-fx-text-fill: #b71c1c;";

    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtCodigoClase;
    @FXML private TextField txtDuracionMeses;
    @FXML private TextField txtCupoMaximo;
    @FXML private TextField txtInstrumento;
    @FXML private CheckBox chkEvaluacionNivel;
    @FXML private CheckBox chkSalaPrivada;
    @FXML private TextField txtIntegrantes;
    @FXML private TableView<ClaseMusical> tabla;
    @FXML private TableColumn<ClaseMusical, String> colTipo;
    @FXML private TableColumn<ClaseMusical, String> colCodigoClase;
    @FXML private TableColumn<ClaseMusical, String> colDuracionMeses;
    @FXML private TableColumn<ClaseMusical, String> colCupoMaximo;
    @FXML private TableColumn<ClaseMusical, String> colCosto;
    @FXML private Label lblMensaje;

    private final ClaseMusicalRepository repositorio = new ClaseMusicalRepository(
            new JsonDao<>("clases_individuales.json", ClaseIndividual.class),
            new JsonDao<>("clases_grupales.json", ClaseGrupal.class));
    private final ObservableList<ClaseMusical> datosTabla = FXCollections.observableArrayList();

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
        colCodigoClase.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getCodigoClase()));
        colDuracionMeses.setCellValueFactory(celda -> new SimpleStringProperty(String.valueOf(celda.getValue().getDuracionMeses())));
        colCupoMaximo.setCellValueFactory(celda -> new SimpleStringProperty(String.valueOf(celda.getValue().getCupoMaximo())));
        colCosto.setCellValueFactory(celda -> new SimpleStringProperty(
                FORMATO_MONEDA.format(celda.getValue().calcularCosto())));
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
            ClaseMusical nuevo = construirDesdeFormulario();
            repositorio.agregar(nuevo);
            refrescarTabla();
            mostrarExito(nuevo.getCodigoClase() + " (" + nuevo.getTipo() + ") registrada correctamente.");
            limpiarFormulario();
        } catch (IllegalArgumentException | PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        ClaseMusical seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Debe seleccionar una clase de la tabla para eliminarla.");
            return;
        }
        try {
            repositorio.eliminar(seleccionado);
            refrescarTabla();
            mostrarExito(seleccionado.getCodigoClase() + " (" + seleccionado.getTipo() + ") eliminada correctamente.");
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Valida el formulario y construye el objeto del tipo elegido. Lanza
     * IllegalArgumentException con el campo y el motivo si algún dato no es válido.
     */
    private ClaseMusical construirDesdeFormulario() {
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo: debe seleccionar un tipo de clase.");
        }
        String codigoClase = leerTexto(txtCodigoClase, "Código de clase");
        int duracionMeses = leerEntero(txtDuracionMeses, "Duración (meses)", 1, 12, "debe estar entre 1 y 12.");
        int cupoMaximo = leerEntero(txtCupoMaximo, "Cupo máximo", 1, Integer.MAX_VALUE, "debe ser mayor que cero.");

        if (TIPO_CLASE_INDIVIDUAL.equals(tipo)) {
            String instrumento = leerTexto(txtInstrumento, "Instrumento principal");
            ClaseIndividual claseIndividual = new ClaseIndividual(codigoClase, duracionMeses, cupoMaximo, instrumento, chkEvaluacionNivel.isSelected());
            if (chkSalaPrivada.isSelected()) {
                claseIndividual.asignarSalaPrivada();
            }
            return claseIndividual;
        }

        // Segundo tipo: Clase Grupal
        int integrantes = leerEntero(txtIntegrantes, "Integrantes", 1, Integer.MAX_VALUE, "debe ser mayor que cero.");
        ClaseGrupal claseGrupal = new ClaseGrupal(codigoClase, duracionMeses, cupoMaximo, integrantes);
        return claseGrupal;
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
        boolean esClaseIndividual = TIPO_CLASE_INDIVIDUAL.equals(tipo);
        boolean esClaseGrupal = TIPO_CLASE_GRUPAL.equals(tipo);
        txtInstrumento.setDisable(!esClaseIndividual);
        if (!esClaseIndividual) {
            txtInstrumento.clear();
        }
        chkEvaluacionNivel.setDisable(!esClaseIndividual);
        if (!esClaseIndividual) {
            chkEvaluacionNivel.setSelected(false);
        }
        chkSalaPrivada.setDisable(!esClaseIndividual);
        if (!esClaseIndividual) {
            chkSalaPrivada.setSelected(false);
        }
        txtIntegrantes.setDisable(!esClaseGrupal);
        if (!esClaseGrupal) {
            txtIntegrantes.clear();
        }
    }

    private void limpiarFormulario() {
        txtCodigoClase.clear();
        txtDuracionMeses.clear();
        txtCupoMaximo.clear();
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
