package javafx.models;

import javafx.DAO.ConfAttending;
import javafx.entities.AttendConferencesEntity;
import javafx.entities.ConferencesEntity;
import javafx.entities.PlacesEntity;
import javafx.entities.UsersEntity;
import javafx.utils.SessionUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendingModel extends SessionUtil implements ConfAttending {

    @Override
    public void add(AttendConferencesEntity attending) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.save(attending);
        closeTransactionSession();
    }

    @Override
    public List<AttendConferencesEntity> getAll() throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity";
        Query query = getSession().createQuery(hql);
        List<AttendConferencesEntity> List = query.list();
        closeTransactionSession();
        return List;
    }

    @Override
    public List<AttendConferencesEntity> getFullField() throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity as a join ConferencesEntity as b on a.conferenceId= b.conferenceId join PlacesEntity as c on b.placeId = c.placeId";
        Query query = getSession().createQuery(hql);
        List<Object[]> list = query.list();
        List<AttendConferencesEntity> results = new ArrayList<>();
        for (Object[] a : list){
            AttendConferencesEntity attending = (AttendConferencesEntity)a[0];
            ConferencesEntity conf= (ConferencesEntity) a[1];
            PlacesEntity place = (PlacesEntity) a[2];
            attending.setNameConf(conf.getName());
            attending.setNamePlace(place.getName());
            attending.setTimeStart(conf.getTimeStart());
            attending.setTimeEnd(conf.getTimeEnd());
            results.add(attending);
        }
        closeTransactionSession();
        return results;
    }

    @Override
    public List<AttendConferencesEntity> getFullFieldByUser(int userId) throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity as a join ConferencesEntity as b on a.conferenceId= b.conferenceId join PlacesEntity as c on b.placeId = c.placeId where a.userId =:userId";
        Query query = getSession().createQuery(hql);
        query.setParameter("userId", userId);

        List<Object[]> list = query.list();
        List<AttendConferencesEntity> results = new ArrayList<>();
        for (Object[] a : list){
            AttendConferencesEntity attending = (AttendConferencesEntity)a[0];
            ConferencesEntity conf= (ConferencesEntity) a[1];
            PlacesEntity place = (PlacesEntity) a[2];
            attending.setNameConf(conf.getName());
            attending.setNamePlace(place.getName());
            attending.setTimeStart(conf.getTimeStart());
            attending.setTimeEnd(conf.getTimeEnd());
            results.add(attending);
        }
        closeTransactionSession();
        return results;
    }

    @Override
    public List<AttendConferencesEntity> getFullFieldByConf(int confId) throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity as a join UsersEntity as b on a.userId = b.userId where a.conferenceId =:confId";
        Query query = getSession().createQuery(hql);
        query.setParameter("confId", confId);

        List<Object[]> list = query.list();
        List<AttendConferencesEntity> results = new ArrayList<>();
        for (Object[] a : list){
            AttendConferencesEntity attending = (AttendConferencesEntity)a[0];
            UsersEntity users = (UsersEntity) a[1];
            attending.setUserName(users.getUserName());
            attending.setUserFullName(users.getName());
            results.add(attending);
        }
        closeTransactionSession();
        return results;
    }

    @Override
    public AttendConferencesEntity getByAttendId(int id) throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity where attendId =:attendId";
        Query query = getSession().createQuery(hql);
        query.setParameter("attendId", id);

        AttendConferencesEntity attending = (AttendConferencesEntity)query.uniqueResult();
        closeTransactionSession();
        return attending;
    }

    @Override
    public AttendConferencesEntity getByUserAndConfId(int userId, int confId) throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity where userId =:userId and conferenceId =:confId";
        Query query = getSession().createQuery(hql);
        query.setParameter("userId", userId);
        query.setParameter("confId",confId);
        AttendConferencesEntity attending = (AttendConferencesEntity) query.uniqueResult();
        closeTransactionSession();
        return attending;
    }

    @Override
    public List<AttendConferencesEntity> getByConfId(int id) throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity where conferenceId =:confId";
        Query query = getSession().createQuery(hql);
        query.setParameter("confId", id);
        List<AttendConferencesEntity> list = query.list();
        return list;
    }

    @Override
    public List<AttendConferencesEntity> getByUserId(int id) throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity where userId =:userId";
        Query query = getSession().createQuery(hql);
        query.setParameter("userId", id);
        List<AttendConferencesEntity> list = query.list();
        return list;
    }

    @Override
    public void update(AttendConferencesEntity attending) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.update(attending);
        closeTransactionSession();
    }

    @Override
    public void delete(AttendConferencesEntity attending) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.remove(attending);
        closeTransactionSession();
    }

    @Override
    public boolean checkApproved(int userId, int confId) throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity where userId =:userId and conferenceId =:confId";
        Query query = getSession().createQuery(hql);
        query.setParameter("userId", userId);
        query.setParameter("confId",confId);
        AttendConferencesEntity attending = (AttendConferencesEntity) query.uniqueResult();
        closeTransactionSession();
        return attending.isApproved();
    }

    @Override
    public boolean checkAttending(int userId, int confId) throws SQLException {
        openTransactionSession();
        String hql = "from AttendConferencesEntity where userId =:userId and conferenceId =:confId";
        Query query = getSession().createQuery(hql);
        query.setParameter("userId", userId);
        query.setParameter("confId",confId);
        AttendConferencesEntity attending = (AttendConferencesEntity) query.uniqueResult();
        closeTransactionSession();
        return attending!=null;
    }

}
