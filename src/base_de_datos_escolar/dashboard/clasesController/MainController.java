package base_de_datos_escolar.dashboard.clasesController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

//PRIMER INTENTO DE CONEXION A MENÚ
public class MainController {

    @FXML
    private AnchorPane centerContent;

    @FXML
    public void initialize() {
        handleDashboard();
    }


    private void loadUI(String fxmlFile) {
        try {

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

    @FXML
    public void handleDashboard() {
        loadUI("/base_de_datos_escolar/dashboard/archivos_fxml/Dashboard.fxml");
    }



}