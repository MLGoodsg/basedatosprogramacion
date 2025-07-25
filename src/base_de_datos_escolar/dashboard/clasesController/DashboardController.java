package base_de_datos_escolar.dashboard.clasesController;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DashboardController {

    @FXML private Label cantidadEscuela;
    @FXML private Label cantidadMaestro;
    @FXML private Label cantidadEstudiantes;
    @FXML private Label cantidadPadres;

    @FXML private ComboBox<String> cmbAnioacademico;
    @FXML private ComboBox<String> cmbRangopromedio;
    @FXML private ComboBox<String> cmbSexo;
    @FXML private ComboBox<String> cmbSeccion;
    @FXML private ComboBox<String> cmbProvincia;
    @FXML private ComboBox<String> cmbDistrito;
    @FXML private ComboBox<String> cmbCorregimiento;

    private final Map<String, Integer> provinciasMap = new TreeMap<>();
    private final Map<String, Integer> distritosMap = new TreeMap<>();
    private final Map<String, Integer> corregimientosMap = new TreeMap<>();

    @FXML
    public void initialize() {
        cargarFiltrosEstaticos();
        cargarCantidadEnSegundoPlano();
        cargarProvincias();

        cmbProvincia.setOnAction(e -> {
            String provinciaSeleccionada = cmbProvincia.getValue();
            cmbDistrito.getItems().clear();
            cmbCorregimiento.getItems().clear();
            cmbDistrito.setDisable(true);
            cmbCorregimiento.setDisable(true);
            if (provinciaSeleccionada != null) {
                cargarDistritos(provinciasMap.get(provinciaSeleccionada));
            }
        });

        cmbDistrito.setOnAction(e -> {
            String distritoSeleccionado = cmbDistrito.getValue();
            cmbCorregimiento.getItems().clear();
            cmbCorregimiento.setDisable(true);
            if (distritoSeleccionado != null) {
                cargarCorregimientos(distritosMap.get(distritoSeleccionado));
            }
        });
    }

    private Connection conectar() throws SQLException {
        final String URL = "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar";
        final String USUARIO = "root";
        final String CONTRASENA = "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw";
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    private Map<String, Integer> obtenerCantidadRegistros(Connection conn) throws SQLException {
        Map<String, Integer> resultados = new HashMap<>();

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM institucion")) {
            if (rs.next()) resultados.put("escuelas", rs.getInt(1));
        }
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM estudiante")) {
            if (rs.next()) resultados.put("estudiantes", rs.getInt(1));
        }
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM empleado WHERE tipo_cargo = 'Docente'")) {
            if (rs.next()) resultados.put("maestros", rs.getInt(1));
        }
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM acudiente")) {
            if (rs.next()) resultados.put("padres", rs.getInt(1));
        }

        return resultados;
    }

    private void cargarCantidadEnSegundoPlano() {
        Task<Map<String, Integer>> task = new Task<>() {
            @Override
            protected Map<String, Integer> call() throws Exception {
                try (Connection conn = conectar()) {
                    return obtenerCantidadRegistros(conn);
                }
            }
        };

        task.setOnSucceeded(event -> {
            Map<String, Integer> resultados = task.getValue();
            cantidadEscuela.setText(String.valueOf(resultados.getOrDefault("escuelas", 0)));
            cantidadEstudiantes.setText(String.valueOf(resultados.getOrDefault("estudiantes", 0)));
            cantidadMaestro.setText(String.valueOf(resultados.getOrDefault("maestros", 0)));
            cantidadPadres.setText(String.valueOf(resultados.getOrDefault("padres", 0)));
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            mostrarAlerta("Error de Conexión", "No se pudo cargar los datos del dashboard: " + ex.getMessage(), Alert.AlertType.ERROR);
        });

        new Thread(task).start();
    }

    private void cargarProvincias() {
        provinciasMap.clear();
        String consulta = "SELECT id_provincia, nombre FROM provincia";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {

            while (rs.next()) {
                provinciasMap.put(rs.getString("nombre"), rs.getInt("id_provincia"));
            }
            cmbProvincia.getItems().setAll(provinciasMap.keySet());

        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "No se pudieron cargar las provincias: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDistritos(int idProvincia) {
        distritosMap.clear();
        String consulta = "SELECT id_distrito, nombre FROM distrito WHERE id_provincia = ?";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(consulta)) {

            pstmt.setInt(1, idProvincia);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    distritosMap.put(rs.getString("nombre"), rs.getInt("id_distrito"));
                }
                cmbDistrito.getItems().setAll(distritosMap.keySet());
                cmbDistrito.setDisable(false);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "No se pudieron cargar los distritos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarCorregimientos(int idDistrito) {
        corregimientosMap.clear();
        String consulta = "SELECT id_corregimiento, nombre FROM corregimiento WHERE id_distrito = ?";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(consulta)) {

            pstmt.setInt(1, idDistrito);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    corregimientosMap.put(rs.getString("nombre"), rs.getInt("id_corregimiento"));
                }
                cmbCorregimiento.getItems().setAll(corregimientosMap.keySet());
                cmbCorregimiento.setDisable(false);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de Base de Datos", "No se pudieron cargar los corregimientos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarFiltrosEstaticos() {
        cmbAnioacademico.setItems(FXCollections.observableArrayList("2023", "2024", "2025"));
        cmbSeccion.setItems(FXCollections.observableArrayList("Primaria", "Secundaria"));
        cmbRangopromedio.setItems(FXCollections.observableArrayList("Menor a 3.00", "3.00 - 4.00", "4.01 - 5.00"));
        cmbSexo.setItems(FXCollections.observableArrayList("F", "M"));
    }

    @FXML
    private void reiniciarFiltros() {
        cmbProvincia.setValue(null);
        cmbDistrito.setValue(null);
        cmbCorregimiento.setValue(null);
        cmbAnioacademico.setValue(null);
        cmbSeccion.setValue(null);
        cmbRangopromedio.setValue(null);
        cmbSexo.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}