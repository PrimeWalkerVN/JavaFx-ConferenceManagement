package javafx.models;

import javafx.DAO.UsersDao;
import javafx.entities.UsersEntity;
import javafx.entities.UsersEntity;
import javafx.entities.UsersEntity;
import javafx.utils.SessionUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;

public class UsersModel extends SessionUtil implements UsersDao {

    @Override
    public void add(UsersEntity user) throws SQLException {
        //open session with a transaction
        openTransactionSession();
        Session session = getSession();
        session.save(user);
        //close session with a transaction
        closeTransactionSession();

    }

    @Override
    public List<UsersEntity> getAll() throws SQLException {
        openTransactionSession();
        String hql = "from UsersEntity ";
        Query query = getSession().createQuery(hql);
        List<UsersEntity> usersList = query.list();
        closeTransactionSession();
        return usersList;
    }

    @Override
    public UsersEntity getById(int id) throws SQLException {
        openTransactionSession();
        UsersEntity user = getSession().get(UsersEntity.class, id);
        closeTransactionSession();
        return user;
    }

    @Override
    public UsersEntity getByUsername(String username) throws SQLException {
        openTransactionSession();
        String hql = "from UsersEntity where userName =:username";
        Query query = getSession().createQuery(hql);
        query.setParameter("username", username);
        UsersEntity user = (UsersEntity) query.uniqueResult();
        closeTransactionSession();
        return user;
    }

    @Override
    public void update(UsersEntity user) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.update(user);
        closeTransactionSession();
    }

    @Override
    public void delete(UsersEntity user) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.remove(user);
        closeTransactionSession();
    }

    @Override
    public boolean checkUser(String username) throws SQLException {
        openTransactionSession();
        String hql = "from UsersEntity where userName =:username";
        Query query = getSession().createQuery(hql);
        query.setParameter("username", username);
        UsersEntity user = (UsersEntity) query.uniqueResult();
        closeTransactionSession();
        return user!=null;
    }

    @Override
    public boolean checkPassword(String username, String password) throws SQLException {
        openTransactionSession();
        String hql = "from UsersEntity where userName =:username";
        Query query = getSession().createQuery(hql);
        query.setParameter("username", username);
        UsersEntity user = (UsersEntity) query.uniqueResult();
        closeTransactionSession();

        return user.getPassword().equals(password);
    }

    @Override
    public boolean checkBanned(int id) throws SQLException {
        openTransactionSession();
        UsersEntity user = getSession().get(UsersEntity.class, id);
        closeTransactionSession();
        return user.getStatus();
    }
}
