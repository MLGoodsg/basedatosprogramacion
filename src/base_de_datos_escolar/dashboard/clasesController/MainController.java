package base_de_datos_escolar.dashboard.clasesController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private AnchorPane centerContent;

    /**
     * Este método se ejecuta automáticamente al cargar la vista y
     * muestra el dashboard por defecto.
     */
    @FXML
    public void initialize() {
        handleDashboard();
    }

    /**
     * Carga una vista FXML en el panel central de la aplicación.
     * @param fxmlFile La ruta absoluta al archivo FXML (desde la raíz del classpath).
     */
    private void loadUI(String fxmlFile) {
        try {
            // Carga la vista desde la ruta especificada
            URL url = getClass().getResource(fxmlFile);
            if (url == null) {
                throw new IOException("No se pudo encontrar el archivo: " + fxmlFile);
            }
            Node pane = FXMLLoader.load(url);
            centerContent.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
            // Muestra una alerta al usuario si la vista no se puede cargar
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudo cargar la interfaz de usuario.");
            alert.setContentText("Asegúrate de que el archivo '" + fxmlFile + "' exista en la ruta correcta.");
            alert.showAndWait();
        }
    }

    // --- Métodos de Manejo de Botones ---
    // NOTA: Asegúrate de que las rutas a tus archivos FXML sean correctas.
    // Deben ser rutas absolutas empezando con '/'.

    @FXML
    public void handleDashboard() {
        // Ejemplo de ruta absoluta
        loadUI("/base_de_datos_escolar/dashboard/archivos_fxml/Dashboard.fxml");
    }

    @FXML
    public void handleStudents() {
        loadUI("/base_de_datos_escolar/controldeusuarios/archivos_fxml/Estudiante.fxml");
    }

    @FXML
    public void handleTeachers() {
        loadUI("/base_de_datos_escolar/controldeusuarios/archivos_fxml/Empleado.fxml");
    }

    @FXML
    public void handleParents() {
        loadUI("/base_de_datos_escolar/controldeusuarios/archivos_fxml/Acudiente.fxml");
    }

    @FXML
    public void handleEvents() {
        // Debes crear este archivo FXML
        // loadUI("/base_de_datos_escolar/events/EventsView.fxml");
    }

    @FXML
    public void handleExams() {
        // Debes crear este archivo FXML
        // loadUI("/base_de_datos_escolar/exams/ExamsView.fxml");
    }
}