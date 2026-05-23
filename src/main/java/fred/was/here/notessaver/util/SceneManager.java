package fred.was.here.notessaver.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Central scene-switching helper.
 * All FXML files live under resources/com/notesvault/view/.
 */
public class SceneManager {

    private static Stage primaryStage;

    private SceneManager() {}

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Load an FXML file and swap the primary stage's scene.
     *
     * @param fxmlFile  filename, e.g. "login.fxml"
     * @param title     window title
     * @return the controller instance loaded by FXMLLoader
     */
    public static <T> T switchScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fred/was/here/notessaver/" + fxmlFile)
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    SceneManager.class.getResource("/fred/was/here/notessaver/styles.css").toExternalForm()
            );
            primaryStage.setTitle(title + " — NotesVault");
            primaryStage.setScene(scene);
            primaryStage.show();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load FXML: " + fxmlFile, e);
        }
    }

    public static Stage getStage() { return primaryStage; }
}