package javafx.DAO;

import javafx.entities.AttendConferencesEntity;

import java.sql.SQLException;
import java.util.List;

public interface ConfAttending {
    //create
    void add(AttendConferencesEntity attending) throws SQLException;

    //read
    List<AttendConferencesEntity> getAll() throws SQLException;
    List<AttendConferencesEntity> getFullField() throws SQLException;
    List<AttendConferencesEntity> getFullFieldByUser(int userId) throws SQLException;
    List<AttendConferencesEntity> getFullFieldByConf(int confId) throws SQLException;
    AttendConferencesEntity getByAttendId(int id) throws SQLException;
    AttendConferencesEntity getByUserAndConfId(int userId, int confId) throws SQLException;
    List<AttendConferencesEntity> getByUserId(int id) throws SQLException;
    List<AttendConferencesEntity>getByConfId(int id) throws SQLException;

    //update
    void update(AttendConferencesEntity attending) throws SQLException;

    //delete
    void delete(AttendConferencesEntity attending) throws SQLException;

    //check
    boolean checkApproved(int userId, int confId) throws SQLException;
    boolean checkAttending(int userId, int confId) throws SQLException;



}
