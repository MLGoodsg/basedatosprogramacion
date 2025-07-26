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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class AcudienteController implements Initializable {

    @FXML private TableView<Acudiente> tableViewAcudientes;
    @FXML private TableColumn<Acudiente, Integer> colId;
    @FXML private TableColumn<Acudiente, String> colNombre;
    @FXML private TableColumn<Acudiente, String> colApellido;
    @FXML private TableColumn<Acudiente, String> colCedula;
    @FXML private TableColumn<Acudiente, String> colTelefono;
    @FXML private TableColumn<Acudiente, String> colParentesco;
    @FXML private TableColumn<Acudiente, String> colAcudido;
    @FXML private ComboBox<String> comboInstituciones;
    @FXML private TextField txtBuscar;
    @FXML private Label lblIdUsuario;

    private final Map<String, Integer> institucionesMap = new HashMap<>();
    private ObservableList<Acudiente> listaAcudientes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuracion de las columnas del tableview
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colParentesco.setCellValueFactory(new PropertyValueFactory<>("parentesco"));
        colAcudido.setCellValueFactory(new PropertyValueFactory<>("nombreEstudiante"));

        listaAcudientes = FXCollections.observableArrayList();
        tableViewAcudientes.setItems(listaAcudientes);

        //filtro de búsqueda
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            buscarAcudientes(newValue);
        });

        // listeners del combobox de instituciones
        comboInstituciones.setOnAction(event -> {
            String institucionSeleccionada = comboInstituciones.getValue();
            if (institucionSeleccionada != null) {
                filtrarPorInstitucion();
            }
        });

        cargarInstituciones();
        cargarDatos();
    }

    private void cargarInstituciones() {
        String sql = "SELECT id_institucion, nombre FROM institucion";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            comboInstituciones.getItems().add("Todas las instituciones");
            institucionesMap.put("Todas las instituciones", -1);
            
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int id = rs.getInt("id_institucion");
                comboInstituciones.getItems().add(nombre);
                institucionesMap.put(nombre, id);
            }
            
            comboInstituciones.setValue("Todas las instituciones");
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar instituciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDatos() {
        String sql = "SELECT a.*, e.nombre as nombre_estudiante, u.id_institucion " +
                    "FROM acudiente a " +
                    "LEFT JOIN estudiante e ON a.id_estudiante = e.id_estudiante " +
                    "LEFT JOIN usuario u ON a.cedula = u.cedula";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            listaAcudientes.clear();
            
            while (rs.next()) {
                Acudiente acudiente = new Acudiente(
                    rs.getInt("id_acudiente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("cedula"),
                    rs.getString("telefono"),
                    rs.getString("parentesco"),
                    rs.getString("nombre_estudiante")
                );
                listaAcudientes.add(acudiente);
            }
            
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filtrarPorInstitucion() {
        String institucionSeleccionada = comboInstituciones.getValue();
        if (institucionSeleccionada == null) return;

        String sql;
        if (institucionSeleccionada.equals("Todas las instituciones")) {
            sql = "SELECT a.*, e.nombre as nombre_estudiante, u.id_institucion " +
              "FROM acudiente a " +
              "LEFT JOIN estudiante e ON a.id_estudiante = e.id_estudiante " +
              "LEFT JOIN usuario u ON a.cedula = u.cedula";
        } else {
            sql = "SELECT a.*, e.nombre as nombre_estudiante, u.id_institucion " +
              "FROM acudiente a " +
              "LEFT JOIN estudiante e ON a.id_estudiante = e.id_estudiante " +
              "LEFT JOIN usuario u ON a.cedula = u.cedula " +
              "WHERE e.id_institucion = ?";
        }

        try (Connection conn = conectar()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
        
            if (!institucionSeleccionada.equals("Todas las instituciones")) {
                stmt.setInt(1, institucionesMap.get(institucionSeleccionada));
            }
        
            ResultSet rs = stmt.executeQuery();
            listaAcudientes.clear();
        
            while (rs.next()) {
                Acudiente acudiente = new Acudiente(
                    rs.getInt("id_acudiente"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("cedula"),
                    rs.getString("telefono"),
                    rs.getString("parentesco"),
                    rs.getString("nombre_estudiante")
                );
                listaAcudientes.add(acudiente);
            }
        
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al filtrar datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void agregarAcudiente() {
        SesionUsuario.usuarioActivo=true;
        SesionUsuario.setVentanaActual("Acudiente");

        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "/base_de_datos_escolar/dashboard/archivos_fxml/signup.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Registro de Acudiente");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHidden(e -> cargarDatos());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de registro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void editarAcudiente() {
        Acudiente acudienteSeleccionado = tableViewAcudientes.getSelectionModel().getSelectedItem();
        if (acudienteSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, seleccione un acudiente para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/base_de_datos_escolar/controldeusuarios/archivos_fxml/ModificacionAcudientes.fxml"));
            Parent root = loader.load();

            ModificadorAcudienteController controller = loader.getController();
            controller.setAcudienteId(String.valueOf(acudienteSeleccionado.getId()));

            Stage stage = new Stage();
            stage.setTitle("Modificar Acudiente");
            stage.setScene(new Scene(root));
            stage.show();

            stage.setOnHidden(e -> cargarDatos());

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario de edición.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarAcudiente() {
        Acudiente acudienteSeleccionado = tableViewAcudientes.getSelectionModel().getSelectedItem();
        if (acudienteSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, seleccione un acudiente para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea eliminar este acudiente?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try (Connection conn = conectar()) {
                conn.setAutoCommit(false);
                try {
                    // eliminar de la tabla usuario
                    String sqlUsuario = "DELETE FROM usuario WHERE cedula = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {
                        stmt.setString(1, acudienteSeleccionado.getCedula());
                        stmt.executeUpdate();
                    }

                    //   eliminar de la tabla acudiente
                    String sqlAcudiente = "DELETE FROM acudiente WHERE id_acudiente = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlAcudiente)) {
                        stmt.setInt(1, acudienteSeleccionado.getId());
                        stmt.executeUpdate();
                    }

                    conn.commit();
                    mostrarAlerta("Éxito", "Acudiente eliminado correctamente.", Alert.AlertType.INFORMATION);
                    cargarDatos();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo eliminar el acudiente: " + e.getMessage(),
                        Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void actualizarDatos() {
        String institucionSeleccionada = comboInstituciones.getValue();
        if (institucionSeleccionada != null) {
            filtrarPorInstitucion();
        } else {
            cargarDatos();
        }
    }

    private void buscarAcudientes(String criterio) {
        if (criterio == null || criterio.isEmpty()) {
            cargarDatos();
            return;
        }

        String sql = "SELECT a.*, e.nombre as nombre_estudiante " +
                "FROM acudiente a " +
                "LEFT JOIN estudiante e ON a.id_estudiante = e.id_estudiante " +
                "WHERE a.nombre LIKE ? OR a.apellido LIKE ? OR a.cedula LIKE ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String busqueda = "%" + criterio + "%";
            stmt.setString(1, busqueda);
            stmt.setString(2, busqueda);
            stmt.setString(3, busqueda);

            ResultSet rs = stmt.executeQuery();
            listaAcudientes.clear();

            while (rs.next()) {
                Acudiente acudiente = new Acudiente(
                        rs.getInt("id_acudiente"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("cedula"),
                        rs.getString("telefono"),
                        rs.getString("parentesco"),
                        rs.getString("nombre_estudiante")
                );
                listaAcudientes.add(acudiente);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al buscar acudientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar",
            "root",
            "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw"
        );
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}