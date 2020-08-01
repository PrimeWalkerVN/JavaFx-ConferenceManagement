package javafx.DAO;

import javafx.entities.AdminsEntity;
import javafx.entities.UsersEntity;

import java.sql.SQLException;
import java.util.List;

public interface AdminsDao {

    //create
    void add(AdminsEntity admin) throws SQLException;

    //read
    List<AdminsEntity> getAll() throws SQLException;
    AdminsEntity getById(int id) throws SQLException;
    AdminsEntity getByUsername(String username) throws SQLException;

    //update
    void update(AdminsEntity admin) throws SQLException;

    //delete
    void delete(AdminsEntity admin) throws SQLException;

    //check
    boolean checkAdmin(String username)throws SQLException;
    boolean checkPassword(String username, String password) throws  SQLException;
}
