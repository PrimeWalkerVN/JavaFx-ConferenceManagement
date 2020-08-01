package javafx.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.entities.ConferencesEntity;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.models.ConferencesModel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.MainApp.loaderFXML;

public class MainController implements Initializable {
    @FXML
    private TextField filterField;


    @FXML
    private TableView<ConferencesEntity> confTable;

    @FXML
    private TableColumn<ConferencesEntity, Integer> confId;

    @FXML
    private TableColumn<ConferencesEntity, String> confName;

    @FXML
    private TableColumn<ConferencesEntity, String> confDesc;

    @FXML
    private TableColumn<ConferencesEntity, Integer> confCapacity;

    @FXML
    private TableColumn<ConferencesEntity, Time> confTime;

    @FXML
    private ComboBox<String> viewDropDown;

    private final ObservableList<ConferencesEntity> dataList = FXCollections.observableArrayList();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewDropDown.getItems().add("Table");
        viewDropDown.getItems().add("List");
        viewDropDown.getSelectionModel().selectFirst();
        confId.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,Integer>("conferenceId"));
        confName.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,String>("name"));
        confDesc.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,String>("shortDescription"));
        confCapacity.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,Integer>("capacity"));
        confTime.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,Time>("dateStartFormat"));
        getDataList();
        confTable.setItems(dataList);
        setTableFactory();

        // Filter
        // 1. Wrap the ObservableList in a FilteredList
        FilteredList<ConferencesEntity> filteredData = new FilteredList<>(dataList, e -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(conference -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (conference.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                if (Integer.toString(conference.getConferenceId()).contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                if (conference.getShortDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }

                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<ConferencesEntity> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(confTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        confTable.setItems(sortedData);

        confTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() > 1){
                    System.out.println("double click");
                    System.out.println(confTable.getSelectionModel().getSelectedItem());
                    confDetailStage(confTable.getSelectionModel().getSelectedItem());
                }
            }
        });

        viewDropDown.setOnAction(actionEvent -> {
            if(viewDropDown.getValue().equals("List")){
                FXMLLoaderController.setMain("main2");
            }
        });
    }

    public static void confDetailStage(ConferencesEntity conf){
        try{
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Detail Conference");
            FXMLLoader loader = loaderFXML("confDetail");
            Scene scene = new Scene(loader.load());
            loader.<ConfDetailController>getController().initData(conf);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void getDataList(){
        try{
            ConferencesModel confModel = new ConferencesModel();
            List<ConferencesEntity> list = confModel.getAll();
            dataList.addAll(list);

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }

    private void setTableFactory(){
        confTable.setRowFactory(new Callback<TableView<ConferencesEntity>, TableRow<ConferencesEntity>>() {
            @Override
            public TableRow<ConferencesEntity> call(TableView<ConferencesEntity> conferencesEntityTableView) {
                return new TableRow<>(){
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
            }
        });
    }
    private Node createDetailsPane(ObjectProperty<ConferencesEntity> item) {
        BorderPane detailsPane = new BorderPane();
        detailsPane.setStyle("-fx-background-color: #0090ff");
        Label nameLabel = new Label();
        Label shortDescLabel = new Label();
        VBox labels = new VBox(5, nameLabel, shortDescLabel);
        labels.setAlignment(Pos.CENTER_LEFT);
        labels.setPadding(new Insets(2, 2, 2, 16));
        detailsPane.setCenter(labels);

        item.addListener((obs, oldItem, newItem) -> {
            if (newItem == null) {
                nameLabel.setText("");
            } else {
                InputStream is=new ByteArrayInputStream(item.getValue().getImage());
                Image image = new Image(is);
                ImageView imageView = new ImageView(image);
                BorderPane.setMargin(imageView, new Insets(6));
                imageView.setFitWidth(100);
                imageView.setFitHeight(60);
                detailsPane.setLeft(imageView);
                nameLabel.setText("Ending: "+newItem.getDateEndFormat());
                shortDescLabel.setText("Detail: "+newItem.getDetailDescription());
            }
        });

        return detailsPane ;
    }

}
