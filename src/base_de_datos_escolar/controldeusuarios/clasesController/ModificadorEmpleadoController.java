package base_de_datos_escolar.controldeusuarios.clasesController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class ModificadorEmpleadoController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCedula;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private ComboBox<String> comboTipoCargo;
    @FXML private TextField txtSalario;
    @FXML private TextField txtDireccion;
    @FXML private ComboBox<String> comboSexo;
    @FXML private ComboBox<Institucion> comboInstitucion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private String empleadoId;
    private Connection conexion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conectarBD();
        configurarCombos();
        cargarInstituciones();
    }

    private void configurarCombos() {
        comboTipoCargo.getItems().addAll("Docente", "Trabajador Social", "Administrador", "Director");
        comboSexo.getItems().addAll("F", "M");
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
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_institucion, nombre FROM institucion")) {

            while (rs.next()) {
                comboInstitucion.getItems().add(new Institucion(
                        rs.getInt("id_institucion"),
                        rs.getString("nombre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las instituciones", Alert.AlertType.ERROR);
        }
    }

    public void setEmpleadoId(String id) {
        this.empleadoId = id;
        cargarDatosEmpleado();
    }

    private void cargarDatosEmpleado() {
        if (empleadoId != null) {
            try (PreparedStatement ps = conexion.prepareStatement(
                    "SELECT * FROM empleado WHERE id_empleado = ?")) {
                ps.setString(1, empleadoId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    txtNombre.setText(rs.getString("nombre"));
                    txtApellido.setText(rs.getString("apellido"));
                    txtCedula.setText(rs.getString("cedula"));
                    Date fechaNac = rs.getDate("fecha_nacimiento");
                    if (fechaNac != null) {
                        dpFechaNacimiento.setValue(fechaNac.toLocalDate());
                    }
                    comboTipoCargo.setValue(rs.getString("tipo_cargo"));
                    txtSalario.setText(String.valueOf(rs.getDouble("salario")));
                    txtDireccion.setText(rs.getString("direccion"));
                    comboSexo.setValue(rs.getString("sexo"));

                    int idInstitucion = rs.getInt("id_institucion");
                    comboInstitucion.getItems().forEach(institucion -> {
                        if (institucion.getId() == idInstitucion) {
                            comboInstitucion.setValue(institucion);
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudieron cargar los datos del empleado", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void guardar() {
        if (!validarCampos()) {
            return;
        }

        String sql;
        if (empleadoId == null) {
            sql = "INSERT INTO empleado (nombre, apellido, fecha_nacimiento, tipo_cargo, salario, " +
                    "cedula, direccion, sexo, id_institucion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE empleado SET nombre=?, apellido=?, fecha_nacimiento=?, tipo_cargo=?, " +
                    "salario=?, cedula=?, direccion=?, sexo=?, id_institucion=? WHERE id_empleado=?";
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, txtNombre.getText());
            ps.setString(2, txtApellido.getText());
            ps.setDate(3, java.sql.Date.valueOf(dpFechaNacimiento.getValue()));
            ps.setString(4, comboTipoCargo.getValue());
            ps.setDouble(5, Double.parseDouble(txtSalario.getText()));
            ps.setString(6, txtCedula.getText());
            ps.setString(7, txtDireccion.getText());
            ps.setString(8, comboSexo.getValue());
            ps.setInt(9, comboInstitucion.getValue().getId());

            if (empleadoId != null) {
                ps.setString(10, empleadoId);
            }

            ps.executeUpdate();

            mostrarAlerta("Éxito", "Datos guardados correctamente", Alert.AlertType.INFORMATION);
            cerrarVentana();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron guardar los datos: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty() ||
                txtCedula.getText().isEmpty() || dpFechaNacimiento.getValue() == null ||
                comboTipoCargo.getValue() == null || txtSalario.getText().isEmpty() ||
                txtDireccion.getText().isEmpty() || comboSexo.getValue() == null ||
                comboInstitucion.getValue() == null) {

            mostrarAlerta("Error", "Todos los campos son obligatorios", Alert.AlertType.ERROR);
            return false;
        }

        try {
            Double.parseDouble(txtSalario.getText());
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El salario debe ser un número válido", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}