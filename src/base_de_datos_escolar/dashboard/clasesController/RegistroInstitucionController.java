package base_de_datos_escolar.dashboard.clasesController;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class RegistroInstitucionController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtAnioFundacion;
    @FXML private TextField txtCapacidadEstudiantes;
    @FXML private TextField txtDireccion;

    @FXML private ComboBox<String> comboNivelEducativo;
    @FXML private ComboBox<String> comboProvincia;
    @FXML private ComboBox<String> comboDistrito;
    @FXML private ComboBox<String> comboCorregimiento;

    private Map<String, Integer> provinciasMap = new HashMap<>();
    private Map<String, Integer> distritosMap = new HashMap<>();
    private Map<String, Integer> corregimientosMap = new HashMap<>();

    @FXML
    public void initialize() {
        comboNivelEducativo.setItems(FXCollections.observableArrayList("Primaria", "Secundaria", "Ambos"));

        cargarProvincias();

        comboProvincia.setOnAction(e -> {
            String provincia = comboProvincia.getValue();
            if (provincia != null) {
                cargarDistritos(provinciasMap.get(provincia));
                comboDistrito.setDisable(false);
                comboCorregimiento.getItems().clear();
                comboCorregimiento.setDisable(true);
            }
        });

        comboDistrito.setOnAction(e -> {
            String distrito = comboDistrito.getValue();
            if (distrito != null) {
                cargarCorregimientos(distritosMap.get(distrito));
                comboCorregimiento.setDisable(false);
            }
        });
    }


    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://maglev.proxy.rlwy.net:24319/railway",
                "root",
                "mfMmjJemvZXmztSmXQiraWQjUBDLmhPE"
        );
    }

    private void cargarProvincias() {
        provinciasMap.clear();
        comboProvincia.getItems().clear();

        String consultaProvincia = "SELECT id_provincia, nombre FROM provincia ORDER BY nombre";

        try (Connection conn = conectar();
             Statement crearConsutla = conn.createStatement();
             ResultSet provincia = crearConsutla.executeQuery(consultaProvincia)) {

            while (provincia.next()) {
                int id = provincia.getInt("id_provincia");
                String nombre = provincia.getString("nombre");
                provinciasMap.put(nombre, id);
            }
            comboProvincia.getItems().addAll(provinciasMap.keySet());

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las provincias: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void cargarDistritos(int idProvincia) {
        distritosMap.clear();
        comboDistrito.getItems().clear();

        String consultaDistrito = "SELECT id_distrito, nombre FROM distrito WHERE id_provincia = ? ORDER BY nombre";

        try (Connection conn = conectar();
             PreparedStatement busquedaDistrito = conn.prepareStatement(consultaDistrito)) {

            busquedaDistrito.setInt(1, idProvincia);
            ResultSet distrito= busquedaDistrito.executeQuery();

            while (distrito.next()) {
                int id = distrito.getInt("id_distrito");
                String nombre = distrito.getString("nombre");
                distritosMap.put(nombre, id);
            }
            comboDistrito.getItems().addAll(distritosMap.keySet());

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los distritos: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void cargarCorregimientos(int idDistrito) {
        corregimientosMap.clear();
        comboCorregimiento.getItems().clear();

        String consultaCorregimientoSQL = "SELECT id_corregimiento, nombre FROM corregimiento WHERE id_distrito = ? ORDER BY nombre";

        try (Connection conn = conectar();
             PreparedStatement busquedaCorregimiento = conn.prepareStatement(consultaCorregimientoSQL)) {

            busquedaCorregimiento.setInt(1, idDistrito);
            ResultSet corregimiento = busquedaCorregimiento.executeQuery();

            while (corregimiento.next()) {
                int id = corregimiento.getInt("id_corregimiento");
                String nombre = corregimiento.getString("nombre");
                corregimientosMap.put(nombre, id);
            }
            comboCorregimiento.getItems().addAll(corregimientosMap.keySet());

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los corregimientos: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    private void guardarInstitucion() {
        String nombre = txtNombre.getText();
        String anioFundacionStr = txtAnioFundacion.getText();
        String capacidadStr = txtCapacidadEstudiantes.getText();
        String direccion = txtDireccion.getText();
        String nivelEducativo = comboNivelEducativo.getValue();
        String provincia = comboProvincia.getValue();
        String distrito = comboDistrito.getValue();
        String corregimiento = comboCorregimiento.getValue();

        if (nombre.isEmpty() || anioFundacionStr.isEmpty() || capacidadStr.isEmpty() || direccion.isEmpty() || nivelEducativo == null ||
                provincia == null || distrito == null || corregimiento == null) {

            mostrarAlerta("Campos incompletos", "Por favor complete todos los campos.", AlertType.WARNING);
            return;
        }

        int anioFundacion;
        int capacidadEstudiantes;

        try {
            anioFundacion = Integer.parseInt(anioFundacionStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Año de fundación inválido.", AlertType.ERROR);
            return;
        }

        try {
            capacidadEstudiantes = Integer.parseInt(capacidadStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Capacidad de estudiantes inválida.", AlertType.ERROR);
            return;
        }

        int idProvincia = provinciasMap.get(provincia);
        int idDistrito = distritosMap.get(distrito);
        int idCorregimiento = corregimientosMap.get(corregimiento);

        String insertarInstitucionSQL = "INSERT INTO institucion_educativa (nombre_institucion, anio_fundacion, capacidad_estudiantes, direccion, nivel_educativo, id_provincia, id_distrito, id_corregimiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conectar();
             PreparedStatement datosInstitucion = conn.prepareStatement(insertarInstitucionSQL)) {

            datosInstitucion.setString(1, nombre);
            datosInstitucion.setInt(2, anioFundacion);
            datosInstitucion.setInt(3, capacidadEstudiantes);
            datosInstitucion.setString(4, direccion);
            datosInstitucion.setString(5, nivelEducativo);
            datosInstitucion.setInt(6, idProvincia);
            datosInstitucion.setInt(7, idDistrito);
            datosInstitucion.setInt(8, idCorregimiento);

            int filas = datosInstitucion.executeUpdate();

            if (filas > 0) {
                mostrarAlerta("Registro exitoso", "Institución registrada correctamente.", AlertType.INFORMATION);
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo registrar la institución.", AlertType.ERROR);
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error en la base de datos: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtAnioFundacion.clear();
        txtCapacidadEstudiantes.clear();
        txtDireccion.clear();
        comboNivelEducativo.getSelectionModel().clearSelection();
        comboProvincia.getSelectionModel().clearSelection();
        comboDistrito.getItems().clear();
        comboDistrito.setDisable(true);
        comboCorregimiento.getItems().clear();
        comboCorregimiento.setDisable(true);
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
