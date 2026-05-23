package fred.was.here.notessaver.controller;

import fred.was.here.notessaver.dao.UserDAO;
import fred.was.here.notessaver.model.User;
import fred.was.here.notessaver.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;

public class RegisterController {

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label         errorLabel;
    @FXML private Button        registerBtn;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().strip();
        String email    = emailField.getText().strip();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();

        errorLabel.setText("");

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }
        if (username.length() < 3) {
            errorLabel.setText("Username must be at least 3 characters.");
            return;
        }
        if (!email.contains("@")) {
            errorLabel.setText("Enter a valid email address.");
            return;
        }
        if (password.length() < 6) {
            errorLabel.setText("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            confirmPasswordField.clear();
            return;
        }

        registerBtn.setDisable(true);
        registerBtn.setText("Creating account…");

        Optional<User> result = userDAO.register(username, email, password);

        registerBtn.setDisable(false);
        registerBtn.setText("Create Account");

        if (result.isPresent()) {
            DashboardController dashboard =
                    SceneManager.switchScene("dashboard.fxml", "Dashboard");
            dashboard.initUser(result.get());
        } else {
            errorLabel.setText("Username or email already taken. Try a different one.");
        }
    }

    @FXML
    private void goToLogin() {
        SceneManager.switchScene("login.fxml", "Login");
    }
}