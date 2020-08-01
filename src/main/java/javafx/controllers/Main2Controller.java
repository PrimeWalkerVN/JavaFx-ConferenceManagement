package javafx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.entities.ConferencesEntity;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.models.ConfCellFactory;
import javafx.models.ConferencesModel;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class Main2Controller implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> viewDropDown;

    @FXML
    private ListView<ConferencesEntity> confList;

    private final ObservableList<ConferencesEntity> confObservableList;
    public Main2Controller(){
        confObservableList = FXCollections.observableArrayList();
        getDataList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confList.setCellFactory(new ConfCellFactory());
        confList.setItems(confObservableList);
        viewDropDown.getItems().add("List");
        viewDropDown.getItems().add("Table");
        viewDropDown.getSelectionModel().selectFirst();

        confList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() > 1){
                    System.out.println("double click");
                    MainController.confDetailStage(confList.getSelectionModel().getSelectedItem());
                }
            }
        });

        viewDropDown.setOnAction(actionEvent -> {
            if(viewDropDown.getValue().equals("Table")){
                FXMLLoaderController.setMain("main");
            }
        });
        searchData();
    }

    public void getDataList(){
        try{
            ConferencesModel confModel = new ConferencesModel();
            List<ConferencesEntity> list = confModel.getAll();
            confObservableList.addAll(list);

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }

    private void searchData(){
        FilteredList<ConferencesEntity> filteredData = new FilteredList<>(confObservableList, e -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(conference -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (conference.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (Integer.toString(conference.getConferenceId()).contains(lowerCaseFilter)) {
                    return true;
                }
                if (conference.getShortDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false; // Does not match.
            });
        });

        SortedList<ConferencesEntity> sortedData = new SortedList<>(filteredData);
        confList.setItems(sortedData);
    }
}
