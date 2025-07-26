package base_de_datos_escolar.controldeusuarios.clasesController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class RegistroAcademicoController {

    @FXML
    private Label lblNombre, lblCurso, lblPromedio, lblAsistencia, lblReportes;
    @FXML
    private TableView<CalificacionDetalle> tableCalificaciones;
    @FXML
    private TableColumn<CalificacionDetalle, String> colMateria;
    @FXML
    private TableColumn<CalificacionDetalle, Double> colNota;
    @FXML
    private TableColumn<CalificacionDetalle, String> colTipo;
    @FXML
    private TableColumn<CalificacionDetalle, Double> colPorcentaje;
    @FXML
    private Label lblIdUsuario;

    private Connection conexion;
    private int idEstudiante;

    private ObservableList<CalificacionDetalle> listaCalificaciones = FXCollections.observableArrayList();

    public void setEstudiante(Estudiante estudiante) {
        this.idEstudiante = estudiante.getId();
        lblNombre.setText(estudiante.getNombre() + " " + estudiante.getApellido());
        conectarBD();
        cargarRegistroAcademico();
        cargarCalificaciones();
    }

    private void conectarBD() {
        try {
            String url = "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar";
            String usuario = "root";
            String clave = "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw";
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, usuario, clave);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarRegistroAcademico() {
        String sql = "SELECT curso, grado, seccion, promedio, asistencia_total, cantidad_asistencia, " +
                "cantidad_reportes_seguimiento, cantidad_reportes_conducta FROM historial_academico WHERE id_estudiante = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idEstudiante);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lblCurso.setText(rs.getString("curso") + " " + rs.getString("grado") + " - " + rs.getString("seccion"));
                lblPromedio.setText("Promedio: " + rs.getDouble("promedio"));
                lblAsistencia.setText("Asistencia: " + rs.getInt("cantidad_asistencia") + "/" + rs.getInt("asistencia_total"));
                lblReportes.setText("Reportes: Seguimiento " + rs.getInt("cantidad_reportes_seguimiento") +
                        ", Conducta " + rs.getInt("cantidad_reportes_conducta"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarCalificaciones() {
        colMateria.setCellValueFactory(new PropertyValueFactory<>("materia"));
        colNota.setCellValueFactory(new PropertyValueFactory<>("nota"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentaje"));

        String sql = "SELECT m.nombre AS materia, c.nota, c.tipo_asignacion, c.porcentaje_asignacion " +
                "FROM calificacion c INNER JOIN materia m ON c.id_materia = m.id_materia " +
                "WHERE c.id_estudiante = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idEstudiante);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listaCalificaciones.add(new CalificacionDetalle(
                        rs.getString("materia"),
                        rs.getDouble("nota"),
                        rs.getString("tipo_asignacion"),
                        rs.getDouble("porcentaje_asignacion")
                ));
            }
            tableCalificaciones.setItems(listaCalificaciones);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
