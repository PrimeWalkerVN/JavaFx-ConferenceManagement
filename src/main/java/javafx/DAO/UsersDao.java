package javafx.DAO;

import javafx.entities.UsersEntity;

import java.sql.SQLException;
import java.util.List;

public interface UsersDao {
    //create
    void add(UsersEntity user) throws SQLException;

    //read
    List<UsersEntity> getAll() throws SQLException;
    UsersEntity getById(int id) throws SQLException;
    UsersEntity getByUsername(String username) throws SQLException;

    //update
    void update(UsersEntity user) throws SQLException;

    //delete
    void delete(UsersEntity user) throws SQLException;

    //check
    boolean checkUser(String username)throws SQLException;
    boolean checkPassword(String username, String password) throws  SQLException;
    boolean checkBanned(int id) throws SQLException;

}
