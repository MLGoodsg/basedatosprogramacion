package base_de_datos_escolar.controldeusuarios.clasesController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MateriaHorarioController implements Initializable {

    @FXML
    private ComboBox<String> cbInstitucion;
    @FXML
    private ComboBox<String> cbPeriodo;
    @FXML
    private ComboBox<String> cbDocente;
    @FXML
    private ComboBox<String> cbMateria;
    @FXML
    private ComboBox<String> cbCurso;
    @FXML
    private ComboBox<String> cbGrado;
    @FXML
    private ComboBox<String> cbDia;
    @FXML
    private ComboBox<String> cbHora;
    @FXML
    private ComboBox<String> cbBachillerato;


    @FXML
    private TableView<MateriaHorario> tablaHorarios;
    @FXML
    private TableColumn<MateriaHorario, Integer> colId;
    @FXML
    private TableColumn<MateriaHorario, String> colInstitucion;
    @FXML
    private TableColumn<MateriaHorario, String> colDocente;
    @FXML
    private TableColumn<MateriaHorario, String> colMateria;
    @FXML
    private TableColumn<MateriaHorario, String> colCurso;
    @FXML
    private TableColumn<MateriaHorario, String> colGrado;
    @FXML
    private TableColumn<MateriaHorario, String> colDia;
    @FXML
    private TableColumn<MateriaHorario, String> colHora;

    private ObservableList<MateriaHorario> listaHorarios = FXCollections.observableArrayList();

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar",
                "root",
                "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw"
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInstituciones();
        cargarDiasHorasGrado();
        configurarListeners();
        cargarHorarios();
        cargarBachillerato(null);
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colInstitucion.setCellValueFactory(new PropertyValueFactory<>("nombreInstitucion"));
        colDocente.setCellValueFactory(new PropertyValueFactory<>("nombreDocente"));
        colMateria.setCellValueFactory(new PropertyValueFactory<>("nombreMateria"));
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colGrado.setCellValueFactory(new PropertyValueFactory<>("grado"));
        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));

        tablaHorarios.setItems(listaHorarios);
    }

    private void cargarInstituciones() {
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT nombre FROM institucion")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) cbInstitucion.getItems().add(rs.getString("nombre"));
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar instituciones", Alert.AlertType.ERROR);
        }
    }

    private void cargarPeriodos(String institucion) {
        cbPeriodo.getItems().clear();
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DISTINCT anio FROM periodo_academico p JOIN institucion i ON p.id_institucion = i.id_institucion WHERE i.nombre = ?")) {
            stmt.setString(1, institucion);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) cbPeriodo.getItems().add(rs.getString("anio"));
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar periodos", Alert.AlertType.ERROR);
        }
    }

    private void cargarDocentes(String institucionSeleccionada) {
        cbDocente.getItems().clear();
        try (Connection conn = conectar()) {
            int idInstitucion = obtenerIdInstitucion(institucionSeleccionada);
            String sql = "SELECT CONCAT(nombre, ' ', apellido) AS docente " +
                    "FROM empleado WHERE id_institucion = ? AND tipo_cargo = 'Docente'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idInstitucion);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cbDocente.getItems().add(rs.getString("docente"));
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar docentes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarCursos() {
        String institucionSeleccionada = cbInstitucion.getValue();
        if (institucionSeleccionada == null) return;

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT nivel_educativo FROM institucion WHERE nombre = ?")) {

            stmt.setString(1, institucionSeleccionada);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nivelEducativo = rs.getString("nivel_educativo");
                cbCurso.getItems().clear();

                if (nivelEducativo.equals("Primaria")) {
                    cbCurso.getItems().addAll("1°", "2°", "3°", "4°", "5°", "6°");
                    cbBachillerato.setDisable(true);

                } else if (nivelEducativo.equals("Secundaria")) {
                    cbCurso.getItems().addAll("7°", "8°", "9°", "10°", "11°", "12°");
                    cbBachillerato.setDisable(false);

                } else {
                    cbCurso.getItems().addAll("1°", "2°", "3°", "4°", "5°", "6°",
                            "7°", "8°", "9°", "10°", "11°", "12°");
                    cbBachillerato.setDisable(false);

                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar cursos", Alert.AlertType.ERROR);
        }
    }


    private void cargarBachillerato(String curso) {
        cbBachillerato.getItems().clear();
        if ((curso == null) || ((cbCurso.getValue().equals("7°")) || (cbCurso.getValue().equals("8°")) || (cbCurso.getValue().equals("9°")))) {
            cbBachillerato.setDisable(true);
        } else {
            cbBachillerato.setDisable(false);
            try (Connection conn = conectar();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT DISTINCT especialidad FROM materia_meduca WHERE curso = ? AND especialidad IS NOT NULL")) {

                stmt.setString(1, curso);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    cbBachillerato.getItems().add(rs.getString("especialidad"));
                }

                if (cbBachillerato.getItems().isEmpty()) {
                    cbBachillerato.getItems().add("General");
                }

            } catch (SQLException e) {
                mostrarAlerta("Error", "Error al cargar bachilleratos", Alert.AlertType.ERROR);
            }
        }
    }

    private void cargarMaterias(String curso) {
        cbMateria.getItems().clear();
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT nombre FROM materia_meduca " +
                             "WHERE curso = ? AND (especialidad IS NULL OR especialidad = ?) ORDER BY nombre ASC")) {

            stmt.setString(1, curso);
            stmt.setString(2, cbBachillerato.getValue() != null ? cbBachillerato.getValue() : "");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cbMateria.getItems().add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al cargar materias: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarDiasHorasGrado() {
        cbDia.getItems().addAll("lunes", "martes", "miércoles", "jueves", "viernes");
        cbHora.getItems().addAll("7:00-7:45", "7:45-8:30", "8:30-9:15", "9:15-10:00", "10:00-10:30",
                "10:30-11:15", "11:15-12:00", "12:00-12:45", "12:45-1:30", "1:30-2:15", "2:15-3:00");
        cbGrado.getItems().addAll("A", "B", "C");
    }

    private void configurarListeners() {
        cbInstitucion.setOnAction(e -> {
            if (cbInstitucion.getValue() != null) {
                cargarPeriodos(cbInstitucion.getValue());
                cargarDocentes(cbInstitucion.getValue());
                cargarCursos();
            }
        });

        cbCurso.setOnAction(e -> {
            if (cbCurso.getValue() != null) {
                cargarBachillerato(cbCurso.getValue());
                cargarMaterias(cbCurso.getValue());

            }
        });

        cbBachillerato.setOnAction(e -> {
            if (cbCurso.getValue() != null) {
                cargarMaterias(cbCurso.getValue());
            }
        });
    }

    @FXML
    private void agregarHorario() {
        if (!validarCampos()) {
            mostrarAlerta("Error", "Complete todos los campos", Alert.AlertType.WARNING);
            return;
        }

        try (Connection conn = conectar()) {
            String sql = "INSERT INTO materia_horario (id_institucion,id_docente,id_materia_meduca,id_periodo,curso,grado,dia,hora) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            int idInstitucion = obtenerIdInstitucion(cbInstitucion.getValue());
            int idDocente = obtenerIdDocente(cbDocente.getValue());
            int idMateria = obtenerIdMateria(cbMateria.getValue());
            int idPeriodo = obtenerIdPeriodo(cbPeriodo.getValue(), idInstitucion);

            stmt.setInt(1, idInstitucion);
            stmt.setInt(2, idDocente);
            stmt.setInt(3, idMateria);
            stmt.setInt(4, idPeriodo);
            stmt.setString(5, cbCurso.getValue());
            stmt.setString(6, cbGrado.getValue());
            stmt.setString(7, cbDia.getValue());
            stmt.setString(8, cbHora.getValue());


            stmt.executeUpdate();
            mostrarMensaje("Horario agregado correctamente");
            cargarHorarios();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo agregar el horario" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos() {
        return cbInstitucion.getValue() != null && cbPeriodo.getValue() != null &&
                cbDocente.getValue() != null && cbCurso.getValue() != null &&
                cbMateria.getValue() != null && cbGrado.getValue() != null &&
                cbDia.getValue() != null && cbHora.getValue() != null;
    }

    private void cargarHorarios() {
        listaHorarios.clear();
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT h.id_materia as id, i.nombre as institucion, CONCAT(e.nombre,' ',e.apellido) as docente, m.nombre as materia, h.curso, h.grado, h.dia, h.hora, h.id_periodo " +
                             "FROM materia_horario h " +
                             "JOIN institucion i ON h.id_institucion=i.id_institucion " +
                             "JOIN empleado e ON h.id_docente=e.id_empleado " +
                             "JOIN materia_meduca m ON h.id_materia_meduca=m.id_materia_meduca")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listaHorarios.add(new MateriaHorario(
                        rs.getInt("id"),
                        0,
                        rs.getString("institucion"),
                        0,
                        rs.getString("docente"),
                        0,
                        rs.getString("materia"),
                        rs.getString("curso"),
                        rs.getString("grado"),
                        rs.getString("dia"),
                        rs.getString("hora"),
                        rs.getInt("id_periodo")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los horarios", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarMateriaHorario() {
        MateriaHorario seleccionado = tablaHorarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Seleccione un horario para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar el horario seleccionado?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try (Connection conn = conectar();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM materia_horario WHERE id_materia=?")) {
                    stmt.setInt(1, seleccionado.getId());
                    stmt.executeUpdate();
                    cargarHorarios();
                    mostrarMensaje("Horario eliminado");
                } catch (SQLException e) {
                    mostrarAlerta("Error", "No se pudo eliminar", Alert.AlertType.ERROR);
                }
            }
        });
    }

    @FXML
    private void limpiarCampos() {
        cbInstitucion.setValue(null);
        cbPeriodo.setValue(null);
        cbDocente.setValue(null);
        cbMateria.setValue(null);
        cbCurso.setValue(null);
        cbGrado.setValue(null);
        cbDia.setValue(null);
        cbHora.setValue(null);
        cbBachillerato.setValue(null);

    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Métodos para obtener IDs
    private int obtenerIdInstitucion(String nombre) throws SQLException {
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT id_institucion FROM institucion WHERE nombre=?")) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_institucion");
            throw new SQLException("Institución no encontrada");
        }
    }

    private int obtenerIdDocente(String nombre) throws SQLException {
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT id_empleado FROM empleado WHERE CONCAT(nombre,' ',apellido)=?")) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_empleado");
            throw new SQLException("Docente no encontrado");
        }
    }

    private int obtenerIdMateria(String nombre) throws SQLException {
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT id_materia_meduca FROM materia_meduca WHERE nombre=?")) {
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_materia_meduca");
            throw new SQLException("Materia no encontrada");
        }
    }

    private int obtenerIdPeriodo(String anio, int idInstitucion) throws SQLException {
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement("SELECT id_periodo FROM periodo_academico WHERE anio=? AND id_institucion=?")) {
            stmt.setString(1, anio);
            stmt.setInt(2, idInstitucion);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id_periodo");
            throw new SQLException("Periodo no encontrado");
        }
    }
}
