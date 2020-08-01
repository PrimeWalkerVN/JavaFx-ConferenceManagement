package javafx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.entities.AttendConferencesEntity;
import javafx.entities.ConferencesEntity;
import javafx.entities.UsersEntity;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.models.AttendingModel;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.ResourceBundle;

public class ConfApprovalController implements Initializable {

    private ConferencesEntity conf;
    private final ObservableList<AttendConferencesEntity> dataList = FXCollections.observableArrayList();
    public ConfApprovalController(ConferencesEntity conf){
        this.conf = conf;
    }

    @FXML
    private Text confnameField;

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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confnameField.setText(conf.getName());
        userIdCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,Integer>("userId"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("userName"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("userFullName"));
        timeResCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,Time>("timeRegister"));
        statusCol.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("approvedString"));
        setTableFactory();
        resetDataList();
    }

    private void setTableFactory(){
        usersTable.setRowFactory(new Callback<TableView<AttendConferencesEntity>, TableRow<AttendConferencesEntity>>() {
            @Override
            public TableRow<AttendConferencesEntity> call(TableView<AttendConferencesEntity> attendConferencesEntityTableView) {
                ContextMenu cm1 = new ContextMenu();
                ContextMenu cm2 = new ContextMenu();
                MenuItem approveCm = new MenuItem("Approve");
                cm1.getItems().add(approveCm);
                MenuItem declineCm = new MenuItem("Decline");
                cm2.getItems().add(declineCm);
                TableRow<AttendConferencesEntity> row = new TableRow<>(){
                    @Override
                    protected void updateItem(AttendConferencesEntity attending, boolean b) {
                        super.updateItem(attending, b);
                        if(b){
                            setContextMenu(null);
                        }else if(attending.isApproved()){
                            setContextMenu(cm2);
                        }else{
                            setContextMenu(cm1);
                        }
                    }
                };
                AttendingModel attendingModel = new AttendingModel();
                approveCm.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        AttendConferencesEntity a = usersTable.getSelectionModel().getSelectedItem();
                        a.setApproved(true);
                        try {
                            attendingModel.update(a);
                            resetDataList();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });
                declineCm.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        AttendConferencesEntity a = usersTable.getSelectionModel().getSelectedItem();
                        a.setApproved(false);
                        try {
                            attendingModel.update(a);
                            resetDataList();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });
                return row;
            }
        });
    }

    public void resetDataList(){
        dataList.clear();
        getDataList();
        usersTable.setItems(dataList);
    }

    public void getDataList(){
        UsersEntity user = FXMLLoaderController.getUser();
        try{
            AttendingModel attendingModel = new AttendingModel();
            List<AttendConferencesEntity> list = attendingModel.getFullFieldByConf(conf.getConferenceId());
            dataList.addAll(list);

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }
}
