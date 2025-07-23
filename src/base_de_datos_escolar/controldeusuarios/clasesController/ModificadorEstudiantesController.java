package base_de_datos_escolar.controldeusuarios.clasesController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ModificadorEstudiantesController implements Initializable {

    @FXML private TextField usuarioField;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private PasswordField contraseniaField;
    @FXML private ComboBox<String> tipoUsuarioBox;
    @FXML private TextField cedulaEmpleadoField;
    @FXML private DatePicker campoFechaNacimiento;
    @FXML private TextField campoDireccion;
    @FXML private ComboBox<String> campoSexo;
    @FXML private ComboBox<String> comboInstitucion;
    @FXML private Button btnSubirImagen;
    @FXML private ImageView imagenUsuario;

    private String estudianteId;
    private final Map<String, Integer> institucionesMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuración inicial
        tipoUsuarioBox.setValue("Estudiante");
        tipoUsuarioBox.setDisable(true); // No permitir cambiar el tipo de usuario
        campoSexo.getItems().addAll("M", "F");


        // Deshabilitar campos que no deberían modificarse
        usuarioField.setDisable(true);
        contraseniaField.setDisable(true);
        
        cargarInstituciones();
    }

    public void setEstudianteId(String id) {
        this.estudianteId = id;
        if (id != null && !id.isEmpty()) {
            cargarDatosEstudiante(id);
        }
    }

    private void cargarDatosEstudiante(String idEstudiante) {
        String sql = "SELECT e.*, u.nombre_usuario FROM estudiante e " +
                    "LEFT JOIN usuario u ON e.cedula = u.cedula " +
                    "WHERE e.id_estudiante = ?";
                    
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, idEstudiante);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombreField.setText(rs.getString("nombre"));
                apellidoField.setText(rs.getString("apellido"));
                cedulaEmpleadoField.setText(rs.getString("cedula"));
                campoFechaNacimiento.setValue(rs.getDate("fecha_nacimiento").toLocalDate());
                campoDireccion.setText(rs.getString("direccion"));
                campoSexo.setValue(rs.getString("sexo"));
                usuarioField.setText(rs.getString("nombre_usuario"));

                // Seleccionar la institución correcta en el combo
                int idInstitucion = rs.getInt("id_institucion");
                for (Map.Entry<String, Integer> entry : institucionesMap.entrySet()) {
                    if (entry.getValue() == idInstitucion) {
                        comboInstitucion.setValue(entry.getKey());
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos del estudiante: " + e.getMessage(),
                Alert.AlertType.ERROR);
        }
    }

    private void cargarInstituciones() {
        String sql = "SELECT id_institucion, nombre FROM institucion";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombreInstitucion = rs.getString("nombre");
                comboInstitucion.getItems().add(nombreInstitucion);
                institucionesMap.put(nombreInstitucion, rs.getInt("id_institucion"));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las instituciones: " + e.getMessage(),
                Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarUsuario() {
        if (!validarCampos()) {
            return;
        }

        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);
            try {
                // Actualizar tabla estudiante
                String sqlEstudiante = """
                    UPDATE estudiante 
                    SET nombre = ?, apellido = ?, cedula = ?, 
                        fecha_nacimiento = ?, direccion = ?, sexo = ?, 
                        id_institucion = ? 
                    WHERE id_estudiante = ?
                    """;
                
                try (PreparedStatement stmt = conn.prepareStatement(sqlEstudiante)) {
                    stmt.setString(1, nombreField.getText());
                    stmt.setString(2, apellidoField.getText());
                    stmt.setString(3, cedulaEmpleadoField.getText());
                    stmt.setDate(4, Date.valueOf(campoFechaNacimiento.getValue()));
                    stmt.setString(5, campoDireccion.getText());
                    stmt.setString(6, campoSexo.getValue());
                    stmt.setInt(7, institucionesMap.get(comboInstitucion.getValue()));
                    stmt.setString(8, estudianteId);
                    stmt.executeUpdate();
                }

                // Actualizar tabla usuario
                String sqlUsuario = """
                    UPDATE usuario 
                    SET nombre = ?, apellido = ?, id_institucion = ?
                    WHERE cedula = ?
                    """;
                
                try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {
                    stmt.setString(1, nombreField.getText());
                    stmt.setString(2, apellidoField.getText());
                    stmt.setInt(3, institucionesMap.get(comboInstitucion.getValue()));
                    stmt.setString(4, cedulaEmpleadoField.getText());
                    stmt.executeUpdate();
                }

                conn.commit();
                mostrarAlerta("Éxito", "Datos actualizados correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo actualizar los datos: " + e.getMessage(),
                Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos() {
        if (nombreField.getText().isEmpty() || 
            apellidoField.getText().isEmpty() || 
            cedulaEmpleadoField.getText().isEmpty() || 
            campoFechaNacimiento.getValue() == null ||
            campoDireccion.getText().isEmpty() || 
            campoSexo.getValue() == null || 
            comboInstitucion.getValue() == null) {
            
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void cerrarVentana() {
        ((Stage) nombreField.getScene().getWindow()).close();
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