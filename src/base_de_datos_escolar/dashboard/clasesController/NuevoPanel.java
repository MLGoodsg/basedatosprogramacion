package base_de_datos_escolar.dashboard.clasesController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.*;

public class NuevoPanel {
    @FXML private Button dashboard_button;
    @FXML private Button estudiantes_button;
    @FXML private Button maestros_button;
    @FXML private Label cantidadEscuela;
    @FXML private Label cantidadMaestro;
    @FXML private Label cantidadEstudiantes;
    @FXML private Label cantidadPadres;


    @FXML
    private AnchorPane central_pane;
    @FXML
    public void initialize() {
        dashboardClick(); // Esto simula un clic en el botón del dashboard
    }

    private void loadUI(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/base_de_datos_escolar/dashboard/archivos_fxml/" + fxmlFile));
            AnchorPane pane = loader.load();

            // No necesitas llamar manualmente cargarCantidad porque se llama en initialize()
            central_pane.getChildren().setAll(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void dashboardClick() {
        resetEstilo();
        dashboard_button.getStyleClass().add("dashboard_button_active");
        loadUI("dashboard.fxml");

    }
    @FXML
    private void estudiantebuttonClick() {
        resetEstilo();
        estudiantes_button.getStyleClass().add("dashboard_button_active");
        loadUI("students.fxml");
    }

    @FXML
    private void maestrobuttonClick() {
        resetEstilo();
        maestros_button.getStyleClass().add("dashboard_button_active");
        loadUI("teachers.fxml");
    }

    private void resetEstilo() {
        dashboard_button.getStyleClass().remove("dashboard_button_active");
        estudiantes_button.getStyleClass().remove("dashboard_button_active");
        maestros_button.getStyleClass().remove("dashboard_button_active");
    }
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://maglev.proxy.rlwy.net:24319/railway",
                "root",
                "mfMmjJemvZXmztSmXQiraWQjUBDLmhPE"
        );
    }

}
