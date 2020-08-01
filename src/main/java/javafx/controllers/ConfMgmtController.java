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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.models.AttendingModel;
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
import javafx.stage.Window;
import javafx.util.Callback;
import tornadofx.control.DateTimePicker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.MainApp.loaderFXML;

public class ConfMgmtController implements Initializable {
    @FXML
    private TextField searchField;

    @FXML
    private DateTimePicker startDate;

    @FXML
    private DateTimePicker endDate;

    @FXML
    private ComboBox<String> comboFilter;

    @FXML
    private TableView<ConferencesEntity> confTable;

    @FXML
    private TableColumn<ConferencesEntity, Integer> confIdCol;

    @FXML
    private TableColumn<ConferencesEntity, String> confNameCol;

    @FXML
    private TableColumn<ConferencesEntity, String> confDescCol;

    @FXML
    private TableColumn<ConferencesEntity, Time> confStartCol;

    @FXML
    private TableColumn<ConferencesEntity, Integer> confCapCol;

    @FXML
    void addNewConfAction(ActionEvent event) {
        confAdditionStage();
        resetDataList();
    }


    private final ObservableList<ConferencesEntity> dataList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ContextMenu cm = new ContextMenu();
        MenuItem approvalCm = new MenuItem("Approval");
        MenuItem editCm = new MenuItem("Edit");
        MenuItem deleteCm = new MenuItem("Delete");
        cm.getItems().addAll(approvalCm,editCm,deleteCm);
        confTable.setContextMenu(cm);
        comboFilter.getItems().add("Conf Name");
        comboFilter.getItems().add("Conf ID");
        comboFilter.getItems().add("Conf Desc");
        comboFilter.getSelectionModel().selectFirst();
        confIdCol.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,Integer>("conferenceId"));
        confNameCol.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,String>("name"));
        confDescCol.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,String>("shortDescription"));
        confStartCol.setCellValueFactory(new PropertyValueFactory<ConferencesEntity,Time>("dateStartFormat"));
        confCapCol.setCellValueFactory(new PropertyValueFactory<ConferencesEntity, Integer>("capacity"));
        setTableFactory();
        resetDataList();

        confTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() > 1){
                    openConf(confTable.getSelectionModel().getSelectedItem().getConferenceId());
                }

            }
        });

        deleteCm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteConf(confTable.getSelectionModel().getSelectedItem().getConferenceId());
            }
        });
        editCm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(checkConfNotHappen(confTable.getSelectionModel().getSelectedItem())){
                    confUpdateStage(confTable.getSelectionModel().getSelectedItem());
                    resetDataList();
                }else {
                    showAlert(Alert.AlertType.ERROR,"Error Edit Conference","Can't conference was happen");
                }

            }
        });
        approvalCm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                confApprovalStage(confTable.getSelectionModel().getSelectedItem());
            }
        });
    }



    void deleteConf(int id){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete conference");
        alert.setHeaderText("Delete conference may affect other object.");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            ConferencesModel model = new ConferencesModel();
            try {
                model.delete(id);
                resetDataList();
                showAlert(Alert.AlertType.INFORMATION,"Delete conference","Delete success!");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showAlert(Alert.AlertType.ERROR,"Can't delete", throwables.toString());
            }
        } else {
            alert.close();
        }
    }

    boolean checkConfNotHappen(ConferencesEntity conf){
        Date now = new Date();
        Timestamp nowTs = new Timestamp(now.getTime());
        Timestamp confStart = conf.getTimeStart();
        return nowTs.compareTo(confStart) <= 0;
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
    private void resetDataList(){
        dataList.clear();
        getDataList();
        confTable.setItems(dataList);
        filterData();
    }

    private void getDataList(){

        try{
            ConferencesModel conferencesModel = new ConferencesModel();
            List<ConferencesEntity> list = conferencesModel.getAll();
            dataList.addAll(list);

        }catch (SQLException ex){
            System.out.println(ex);
        }
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    private void filterData(){
        FilteredList<ConferencesEntity> filteredData = new FilteredList<>(dataList, e -> true);
        startDate.valueProperty().addListener((ov, oldValue, newValue) -> {
            filteredData.setPredicate(conf -> {
                if(newValue!=null){
                    LocalDate temp = conf.getTimeStart().toLocalDateTime().toLocalDate();
                    if(temp.compareTo(newValue) >= 0){
                        return true;
                    }
                    return false;
                }
                return true;
            });
        });
        endDate.valueProperty().addListener((ov, oldValue, newValue) -> {
            filteredData.setPredicate(conf -> {
                if(newValue!=null && startDate.getValue()!=null){
                    LocalDate temp = conf.getTimeEnd().toLocalDateTime().toLocalDate();
                    LocalDate temp2 = conf.getTimeStart().toLocalDateTime().toLocalDate();
                    if(temp.compareTo(newValue) <= 0 && temp2.compareTo(startDate.getValue())>=0){
                        return true;
                    }
                    return false;
                }
                return true;
            });
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(conference -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if(comboFilter.getValue().equals("Conf Name")){
                    if (conference.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches first name.
                    }
                }else if(comboFilter.getValue().equals("Conf ID")) {
                    if (Integer.toString(conference.getConferenceId()).contains(lowerCaseFilter)) {
                        return true;
                    }
                }else if (conference.getShortDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        SortedList<ConferencesEntity> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(confTable.comparatorProperty());
        confTable.setItems(sortedData);
    }

    public static void confAdditionStage(){
        try{
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add new conference");
            FXMLLoader loader = loaderFXML("confAddition");
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    public static void confUpdateStage(ConferencesEntity conf){
        try{
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit conference");
            FXMLLoader loader = loaderFXML("confUpdate");
            loader.setController(new ConfUpdateController(conf));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    public static void confApprovalStage(ConferencesEntity conf){
        try{
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit conference");
            FXMLLoader loader = loaderFXML("confApproval");
            loader.setController(new ConfApprovalController(conf));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (IOException ex){
            ex.printStackTrace();
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
                shortDescLabel.setText("Detail:"+newItem.getDetailDescription());
            }
        });

        return detailsPane ;
    }
}
