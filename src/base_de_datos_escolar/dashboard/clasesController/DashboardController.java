package base_de_datos_escolar.dashboard.clasesController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;


import javafx.scene.control.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javafx.concurrent.Task;

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

    private Map<String, Integer> provinciasMap = new HashMap<>();
    private Map<String, Integer> distritosMap = new HashMap<>();
    private Map<String, Integer> corregimientosMap = new HashMap<>();

    @FXML
    public void initialize() {
        cmbAnioacademico.setItems(FXCollections.observableArrayList("2023", "2024", "2025"));
        cmbSeccion.setItems(FXCollections.observableArrayList("Primaria", "Secundaria"));
        cmbRangopromedio.setItems(FXCollections.observableArrayList("menor a 3.00", "3.00-4.00", "4.00-5.00"));
        cmbSexo.setItems(FXCollections.observableArrayList("F", "M"));

        cargarCantidadEnSegundoPlano();
        cargarProvincias();
        cmbProvincia.setOnAction(e -> {
            String provincia = cmbProvincia.getValue();
            if (provincia != null) {
                cargarDistritos(provinciasMap.get(provincia));
                cmbDistrito.setDisable(false);
                cmbCorregimiento.getItems().clear();
                cmbCorregimiento.setDisable(true);
            }
        });

        cmbDistrito.setOnAction(e -> {
            String distrito = cmbDistrito.getValue();
            if (distrito != null) {
                cargarCorregimientos(distritosMap.get(distrito));
                cmbCorregimiento.setDisable(false);
            }
        });
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar",
                "root",
                "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw"
        );
    }


    private Map<String, Integer> obtenerCantidadRegistros(Connection conn) throws SQLException {
        Map<String, Integer> resultados = new HashMap<>();

        String tablaInstituciones = "SELECT COUNT(*) AS total FROM institucion";
        String tablaEstudiantes = "SELECT COUNT(*) AS total FROM estudiante";
        String tablaMaestros = "SELECT COUNT(*) AS total FROM empleado WHERE tipo_cargo = 'Docente'";
        String tablaAcudientes = "SELECT COUNT(*) AS total FROM acudiente";

        PreparedStatement psInst = conn.prepareStatement(tablaInstituciones);
        PreparedStatement psEst = conn.prepareStatement(tablaEstudiantes);
        PreparedStatement psMaes = conn.prepareStatement(tablaMaestros);
        PreparedStatement psAcud = conn.prepareStatement(tablaAcudientes);

        ResultSet rsInst = psInst.executeQuery();
        if (rsInst.next()) resultados.put("escuelas", rsInst.getInt("total"));

        ResultSet rsEst = psEst.executeQuery();
        if (rsEst.next()) resultados.put("estudiantes", rsEst.getInt("total"));

        ResultSet rsMaes = psMaes.executeQuery();
        if (rsMaes.next()) resultados.put("maestros", rsMaes.getInt("total"));

        ResultSet rsAcud = psAcud.executeQuery();
        if (rsAcud.next()) resultados.put("padres", rsAcud.getInt("total"));

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
            System.out.println("Error al cargar cantidades: " + task.getException().getMessage());
        });

        Thread hilo = new Thread(task);
        hilo.setDaemon(true); // Para que no bloquee el cierre del programa
        hilo.start();
    }

    private void cargarAnioacademico() {
        provinciasMap.clear();
        cmbProvincia.getItems().clear();

        String consultaProvincia = "SELECT id_provincia, nombre FROM provincia ORDER BY nombre";

        try (Connection conn = conectar();
             Statement crearConsutla = conn.createStatement();
             ResultSet provincia = crearConsutla.executeQuery(consultaProvincia)) {

            while (provincia.next()) {
                int id = provincia.getInt("id_provincia");
                String nombre = provincia.getString("nombre");
                provinciasMap.put(nombre, id);
            }
            cmbProvincia.getItems().addAll(provinciasMap.keySet());

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las provincias: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarProvincias() {
        provinciasMap.clear();
        cmbProvincia.getItems().clear();

        String consultaProvincia = "SELECT id_provincia, nombre FROM provincia ORDER BY nombre";

        try (Connection conn = conectar();
             Statement crearConsutla = conn.createStatement();
             ResultSet provincia = crearConsutla.executeQuery(consultaProvincia)) {

            while (provincia.next()) {
                int id = provincia.getInt("id_provincia");
                String nombre = provincia.getString("nombre");
                provinciasMap.put(nombre, id);
            }
            cmbProvincia.getItems().addAll(provinciasMap.keySet());

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las provincias: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDistritos(int idProvincia) {
        distritosMap.clear();
        cmbDistrito.getItems().clear();

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
            cmbDistrito.getItems().addAll(distritosMap.keySet());

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los distritos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarCorregimientos(int idDistrito) {
        corregimientosMap.clear();
        cmbCorregimiento.getItems().clear();

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
            cmbCorregimiento.getItems().addAll(corregimientosMap.keySet());

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los corregimientos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    private void borrarFiltro(){
        cmbProvincia.getItems().clear();
        cmbDistrito.getItems().clear();
        cmbCorregimiento.getItems().clear();
        cmbAnioacademico.getItems().clear();
        cmbSeccion.getItems().clear();
        cmbRangopromedio.getItems().clear();
        cmbSexo.getItems().clear();
        cargarProvincias();
        cmbAnioacademico.setItems(FXCollections.observableArrayList("2023", "2024", "2025"));
        cmbSeccion.setItems(FXCollections.observableArrayList("Primaria", "Secundaria"));
        cmbRangopromedio.setItems(FXCollections.observableArrayList("menor a 3.00", "3.00-4.00", "4.00-5.00"));
        cmbSexo.setItems(FXCollections.observableArrayList("F", "M"));


    }
}

