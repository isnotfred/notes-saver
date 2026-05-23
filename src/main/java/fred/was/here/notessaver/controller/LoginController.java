package fred.was.here.notessaver.controller;

import fred.was.here.notessaver.dao.UserDAO;
import fred.was.here.notessaver.model.User;
import fred.was.here.notessaver.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        loginBtn;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().strip();
        String password = passwordField.getText();

        errorLabel.setText("");

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Logging in…");

        Optional<User> result = userDAO.login(username, password);

        loginBtn.setDisable(false);
        loginBtn.setText("Login");

        if (result.isPresent()) {
            DashboardController dashboard =
                    SceneManager.switchScene("dashboard.fxml", "Dashboard");
            dashboard.initUser(result.get());
        } else {
            errorLabel.setText("Invalid username or password.");
            passwordField.clear();
        }
    }

    @FXML
    private void goToRegister() {
        SceneManager.switchScene("register.fxml", "Register");
    }
}
