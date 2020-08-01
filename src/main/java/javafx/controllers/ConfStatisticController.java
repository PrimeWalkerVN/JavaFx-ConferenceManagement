package javafx.controllers;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.entities.AttendConferencesEntity;
import javafx.entities.ConferencesEntity;
import javafx.entities.UsersEntity;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.models.AttendingModel;
import javafx.models.ConferencesModel;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import tornadofx.control.DateTimePicker;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ConfStatisticController implements Initializable {

    @FXML
    private DateTimePicker startDate;

    @FXML
    private DateTimePicker endDate;

    @FXML
    private TextField filterField;

    @FXML
    private ComboBox<String> aComboFilter;



    @FXML
    private TableView<AttendConferencesEntity> attendingTable;


    @FXML
    private TableColumn<AttendConferencesEntity, Integer> aId;

    @FXML
    private TableColumn<AttendConferencesEntity, Integer> aConfId;

    @FXML
    private TableColumn<AttendConferencesEntity, String> aConfName;

    @FXML
    private TableColumn<AttendConferencesEntity, String> aConfPlace;

    @FXML
    private TableColumn<AttendConferencesEntity, Time> aConfTime;

    @FXML
    private TableColumn<AttendConferencesEntity, String> aConfStatus;

    private final ObservableList<AttendConferencesEntity> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        aComboFilter.getItems().add("Conf Name");
        aComboFilter.getItems().add("Conf ID");
        aComboFilter.getSelectionModel().selectFirst();
        aId.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,Integer>("attendId"));
        aConfId.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,Integer>("conferenceId"));
        aConfName.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("nameConf"));
        aConfPlace.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("namePlace"));
        aConfStatus.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity,String>("approvedString"));
        aConfTime.setCellValueFactory(new PropertyValueFactory<AttendConferencesEntity, Time>("timeRegister"));
        resetDataList();


        ContextMenu cm = new ContextMenu();
        MenuItem cancelCm = new MenuItem("Cancel");
        cm.getItems().add(cancelCm);
        attendingTable.setContextMenu(cm);
        attendingTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() > 1){
                    System.out.println("double click");
                    openConf(attendingTable.getSelectionModel().getSelectedItem().getConferenceId());
                }

            }
        });

        cancelCm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("cancel");
                cancelAttending(attendingTable.getSelectionModel().getSelectedItem().getAttendId());
            }
        });

    }

    public void getDataList(){
        UsersEntity user = FXMLLoaderController.getUser();
        try{
            AttendingModel attendingModel = new AttendingModel();
            List<AttendConferencesEntity> list = attendingModel.getFullFieldByUser(user.getUserId());
            dataList.addAll(list);

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }


    void cancelAttending(int id){
        AttendingModel attendingModel = new AttendingModel();
        ConferencesModel conferencesModel = new ConferencesModel();
        try {
            AttendConferencesEntity attending = attendingModel.getByAttendId(id);
            ConferencesEntity conf = conferencesModel.getById(attending.getConferenceId());
            Date now = new Date();
            Timestamp nowTs = new Timestamp(now.getTime());
            Timestamp confStart = conf.getTimeStart();

            if(nowTs.compareTo(confStart)<0){
                attendingModel.delete(attending);
                showAlertOk("Cancel attending", "Cancel success!");
                resetDataList();
            }else {
                showAlertError("Can't cancel attending", "The conference was happen!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void resetDataList(){
        dataList.clear();
        getDataList();
        attendingTable.setItems(dataList);
        filterData();
    }

    void filterData(){
        // Filter
        // 1. Wrap the ObservableList in a FilteredList
        FilteredList<AttendConferencesEntity> filteredData = new FilteredList<>(dataList, e -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(attending -> {

                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if(aComboFilter.getValue()== "Conf Name"){
                    if (attending.getNameConf().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                }else {
                    if (Integer.toString(attending.getConferenceId()).contains(lowerCaseFilter)) {
                        return true;
                    }
                }

                return false; // Does not match.
            });
        });

        startDate.valueProperty().addListener((ov, oldValue, newValue) -> {
            filteredData.setPredicate(attending -> {
                if(newValue!=null){
                    LocalDate temp = attending.getTimeStart().toLocalDateTime().toLocalDate();
                    if(temp.compareTo(newValue) >= 0){
                        return true;
                    }
                    return false;
                }
                return true;
            });
        });
        endDate.valueProperty().addListener((ov, oldValue, newValue) -> {
            filteredData.setPredicate(attending -> {
                if(newValue!=null){
                    LocalDate temp = attending.getTimeEnd().toLocalDateTime().toLocalDate();
                    if(temp.compareTo(newValue) <= 0){
                        return true;
                    }
                    return false;
                }
                return true;
            });
        });


        // 3. Wrap the FilteredList in a SortedList.
        SortedList<AttendConferencesEntity> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(attendingTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        attendingTable.setItems(sortedData);
    }
    void openConf(int id){
        ConferencesModel conferencesModel = new ConferencesModel();
        try {
            ConferencesEntity conf = conferencesModel.getById(id);
            MainController.confDetailStage(conf);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    void showAlertOk(String title, String header){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    void showAlertError(String title, String header){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}
