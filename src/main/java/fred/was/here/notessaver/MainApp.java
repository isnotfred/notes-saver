package fred.was.here.notessaver;

import fred.was.here.notessaver.util.DatabaseUtil;
import fred.was.here.notessaver.util.SceneManager;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Test DB connection at startup
        if (!DatabaseUtil.testConnection()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Cannot connect to PostgreSQL");
            alert.setContentText(
                    "Make sure Docker is running and the container is up.\n" +
                            "Run:  docker-compose up -d  inside the /docker folder."
            );
            alert.showAndWait();
            return;
        }

        primaryStage.setResizable(true);
        primaryStage.setMinWidth(480);
        primaryStage.setMinHeight(400);

        SceneManager.init(primaryStage);
        SceneManager.switchScene("login.fxml", "Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
