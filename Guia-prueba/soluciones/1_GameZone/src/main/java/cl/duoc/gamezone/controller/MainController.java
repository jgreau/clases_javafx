package cl.duoc.gamezone.controller;

import cl.duoc.gamezone.dao.VideojuegoJsonDao;
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
    // Configuración regional de Chile: coma para los decimales y punto para los miles.
    private static final Locale CHILE = Locale.forLanguageTag("es-CL");
    // Formato de dinero: "$#,##0" muestra 21989 como $21.989.
    private static final DecimalFormat FORMATO_MONEDA = new DecimalFormat("$#,##0", DecimalFormatSymbols.getInstance(CHILE));

    // Controles de la vista (main-view.fxml). Cada atributo @FXML debe llamarse igual que su fx:id.
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

    // El repositorio guarda y carga los datos; el controlador solo le pide que lo haga.
    private final VideojuegoRepository repositorio = new VideojuegoRepository(new VideojuegoJsonDao());
    // Lista observable: lo que se agregue o quite aquí se refleja de inmediato en la tabla.
    private final ObservableList<Videojuego> datosTabla = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Un ComboBox sin selección no muestra su texto de ayuda (promptText) por sí solo.
        // Esta celda personalizada escribe ese texto cuando no hay ningún elemento elegido.
        cbTipo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio || item == null ? cbTipo.getPromptText() : item);
            }
        });
        // Cada vez que el usuario cambia el tipo, se habilitan solo los campos de ese tipo.
        // (obs, anterior, nuevo) -> ... es una lambda: función corta que JavaFX ejecuta en cada cambio.
        cbTipo.valueProperty().addListener((obs, anterior, nuevo) -> actualizarCamposPorTipo());

        // setCellValueFactory le dice a cada columna qué texto mostrar en cada fila.
        // celda.getValue() es el objeto de esa fila; se envuelve en un SimpleStringProperty
        // porque la columna espera una "propiedad" y no un String suelto.
        // El tipo y el valor calculado no son atributos, por eso se piden con sus métodos del modelo.
        colTipo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getTipo()));
        colTitulo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getTitulo()));
        colAnioLanzamiento.setCellValueFactory(celda -> new SimpleStringProperty(String.valueOf(celda.getValue().getAnioLanzamiento())));
        colTamanioGb.setCellValueFactory(celda -> new SimpleStringProperty(String.format(CHILE, "%.1f", celda.getValue().getTamanioGb())));
        colCosto.setCellValueFactory(celda -> new SimpleStringProperty(
                FORMATO_MONEDA.format(celda.getValue().calcularPrecio())));
        // Enlaza la tabla con la lista observable: la tabla mostrará lo que tenga datosTabla.
        tabla.setItems(datosTabla);

        // Al abrir la ventana no hay tipo elegido, así que todos los campos específicos quedan deshabilitados.
        // Luego se leen los archivos JSON; si falla la lectura, se avisa en la ventana y la app sigue.
        actualizarCamposPorTipo();
        try {
            repositorio.cargar();
        } catch (PersistenciaException e) {
            mostrarMensaje(e.getMessage());
        }
        refrescarTabla();
    }

    @FXML
    private void onRegistrar() {
        try {
            // Valida el formulario y crea el objeto; si algún dato es inválido lanza IllegalArgumentException.
            Videojuego nuevo = construirDesdeFormulario();
            repositorio.agregar(nuevo);
            refrescarTabla();
            mostrarMensaje(nuevo.getTitulo() + " (" + nuevo.getTipo() + ") registrado correctamente.");
            limpiarFormulario();
        // Un solo catch para dos tipos de error: dato inválido (formulario o modelo) y falla al guardar archivos.
        // En ambos casos el mensaje del error se muestra en la ventana, no en la consola.
        } catch (IllegalArgumentException | PersistenciaException e) {
            mostrarMensaje(e.getMessage());
        }
    }

    @FXML
    private void onEliminar() {
        Videojuego seleccionado = tabla.getSelectionModel().getSelectedItem();
        // getSelectedItem() devuelve null cuando no hay ninguna fila seleccionada.
        if (seleccionado == null) {
            mostrarMensaje("Debe seleccionar un videojuego de la tabla para eliminarlo.");
            return;
        }
        try {
            repositorio.eliminar(seleccionado);
            refrescarTabla();
            mostrarMensaje(seleccionado.getTitulo() + " (" + seleccionado.getTipo() + ") eliminado correctamente.");
        } catch (PersistenciaException e) {
            mostrarMensaje(e.getMessage());
        }
    }

    /**
     * Valida el formulario y construye el objeto del tipo elegido. Lanza
     * IllegalArgumentException con el campo y el motivo si algún dato no es válido.
     */
    private Videojuego construirDesdeFormulario() {
        // getValue() devuelve null si no se eligió ningún tipo en el selector.
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo: debe seleccionar un tipo de videojuego.");
        }
        String titulo = leerTexto(txtTitulo, "Título");
        int anioLanzamiento = leerEntero(txtAnioLanzamiento, "Año de lanzamiento", 1990, 2026, "debe estar entre 1990 y 2026.");
        double tamanioGb = leerDecimalPositivo(txtTamanioGb, "Tamaño (GB)");

        // Según el tipo elegido se leen solo sus campos específicos y se crea el subtipo correspondiente.
        // Se compara con la constante a la izquierda para evitar errores si tipo fuera null.
        if (TIPO_JUEGO_DIGITAL.equals(tipo)) {
            String plataforma = leerTexto(txtPlataforma, "Plataforma");
            JuegoDigital juegoDigital = new JuegoDigital(titulo, anioLanzamiento, tamanioGb, plataforma, chkParcheDiaUno.isSelected());
            // El constructor deja este dato en falso; si la casilla está marcada se activa con el método de la interfaz.
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
            // parseInt lanza NumberFormatException si el texto no es un número entero (por ejemplo "abc" o "2.5").
            valor = Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombre + ": debe ser un número entero.");
        }
        // Integer.MAX_VALUE como máximo significa "sin límite superior".
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException(nombre + ": " + regla);
        }
        return valor;
    }

    private double leerDecimalPositivo(TextField campo, String nombre) {
        double valor;
        try {
            // Se cambia la coma por punto para aceptar "22,5" y "22.5"; parseDouble solo entiende el punto.
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
        // setDisable(true) bloquea el control. Se deshabilita todo campo que NO pertenece al tipo elegido,
        // y se vacía para que no quede un valor viejo que se confunda al registrar.
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
        // null deja el selector sin elección (vuelve a verse el texto de ayuda).
        cbTipo.setValue(null);
        actualizarCamposPorTipo();
    }

    private void refrescarTabla() {
        // setAll reemplaza todo el contenido de la lista observable; la tabla se redibuja sola.
        datosTabla.setAll(repositorio.listar());
    }

    private void mostrarMensaje(String mensaje) {
        lblMensaje.setText(mensaje);
    }
}
