package base_de_datos_escolar.dashboard.clasesController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainController {

    @FXML
    private AnchorPane centerContent;

    private void loadUI(String fxmlFile) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource(fxmlFile));
            centerContent.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDashboard() {
        loadUI("dashboard.fxml");
    }

    @FXML
    public void handleStudents() {
        loadUI("students.fxml");
    }

    @FXML
    public void handleTeachers() {
        loadUI("teachers.fxml");
    }

    @FXML
    public void handleParents() {
        loadUI("parents.fxml");
    }

    @FXML
    public void handleEvents() {
        loadUI("events.fxml");
    }

    @FXML
    public void handleExams() {
        loadUI("exams.fxml");
    }
}
