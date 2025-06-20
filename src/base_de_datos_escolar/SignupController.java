package base_de_datos_escolar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

public class SignupController {

    @FXML private TextField usuarioField;
    @FXML private TextField nombreField;
    @FXML private TextField apellidoField;
    @FXML private PasswordField contraseniaField;

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/base_de_datos_escolar", "root", "root");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String usuario = usuarioField.getText();
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String contrasenia = contraseniaField.getText();

        String sql = "INSERT INTO usuarios (user_nomuser, nombre, apellido, contrasenia) VALUES (?, ?, ?, ?)";

        try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, nombre);
            stmt.setString(3, apellido);
            stmt.setString(4, contrasenia); // 🔐 Idealmente cifrado

            stmt.executeUpdate();
            mostrarAlerta("Éxito", "Usuario registrado correctamente", AlertType.INFORMATION);
            goToLogin(event);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo registrar: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usuarioField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
