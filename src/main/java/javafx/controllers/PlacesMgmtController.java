package javafx.controllers;

import javafx.MainApp;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.entities.PlacesEntity;
import javafx.entities.UsersEntity;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.models.PlacesModel;
import javafx.models.UsersModel;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class PlacesMgmtController implements Initializable {


    @FXML
    private AnchorPane placesMgmtPane;
    @FXML
    private TextField searchField;

    @FXML
    private TableView<PlacesEntity> placesTable;

    @FXML
    private TableColumn<PlacesEntity, Integer> placeIdCol;

    @FXML
    private TableColumn<PlacesEntity, String> nameCol;

    @FXML
    private TableColumn<PlacesEntity, String> locationCol;

    @FXML
    private TableColumn<PlacesEntity, Integer> capacityCol;

    @FXML
    void addNewAction(ActionEvent event) {
        Dialog dialog = new Dialog();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(placesMgmtPane.getScene().getWindow());
        dialog.setWidth(600);
        dialog.setHeight(300);
        try {
            dialog.getDialogPane().setContent(MainApp.loaderFXML("placeAddition").load());
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(e -> window.hide());
            dialog.showAndWait();
            resetDataList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final ObservableList<PlacesEntity> dataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ContextMenu cm = new ContextMenu();
        MenuItem deleteCm = new MenuItem("Delete");
        cm.getItems().addAll(deleteCm);
        placesTable.setContextMenu(cm);
        placeIdCol.setCellValueFactory(new PropertyValueFactory<PlacesEntity,Integer>("placeId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<PlacesEntity,String>("name"));
        locationCol.setCellValueFactory(new PropertyValueFactory<PlacesEntity,String>("address"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<PlacesEntity,Integer>("capacity"));
        resetDataList();

        deleteCm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                PlacesModel placesModel = new PlacesModel();
                try {
                    placesModel.delete(placesTable.getSelectionModel().getSelectedItem());
                    PlaceAdditionController.showAlert(Alert.AlertType.INFORMATION,placesMgmtPane.getScene().getWindow(),"Delete place","Remove success!");
                    resetDataList();
                } catch (SQLException throwables) {
                    PlaceAdditionController.showAlert(Alert.AlertType.ERROR,placesMgmtPane.getScene().getWindow(),"Delete place","Remove error!");
                    throwables.printStackTrace();
                }
            }
        });
    }

    private void resetDataList(){
        dataList.clear();
        getDataList();
        placesTable.setItems(dataList);
        filterData();
    }

    private void getDataList(){
        try{
            PlacesModel placesModel = new PlacesModel();
            List<PlacesEntity> list = placesModel.getAll();
            dataList.addAll(list);

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }
    private void filterData(){
        FilteredList<PlacesEntity> filteredData = new FilteredList<>(dataList, e -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(place -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();


                if (place.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                if (place.getAddress().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });
        SortedList<PlacesEntity> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(placesTable.comparatorProperty());
        placesTable.setItems(sortedData);
    }

}
