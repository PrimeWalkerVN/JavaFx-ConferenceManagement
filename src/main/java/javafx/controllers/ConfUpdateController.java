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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class ConfUpdateController implements Initializable {

    private ConferencesEntity conf;
    private byte[] imageData = null;

    public ConfUpdateController(ConferencesEntity conf){
        this.conf = conf;
        this.imageData = conf.getImage();
    }

    @FXML
    private AnchorPane confUpdatePane;

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



    @FXML
    void chooseImageAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(confUpdatePane.getScene().getWindow());
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

    @FXML
    void updateConfAction(ActionEvent event) {
        if(checkField() && imageData!=null){
            conf.setImage(imageData);
            conf.setName(confNameField.getText());
            conf.setTimeEnd(Timestamp.valueOf(endingPicker.getDateTimeValue()));
            conf.setTimeStart(Timestamp.valueOf(startingPicker.getDateTimeValue()));
            conf.setPlaceId(placeComboBox.getSelectionModel().getSelectedItem().getPlaceId());
            conf.setCapacity(placeComboBox.getSelectionModel().getSelectedItem().getCapacity());
            conf.setShortDescription(shortDescField.getText());
            conf.setDetailDescription(detailDescField.getText());
            ConferencesModel conferencesModel = new ConferencesModel();
            Window window = confUpdatePane.getScene().getWindow();
            try {
                conferencesModel.update(conf);
                ConfAdditionController.showAlert(Alert.AlertType.INFORMATION,window,"Update Conference","Save success.");
                Stage stage = (Stage)window;
                stage.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                ConfAdditionController.showAlert(Alert.AlertType.ERROR,window,"Error updating","Cannot update because " + throwables.toString());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getPlaces();
        confNameField.setText(conf.getName());
        endingPicker.setDateTimeValue(conf.getTimeEnd().toLocalDateTime());
        startingPicker.setDateTimeValue(conf.getTimeStart().toLocalDateTime());
        shortDescField.setText(conf.getShortDescription());
        detailDescField.setText(conf.getDetailDescription());
        capacityId.setText(String.valueOf(conf.getCapacity()));
        InputStream is=new ByteArrayInputStream(imageData);
        imageViewId.setImage(new Image(is,230,160,false,false));

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
            PlacesEntity place = placesModel.getById(conf.getPlaceId());
            placeComboBox.getSelectionModel().select(place);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean checkField(){
        String messageError = "";
        if(imageViewId.getImage().isError() || imageData==null) messageError = messageError + "Image invalid.\n";

        if(confNameField.getText().isEmpty()) messageError += "Input Name field.\n";

        if(startingPicker.getValue() == null) messageError += "Input starting conference date time.\n";

        if(endingPicker.getValue() == null) messageError += "Input ending conference date time.\n";

        if(placeComboBox.getSelectionModel().getSelectedItem() == null) messageError += "Choose place for conference\n";

        if(shortDescField.getText().isEmpty()) messageError += "Input short description\n";

        if(detailDescField.getText().isEmpty()) messageError += "Input detail description\n";

        if(messageError.isEmpty()) {
            return true;
        }else {
            Window window = confUpdatePane.getScene().getWindow();
            ConfAdditionController.showAlert(Alert.AlertType.ERROR,window,"Error input", messageError);
            return false;
        }
    }

}
