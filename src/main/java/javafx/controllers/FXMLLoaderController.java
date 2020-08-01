package javafx.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.entities.AdminsEntity;
import javafx.entities.UsersEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javafx.MainApp.*;

public class FXMLLoaderController implements Initializable {

    private Pane view;

    private static boolean adminRole = false;
    private static UsersEntity user;
    private static AdminsEntity admin;
    private static boolean logged = false;
    private static BorderPane mainPaneStatic;


    @FXML
    private BorderPane mainPane;

    @FXML
    private Text labelAccount;

    @FXML
    private JFXButton mainScreenButton;

    @FXML
    private JFXButton profileButton;

    @FXML
    private JFXButton confMgmtButton;

    @FXML
    private JFXButton userMgmtButton;

    @FXML
    private JFXButton confStatisticButton;

    @FXML
    private JFXButton placesMgmtButton;

    @FXML
    private JFXButton logoutButton;

    @FXML
    private JFXButton loginButton;

    @FXML
    void helpAction(ActionEvent event) {
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.NONE);
        stage.setTitle("Detail Conference");
        FXMLLoader loader = null;
        try {
            loader = loaderFXML("helpScreen");
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void confMgmtAction(ActionEvent event) {
        Pane view = getPage("confMgmt");
        mainPane.setRight(view);
    }

    @FXML
    void confStatisticAction(ActionEvent event) {
        Pane view = getPage("confStatistic");
        mainPane.setRight(view);
    }

    @FXML
    void mainAction(ActionEvent event) {
        Pane view = getPage("main2");
        mainPane.setRight(view);
    }

    @FXML
    void profileAction(ActionEvent event) {
        Pane view = getPage("profile");
        mainPane.setRight(view);
    }

    @FXML
    void userMgmtAction(ActionEvent event) {
        Pane view = getPage("userMgmt");
        mainPane.setRight(view);
    }

    @FXML
    void placesMgmtAction(ActionEvent event) {
        Pane view = getPage("placesMgmt");
        mainPane.setRight(view);
    }

    @FXML
    void logoutAction(ActionEvent event) {
        Pane view = getPage("main2");
        mainPane.setRight(view);
        logoutStaticButton.setVisible(false);
        unSetVisible();
        logged = false;
    }


    @FXML
    void loginButtonAction(ActionEvent event) {
        try{
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Login");
            FXMLLoader loader = loaderFXML("login");
            Scene scene = new Scene(loader.load());
            loader.<LoginController>getController().initData(scene,stage);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    public static void setMain(String name){
        Pane view = getPage(name);
        mainPaneStatic.setRight(view);
    }

    private static Text labelAccountStatic;

    private static  JFXButton mainStaticButton;

    private static JFXButton profileStaticButton;

    private static JFXButton confMgmtStaticButton;

    private static JFXButton userMgmtStaticButton;

    private static JFXButton confStatisticStaticButton;

    private static JFXButton logoutStaticButton;

    private static JFXButton loginStaticButton;

    private static JFXButton placesMgmtStaticButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Pane view = getPage("main2");
        mainPane.setRight(view);
        mainPaneStatic = mainPane;

        confMgmtStaticButton = confMgmtButton;
        userMgmtStaticButton = userMgmtButton;
        profileStaticButton = profileButton;
        confStatisticStaticButton = confStatisticButton;
        labelAccountStatic = labelAccount;
        logoutStaticButton = logoutButton;
        loginStaticButton = loginButton;
        placesMgmtStaticButton = placesMgmtButton;

    }



    public static Pane getPage(String fileName){
        Pane view = null;
        try {
            view = (Pane) loadFXML(fileName);

        }catch (IOException ex){
            System.out.println(ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("An error has occurred!");
            alert.setHeaderText("Database Connection Error!");
            alert.setContentText("Please contact the developer\n" + ex.toString());
            alert.showAndWait();
        }
        return view;
    }

    public static UsersEntity getUser(){
        return user;
    }

    public static void setUser(UsersEntity usersEntity, boolean setAdminRole){
        user = usersEntity;
        admin = null;
        adminRole = setAdminRole;
    }

    public static boolean getAdminRole(){return adminRole;}
    public static boolean isLogged(){return logged;}

    public static AdminsEntity getAdmin(){
        return admin;
    }

    public static void setAdmin(AdminsEntity adminsEntity, boolean setAdminRole){
        admin = adminsEntity;
        user = null;
        adminRole = setAdminRole;
    }






    public static void setVisibleAdmin(){
        confStatisticStaticButton.setVisible(false);
        profileStaticButton.setVisible(true);
        userMgmtStaticButton.setVisible(true);
        confMgmtStaticButton.setVisible(true);
        logoutStaticButton.setVisible(true);
        labelAccountStatic.setVisible(true);
        placesMgmtStaticButton.setVisible(true);
        labelAccountStatic.setText("Hello, " + admin.getName());
        loginStaticButton.setVisible(false);
        logged = true;
    }

    public static void setVisibleUser(){
        profileStaticButton.setVisible(true);
        confStatisticStaticButton.setVisible(true);
        logoutStaticButton.setVisible(true);
        labelAccountStatic.setVisible(true);
        labelAccountStatic.setText("Hello, " + user.getName());
        loginStaticButton.setVisible(false);
        logged = true;
    }

    public static void unSetVisible(){
        labelAccountStatic.setVisible(false);
        confStatisticStaticButton.setVisible(false);
        profileStaticButton.setVisible(false);
        userMgmtStaticButton.setVisible(false);
        confMgmtStaticButton.setVisible(false);
        placesMgmtStaticButton.setVisible(false);
        logoutStaticButton.setVisible(false);
        loginStaticButton.setVisible(true);
        user = null;
        admin = null;
        logged = false;
    }


}
