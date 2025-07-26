package base_de_datos_escolar.dashboard.clasesController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VentanaPrincipal extends Application {


    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/base_de_datos_escolar/dashboard/archivos_fxml/VentanaPrincipal.fxml")); // ← sin barra y en misma carpeta
        primaryStage.setTitle("Sistema Escolar");
        primaryStage.setScene(new Scene(root, 761.0, 533.0));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
