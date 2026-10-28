package cl.duoc.costaserena.controller;

import cl.duoc.costaserena.dao.JsonDao;
import cl.duoc.costaserena.dao.PersistenciaException;
import cl.duoc.costaserena.model.Habitacion;
import cl.duoc.costaserena.model.HabitacionSuite;
import cl.duoc.costaserena.model.HabitacionEstandar;
import cl.duoc.costaserena.repository.HabitacionRepository;
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

    private static final String TIPO_HABITACION_SUITE = "Habitación Suite";
    private static final String TIPO_HABITACION_ESTANDAR = "Habitación Estándar";
    private static final Locale CHILE = Locale.forLanguageTag("es-CL");
    private static final DecimalFormat FORMATO_MONEDA = new DecimalFormat("$#,##0", DecimalFormatSymbols.getInstance(CHILE));
    private static final String COLOR_OK = "-fx-text-fill: #1b5e20;";
    private static final String COLOR_ERROR = "-fx-text-fill: #b71c1c;";

    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtNumero;
    @FXML private TextField txtPiso;
    @FXML private TextField txtCapacidad;
    @FXML private TextField txtVista;
    @FXML private CheckBox chkDesayunoBuffet;
    @FXML private CheckBox chkLateCheckout;
    @FXML private TextField txtCamasAdicionales;
    @FXML private TableView<Habitacion> tabla;
    @FXML private TableColumn<Habitacion, String> colTipo;
    @FXML private TableColumn<Habitacion, String> colNumero;
    @FXML private TableColumn<Habitacion, String> colPiso;
    @FXML private TableColumn<Habitacion, String> colCapacidad;
    @FXML private TableColumn<Habitacion, String> colCosto;
    @FXML private Label lblMensaje;

    private final HabitacionRepository repositorio = new HabitacionRepository(
            new JsonDao<>("habitaciones_suite.json", HabitacionSuite.class),
            new JsonDao<>("habitaciones_estandar.json", HabitacionEstandar.class));
    private final ObservableList<Habitacion> datosTabla = FXCollections.observableArrayList();

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
        colNumero.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getNumero()));
        colPiso.setCellValueFactory(celda -> new SimpleStringProperty(String.valueOf(celda.getValue().getPiso())));
        colCapacidad.setCellValueFactory(celda -> new SimpleStringProperty(String.valueOf(celda.getValue().getCapacidad())));
        colCosto.setCellValueFactory(celda -> new SimpleStringProperty(
                FORMATO_MONEDA.format(celda.getValue().calcularTarifa())));
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
            Habitacion nuevo = construirDesdeFormulario();
            repositorio.agregar(nuevo);
            refrescarTabla();
            mostrarExito(nuevo.getNumero() + " (" + nuevo.getTipo() + ") registrada correctamente.");
            limpiarFormulario();
        } catch (IllegalArgumentException | PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Habitacion seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Debe seleccionar una habitación de la tabla para eliminarla.");
            return;
        }
        try {
            repositorio.eliminar(seleccionado);
            refrescarTabla();
            mostrarExito(seleccionado.getNumero() + " (" + seleccionado.getTipo() + ") eliminada correctamente.");
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Valida el formulario y construye el objeto del tipo elegido. Lanza
     * IllegalArgumentException con el campo y el motivo si algún dato no es válido.
     */
    private Habitacion construirDesdeFormulario() {
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo: debe seleccionar un tipo de habitación.");
        }
        String numero = leerTexto(txtNumero, "Número de habitación");
        int piso = leerEntero(txtPiso, "Piso", 1, 20, "debe estar entre 1 y 20.");
        int capacidad = leerEntero(txtCapacidad, "Capacidad (huéspedes)", 1, Integer.MAX_VALUE, "debe ser mayor que cero.");

        if (TIPO_HABITACION_SUITE.equals(tipo)) {
            String vista = leerTexto(txtVista, "Vista");
            HabitacionSuite habitacionSuite = new HabitacionSuite(numero, piso, capacidad, vista, chkDesayunoBuffet.isSelected());
            if (chkLateCheckout.isSelected()) {
                habitacionSuite.activarLateCheckout();
            }
            return habitacionSuite;
        }

        // Segundo tipo: Habitación Estándar
        int camasAdicionales = leerEntero(txtCamasAdicionales, "Camas adicionales", 0, Integer.MAX_VALUE, "debe ser mayor o igual que cero.");
        HabitacionEstandar habitacionEstandar = new HabitacionEstandar(numero, piso, capacidad, camasAdicionales);
        return habitacionEstandar;
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
        boolean esHabitacionSuite = TIPO_HABITACION_SUITE.equals(tipo);
        boolean esHabitacionEstandar = TIPO_HABITACION_ESTANDAR.equals(tipo);
        txtVista.setDisable(!esHabitacionSuite);
        if (!esHabitacionSuite) {
            txtVista.clear();
        }
        chkDesayunoBuffet.setDisable(!esHabitacionSuite);
        if (!esHabitacionSuite) {
            chkDesayunoBuffet.setSelected(false);
        }
        chkLateCheckout.setDisable(!esHabitacionSuite);
        if (!esHabitacionSuite) {
            chkLateCheckout.setSelected(false);
        }
        txtCamasAdicionales.setDisable(!esHabitacionEstandar);
        if (!esHabitacionEstandar) {
            txtCamasAdicionales.clear();
        }
    }

    private void limpiarFormulario() {
        txtNumero.clear();
        txtPiso.clear();
        txtCapacidad.clear();
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
