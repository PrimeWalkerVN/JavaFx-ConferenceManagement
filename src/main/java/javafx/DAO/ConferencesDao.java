package javafx.DAO;

import javafx.entities.ConferencesEntity;

import java.sql.SQLException;
import java.util.List;

public interface ConferencesDao {
    //create
    void add(ConferencesEntity conf) throws SQLException;

    //read
    List<ConferencesEntity> getAll() throws SQLException;
    ConferencesEntity getById(int id) throws SQLException;

    //update
    void update(ConferencesEntity conf) throws SQLException;

    //delete
    void delete(ConferencesEntity conf) throws SQLException;
    void delete(int id) throws SQLException;

    //check
    boolean checkFull(ConferencesEntity conf) throws SQLException;
}
