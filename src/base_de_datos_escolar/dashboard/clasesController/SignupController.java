package base_de_datos_escolar.dashboard.clasesController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.io.IOException;

public class SignupController implements Initializable {

    @FXML private TextField usuarioField;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private PasswordField contraseniaField;
    @FXML private ComboBox<String> tipoUsuarioBox;
    @FXML private TextField cedulaEmpleadoField;
    @FXML private DatePicker fechaNacimientoEmpleado;
    @FXML private TextField salarioEmpleadoField;
    @FXML private TextField direccionEmpleadoField;
    @FXML private ComboBox<String> sexoEmpleadoBox;
    @FXML private ComboBox<String> comboInstitucion;
    @FXML private Button btnSubirImagen;
    @FXML private ImageView imagenUsuario;

    private File archivoImagenSeleccionada;

    // Estudiante
    @FXML private DatePicker campoFechaNacimiento;
    @FXML private TextField campoDireccion;
    @FXML private ComboBox<String> campoSexo;

    // Acudiente
    @FXML private TextField telefonoAcudiente;
    @FXML private TextField correoAcudiente;
    @FXML private ComboBox<String> parentescoAcudiente;
    @FXML private ComboBox<String> estadoCivilAcudiente;
    @FXML private TextField salarioAcudiente;
    @FXML private TextField direccionAcudiente;
    @FXML private ComboBox<String> sexoAcudienteBox;
    @FXML private TextField cedulaEstudianteAcudidoField;
    @FXML private Label nombreEstudianteLabel;

    private final Map<String, Integer> institucionesMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tipoUsuarioBox.getItems().addAll("Administrador", "Docente", "Director", "Trabajador social", "Estudiante", "Acudiente");
        campoSexo.getItems().addAll("M", "F");
        sexoEmpleadoBox.getItems().addAll("M", "F");
        sexoAcudienteBox.getItems().addAll("M", "F");
        parentescoAcudiente.getItems().addAll("Padre", "Madre", "Tutor", "Acudiente");
        estadoCivilAcudiente.getItems().addAll("Soltero", "Casado", "Viudo", "Unido");

        tipoUsuarioBox.setValue("Estudiante");
        cargarInstituciones();
        actualizarCamposSegunTipo();
        tipoUsuarioBox.setOnAction(e -> actualizarCamposSegunTipo());

        cedulaEstudianteAcudidoField.textProperty().addListener((obs, oldVal, newVal) -> buscarNombreEstudiante(newVal));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        campoFechaNacimiento.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }
            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });

        fechaNacimientoEmpleado.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }
            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });

    }

    @FXML
    private void subirImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona una imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            archivoImagenSeleccionada = archivo;
            Image imagen = new Image(archivo.toURI().toString());
            imagenUsuario.setImage(imagen);
        }
    }
    private void buscarNombreEstudiante(String cedula) {
        if (cedula == null || cedula.isEmpty()) {
            nombreEstudianteLabel.setText("");
            return;
        }
        String consultaEstudiante = "SELECT nombre, apellido FROM estudiantes WHERE cedula_estudiante = ?";
        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(consultaEstudiante)) {
            stmt.setString(1, cedula);
            ResultSet busquedaEstudiante = stmt.executeQuery();
            if (busquedaEstudiante.next()) {
                nombreEstudianteLabel.setText(busquedaEstudiante.getString("nombre") + " " + busquedaEstudiante.getString("apellido"));
            } else {
                nombreEstudianteLabel.setText("Estudiante no encontrado");
            }
        } catch (SQLException e) {
            nombreEstudianteLabel.setText("Error de conexión");
        }
    }

    private void actualizarCamposSegunTipo() {
        String tipo = tipoUsuarioBox.getValue();
        boolean esEstudiante = "Estudiante".equals(tipo);
        boolean esAcudiente = "Acudiente".equals(tipo);

        fechaNacimientoEmpleado.setVisible(!esEstudiante && !esAcudiente);
        salarioEmpleadoField.setVisible(!esEstudiante && !esAcudiente);
        direccionEmpleadoField.setVisible(!esEstudiante && !esAcudiente);
        sexoEmpleadoBox.setVisible(!esEstudiante && !esAcudiente);

        campoFechaNacimiento.setVisible(esEstudiante);
        campoDireccion.setVisible(esEstudiante);
        campoSexo.setVisible(esEstudiante);

        telefonoAcudiente.setVisible(esAcudiente);
        correoAcudiente.setVisible(esAcudiente);
        parentescoAcudiente.setVisible(esAcudiente);
        estadoCivilAcudiente.setVisible(esAcudiente);
        salarioAcudiente.setVisible(esAcudiente);
        direccionAcudiente.setVisible(esAcudiente);
        sexoAcudienteBox.setVisible(esAcudiente);
        cedulaEstudianteAcudidoField.setVisible(esAcudiente);
        nombreEstudianteLabel.setVisible(esAcudiente);
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
        return DriverManager.getConnection(
                "jdbc:mysql://maglev.proxy.rlwy.net:24319/railway",
                "root",
                "mfMmjJemvZXmztSmXQiraWQjUBDLmhPE"
        );
    }

    @FXML
    private void hacerRegistro(ActionEvent event) {
        String usuario = usuarioField.getText();
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String contrasenia = contraseniaField.getText();
        String tipoUsuario = tipoUsuarioBox.getValue();
        String cedula = cedulaEmpleadoField.getText();
        String institucionSeleccionada = comboInstitucion.getValue();
        Integer idInstitucion = institucionesMap.get(institucionSeleccionada);

        if (nombre.isEmpty() || apellido.isEmpty() || contrasenia.isEmpty() || tipoUsuario == null || cedula.isEmpty() || institucionSeleccionada == null) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        switch (tipoUsuario) {
            case "Estudiante" -> registrarEstudiante(cedula, nombre, apellido, contrasenia, idInstitucion);
            case "Acudiente" -> registrarAcudiente(cedula, nombre, apellido, contrasenia, idInstitucion);
            default -> registrarEmpleado(usuario, nombre, apellido, contrasenia, tipoUsuario, cedula, idInstitucion);
        }
    }

    private void registrarEstudiante(String cedula, String nombre, String apellido, String contrasenia, Integer idInstitucion) {
        LocalDate fechaNacimiento = campoFechaNacimiento.getValue();
        String direccion = campoDireccion.getText();
        String sexo = campoSexo.getValue();

        if (fechaNacimiento == null || direccion.isEmpty() || sexo == null) {
            mostrarAlerta("Campos incompletos", "Completa todos los campos del estudiante.", Alert.AlertType.WARNING);
            return;
        }

        FileInputStream fis = null;

        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);

            String sqlUsuario = "INSERT INTO usuarios (user_nomuser, nombre, apellido, contrasenia, tipo_usuario, cedula, id_institucion, foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario)) {
                stmt.setString(1, usuarioField.getText());
                stmt.setString(2, nombre);
                stmt.setString(3, apellido);
                stmt.setString(4, contrasenia);
                stmt.setString(5, "Estudiante");
                stmt.setString(6, cedula);
                stmt.setInt(7, idInstitucion);

                if (archivoImagenSeleccionada != null) {
                    try {
                        fis = new FileInputStream(archivoImagenSeleccionada);
                        stmt.setBinaryStream(8, fis, (int) archivoImagenSeleccionada.length());
                    } catch (IOException e) {
                        stmt.setNull(8, Types.BLOB);
                        System.err.println("Error leyendo la imagen: " + e.getMessage());
                    }
                } else {
                    stmt.setNull(8, Types.BLOB);
                }

                stmt.executeUpdate();
            }


            String insertEstudiante = "INSERT INTO estudiantes (cedula_estudiante, nombre, apellido, fecha_nacimiento, direccion, sexo, id_sede) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtEst = conn.prepareStatement(insertEstudiante)) {
                stmtEst.setString(1, cedula);
                stmtEst.setString(2, nombre);
                stmtEst.setString(3, apellido);
                stmtEst.setDate(4, Date.valueOf(fechaNacimiento));
                stmtEst.setString(5, direccion);
                stmtEst.setString(6, sexo);
                stmtEst.setInt(7, idInstitucion);
                stmtEst.executeUpdate();
            }

            conn.commit();
            mostrarAlerta("Éxito", "Estudiante registrado correctamente", Alert.AlertType.INFORMATION);
            irAlLogin(null);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo registrar el estudiante: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void registrarEmpleado(String usuario, String nombre, String apellido, String contrasenia, String tipoUsuario, String cedula, Integer idInstitucion) {
        LocalDate fechaNacimiento = fechaNacimientoEmpleado.getValue();
        String salarioText = salarioEmpleadoField.getText();
        String direccion = direccionEmpleadoField.getText();
        String sexo = sexoEmpleadoBox.getValue();

        if (fechaNacimiento == null || salarioText.isEmpty() || direccion.isEmpty() || sexo == null) {
            mostrarAlerta("Campos incompletos", "Completa todos los campos del empleado.", Alert.AlertType.WARNING);
            return;
        }

        FileInputStream fis = null;
        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);

            String insertEmpleado = "INSERT INTO empleados (cedula_empleado, nombre, apellido, fecha_nacimiento, tipo_cargo, salario, direccion, sexo, id_institucion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtEmp = conn.prepareStatement(insertEmpleado)) {
                stmtEmp.setString(1, cedula);
                stmtEmp.setString(2, nombre);
                stmtEmp.setString(3, apellido);
                stmtEmp.setDate(4, Date.valueOf(fechaNacimiento));
                stmtEmp.setString(5, tipoUsuario);
                stmtEmp.setDouble(6, Double.parseDouble(salarioText));
                stmtEmp.setString(7, direccion);
                stmtEmp.setString(8, sexo);
                stmtEmp.setInt(9, idInstitucion);
                stmtEmp.executeUpdate();
            }

            String insertUsuario = "INSERT INTO usuarios (user_nomuser, nombre, apellido, contrasenia, tipo_usuario, cedula, id_institucion,foto) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
            try (PreparedStatement stmtUsuario = conn.prepareStatement(insertUsuario)) {
                stmtUsuario.setString(1, usuario);
                stmtUsuario.setString(2, nombre);
                stmtUsuario.setString(3, apellido);
                stmtUsuario.setString(4, contrasenia);
                stmtUsuario.setString(5, tipoUsuario);
                stmtUsuario.setString(6, cedula);
                stmtUsuario.setInt(7, idInstitucion);

                if (archivoImagenSeleccionada != null) {
                    try {
                        fis = new FileInputStream(archivoImagenSeleccionada);
                        stmtUsuario.setBinaryStream(8, fis, (int) archivoImagenSeleccionada.length());
                    } catch (IOException e) {
                        stmtUsuario.setNull(8, Types.BLOB);
                        System.err.println("Error leyendo la imagen: " + e.getMessage());
                    }
                } else {
                    stmtUsuario.setNull(8, Types.BLOB);
                }

                stmtUsuario.executeUpdate();
            }

            conn.commit();
            mostrarAlerta("Éxito", "Empleado registrado correctamente", Alert.AlertType.INFORMATION);
            irAlLogin(null);
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo registrar el empleado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void registrarAcudiente(String cedula, String nombre, String apellido, String contrasenia, Integer idInstitucion) {
        String telefono = telefonoAcudiente.getText();
        String correo = correoAcudiente.getText();
        String parentesco = parentescoAcudiente.getValue();
        String estadoCivil = estadoCivilAcudiente.getValue();
        String salarioText = salarioAcudiente.getText();
        String direccion = direccionAcudiente.getText();
        String sexo = sexoAcudienteBox.getValue();
        String cedulaEstudiante = cedulaEstudianteAcudidoField.getText();

        if (telefono.isEmpty() || correo.isEmpty() || parentesco == null || estadoCivil == null ||
                salarioText.isEmpty() || direccion.isEmpty() || sexo == null || cedulaEstudiante.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Completa todos los campos del acudiente.", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);

            // Insertar en tabla usuarios
            String insertUsuario = "INSERT INTO usuarios (user_nomuser, nombre, apellido, contrasenia, tipo_usuario, cedula, id_institucion) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtUsuario = conn.prepareStatement(insertUsuario)) {
                stmtUsuario.setString(1, usuarioField.getText());
                stmtUsuario.setString(2, nombre);
                stmtUsuario.setString(3, apellido);
                stmtUsuario.setString(4, contrasenia);
                stmtUsuario.setString(5, "Acudiente");
                stmtUsuario.setString(6, cedula);
                stmtUsuario.setInt(7, idInstitucion);
                stmtUsuario.executeUpdate();
            }

            // Insertar en tabla acudientes
            String insertAcudiente = "INSERT INTO acudientes (cedula_acudiente, nombre, apellido, telefono, correo, parentesco, estado_civil, salario, direccion, sexo, cedula_estudiante) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtAcu = conn.prepareStatement(insertAcudiente)) {
                stmtAcu.setString(1, cedula);
                stmtAcu.setString(2, nombre);
                stmtAcu.setString(3, apellido);
                stmtAcu.setString(4, telefono);
                stmtAcu.setString(5, correo);
                stmtAcu.setString(6, parentesco);
                stmtAcu.setString(7, estadoCivil);
                stmtAcu.setDouble(8, Double.parseDouble(salarioText));
                stmtAcu.setString(9, direccion);
                stmtAcu.setString(10, sexo);
                stmtAcu.setString(11, cedulaEstudiante);
                stmtAcu.executeUpdate();
            }

            conn.commit();
            mostrarAlerta("Éxito", "Acudiente registrado correctamente", Alert.AlertType.INFORMATION);
            irAlLogin(null);
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo registrar el acudiente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    @FXML
    private void irAlLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/base_de_datos_escolar/dashboard/archivos_fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirRegistroInstitucion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/base_de_datos_escolar/dashboard/archivos_fxml/registro_institucion.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Registro de Institución Educativa");
            stage.setScene(new Scene(root));
            stage.show();
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