package base_de_datos_escolar.dashboard.clasesController;

import base_de_datos_escolar.controldeusuarios.clasesController.PanelUsuariosController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class NuevoPanel {
    @FXML private Button dashboard_button;
    @FXML private Button estudiantes_button;
    @FXML private Button maestros_button;
    @FXML private Label cantidadEscuela;
    @FXML private Label cantidadMaestro;
    @FXML private Label cantidadEstudiantes;
    @FXML private Label cantidadPadres;
    @FXML private Label lblNombreUsuario;
    @FXML private ImageView imgFotoUsuario;
    private boolean usuarioActivoLog=false;

    @FXML
    private AnchorPane central_pane;
    @FXML
    public void initialize() {
        usuarioActivoLog=true;
        SesionUsuario.usuarioActivo=usuarioActivoLog;
        lblNombreUsuario.setText(SesionUsuario.nombre+" "+SesionUsuario.apellido);


        if (SesionUsuario.foto != null && SesionUsuario.foto.length > 0) {
            InputStream input = new ByteArrayInputStream(SesionUsuario.foto);
            Image imagen = new Image(input);
            imgFotoUsuario.setImage(imagen);
        }
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


    @FXML
    private void botonControldeusuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/base_de_datos_escolar/controldeusuarios/archivos_fxml/PanelUsuarios.fxml"));
            Parent root = loader.load();

            PanelUsuariosController controller = loader.getController();
            // Puedes pasarle datos si quieres: controller.setUsuario(usuarioLogueado);

            Stage stage = new Stage();
            stage.setTitle("Panel de Usuarios");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            // Cierra todas las ventanas abiertas
            Stage currentStage = (Stage) central_pane.getScene().getWindow();


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/base_de_datos_escolar/dashboard/archivos_fxml/login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Inicio de Sesión");
            loginStage.setScene(new Scene(root));

            SesionUsuario.limpiarSesion();

            // Mostrar ventana de login y cerrar la actual
            loginStage.show();
            currentStage.close();


        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de inicio de sesión", Alert.AlertType.ERROR);
        }
    }

    private void resetEstilo() {
        dashboard_button.getStyleClass().remove("dashboard_button_active");
        estudiantes_button.getStyleClass().remove("dashboard_button_active");
        maestros_button.getStyleClass().remove("dashboard_button_active");
    }
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar",
                "root",
                "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw"
        );
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
