package base_de_datos_escolar.dashboard.clasesController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasenia;

    private Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // URL para conexion a base de datos
        return DriverManager.getConnection(
                "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar",
                "root",
                "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw"
        );

    }


    @FXML
    private void iniciarSesion(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String clave = txtContrasenia.getText();
        String consultaUsuario = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasenia = ?";


        try (Connection conn = conectar(); PreparedStatement bd_iniciodesesion = conn.prepareStatement(consultaUsuario);) {
            bd_iniciodesesion.setString(1, usuario);
            bd_iniciodesesion.setString(2, clave); // *Por seguridad deberías cifrar contraseñas con hash*

            ResultSet busqueda_usuario = bd_iniciodesesion.executeQuery();

            //Verificador de conexion a la base de datos
            if (busqueda_usuario.next()) {
                // GUARDAR EN SESIÓN
                SesionUsuario.nombre = busqueda_usuario.getString("nombre");
                SesionUsuario.apellido = busqueda_usuario.getString("apellido");
                SesionUsuario.foto = busqueda_usuario.getBytes("foto_usuario");

                // CARGAR PANEL PRINCIPAL
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/base_de_datos_escolar/dashboard/archivos_fxml/NuevoPanel.fxml"));
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Dashboard");
                    stage.setMaximized(true);
                    stage.show();

                    ((Stage) txtUsuario.getScene().getWindow()).close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mostrarAlerta("Error", "Credenciales incorrectas", AlertType.ERROR);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error de conexión", e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    private void irARegistro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/base_de_datos_escolar/dashboard/archivos_fxml/signup.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
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
