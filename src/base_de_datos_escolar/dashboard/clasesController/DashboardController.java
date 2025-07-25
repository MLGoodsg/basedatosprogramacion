package base_de_datos_escolar.dashboard.clasesController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.fxml.FXML;

public class DashboardController {

    //<editor-fold desc="FXML Declarations">
    @FXML private Label cantidadEscuela;
    @FXML private Label cantidadMaestro;
    @FXML private Label cantidadEstudiantes;
    @FXML private Label cantidadPadres;
    @FXML private ComboBox<String> cmbSexo;
    @FXML private ComboBox<String> cmbProvincia;
    @FXML private ComboBox<String> cmbDistrito;
    @FXML private ComboBox<String> cmbCorregimiento;
    @FXML private ComboBox<String> cmbRangopromedio;
    @FXML private PieChart graficaAprob;
    @FXML private PieChart generoChart;
    @FXML private BarChart<String, Number> graficaPaneNivelE;
    @FXML private BarChart<String, Number> institucionChart;
    @FXML private PieChart graficaSeguimiento;
    @FXML private BarChart<String, Number> graficaPaneAsistencia;
    @FXML private ComboBox<String> cmbAnioacademico;
    @FXML private ComboBox<String> cmbSeccion;
    //</editor-fold>

    private final Map<String, Integer> provinciasMap = new TreeMap<>();
    private final Map<String, Integer> distritosMap = new TreeMap<>();
    private final Map<String, Integer> corregimientosMap = new TreeMap<>();
    private static final DecimalFormat twoDpFormat = new DecimalFormat("#.##");

    @FXML
    public void initialize() {
        configurarFiltros();
        deshabilitarGraficasNoDisponibles();
        cargarProvincias();
        actualizarDashboard();
        configurarListeners();
    }

    private void actualizarDashboard() {
        cargarContadores();
        cargarDatosAprobacion();
        cargarDatosGraficaGenero();
        cargarDatosGraficaInstitucion();
        cargarDatosEmpleadosPorCargo();
    }

    // --- SECCIÓN DE CARGA DE DATOS (CON ETIQUETAS MEJORADAS) ---

    private void cargarDatosAprobacion() {
        String sql = "SELECT CASE WHEN promedio >= 3.0 THEN 'Aprobados' ELSE 'Reprobados' END AS estado, COUNT(*) AS cantidad " +
                "FROM (SELECT AVG(nota) AS promedio FROM calificacion GROUP BY id_estudiante) AS promedios GROUP BY estado";

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try (Connection conn = conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            int totalEstudiantes = 0;
            List<Map<String, Object>> resultados = new ArrayList<>();
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                totalEstudiantes += cantidad;
                resultados.add(Map.of("estado", rs.getString("estado"), "cantidad", cantidad));
            }

            for (Map<String, Object> fila : resultados) {
                String estado = (String) fila.get("estado");
                int cantidad = (int) fila.get("cantidad");
                double porcentaje = (double) cantidad / totalEstudiantes * 100;

                PieChart.Data rebanada = new PieChart.Data(String.format("%s (%d)", estado, cantidad), cantidad);
                data.add(rebanada);
            }

            graficaAprob.setData(data);
            graficaAprob.setTitle("Estadística de Aprobación");

            // ¡Paso Clave! Hacemos visibles las etiquetas sobre el gráfico
            graficaAprob.setLabelsVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Gráfica", "No se pudieron cargar las estadísticas de aprobación.", Alert.AlertType.ERROR);
        }
    }
    private void cargarDatosGraficaGenero() {
        StringBuilder sql = new StringBuilder("SELECT e.sexo, COUNT(e.id_estudiante) as cantidad FROM estudiante e LEFT JOIN institucion i ON e.id_institucion = i.id_institucion");
        List<Object> params = new ArrayList<>();
        construirWhereClause(sql, params); // Asumiendo que tienes este método de ayuda para filtros
        sql.append(" GROUP BY e.sexo");

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            setParameters(pstmt, params); // Asumiendo que tienes este método de ayuda
            ResultSet rs = pstmt.executeQuery();

            int total = 0;
            List<Map<String, Object>> resultados = new ArrayList<>();
            while (rs.next()) {
                String sexo = rs.getString("sexo");
                if (sexo != null && !sexo.trim().isEmpty()) {
                    int cantidad = rs.getInt("cantidad");
                    total += cantidad;
                    resultados.add(Map.of("sexo", sexo, "cantidad", cantidad));
                }
            }

            for (Map<String, Object> fila : resultados) {
                String sexo = (String) fila.get("sexo");
                int cantidad = (int) fila.get("cantidad");
                double porcentaje = (double) cantidad / total * 100;
                String etiqueta = sexo.equalsIgnoreCase("F") ? "Femenino" : "Masculino";

                PieChart.Data rebanada = new PieChart.Data(String.format("%s (%d)", etiqueta, cantidad), cantidad);

                rebanada.setName(String.format("%.1f%%", porcentaje));
                data.add(rebanada);
            }

            generoChart.setData(data);
            generoChart.setTitle(data.isEmpty() ? "Género (Sin Datos)" : "Distribución por Género");


            generoChart.setLabelsVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Gráfica", "No se pudieron cargar los datos de género.", Alert.AlertType.ERROR);
        }
    }
    private void cargarDatosGraficaInstitucion() {
        StringBuilder sql = new StringBuilder("SELECT i.nombre, COUNT(e.id_estudiante) AS cantidad FROM estudiante e LEFT JOIN institucion i ON e.id_institucion = i.id_institucion");
        List<Object> params = new ArrayList<>();
        construirWhereClause(sql, params);
        sql.append(" GROUP BY i.nombre HAVING COUNT(e.id_estudiante) > 0 ORDER BY cantidad DESC");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            setParameters(pstmt, params);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String nombreInstitucion = rs.getString("nombre");
                if (nombreInstitucion != null) {
                    series.getData().add(new XYChart.Data<>(nombreInstitucion, rs.getInt("cantidad")));
                }
            }
            institucionChart.getData().clear();
            institucionChart.getData().add(series);
            institucionChart.setTitle(series.getData().isEmpty() ? "Estudiantes (Sin Datos)" : "Estudiantes por Institución");

            // Añadir etiquetas numéricas a las barras
            addLabelsToBarChart(institucionChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarDatosEmpleadosPorCargo() {
        String sql = "SELECT tipo_cargo, COUNT(*) AS cantidad FROM empleado GROUP BY tipo_cargo ORDER BY cantidad DESC";
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try (Connection conn = conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String cargo = rs.getString("tipo_cargo");
                if (cargo != null) {
                    series.getData().add(new XYChart.Data<>(cargo, rs.getInt("cantidad")));
                }
            }
            graficaPaneNivelE.getData().clear();
            graficaPaneNivelE.getData().add(series);
            graficaPaneNivelE.setTitle("Empleados por Cargo");

            // Añadir etiquetas numéricas a las barras
            addLabelsToBarChart(graficaPaneNivelE);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * NUEVO MÉTODO DE AYUDA: Recorre un gráfico de barras y añade una etiqueta
     * de texto con el valor numérico sobre cada barra.
     */
    private void addLabelsToBarChart(BarChart<String, Number> chart) {
        for (XYChart.Series<String, Number> series : chart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    Text label = new Text(data.getYValue().toString());
                    label.setStyle("-fx-font-weight: bold;");
                    // Posicionar la etiqueta sobre la barra
                    node.parentProperty().addListener((obs, oldParent, newParent) -> {
                        if (newParent != null) {
                            javafx.scene.layout.Pane parentPane = (javafx.scene.layout.Pane) newParent;
                            parentPane.getChildren().add(label);
                        }
                    });
                    node.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
                        label.setLayoutX(Math.round(newBounds.getMinX() + newBounds.getWidth() / 2 - label.getLayoutBounds().getWidth() / 2));
                        label.setLayoutY(Math.round(newBounds.getMinY() - 5));
                    });
                }
            }
        }
    }

    // --- El resto del código permanece igual ---

    //<editor-fold desc="Cargas, Filtros y Otros Métodos de Ayuda (Sin cambios)">
    private Connection conectar() throws SQLException {
        final String URL = "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar";
        final String USUARIO = "root";
        final String CONTRASENA = "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw";
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
    private void cargarContadores() {
        String sql = "SELECT (SELECT COUNT(*) FROM institucion) AS e, (SELECT COUNT(*) FROM estudiante) AS s, (SELECT COUNT(*) FROM empleado WHERE tipo_cargo = 'Docente') AS m, (SELECT COUNT(*) FROM acudiente) AS p;";
        try (Connection conn = conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                cantidadEscuela.setText(rs.getString("e"));
                cantidadEstudiantes.setText(rs.getString("s"));
                cantidadMaestro.setText(rs.getString("m"));
                cantidadPadres.setText(rs.getString("p"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void construirWhereClause(StringBuilder sql, List<Object> params) {
        List<String> conditions = new ArrayList<>();
        if (cmbCorregimiento.getValue() != null && corregimientosMap.containsKey(cmbCorregimiento.getValue())) {
            conditions.add("i.id_corregimiento = ?");
            params.add(corregimientosMap.get(cmbCorregimiento.getValue()));
        } else if (cmbDistrito.getValue() != null && distritosMap.containsKey(cmbDistrito.getValue())) {
            conditions.add("i.id_distrito = ?");
            params.add(distritosMap.get(cmbDistrito.getValue()));
        } else if (cmbProvincia.getValue() != null && provinciasMap.containsKey(cmbProvincia.getValue())) {
            conditions.add("i.id_provincia = ?");
            params.add(provinciasMap.get(cmbProvincia.getValue()));
        }
        if (cmbSexo.getValue() != null) {
            conditions.add("e.sexo = ?");
            params.add(cmbSexo.getValue().substring(0, 1));
        }

        if (cmbRangopromedio.getValue() != null) {
            String subQuery = "e.id_estudiante IN (SELECT id_estudiante FROM calificacion GROUP BY id_estudiante HAVING AVG(nota) %s)";
            String rango = cmbRangopromedio.getValue();
            switch (rango) {
                case "Menor a 3.00":
                    conditions.add(String.format(subQuery, "< 3.0"));
                    break;
                case "3.00 - 4.00":
                    conditions.add(String.format(subQuery, "BETWEEN 3.0 AND 4.0"));
                    break;
                case "4.01 - 5.00":
                    conditions.add(String.format(subQuery, ">= 4.01"));
                    break;
            }
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
    }
    private void setParameters(PreparedStatement pstmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            pstmt.setObject(i + 1, params.get(i));
        }
    }
    private void configurarListeners() {
        cmbProvincia.setOnAction(e -> {
            cargarDistritos(provinciasMap.get(cmbProvincia.getValue()));
            actualizarDashboard();
        });
        cmbDistrito.setOnAction(e -> {
            cargarCorregimientos(distritosMap.get(cmbDistrito.getValue()));
            actualizarDashboard();
        });
        cmbCorregimiento.setOnAction(e -> actualizarDashboard());
        cmbSexo.setOnAction(e -> actualizarDashboard());
        cmbRangopromedio.setOnAction(e -> actualizarDashboard());
    }
    private void configurarFiltros() {
        cmbSexo.setItems(FXCollections.observableArrayList("Femenino", "Masculino"));
        cmbRangopromedio.setItems(FXCollections.observableArrayList("Menor a 3.00", "3.00 - 4.00", "4.01 - 5.00"));
        cmbRangopromedio.setDisable(false);
        cmbAnioacademico.setDisable(true);
        cmbSeccion.setDisable(true);
    }
    private void deshabilitarGraficasNoDisponibles() {
        graficaPaneAsistencia.setTitle("Asistencia (No Disponible)");
        graficaPaneAsistencia.getData().clear();
    }
    @FXML
    private void reiniciarFiltros() {
        cmbProvincia.setValue(null);
        cmbDistrito.setValue(null);
        cmbCorregimiento.setValue(null);
        cmbSexo.setValue(null);
        cmbRangopromedio.setValue(null);
        cmbDistrito.getItems().clear();
        cmbCorregimiento.getItems().clear();
        cmbDistrito.setDisable(true);
        cmbCorregimiento.setDisable(true);
        actualizarDashboard();
    }
    private void cargarProvincias() {
        provinciasMap.clear();
        try (Connection conn = conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT id_provincia, nombre FROM provincia")) {
            while (rs.next()) {
                provinciasMap.put(rs.getString("nombre"), rs.getInt("id_provincia"));
            }
            cmbProvincia.getItems().setAll(provinciasMap.keySet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void cargarDistritos(Integer idProvincia) {
        cmbDistrito.getItems().clear();
        distritosMap.clear();
        if (idProvincia == null) return;
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement("SELECT id_distrito, nombre FROM distrito WHERE id_provincia = ?")) {
            pstmt.setInt(1, idProvincia);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                distritosMap.put(rs.getString("nombre"), rs.getInt("id_distrito"));
            }
            cmbDistrito.getItems().setAll(distritosMap.keySet());
            cmbDistrito.setDisable(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void cargarCorregimientos(Integer idDistrito) {
        cmbCorregimiento.getItems().clear();
        corregimientosMap.clear();
        if (idDistrito == null) return;
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement("SELECT id_corregimiento, nombre FROM corregimiento WHERE id_distrito = ?")) {
            pstmt.setInt(1, idDistrito);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                corregimientosMap.put(rs.getString("nombre"), rs.getInt("id_corregimiento"));
            }
            cmbCorregimiento.getItems().setAll(corregimientosMap.keySet());
            cmbCorregimiento.setDisable(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    //</editor-fold>
}