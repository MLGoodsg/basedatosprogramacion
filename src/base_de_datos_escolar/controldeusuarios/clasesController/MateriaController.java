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

public class MateriaController implements Initializable {

    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cbCurso;
    @FXML
    private TextField txtHorasClase;
    @FXML
    private ComboBox<String> cbArea;
    @FXML
    private TextField txtEspecialidad;

    @FXML
    private TableView<Materia> tablaMateria;
    @FXML
    private TableColumn<Materia, Integer> colId;
    @FXML
    private TableColumn<Materia, String> colNombre;
    @FXML
    private TableColumn<Materia, String> colCurso;
    @FXML
    private TableColumn<Materia, Integer> colHorasClase;
    @FXML
    private TableColumn<Materia, String> colArea;
    @FXML
    private TableColumn<Materia, String> colEspecialidad;

    private ObservableList<Materia> listaMaterias = FXCollections.observableArrayList();

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
        cargarCombos();
        cargarMaterias();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        colHorasClase.setCellValueFactory(new PropertyValueFactory<>("horasClase"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));

        tablaMateria.setItems(listaMaterias);
    }

    private void cargarCombos() {
        cbCurso.getItems().addAll("1°", "2°", "3°", "4°", "5°", "6°", "7°", "8°", "9°", "10°", "11°", "12°");
        cbArea.getItems().addAll("HUMANÍSTICA", "CIENTÍFICA", "TÉCNICA", "OTROS");
    }

    private void cargarMaterias() {
        listaMaterias.clear();
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM materia_meduca")) {

            while (rs.next()) {
                listaMaterias.add(new Materia(
                        rs.getInt("id_materia_meduca"),
                        rs.getString("nombre"),
                        rs.getString("curso"),
                        rs.getInt("horas_clase"),
                        rs.getString("area"),
                        rs.getString("especialidad")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las materias.");
        }
    }

    @FXML
    private void agregarMateria() {
        if (!validarCampos()) return;

        try (Connection conn = conectar()) {
            String sql = "INSERT INTO materia_horario (id_materia_meduca, curso, horas_clase, area, especialidad) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNombre.getText());
            stmt.setString(2, cbCurso.getValue());
            stmt.setInt(3, Integer.parseInt(txtHorasClase.getText()));
            stmt.setString(4, cbArea.getValue());
            stmt.setString(5, txtEspecialidad.getText());
            stmt.executeUpdate();

            mostrarAlerta("Éxito", "Materia agregada correctamente.");
            cargarMaterias();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo agregar la materia.");
        }
    }

    @FXML
    private void modificarMateria() {
        Materia seleccionada = tablaMateria.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Aviso", "Seleccione una materia para modificar.");
            return;
        }

        if (!validarCampos()) return;

        try (Connection conn = conectar()) {
            String sql = "UPDATE materia_meduca SET nombre=?, curso=?, horas_clase=?, area=?, especialidad=? WHERE id_materia_meduca=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNombre.getText());
            stmt.setString(2, cbCurso.getValue());
            stmt.setInt(3, Integer.parseInt(txtHorasClase.getText()));
            stmt.setString(4, cbArea.getValue());
            stmt.setString(5, txtEspecialidad.getText());
            stmt.setInt(6, seleccionada.getId());
            stmt.executeUpdate();

            mostrarAlerta("Éxito", "Materia modificada correctamente.");
            cargarMaterias();
            limpiarCampos();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo modificar la materia.");
        }
    }

    @FXML
    private void eliminarMateria() {
        Materia seleccionada = tablaMateria.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Aviso", "Seleccione una materia para eliminar.");
            return;
        }

        try (Connection conn = conectar()) {
            String sql = "DELETE FROM materia_meduca WHERE id_materia_meduca=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, seleccionada.getId());
            stmt.executeUpdate();

            mostrarAlerta("Éxito", "Materia eliminada correctamente.");
            cargarMaterias();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo eliminar la materia.");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        cbCurso.setValue(null);
        txtHorasClase.clear();
        cbArea.setValue(null);
        txtEspecialidad.clear();
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || cbCurso.getValue() == null || txtHorasClase.getText().isEmpty() ||
                cbArea.getValue() == null || txtEspecialidad.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
