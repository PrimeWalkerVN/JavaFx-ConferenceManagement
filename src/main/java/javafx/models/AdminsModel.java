package javafx.models;

import javafx.DAO.AdminsDao;
import javafx.entities.AdminsEntity;
import javafx.entities.AdminsEntity;
import javafx.utils.SessionUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;

public class AdminsModel extends SessionUtil implements AdminsDao {

    @Override
    public void add(AdminsEntity admin) throws SQLException {
        //open session with a transaction
        openTransactionSession();
        Session session = getSession();
        session.save(admin);
        //close session with a transaction
        closeTransactionSession();

    }

    @Override
    public List<AdminsEntity> getAll() throws SQLException {
        openTransactionSession();
        String hql = "from AdminsEntity ";
        Query query = getSession().createQuery(hql);
        List<AdminsEntity> adminsList = query.list();
        closeTransactionSession();
        return adminsList;
    }

    @Override
    public AdminsEntity getById(int id) throws SQLException {
        openTransactionSession();
        AdminsEntity admin = getSession().get(AdminsEntity.class, id);
        closeTransactionSession();
        return admin;
    }

    @Override
    public AdminsEntity getByUsername(String username) throws SQLException {
        openTransactionSession();
        String hql = "from AdminsEntity where userName =:username";
        Query query = getSession().createQuery(hql);
        query.setParameter("username", username);
        AdminsEntity admin = (AdminsEntity) query.uniqueResult();
        closeTransactionSession();
        return admin;
    }

    @Override
    public void update(AdminsEntity admin) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.update(admin);
        closeTransactionSession();
    }

    @Override
    public void delete(AdminsEntity admin) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.remove(admin);
        closeTransactionSession();
    }

    @Override
    public boolean checkAdmin(String username) throws SQLException {
        openTransactionSession();
        String hql = "from AdminsEntity where userName =:username";
        Query query = getSession().createQuery(hql);
        query.setParameter("username", username);
        AdminsEntity admin = (AdminsEntity) query.uniqueResult();
        closeTransactionSession();
        return admin!=null;
    }

    @Override
    public boolean checkPassword(String username, String password) throws SQLException {
        openTransactionSession();
        String hql = "from AdminsEntity where userName =:username";
        Query query = getSession().createQuery(hql);
        query.setParameter("username", username);
        AdminsEntity admin = (AdminsEntity) query.uniqueResult();
        closeTransactionSession();

        return admin.getPassword().equals(password);
    }
}
