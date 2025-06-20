package base_de_datos_escolar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VentanaPruebaController {

    @FXML
    private void irALogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irARegistro() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Registro");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

