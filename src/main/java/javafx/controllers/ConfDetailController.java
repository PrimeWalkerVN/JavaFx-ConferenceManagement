package javafx.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.MainApp;
import javafx.collections.ObservableList;
import javafx.entities.AttendConferencesEntity;
import javafx.entities.ConferencesEntity;
import javafx.entities.PlacesEntity;
import javafx.entities.UsersEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.models.AttendingModel;
import javafx.models.ConferencesModel;
import javafx.models.PlacesModel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import static javafx.MainApp.loadFXML;
import static javafx.MainApp.loaderFXML;

public class ConfDetailController implements Initializable {

    @FXML
    private StackPane stackPaneId;

    @FXML
    private ImageView imageViewId;

    @FXML
    private Text confId;

    @FXML
    private Text confname;

    @FXML
    private Text confTimeStart;

    @FXML
    private Text placeName;

    @FXML
    private Text placeCapacity;

    @FXML
    private JFXButton attendButton;

    @FXML
    private Text confDetailDesc;

    @FXML
    private Text confTimeEnd;

    @FXML
    private JFXButton listAttendingButotn;


    @FXML
    void listAttendingAction(ActionEvent event) {
        ObservableList<Node> childs = stackPaneId.getChildren();
        if(childs.size()>1) childs.remove(0);
        childs.get(childs.size()-1).setVisible(false);
        System.out.println(childs);
        ListAttendingController controller = new ListAttendingController(conference);
        try {
            FXMLLoader loader = MainApp.loaderFXML("listAttending");
            loader.setController(controller);
            stackPaneId.getChildren().add(loader.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void changeTop() {
        ObservableList<Node> childs = stackPaneStatic.getChildren();
        if (childs.size() > 1) {
            Node topNode = childs.get(childs.size()-1);
            Node newTopNode = childs.get(childs.size()-2);
            topNode.setVisible(false);
            topNode.toBack();
            newTopNode.setVisible(true);
        }
    }


    private static ConferencesEntity conference;
    private static boolean isAdmin;
    private static UsersEntity user;
    private static PlacesEntity place;
    private static JFXButton attendButtonStatic;
    private static StackPane stackPaneStatic;
    public static void setConference(ConferencesEntity conference) {
        ConfDetailController.conference = conference;
    }

    public static PlacesEntity getPlace() {
        return place;
    }

    public static void setPlace(PlacesEntity place) {
        ConfDetailController.place = place;
    }

    void initData(ConferencesEntity conf){
        setConference(conf);
        PlacesModel placesModel = new PlacesModel();
        try{
            PlacesEntity place = placesModel.getById(conf.getPlaceId());
            setPlace(place);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        setInitData();
        validateConf();

    }
    public void validateConf(){

        if(FXMLLoaderController.getAdminRole()){
            attendButtonStatic.setDisable(true);
        }
        checkApprovedConf();
        Date now = new Date();
        Timestamp nowTs = new Timestamp(now.getTime());
        Timestamp confEndTs = conference.getTimeEnd();
        if(nowTs.compareTo(confEndTs)>0){
            attendButtonStatic.setDisable(true);
            attendButtonStatic.setText("Expired");
        }
        ConferencesModel conferencesModel = new ConferencesModel();
        try {
            if(conferencesModel.checkFull(conference)){
                attendButtonStatic.setDisable(true);
                attendButtonStatic.setText("Full");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void checkApprovedConf(){
        isAdmin = FXMLLoaderController.getAdminRole();
        user = FXMLLoaderController.getUser();
        if(user==null || conference==null) return;
        if(attendButtonStatic!=null) attendButtonStatic.setDisable(isAdmin);
        if(!isAdmin && FXMLLoaderController.isLogged()){
            AttendingModel attendingModel = new AttendingModel();
            try {
                if(attendingModel.checkAttending(user.getUserId(),conference.getConferenceId())){
                    attendButtonStatic.setDisable(true);
                    AttendConferencesEntity attending = attendingModel.getByUserAndConfId(user.getUserId(),conference.getConferenceId());
                    if(attending.isApproved()){
                        attendButtonStatic.setText("Approved");
                    }else {
                        attendButtonStatic.setText("Not be Approved");
                    }
                    attendButtonStatic.setDisable(true);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    void setInitData(){
        InputStream is=new ByteArrayInputStream(conference.getImage());
        imageViewId.setImage(new Image(is,320,220,false,true));
        confId.setText(String.valueOf(conference.getConferenceId()));
        confname.setText(conference.getName());
        confDetailDesc.setText(conference.getDetailDescription());
        confTimeStart.setText(timestampAsString(conference.getTimeStart()));
        confTimeEnd.setText(timestampAsString(conference.getTimeEnd()));
        placeName.setText(place.getName());
        placeCapacity.setText(String.valueOf(place.getCapacity()));
        attendButtonStatic = attendButton;
    }

    public static String timestampAsString(Timestamp timestamp) {
        Date date = new Date();
        date.setTime(timestamp.getTime());
        return new SimpleDateFormat("yyyy-MM-dd h:mm a").format(date);
    }

    @FXML
    void attendAction(ActionEvent event) throws SQLException {
        if(!FXMLLoaderController.isLogged()){
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
        }else {
            user = FXMLLoaderController.getUser();
            isAdmin = FXMLLoaderController.getAdminRole();
            if(!isAdmin){
               addAttendingConf();
            }else {
                attendButton.setDisable(true);
            }

        }

    }

    public void addAttendingConf() throws SQLException{
        AttendConferencesEntity newAttending = new AttendConferencesEntity();
        newAttending.setConferenceId(conference.getConferenceId());
        newAttending.setUserId(user.getUserId());
        newAttending.setApproved(false);
        Date now = new Date();
        Timestamp ts = new Timestamp(now.getTime());
        newAttending.setTimeRegister(ts);
        AttendingModel attendingModel = new AttendingModel();
        attendingModel.add(newAttending);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Attend conference " + conference.getName());
        alert.setHeaderText("Attend success!");
        alert.showAndWait();
        checkApprovedConf();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        attendButtonStatic = attendButton;
        stackPaneStatic = stackPaneId;
    }
}
