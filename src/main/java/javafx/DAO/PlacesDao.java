package javafx.DAO;

import javafx.entities.PlacesEntity;

import java.sql.SQLException;
import java.util.List;

public interface PlacesDao {
    //create
    void add(PlacesEntity place) throws SQLException;

    //read
    List<PlacesEntity> getAll() throws SQLException;
    PlacesEntity getById(int id) throws SQLException;

    //update
    void update(PlacesEntity place) throws SQLException;

    //delete
    void delete(PlacesEntity place) throws SQLException;
}
