package javafx.controllers;

import javafx.MainApp;
import javafx.entities.ConferencesEntity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ListCellController extends ListCell<ConferencesEntity> {

    @FXML
    private AnchorPane paneCell;


    @FXML
    private ImageView imageCell;

    @FXML
    private Text idCell;

    @FXML
    private Text confNameCell;

    @FXML
    private Text confStartCell;

    @FXML
    private Text confEndCell;

    @FXML
    private Text confDescCell;


    public ListCellController() {
        FXMLLoader loader = null;
        try {
            loader = MainApp.loaderFXML("listCell");
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void updateItem(ConferencesEntity conf, boolean b) {
        super.updateItem(conf, b);
        if(b || conf ==null) {
            setText(null);
            setGraphic(null);
        }
        else {
            idCell.setText(String.valueOf(conf.getConferenceId()));
            confNameCell.setText(conf.getName());
            confStartCell.setText(timestampAsString(conf.getTimeStart()));
            confEndCell.setText(timestampAsString(conf.getTimeEnd()));
            confDescCell.setText(conf.getShortDescription());
            InputStream is=new ByteArrayInputStream(conf.getImage());
            imageCell.setImage(new Image(is,300,200,false,false));
            setGraphic(paneCell);
        }
    }

    public static String timestampAsString(Timestamp timestamp) {
        Date date = new Date();
        date.setTime(timestamp.getTime());
        return new SimpleDateFormat("yyyy-MM-dd h:mm a").format(date);
    }
}
