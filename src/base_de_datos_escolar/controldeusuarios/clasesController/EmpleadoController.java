package base_de_datos_escolar.controldeusuarios.clasesController;

import base_de_datos_escolar.dashboard.clasesController.SesionUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class EmpleadoController implements Initializable {

    @FXML private TableView<Empleado> tableViewEmpleados;
    @FXML private TableColumn<Empleado, Integer> colId;
    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colApellido;
    @FXML private TableColumn<Empleado, Date> colFechaNacimiento;
    @FXML private TableColumn<Empleado, String> colTipoCargo;
    @FXML private TableColumn<Empleado, Double> colSalario;
    @FXML private TableColumn<Empleado, String> colCedula;
    @FXML private TableColumn<Empleado, String> colDireccion;
    @FXML private TableColumn<Empleado, String> colSexo;

    @FXML private ComboBox<Institucion> comboInstituciones;
    @FXML private ComboBox<String> comboTipoCargo;

    @FXML private TextField txtBuscar;
    @FXML private Button btnAgregar, btnEditar, btnEliminar;
    @FXML private Label lblIdUsuario;

    private ObservableList<Empleado> empleados = FXCollections.observableArrayList();
    private ObservableList<Institucion> instituciones = FXCollections.observableArrayList();
    private Connection conexion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        conectarBD();
        cargarInstituciones();
        configurarComboTipoCargo();
        cargarEmpleados();

        comboInstituciones.setOnAction(e -> filtrarEmpleados());
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarEmpleados());
        btnEditar.setDisable(true);

        tableViewEmpleados.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    btnEditar.setDisable(newSelection == null);
                    if (newSelection != null) {
                        lblIdUsuario.setText(String.valueOf(newSelection.getId()));
                    }
                }
        );

        tableViewEmpleados.getSortOrder().add(colId);
    }

    private void configurarComboTipoCargo() {
        comboTipoCargo.getItems().addAll(
                "Todos",
                "Docente",
                "Trabajador Social",
                "Administrador",
                "Director"
        );
        comboTipoCargo.setValue("Todos");
        comboTipoCargo.setOnAction(e -> filtrarEmpleados());
    }


    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colTipoCargo.setCellValueFactory(new PropertyValueFactory<>("tipoCargo"));
        colSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));

        colFechaNacimiento.setCellFactory(column -> new TableCell<>() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(sdf.format(item));
                }
            }
        });

        String centeredStyle = "-fx-alignment: CENTER;";
        colId.setStyle(centeredStyle);
        colSexo.setStyle(centeredStyle);
        colFechaNacimiento.setStyle(centeredStyle);
        colCedula.setStyle(centeredStyle);
        colSalario.setStyle(centeredStyle);

        colDireccion.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    setTooltip(new Tooltip(item));
                }
            }
        });
    }

    private void conectarBD() {
        try {
            String url = "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar";
            String usuario = "root";
            String clave = "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw";
            conexion = DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de conexión", "No se pudo conectar a la base de datos", Alert.AlertType.ERROR);
        }
    }

    private void cargarInstituciones() {
        instituciones.clear();
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_institucion, nombre FROM institucion")) {

            while (rs.next()) {
                instituciones.add(new Institucion(
                        rs.getInt("id_institucion"),
                        rs.getString("nombre")
                ));
            }
            comboInstituciones.setItems(instituciones);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las instituciones", Alert.AlertType.ERROR);
        }
    }

    private void cargarEmpleados() {
        empleados.clear();
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM empleado ORDER BY id_empleado")) {

            while (rs.next()) {
                empleados.add(new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getString("tipo_cargo"),
                        rs.getDouble("salario"),
                        rs.getString("cedula"),
                        rs.getString("direccion"),
                        rs.getString("sexo"),
                        rs.getInt("id_institucion")
                ));
            }
            tableViewEmpleados.setItems(empleados);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los empleados", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void actualizarDatos() {
        cargarInstituciones();
        comboTipoCargo.setValue("Todos");
        cargarEmpleados();

    }

    private void filtrarEmpleados() {
        Institucion institucionSeleccionada = comboInstituciones.getValue();
        String textoBusqueda = txtBuscar.getText().toLowerCase();
        String cargoSeleccionado = comboTipoCargo.getValue();

        ObservableList<Empleado> filtrados = empleados.filtered(emp -> {
            boolean coincideTexto = emp.getNombre().toLowerCase().contains(textoBusqueda)
                    || emp.getApellido().toLowerCase().contains(textoBusqueda)
                    || emp.getCedula().toLowerCase().contains(textoBusqueda)
                    || emp.getTipoCargo().toLowerCase().contains(textoBusqueda);

            boolean coincideInstitucion = institucionSeleccionada == null
                    || emp.getIdInstitucion() == institucionSeleccionada.getId();

            boolean coincideCargo = cargoSeleccionado.equals("Todos")
                    || emp.getTipoCargo().equals(cargoSeleccionado);

            return coincideTexto && coincideInstitucion && coincideCargo;
        });

        tableViewEmpleados.setItems(filtrados);
    }


    @FXML
    private void agregarEmpleado() {
        SesionUsuario.usuarioActivo=true;
        SesionUsuario.setVentanaActual("Empleado");

        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "/base_de_datos_escolar/dashboard/archivos_fxml/signup.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Registro de Acudiente");
            stage.setScene(new Scene(root));
            stage.show();

            cargarEmpleados();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de registro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarEmpleado() {
        Empleado seleccionado = tableViewEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Por favor, seleccione un empleado para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/base_de_datos_escolar/controldeusuarios/archivos_fxml/ModificacionEmpleados.fxml"));
            Parent root = loader.load();

            ModificadorEmpleadoController controlador = loader.getController();
            controlador.setEmpleadoId(String.valueOf(seleccionado.getId()));

            Stage stage = new Stage();
            stage.setTitle("Modificación de Empleado");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tableViewEmpleados.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarEmpleados();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de edición.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarEmpleado() {
        Empleado empleadoSeleccionado = tableViewEmpleados.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar un empleado para eliminar.",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Seguro que desea eliminar al empleado \""
                + empleadoSeleccionado.getNombre() + " " + empleadoSeleccionado.getApellido() + "\"?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {


                    String sqlUsuario = "DELETE FROM usuario WHERE cedula = ?";
                    try (PreparedStatement stmt = conexion.prepareStatement(sqlUsuario)) {
                        stmt.setString(1, empleadoSeleccionado.getCedula());
                        stmt.executeUpdate();
                    }


                    try (PreparedStatement stmt = conexion.prepareStatement(
                            "DELETE FROM empleado WHERE id_empleado = ?")) {
                        stmt.setInt(1, empleadoSeleccionado.getId());
                        stmt.executeUpdate();
                        cargarEmpleados();
                        mostrarAlerta("Éxito", "Empleado eliminado correctamente.",
                                Alert.AlertType.INFORMATION);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error", "No se pudo eliminar el empleado: " + e.getMessage(),
                            Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.initOwner(tableViewEmpleados.getScene().getWindow());
        alert.showAndWait();
    }
}