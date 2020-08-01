package javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.utils.HibernateUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * JavaFX App
 */
public class MainApp extends Application {

    private static Scene scene;


    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Conferences Management");
        scene = new Scene(loadFXML("FXMLLoader"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));

    }


    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static FXMLLoader loaderFXML(String fxml) throws IOException {
        return new FXMLLoader(MainApp.class.getResource(fxml + ".fxml"));
    }

    public static void main(String[] args) {
        try {
            HibernateUtil.buildSessionFactory();
            launch(args);
            HibernateUtil.getSessionFactory().close();

        } catch (Throwable ex ) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("An error has occurred!");
                alert.setHeaderText("Database Connection Error!");
                alert.setContentText("Please contact the developer\n" + ex.toString());
                alert.showAndWait();
                Platform.exit();
            });
            System.out.println(ex + "abc");
        }
    }


}