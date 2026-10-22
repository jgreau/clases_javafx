module com.duoc.javafxtable {
    requires javafx.controls;
    requires javafx.fxml;

    // PASO 2 (guía): agregar "requires com.google.gson;"

    opens com.duoc.javafxtable to javafx.fxml;
    // PASO 2 (guía): abrir el paquete también a javafx.base (PropertyValueFactory) y com.google.gson
    exports com.duoc.javafxtable;
}
