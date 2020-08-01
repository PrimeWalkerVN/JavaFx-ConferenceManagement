package javafx.controllers;

import javafx.beans.property.ObjectProperty;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.models.AttendingModel;
import javafx.models.ConferencesModel;
import javafx.models.UsersModel;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class UserMgmtController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> comboFilter;

    @FXML
    private TableView<UsersEntity> usersTable;

    @FXML
    private TableColumn<UsersEntity, Integer> userIdCol;

    @FXML
    private TableColumn<UsersEntity, String> userNameCol;

    @FXML
    private TableColumn<UsersEntity, String> fullNameCol;

    @FXML
    private TableColumn<UsersEntity, String> emailCol;

    @FXML
    private TableColumn<UsersEntity, String> statusCol;


    private final ObservableList<UsersEntity> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboFilter.getItems().add("Username");
        comboFilter.getItems().add("User ID");
        comboFilter.getItems().add("FullName");
        comboFilter.getSelectionModel().selectFirst();
        userIdCol.setCellValueFactory(new PropertyValueFactory<UsersEntity,Integer>("userId"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<UsersEntity,String>("userName"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<UsersEntity,String>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<UsersEntity, String>("email"));
        statusCol.setCellValueFactory(new PropertyValueFactory<UsersEntity, String>("statusString"));
        resetDataList();
        setTableFactory();

    }

    private void setTableFactory(){
        usersTable.setRowFactory(new Callback<TableView<UsersEntity>, TableRow<UsersEntity>>() {
            @Override
            public TableRow<UsersEntity> call(TableView<UsersEntity> attendConferencesEntityTableView) {
                ContextMenu cm1 = new ContextMenu();
                ContextMenu cm2 = new ContextMenu();
                MenuItem activeCm = new MenuItem("Active");
                cm1.getItems().add(activeCm);
                MenuItem banCm = new MenuItem("Ban");
                cm2.getItems().add(banCm);
                TableRow<UsersEntity> row = new TableRow<>(){
                    @Override
                    protected void updateItem(UsersEntity user, boolean b) {
                        super.updateItem(user, b);
                        if(b){
                            setContextMenu(null);
                        }else if(user.getStatus()){
                            setContextMenu(cm2);
                        }else{
                            setContextMenu(cm1);
                        }
                    }

                    Node detailsPane ;
                    {
                        selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                            if (isNowSelected) {
                                getChildren().add(detailsPane);
                            } else {
                                getChildren().remove(detailsPane);
                            }
                            this.requestLayout();
                        });
                        detailsPane = createDetailsPane(itemProperty());
                    }

                    @Override
                    protected double computePrefHeight(double width) {
                        if (isSelected()) {
                            return super.computePrefHeight(width)+detailsPane.prefHeight(getWidth());
                        } else {
                            return super.computePrefHeight(width);
                        }
                    }

                    @Override
                    protected void layoutChildren() {
                        super.layoutChildren();
                        if (isSelected()) {
                            double width = getWidth();
                            double paneHeight = detailsPane.prefHeight(width);
                            detailsPane.resizeRelocate(0, getHeight()-paneHeight, width, paneHeight);
                        }
                    }
                };

                UsersModel usersModel = new UsersModel();
                activeCm.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        UsersEntity a = usersTable.getSelectionModel().getSelectedItem();
                        a.setStatus(true);
                        try {
                            usersModel.update(a);
                            resetDataList();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                });
                banCm.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        UsersEntity a = usersTable.getSelectionModel().getSelectedItem();
                        a.setStatus(false);
                        try {
                            usersModel.update(a);
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

    private Node createDetailsPane(ObjectProperty<UsersEntity> item) {
        ListView<String> attendList = new ListView<>();
        attendList.setMaxSize(0,0);
        attendList.setStyle("-fx-control-inner-background: #0090ff");
        AnchorPane detailsPane = new AnchorPane(attendList);
        detailsPane.setStyle("-fx-background-color: #0090ff");

        item.addListener((obs, oldItem, newItem) -> {
            if (newItem == null) {
                attendList.setItems(null);
            } else {
                AttendingModel attendingModel = new AttendingModel();
                try {
                    List<AttendConferencesEntity> a = attendingModel.getFullFieldByUser(newItem.getUserId());
                    ObservableList<String> temp = FXCollections.observableArrayList();
                    temp.add("Attending Conferences: ");
                    if(a.size() < 1) return;
                    attendList.setMaxSize(700,100);
                    attendList.setPrefWidth(700);
                    detailsPane.setPrefSize(attendList.getMaxWidth(),attendList.getMaxHeight());
                    for (int i = 0; i < a.size(); i++) {
                        temp.add("Conference: " + a.get(i).getNameConf());
                    }
                    attendList.setItems(temp);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        return detailsPane ;
    }

    private void resetDataList(){
        dataList.clear();
        getDataList();
        usersTable.setItems(dataList);
        filterData();
    }

    private void getDataList(){
        try{
            UsersModel usersModel = new UsersModel();
            List<UsersEntity> list = usersModel.getAll();
            dataList.addAll(list);

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }


    private void filterData(){
        FilteredList<UsersEntity> filteredData = new FilteredList<>(dataList, e -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if(comboFilter.getValue().equals("Username")){
                    if (user.getUserName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches first name.
                    }
                }else if(comboFilter.getValue().equals("User ID")) {
                    if (Integer.toString(user.getUserId()).contains(lowerCaseFilter)) {
                        return true;
                    }
                }else if (user.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        SortedList<UsersEntity> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(usersTable .comparatorProperty());
        usersTable.setItems(sortedData);
    }
}
