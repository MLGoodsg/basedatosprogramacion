package base_de_datos_escolar.controldeusuarios.clasesController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ModificadorAcudienteController implements Initializable {

    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private TextField cedulaField;
    @FXML private TextField telefonoField;
    @FXML private TextField correoField;
    @FXML private TextField direccionField;
    @FXML private ComboBox<String> parentescoCombo;
    @FXML private TextField salarioField;
    @FXML private ComboBox<String> estadoCivilCombo;
    @FXML private ComboBox<String> sexoCombo;
    @FXML private ComboBox<String> estudianteCombo;

    private String acudienteId;
    private Connection conexion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar ComboBoxes
        parentescoCombo.getItems().addAll("Padre", "Madre", "Tutor", "Acudiente");
        estadoCivilCombo.getItems().addAll("Soltero", "Casado", "Unido", "Viudo");
        sexoCombo.getItems().addAll("F", "M");

        conectarBD();
        cargarEstudiantes();


        salarioField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                salarioField.setText(oldValue);
            }
        });


        telefonoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                telefonoField.setText(oldValue);
            }
        });
    }

    public void setAcudienteId(String id) {
        this.acudienteId = id;
        if (id != null && !id.isEmpty()) {
            cargarDatosAcudiente(id);
        }
    }

    private void conectarBD() {
        try {
            String url = "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar";
            String usuario = "root";
            String clave = "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw";
            conexion = DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException e) {
            mostrarAlerta("Error de conexión", "No se pudo conectar a la base de datos", Alert.AlertType.ERROR);
        }
    }

    private void cargarEstudiantes() {
        String sql = "SELECT id_estudiante, CONCAT(nombre, ' ', apellido) as nombre_completo FROM estudiante";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String nombreCompleto = rs.getString("nombre_completo");
                estudianteCombo.getItems().add(nombreCompleto);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los estudiantes", Alert.AlertType.ERROR);
        }
    }

    private void cargarDatosAcudiente(String idAcudiente) {
        String sql = """
            SELECT a.*, CONCAT(e.nombre, ' ', e.apellido) as nombre_estudiante 
            FROM acudiente a 
            LEFT JOIN estudiante e ON a.id_estudiante = e.id_estudiante 
            WHERE a.id_acudiente = ?
            """;

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, idAcudiente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombreField.setText(rs.getString("nombre"));
                apellidoField.setText(rs.getString("apellido"));
                cedulaField.setText(rs.getString("cedula"));
                telefonoField.setText(rs.getString("telefono"));
                correoField.setText(rs.getString("correo"));
                direccionField.setText(rs.getString("direccion"));
                parentescoCombo.setValue(rs.getString("parentesco"));
                salarioField.setText(String.format("%.2f", rs.getDouble("salario")));
                estadoCivilCombo.setValue(rs.getString("estado_civil"));
                sexoCombo.setValue(rs.getString("sexo"));
                estudianteCombo.setValue(rs.getString("nombre_estudiante"));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos del acudiente", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void modificarAcudiente() {
        if (!validarCampos()) {
            return;
        }

        String sql = """
            UPDATE acudiente 
            SET nombre = ?, apellido = ?, cedula = ?, telefono = ?, 
                correo = ?, direccion = ?, parentesco = ?, salario = ?, 
                estado_civil = ?, sexo = ?, id_estudiante = 
                (SELECT id_estudiante FROM estudiante WHERE CONCAT(nombre, ' ', apellido) = ?)
            WHERE id_acudiente = ?
            """;

        try {
            conexion.setAutoCommit(false);
            try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
                stmt.setString(1, nombreField.getText());
                stmt.setString(2, apellidoField.getText());
                stmt.setString(3, cedulaField.getText());
                stmt.setString(4, telefonoField.getText());
                stmt.setString(5, correoField.getText());
                stmt.setString(6, direccionField.getText());
                stmt.setString(7, parentescoCombo.getValue());
                stmt.setDouble(8, Double.parseDouble(salarioField.getText()));
                stmt.setString(9, estadoCivilCombo.getValue());
                stmt.setString(10, sexoCombo.getValue());
                stmt.setString(11, estudianteCombo.getValue());
                stmt.setString(12, acudienteId);

                int filasAfectadas = stmt.executeUpdate();
                if (filasAfectadas > 0) {
                    conexion.commit();
                    mostrarAlerta("Éxito", "Datos actualizados correctamente", Alert.AlertType.INFORMATION);
                    cerrarVentana();
                } else {
                    throw new SQLException("No se pudo actualizar el acudiente");
                }
            }
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            mostrarAlerta("Error", "Error al actualizar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validarCampos() {
        if (nombreField.getText().isEmpty() ||
                apellidoField.getText().isEmpty() ||
                cedulaField.getText().isEmpty() ||
                telefonoField.getText().isEmpty() ||
                direccionField.getText().isEmpty() ||
                parentescoCombo.getValue() == null ||
                salarioField.getText().isEmpty() ||
                estadoCivilCombo.getValue() == null ||
                sexoCombo.getValue() == null ||
                estudianteCombo.getValue() == null) {

            mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return false;
        }

        try {
            Double.parseDouble(salarioField.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El salario debe ser un número válido", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void cerrarVentana() {
        ((Stage) nombreField.getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.initOwner(nombreField.getScene().getWindow());
        alert.showAndWait();
    }
}
