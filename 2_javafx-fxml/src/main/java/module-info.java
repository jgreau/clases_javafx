module com.duoc.javafxfxml {
    requires javafx.controls;
    requires javafx.fxml;

    // FXMLLoader usa reflexión para crear los controladores e inyectar los @FXML
    opens com.duoc.javafxfxml to javafx.fxml;
    exports com.duoc.javafxfxml;
}
