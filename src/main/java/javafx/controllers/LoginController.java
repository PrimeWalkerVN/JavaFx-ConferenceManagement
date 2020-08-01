package javafx.controllers;

import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.models.AdminsModel;
import javafx.models.UsersModel;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static javafx.MainApp.loadFXML;
import static javafx.controllers.FXMLLoaderController.setVisibleAdmin;
import static javafx.controllers.FXMLLoaderController.setVisibleUser;

public class LoginController implements Initializable {


    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField pwdField;

    @FXML
    private Button submitButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private JFXComboBox<String> roleComboBox;


    private static Scene scene;
    private static Stage stage;
    @FXML
    private void switchToRegister() throws IOException {
        setRootLogin("register");
    }

    @FXML
    void loginAction(ActionEvent event) throws SQLException, IOException {
        Window window = scene.getWindow();
        if(checkInput(window)){
            String username = userNameField.getText().trim();
            String password = DigestUtils.sha1Hex((pwdField.getText().trim()));
            switch (roleComboBox.getValue()) {
                case "User":
                    UsersModel model = new UsersModel();
                    if(model.checkUser(username)){
                        if (model.checkPassword(username,password)) {
                            if(model.checkBanned(model.getByUsername(username).getUserId())){
                                FXMLLoaderController.setUser(model.getByUsername(username),false);
                                setVisibleUser();
                                ConfDetailController.checkApprovedConf();
                                stage.close();
                            }else{
                                showAlert(Alert.AlertType.ERROR, window, "Banned!", "Your account has been banned!");
                                return;
                            }

                        } else{
                            showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                                    "Username or password wrong!");
                            return;
                        }
                    }else {
                        showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                                "Username not exist in role user!");
                        return;
                    }
                    return;
                case "Admin":
                    AdminsModel model2 = new AdminsModel();
                    if(model2.checkAdmin(username)){
                        if (model2.checkPassword(username,password)) {
                            FXMLLoaderController.setAdmin(model2.getByUsername(username),true);
                            setVisibleAdmin();
                            stage.close();
                        } else{
                            showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                                    "Username or password wrong!");
                            return;
                        }
                    }else {
                        showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                                "Username not exist in role admin!");
                        return;
                    }
            }
        }
    }


    public static void setRootLogin(String fxml) throws IOException{
        scene.setRoot(loadFXML(fxml));
    }


    public void initData(Scene scene, Stage stage){
        LoginController.scene = scene;
        LoginController.stage = stage;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        roleComboBox.getItems().removeAll(roleComboBox.getItems());
        roleComboBox.getItems().addAll("User","Admin");
        roleComboBox.getSelectionModel().select("User");
    }

    private boolean checkInput(Window window){

        if ( userNameField.getText().isEmpty() || pwdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, window, "Form Error!",
                    "Please enter all field");
            return false;
        }
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
