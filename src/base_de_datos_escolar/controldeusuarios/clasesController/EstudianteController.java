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

public class EstudianteController implements Initializable {

    @FXML private TableView<Estudiante> tableViewEstudiantes;
    @FXML private TableColumn<Estudiante, Integer> colId;
    @FXML private TableColumn<Estudiante, String> colNombre;
    @FXML private TableColumn<Estudiante, String> colApellido;
    @FXML private TableColumn<Estudiante, String> colCedula;
    @FXML private TableColumn<Estudiante, Date> colFechaNacimiento;
    @FXML private TableColumn<Estudiante, String> colDireccion;
    @FXML private TableColumn<Estudiante, String> colSexo;

    @FXML private ComboBox<Institucion> comboInstituciones;
    @FXML private TextField txtBuscar;
    @FXML private Button btnAgregar, btnEditar, btnEliminar;
    @FXML private Label lblIdUsuario;


    private ObservableList<Estudiante> estudiantes = FXCollections.observableArrayList();
    private ObservableList<Institucion> instituciones = FXCollections.observableArrayList();
    private Connection conexion;

    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        conectarBD();
        cargarInstituciones();
        cargarEstudiantes();

        // Configuración de listeners para filtro y tableview
        comboInstituciones.setOnAction(e -> filtrarEstudiantes());
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarEstudiantes());
        btnEditar.setDisable(true);


        tableViewEstudiantes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    btnEditar.setDisable(newSelection == null);
                    if (newSelection != null) {
                        lblIdUsuario.setText(String.valueOf(newSelection.getId()));
                    }
                }
        );

        // Ordenar
        tableViewEstudiantes.getSortOrder().add(colId);
    }

    private void configurarColumnas() {
        // Configurar encabezado para cada columna
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));

        // Formato para la fecha
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

        // Alineación y estilo de las columnas
        String centeredStyle = "-fx-alignment: CENTER;";
        colId.setStyle(centeredStyle);
        colSexo.setStyle(centeredStyle);
        colFechaNacimiento.setStyle(centeredStyle);
        colCedula.setStyle(centeredStyle);

        // Columnas con texto largo
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

        // Ancho de columnas
        colId.setPrefWidth(50);
        colNombre.setPrefWidth(120);
        colApellido.setPrefWidth(120);
        colCedula.setPrefWidth(100);
        colFechaNacimiento.setPrefWidth(120);
        colDireccion.setPrefWidth(150);
        colSexo.setPrefWidth(50);
    }

    private void conectarBD() {
        try {
            String url = "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar";
            String usuario = "root";
            String clave = "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw";
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, usuario, clave);
        } catch (Exception e) {
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

    private void cargarEstudiantes() {
        estudiantes.clear();
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM estudiante ORDER BY id_estudiante")) {

            while (rs.next()) {
                estudiantes.add(new Estudiante(
                        rs.getInt("id_estudiante"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("cedula"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getString("direccion"),
                        rs.getString("sexo"),
                        rs.getInt("id_institucion")
                ));
            }
            tableViewEstudiantes.setItems(estudiantes);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar los estudiantes", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void actualizarDatos() {
        cargarInstituciones();
        cargarEstudiantes();
    }

    private void filtrarEstudiantes() {
        Institucion institucionSeleccionada = comboInstituciones.getValue();
        String textoBusqueda = txtBuscar.getText().toLowerCase();

        ObservableList<Estudiante> filtrados = estudiantes.filtered(est -> {
            boolean coincideNombre = est.getNombre().toLowerCase().contains(textoBusqueda)
                    || est.getApellido().toLowerCase().contains(textoBusqueda)
                    || est.getCedula().toLowerCase().contains(textoBusqueda);
            boolean coincideInstitucion = institucionSeleccionada == null
                    || est.getIdInstitucion() == institucionSeleccionada.getId();
            return coincideNombre && coincideInstitucion;
        });

        tableViewEstudiantes.setItems(filtrados);
    }

    @FXML
    private void editarEstudiante() {
        Estudiante seleccionado = tableViewEstudiantes.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Error", "Por favor, seleccione un estudiante para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/base_de_datos_escolar/controldeusuarios/archivos_fxml/ModificacionEstudiantes.fxml"));
            Parent root = loader.load();

            ModificadorEstudiantesController controlador = loader.getController();
            controlador.setEstudianteId(String.valueOf(seleccionado.getId()));

            Stage stage = new Stage();
            stage.setTitle("Modificación de Estudiante");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tableViewEstudiantes.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarEstudiantes();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de edición.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarEstudiante() {
        Estudiante seleccionado = tableViewEstudiantes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar un estudiante para eliminar.",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Seguro que desea eliminar al estudiante \""
                + seleccionado.getNombre() + " " + seleccionado.getApellido() + "\"?");
        confirm.setContentText("Esta acción eliminará también el usuario asociado y no se puede deshacer.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    conexion.setAutoCommit(false); // Iniciamos una transacción

                    try {
                        // Primero eliminamos el usuario asociado
                        try (PreparedStatement stmtUsuario = conexion.prepareStatement(
                                "DELETE FROM usuario WHERE cedula = ?")) {
                            stmtUsuario.setString(1, seleccionado.getCedula());
                            stmtUsuario.executeUpdate();
                        }

                        // Luego eliminamos el estudiante
                        try (PreparedStatement stmtEstudiante = conexion.prepareStatement(
                                "DELETE FROM estudiante WHERE id_estudiante = ?")) {
                            stmtEstudiante.setInt(1, seleccionado.getId());
                            stmtEstudiante.executeUpdate();
                        }

                        conexion.commit(); // Confirmamos la transacción
                        cargarEstudiantes();
                        mostrarAlerta("Éxito", "Estudiante y usuario asociado eliminados correctamente.",
                                Alert.AlertType.INFORMATION);

                    } catch (SQLException e) {
                        conexion.rollback(); // Si hay error, revertimos la transacción
                        throw e;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error", "No se pudo eliminar el estudiante: " + e.getMessage(),
                            Alert.AlertType.ERROR);
                } finally {
                    try {
                        conexion.setAutoCommit(true); // Restauramos el auto-commit
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @FXML
    private void agregarEstudiante() {
        SesionUsuario.usuarioActivo=true;
        SesionUsuario.setVentanaActual("Estudiante");
        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "/base_de_datos_escolar/dashboard/archivos_fxml/signup.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Registro de Estudiante");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de registro.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.initOwner(tableViewEstudiantes.getScene().getWindow());
        alert.showAndWait();
    }
}