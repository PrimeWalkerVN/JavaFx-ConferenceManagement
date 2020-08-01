package javafx.models;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import javafx.controllers.ListCellController;
import javafx.entities.ConferencesEntity;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ConfCellFactory implements Callback<ListView<ConferencesEntity>, ListCell<ConferencesEntity>> {
    @Override
    public ListCell<ConferencesEntity> call(ListView<ConferencesEntity> param) {
        return new ListCellController();
    }
}
