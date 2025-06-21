package base_de_datos_escolar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    @FXML private TextField usuarioField;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private PasswordField contraseniaField;
    @FXML private ComboBox<String> tipoUsuarioBox;
    @FXML private TextField cedulaEmpleadoField;
    @FXML private ComboBox<String> comboInstitucion;

    private final Map<String, Integer> institucionesMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tipoUsuarioBox.getItems().addAll("administrador", "docente", "director", "Trabajador social");
        cargarInstituciones();
    }

    private void cargarInstituciones() {
        String sql = "SELECT id_sede, nombre_institucion FROM institucion_educativa";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_sede");
                String nombre = rs.getString("nombre_institucion");
                comboInstitucion.getItems().add(nombre);
                institucionesMap.put(nombre, id);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las instituciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/base_de_datos_escolar", "root", "root");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String usuario = usuarioField.getText();
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String contrasenia = contraseniaField.getText();
        String tipoUsuario = tipoUsuarioBox.getValue();
        String cedulaEmpleado = cedulaEmpleadoField.getText();
        String institucionSeleccionada = comboInstitucion.getValue();

        Integer idInstitucion = institucionesMap.get(institucionSeleccionada);

        if (usuario.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || contrasenia.isEmpty() || tipoUsuario == null) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        String sql = "INSERT INTO usuarios (user_nomuser, nombre, apellido, contrasenia, tipo_usuario, cedula_empleado, id_institucion) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, nombre);
            stmt.setString(3, apellido);
            stmt.setString(4, contrasenia); // Recomendado: aplicar hash
            stmt.setString(5, tipoUsuario);
            stmt.setString(6, cedulaEmpleado.isEmpty() ? null : cedulaEmpleado);

            if (idInstitucion != null) {
                stmt.setInt(7, idInstitucion);
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();
            mostrarAlerta("Éxito", "Usuario registrado correctamente", Alert.AlertType.INFORMATION);
            goToLogin(event);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo registrar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
