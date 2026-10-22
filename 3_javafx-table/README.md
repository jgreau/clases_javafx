# javafx-table

Proyecto JavaFX 21 **base** para practicar `TableView` + lista observable + persistencia en JSON (Gson).

Contiene el `pom.xml` (sin Gson), `module-info.java`, `TablaApplication`, un `ContactoController` vacío y un
`contacto-view.fxml` incompleto. El resto se completa siguiendo la guía: `doc/JavaFX-Table-Guia.pdf`.

## Qué se construye
- Formulario con 4 campos (nombre, apellido, correo, teléfono) y validaciones básicas.
- `TableView` conectado a una `ObservableList`.
- Botón **Eliminar** (fila seleccionada) y botón **Guardar** (escribe `datos/contactos.json` con Gson).
- Al abrir la aplicación, los contactos se cargan desde `datos/contactos.json`.

## Requisitos
- JDK 21
- Maven 3.9+ (o el Maven integrado de IntelliJ)

## Ejecutar
```
mvn javafx:run
```
Ejecutar siempre desde la raíz del proyecto: la ruta `datos/contactos.json` es relativa.
