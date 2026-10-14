# javafx-fxml

Proyecto JavaFX 21 con **FXML** (vista) + **controlador** (Maven, modular).

- `HolaApplication` + `hola-view.fxml` + `HolaController`: demo mínima.
- `LoginApplication` + `login-view.fxml` + `LoginController` + `bienvenida-view.fxml`: login (usuario `admin`, clave `1234`).

## Requisitos
- JDK 21
- Maven 3.9+ (o el Maven integrado de IntelliJ)

## Ejecutar
```
mvn javafx:run                                                   # login
mvn javafx:run -Dmain.class=com.duoc.javafxfxml.HolaApplication  # demo mínima
```
La documentación paso a paso está en `doc/`.
