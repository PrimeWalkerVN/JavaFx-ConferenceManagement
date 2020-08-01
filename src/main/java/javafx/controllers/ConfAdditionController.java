package javafx.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.entities.ConferencesEntity;
import javafx.entities.PlacesEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.models.ConferencesModel;
import javafx.models.PlacesModel;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import tornadofx.control.DateTimePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class ConfAdditionController implements Initializable {
    @FXML
    public AnchorPane ConfAdditionPane;

    @FXML
    private ImageView imageViewId;

    @FXML
    private TextField confNameField;

    @FXML
    private DateTimePicker startingPicker;

    @FXML
    private DateTimePicker endingPicker;

    @FXML
    private ComboBox<PlacesEntity> placeComboBox;

    @FXML
    private TextArea detailDescField;

    @FXML
    private TextField shortDescField;

    @FXML
    private Text capacityId;

    private byte[] imageData = null;


    @FXML
    void addConfAction(ActionEvent event) {
        if(checkField() && imageData!=null){
            ConferencesEntity conf = new ConferencesEntity();
            conf.setImage(imageData);
            conf.setName(confNameField.getText());
            conf.setTimeEnd(Timestamp.valueOf(endingPicker.getDateTimeValue()));
            conf.setTimeStart(Timestamp.valueOf(startingPicker.getDateTimeValue()));
            conf.setPlaceId(placeComboBox.getSelectionModel().getSelectedItem().getPlaceId());
            conf.setCapacity(placeComboBox.getSelectionModel().getSelectedItem().getCapacity());
            conf.setShortDescription(shortDescField.getText());
            conf.setDetailDescription(detailDescField.getText());

            Window window = ConfAdditionPane.getScene().getWindow();
            ConferencesModel conferencesModel = new ConferencesModel();
            if(conferencesModel.checkExistConfTime(conf)){
                showAlert(Alert.AlertType.ERROR, window,"Time uses place!","At the starting and ending time, this place was having another conference");
                return;
            }

            try {
                conferencesModel.add(conf);
                showAlert(Alert.AlertType.INFORMATION,window,"Add Conference","Save success.");
                Stage stage = (Stage)window;
                stage.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showAlert(Alert.AlertType.ERROR,window,"Error adding","Cannot add because " + throwables.toString());
            }

        }

    }

    @FXML
    void chooseImageAction(ActionEvent event) {


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(ConfAdditionPane.getScene().getWindow());
        if (file != null) {
            getImage(file);
        }
    }

    private void getImage(File file) {
        Image image = new Image(file.toURI().toString(),230,160,false,false);
        imageViewId.setImage(image);
        imageData = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(imageData);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkField(){
        String messageError = "";
        if(imageViewId.getImage().isError() || imageData==null) messageError = messageError + "Image invalid.\n";

        if(confNameField.getText().isEmpty()) messageError += "Input Name field.\n";

        if(startingPicker.getValue() == null) messageError += "Input starting conference date time.\n";

        if(endingPicker.getValue() == null)
            messageError += "Input ending conference date time.\n";
        else{
            if(startingPicker.getValue() != null){
                if(startingPicker.getValue().compareTo(endingPicker.getValue()) > 0){
                    messageError +="Ending must be greater than Starting";
                }
            }
        }

        if(placeComboBox.getSelectionModel().getSelectedItem() == null) messageError += "Choose place for conference\n";

        if(shortDescField.getText().isEmpty()) messageError += "Input short description\n";

        if(detailDescField.getText().isEmpty()) messageError += "Input detail description\n";

        if(messageError.isEmpty()) {
            return true;
        }else {
            Window window = ConfAdditionPane.getScene().getWindow();
            showAlert(Alert.AlertType.ERROR,window,"Error input", messageError);
            return false;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getPlaces();
        placeComboBox.valueProperty().addListener(new ChangeListener<PlacesEntity>() {
            @Override
            public void changed(ObservableValue<? extends PlacesEntity> observableValue, PlacesEntity placesEntity, PlacesEntity t1) {
                capacityId.setText(String.valueOf(t1.getCapacity()));
            }
        });
    }


    public void getPlaces(){
        Callback<ListView<PlacesEntity>, ListCell<PlacesEntity>> factory = lv -> new ListCell<PlacesEntity>() {
            @Override
            protected void updateItem(PlacesEntity item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }

        };

        placeComboBox.setCellFactory(factory);
        placeComboBox.setButtonCell(factory.call(null));
        PlacesModel placesModel = new PlacesModel();
        try {
            List<PlacesEntity> placesList = placesModel.getAll();
            placeComboBox.getItems().addAll(placesList);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}
