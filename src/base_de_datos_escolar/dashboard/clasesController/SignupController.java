package base_de_datos_escolar.dashboard.clasesController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @FXML private DatePicker fechaNacimientoEmpleado;
    @FXML private TextField salarioEmpleadoField;
    @FXML private TextField direccionEmpleadoField;
    @FXML private ComboBox<String> sexoEmpleadoBox;
    @FXML private ComboBox<String> comboInstitucion;
    @FXML private Button btnSubirImagen;
    @FXML private ImageView imagenUsuario;
    @FXML private Label lblid_institucion;

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

    private int idEstudianteAcudiente;
    private int idInstitucionB;

    int idInstitucionEducativa;
    String nombreInstitucionEducativa;

    private final Map<String, Integer> institucionesMap = new HashMap<>();


    public void initialize(URL location, ResourceBundle resources) {
        tipoUsuarioBox.getItems().addAll("Administrador", "Docente", "Director", "Trabajador social", "Estudiante", "Acudiente");

        if(SesionUsuario.getVentanaActual().equals("Acudiente")){
            tipoUsuarioBox.setValue("Acudiente");
            tipoUsuarioBox.setDisable(true);
        }

        if(SesionUsuario.getVentanaActual().equals("Estudiante")){
            tipoUsuarioBox.setValue("Estudiante");
            tipoUsuarioBox.setDisable(true);
        }
        if(SesionUsuario.getVentanaActual().equals("Empleado")){
            tipoUsuarioBox.getItems().clear();
            tipoUsuarioBox.getItems().addAll("Administrador", "Docente", "Director", "Trabajador social");
            tipoUsuarioBox.setDisable(false);
        }

        campoSexo.getItems().addAll("M", "F");
        sexoEmpleadoBox.getItems().addAll("M", "F");
        sexoAcudienteBox.getItems().addAll("M", "F");
        parentescoAcudiente.getItems().addAll("Padre", "Madre", "Tutor", "Acudiente");
        estadoCivilAcudiente.getItems().addAll("Soltero", "Casado", "Viudo", "Unido");

        cargarInstituciones();
        actualizarCamposSegunTipo();
        tipoUsuarioBox.setOnAction(e -> actualizarCamposSegunTipo());

        cedulaEstudianteAcudidoField.textProperty().addListener((obs, oldVal, newVal) -> buscarNombreEstudiante(newVal));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        campoFechaNacimiento.setConverter(new StringConverter<LocalDate>() {

            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });

        fechaNacimientoEmpleado.setConverter(new StringConverter<LocalDate>() {

            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

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
        String consultaEstudiante = "SELECT nombre, apellido, id_estudiante, id_institucion FROM estudiante WHERE cedula = ?";
        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(consultaEstudiante)) {
            stmt.setString(1, cedula);
            ResultSet busquedaEstudiante = stmt.executeQuery();
            if (busquedaEstudiante.next()) {
                nombreEstudianteLabel.setText(busquedaEstudiante.getString("nombre") + " " + busquedaEstudiante.getString("apellido"));
                idEstudianteAcudiente = busquedaEstudiante.getInt("id_estudiante");
                idInstitucionB = busquedaEstudiante.getInt("id_institucion");
                lblid_institucion.setText(String.valueOf(idInstitucionB));
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
        String sql = "SELECT id_institucion, nombre FROM institucion";
        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                idInstitucionEducativa = rs.getInt("id_institucion");
                nombreInstitucionEducativa = rs.getString("nombre");
                comboInstitucion.getItems().add(nombreInstitucionEducativa);
                institucionesMap.put(nombreInstitucionEducativa, idInstitucionEducativa);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las instituciones: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar",
                "root",
                "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw"
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
        String cedulaEstudiante = cedulaEstudianteAcudidoField.getText();
        String institucionSeleccionada = comboInstitucion.getValue();
        Integer idInstitucion = institucionesMap.get(institucionSeleccionada);


        if (nombre.isEmpty() || apellido.isEmpty() || contrasenia.isEmpty() || tipoUsuario == null || (cedula.isEmpty() && cedulaEstudiante.isEmpty()) || (institucionSeleccionada == null && lblid_institucion.getText().isEmpty())) {
            mostrarAlerta("Campos incompletos", "Por favor, completa todos los campos obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        //Validaciones
        if (!nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$") || !apellido.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {
            mostrarAlerta("Error", "El nombre y apellido solo pueden contener letras.", Alert.AlertType.WARNING);
            return;
        }

        // Cedula
        if (!cedula.matches("^[A-Z0-9-]{1,12}$")) {
            mostrarAlerta("Error", "La cédula solo puede contener letras mayúsculas, números y guiones (máximo 12 caracteres).", Alert.AlertType.WARNING);
            return;
        }

        // Contraseña
        if (!contrasenia.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            mostrarAlerta("Error", "La contraseña debe tener al menos 8 caracteres, 1 letra, 1 número y 1 carácter especial.", Alert.AlertType.WARNING);
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


        if (fechaNacimiento.isAfter(LocalDate.of(2020, 12, 31))) {
            mostrarAlerta("Error", "La fecha de nacimiento del estudiante no puede ser posterior al 2020.", Alert.AlertType.WARNING);
            return;
        }

        FileInputStream fotodeusuario = null;

        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);

            String sqlUsuario = "INSERT INTO usuario (nombre_usuario, nombre, apellido, contrasenia, tipo_usuario, cedula, id_institucion, foto_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
                        fotodeusuario = new FileInputStream(archivoImagenSeleccionada);
                        stmt.setBinaryStream(8, fotodeusuario, (int) archivoImagenSeleccionada.length());
                    } catch (IOException e) {
                        stmt.setNull(8, Types.BLOB);
                    }
                } else {
                    stmt.setNull(8, Types.BLOB);
                }
                stmt.executeUpdate();
            }

            String insertEstudiante = "INSERT INTO estudiante (cedula, nombre, apellido, fecha_nacimiento, direccion, sexo, id_institucion) VALUES (?, ?, ?, ?, ?, ?, ?)";
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


        LocalDate fechaLimite = LocalDate.of(2007, 7, 31);
        if (fechaNacimiento.isAfter(fechaLimite)) {
            mostrarAlerta("Error", "El empleado debe ser mayor de edad (nacido antes de agosto 2007).", Alert.AlertType.WARNING);
            return;
        }

        FileInputStream fis = null;
        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);

            String insertEmpleado = "INSERT INTO empleado (cedula, nombre, apellido, fecha_nacimiento, tipo_cargo, salario, direccion, sexo, id_institucion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

            String insertUsuario = "INSERT INTO usuario (nombre_usuario, nombre, apellido, contrasenia, tipo_usuario, cedula, id_institucion, foto_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
        LocalDate fechaNacimiento = fechaNacimientoEmpleado.getValue(); // Si lo usas para edad

        if (telefono.isEmpty() || correo.isEmpty() || parentesco == null || estadoCivil == null ||
                salarioText.isEmpty() || direccion.isEmpty() || sexo == null || cedulaEstudiante.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Completa todos los campos del acudiente.", Alert.AlertType.WARNING);
            return;
        }


        if (!telefono.matches("^\\d{7,8}$")) {
            mostrarAlerta("Error", "El teléfono debe contener solo números y tener entre 7 y 8 dígitos.", Alert.AlertType.WARNING);
            return;
        }


        if (!correo.contains("@")) {
            mostrarAlerta("Error", "El correo debe contener '@'.", Alert.AlertType.WARNING);
            return;
        }


        LocalDate fechaLimite = LocalDate.of(2007, 7, 31);
        if (fechaNacimiento != null && fechaNacimiento.isAfter(fechaLimite)) {
            mostrarAlerta("Error", "El acudiente debe ser mayor de edad.", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);

            String sqlUsuario = "INSERT INTO usuario (nombre_usuario, nombre, apellido, contrasenia, tipo_usuario, cedula, id_institucion, foto_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario)) {
                stmtUsuario.setString(1, usuarioField.getText());
                stmtUsuario.setString(2, nombre);
                stmtUsuario.setString(3, apellido);
                stmtUsuario.setString(4, contrasenia);
                stmtUsuario.setString(5, "Acudiente");
                stmtUsuario.setString(6, cedula);
                stmtUsuario.setInt(7, idInstitucionB);
                if (archivoImagenSeleccionada != null) {
                    try (FileInputStream fis = new FileInputStream(archivoImagenSeleccionada)) {
                        stmtUsuario.setBinaryStream(8, fis, (int) archivoImagenSeleccionada.length());
                    } catch (IOException e) {
                        stmtUsuario.setNull(8, Types.BLOB);
                    }
                } else {
                    stmtUsuario.setNull(8, Types.BLOB);
                }
                stmtUsuario.executeUpdate();
            }

            String sqlAcudiente = "INSERT INTO acudiente (cedula, nombre, apellido, telefono, correo, parentesco, estado_civil, salario, direccion, sexo, id_estudiante) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtAcudiente = conn.prepareStatement(sqlAcudiente)) {
                stmtAcudiente.setString(1, cedula);
                stmtAcudiente.setString(2, nombre);
                stmtAcudiente.setString(3, apellido);
                stmtAcudiente.setString(4, telefono);
                stmtAcudiente.setString(5, correo);
                stmtAcudiente.setString(6, parentesco);
                stmtAcudiente.setString(7, estadoCivil);
                stmtAcudiente.setDouble(8, Double.parseDouble(salarioText));
                stmtAcudiente.setString(9, direccion);
                stmtAcudiente.setString(10, sexo);
                stmtAcudiente.setInt(11, idEstudianteAcudiente);
                stmtAcudiente.executeUpdate();
            }

            conn.commit();
            mostrarAlerta("Éxito", "Acudiente registrado correctamente", Alert.AlertType.INFORMATION);
            irAlLogin(null);
        } catch (SQLException | NumberFormatException e) {
            mostrarAlerta("Error", "No se pudo registrar el acudiente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void irAlLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/base_de_datos_escolar/dashboard/vistas/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesión");
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la ventana de inicio de sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
