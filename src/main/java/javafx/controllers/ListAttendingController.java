package javafx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.entities.AttendConferencesEntity;
import javafx.entities.ConferencesEntity;
import javafx.entities.UsersEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.models.AttendingModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.ResourceBundle;

public class ListAttendingController implements Initializable {


    @FXML
    private Text ammountId;

    @FXML
    void backAction(ActionEvent event) {
        ConfDetailController.changeTop();
    }
    @FXML
    private TableView<AttendConferencesEntity> usersTable;

    @FXML
    private TableColumn<AttendConferencesEntity, Integer> userIdCol;

    @FXML
    private TableColumn<AttendConferencesEntity, String> userNameCol;

    @FXML
    private TableColumn<AttendConferencesEntity, String> fullNameCol;

    @FXML
    private TableColumn<AttendConferencesEntity, Time> timeResCol;

    @FXML
    private TableColumn<AttendConferencesEntity, String> statusCol;


    private ConferencesEntity conf;
    private final ObservableList<AttendConferencesEntity> dataList = FXCollections.observableArrayList();


    public ListAttendingController(ConferencesEntity conf){
        this.conf = conf;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userIdCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,Integer>("userId"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("userName"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("userFullName"));
        timeResCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,Time>("timeRegister"));
        statusCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("approvedString"));
        getDataList();
        usersTable.setItems(dataList);

    }

    public void getDataList(){
        UsersEntity user = FXMLLoaderController.getUser();
        try{
            AttendingModel attendingModel = new AttendingModel();
            List<AttendConferencesEntity> list = attendingModel.getFullFieldByConf(conf.getConferenceId());
            dataList.addAll(list);
            ammountId.setText(String.valueOf(dataList.size()));

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }



}
