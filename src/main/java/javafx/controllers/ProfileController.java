package javafx.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.entities.AdminsEntity;
import javafx.entities.UsersEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.models.AdminsModel;
import javafx.models.UsersModel;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfileController implements Initializable {
    @FXML
    private JFXButton saveInfoButton;

    @FXML
    private JFXTextField emailField;

    @FXML
    private JFXTextField fullNameField;

    @FXML
    private Text userNameField;

    @FXML
    void changePwdAction(ActionEvent event) {
        changePwd(isAdmin);
    }

    @FXML
    void saveInfoAction(ActionEvent event) throws SQLException {
        if(checkChangeField()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm change");
            alert.setHeaderText("Do you want to change your profile?");
            Optional<ButtonType> option = alert.showAndWait();
            if(option.get() == ButtonType.OK) {
                if (isAdmin) {
                    AdminsModel modelAdmin = new AdminsModel();
                    admin.setName(fullNameField.getText());
                    admin.setEmail(emailField.getText());
                    modelAdmin.update(admin);
                }else {
                    UsersModel modelUser = new UsersModel();
                    user.setName(fullNameField.getText());
                    user.setEmail(emailField.getText());
                    modelUser.update(user);
                }
            }
        }
    }

    private static UsersEntity user;
    private static AdminsEntity admin;
    private static boolean isAdmin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(FXMLLoaderController.getAdminRole()){
            if(FXMLLoaderController.getAdmin()!=null){
                admin = FXMLLoaderController.getAdmin();
                userNameField.setText(admin.getUserName());
                fullNameField.setText(admin.getName());
                emailField.setText(admin.getEmail());
            }

            isAdmin = true;
        }else {
            if(FXMLLoaderController.getUser()!=null){
                user = FXMLLoaderController.getUser();
                userNameField.setText(user.getUserName());
                fullNameField.setText(user.getName());
                emailField.setText(user.getEmail());
            }
            isAdmin = false;
        }
    }

    private boolean checkChangeField(){
        if(isAdmin){
            return !fullNameField.getText().equals(admin.getName()) || !emailField.getText().equals(admin.getEmail());
        }else {
            return !fullNameField.getText().equals(user.getName()) || !emailField.getText().equals(user.getEmail());
        }
    }

    private void changePwd( boolean isAdmin){
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Change password");
        dialog.setHeaderText("You can change password if old password is correct and new not empty!");
        // Set the button types.
        ButtonType changePwdButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changePwdButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        PasswordField password1 = new PasswordField();
        password1.setPromptText("Old Password");
        PasswordField password2 = new PasswordField();
        password2.setPromptText("New Password");
        grid.add(new Label("Old password:"), 0, 0);
        grid.add(password1, 1, 0);
        grid.add(new Label("New password:"), 0, 1);
        grid.add(password2, 1, 1);

        Node changePwdButton = dialog.getDialogPane().lookupButton(changePwdButtonType);
        changePwdButton.setDisable(true);

        password1.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean check = false;
            if(isAdmin){
                AdminsModel modelAdmin = new AdminsModel();
                try {
                    String password = DigestUtils.sha1Hex((newValue.trim()));
                    check = modelAdmin.checkPassword(admin.getUserName(), password);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }else {
                UsersModel usersModel = new UsersModel();
                try {
                    String password = DigestUtils.sha1Hex((newValue.trim()));
                    check = usersModel.checkPassword(user.getUserName(), password);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            changePwdButton.setDisable(!check);
        });

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> password1.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changePwdButtonType) {
                return new Pair<>(password1.getText(), password2.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(Password -> {

            if(Password.getValue().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Change Password");
                alert.setHeaderText("Error. Input new password!");
                alert.showAndWait();
                return;
            }
            String password = DigestUtils.sha1Hex(Password.getValue());
            System.out.println("Old=" + Password.getKey() + ", New=" + Password.getValue());
            if(isAdmin){
                AdminsModel modelAdmin = new AdminsModel();
                admin.setPassword(password);
                try {
                    modelAdmin.update(admin);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }else {
                UsersModel usersModel = new UsersModel();
                user.setPassword(password);
                try {
                    usersModel.update(user);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Change Password");
            alert.setHeaderText("Change password success!");
            alert.showAndWait();
        });

    }

}
