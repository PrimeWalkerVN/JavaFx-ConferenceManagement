package javafx.controllers;

import com.jfoenix.controls.JFXComboBox;
import javafx.MainApp;
import javafx.entities.AdminsEntity;
import javafx.entities.UsersEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.models.AdminsModel;
import javafx.models.UsersModel;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static javafx.controllers.LoginController.setRootLogin;

public class RegisterController implements Initializable {

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField pwdField;

    @FXML
    private PasswordField confirmPwdField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField fullNameField;

    @FXML
    private JFXComboBox<String> roleComboBox;

    @FXML
    private Button submitButton;

    @FXML
    private void switchToLogin() throws IOException {
        setRootLogin("login");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleComboBox.getItems().removeAll(roleComboBox.getItems());
        roleComboBox.getItems().addAll("User","Admin");
        roleComboBox.getSelectionModel().select("User");
        roleComboBox.setVisible(false);




    }

    @FXML
    void submitAction(ActionEvent event) throws SQLException, IOException {

        Window window = submitButton.getScene().getWindow();
        if(checkInput(window)){
            String username = userNameField.getText().trim();
            String password = DigestUtils.sha1Hex((pwdField.getText().trim()));
            switch (roleComboBox.getValue()) {
                case "User":
                    UsersModel model = new UsersModel();
                    if (!model.checkUser(username)) {
                        UsersEntity user = new UsersEntity();
                        user.setUserName(username);
                        user.setPassword(password);
                        user.setEmail(emailField.getText());
                        user.setName(fullNameField.getText());
                        user.setStatus(true);
                        model.add(user);
                        showAlert(Alert.AlertType.INFORMATION, window, "Registration Successful!",
                                "Welcome user " + fullNameField.getText());
                        setRootLogin("login");
                    } else {
                        showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                                "Username user has already been taken!");
                        return;
                    }
                    return;
                case "Admin":
                    AdminsModel model2 = new AdminsModel();
                    if (!model2.checkAdmin(username)) {
                        AdminsEntity admin = new AdminsEntity();
                        admin.setUserName(username);
                        admin.setPassword(password);
                        admin.setEmail(emailField.getText());
                        admin.setName(fullNameField.getText());
                        model2.add(admin);

                        showAlert(Alert.AlertType.INFORMATION, window, "Registration Successful!",
                                "Welcome admin " + fullNameField.getText());
                        setRootLogin("login");
                    } else {
                        showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                                "Username admin has already been taken!");
                        return;
                    }
            }
        }
        return;
    }

    private boolean checkInput(Window window){

        if (fullNameField.getText().isEmpty() || userNameField.getText().isEmpty() ||
                emailField.getText().isEmpty() || pwdField.getText().isEmpty() ||
                confirmPwdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                    "Please enter all field");
            return false;
        }
        if(!pwdField.getText().equals(confirmPwdField.getText())) {
            showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                    "Password and Confirm Password must be matched!");
            return false;
        }

        if(!emailField.getText().contains("@")) {
            showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                    "Invalid email!");
            return false;
        }else
            return true;
    }


    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}
