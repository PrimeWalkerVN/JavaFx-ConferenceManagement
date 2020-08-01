package javafx.controllers;

import javafx.entities.PlacesEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.models.PlacesModel;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import java.sql.SQLException;

public class PlaceAdditionController {

    @FXML
    private AnchorPane placeAdditionPane;
    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField capacityField;

    @FXML
    void addPlaceAction(ActionEvent event) {
        Window window = placeAdditionPane.getScene().getWindow();
        if(checkField()){
            PlacesModel placesModel = new PlacesModel();
            PlacesEntity place = new PlacesEntity();
            place.setName(nameField.getText());
            place.setAddress(addressField.getText());
            place.setCapacity(Integer.parseInt(capacityField.getText()));
            try {
                if(placesModel.checkNamePlace(place.getName())){
                    placesModel.add(place);
                    showAlert(Alert.AlertType.INFORMATION,window,"Add place","Add success!");
                    window.hide();
                }else showAlert(Alert.AlertType.ERROR,window,"Error","Name already exists!");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showAlert(Alert.AlertType.ERROR,window,"Error",throwables.toString());
            }
        }
    }

    boolean checkField(){
        String messageError = "";
        if(nameField.getText().isEmpty()) messageError += "Input Place name field.\n";
        if(addressField.getText().isEmpty()) messageError += "Input Place address field.\n";
        if(capacityField.getText().isEmpty()) {
            messageError += "Input Place capacity field.\n";
        }else {
            try{
                int a = Integer.parseInt(capacityField.getText());
                if(a<1 || a>10000 )
                    messageError +="Capcacity field must be at least 1 and less than 10000\n";
            }catch (NumberFormatException e){
                messageError +="Can not read number in capacity field\n";
            }
        }
        if(messageError.isEmpty()){
            return true;
        }else {
            showAlert(Alert.AlertType.ERROR,placeAdditionPane.getScene().getWindow(),"Error input", messageError);
            return false;
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
