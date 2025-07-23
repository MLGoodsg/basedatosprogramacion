package base_de_datos_escolar.controldeusuarios.clasesController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import base_de_datos_escolar.dashboard.clasesController.SesionUsuario;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.sql.*;

public class PanelUsuariosController {
    @FXML private Button dashboard_button;
    @FXML private Button estudiantes_button;
    @FXML private Button maestros_button;
    @FXML private Label cantidadEscuela;
    @FXML private Label cantidadMaestro;
    @FXML private Label cantidadEstudiantes;
    @FXML private Label cantidadPadres;
    @FXML private Label lblNombreUsuario;
    @FXML private ImageView imgFotoUsuario;

    @FXML
    private AnchorPane central_pane;
    @FXML
    public void initialize() {

        lblNombreUsuario.setText(SesionUsuario.nombre+" "+SesionUsuario.apellido);

        if (SesionUsuario.foto != null && SesionUsuario.foto.length > 0) {
            InputStream input = new ByteArrayInputStream(SesionUsuario.foto);
            Image imagen = new Image(input);
            imgFotoUsuario.setImage(imagen);
        }
        estudiantesClick(); // Esto simula un clic en el botón del Estudiante
    }

    private void loadUI(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/base_de_datos_escolar/controldeusuarios/archivos_fxml/" + fxmlFile));
            AnchorPane pane = loader.load();

            central_pane.getChildren().setAll(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void estudiantesClick() {

        loadUI("VistaEstudiante.fxml");

    }
    @FXML
    private void empleadosClick() {

        loadUI("VistaEmpleados.fxml");
    }

    @FXML
    private void acudientesClick() {

        loadUI("VistaAcudientes.fxml");
    }


    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar",
                "root",
                "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw"
        );
    }

}

