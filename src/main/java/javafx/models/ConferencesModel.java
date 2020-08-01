package javafx.models;

import javafx.DAO.ConferencesDao;
import javafx.entities.AttendConferencesEntity;
import javafx.entities.ConferencesEntity;
import javafx.entities.PlacesEntity;
import javafx.utils.SessionUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class ConferencesModel extends SessionUtil implements ConferencesDao {

    // Create
    @Override
    public void add(ConferencesEntity conf) throws SQLException {
        //open session with a transaction
        openTransactionSession();
        Session session = getSession();
        session.save(conf);
        //close session with a transaction
        closeTransactionSession();
    }

    // Read
    public List<ConferencesEntity> getAll() throws SQLException {
        openTransactionSession();
        String hql = "from ConferencesEntity ";
        Query query = getSession().createQuery(hql);
        List<ConferencesEntity> conferenceList = query.list();
        closeTransactionSession();
        return conferenceList;
    }

    public ConferencesEntity getById(int id) throws SQLException {
        openTransactionSession();
        ConferencesEntity conference = getSession().get(ConferencesEntity.class, id);
        closeTransactionSession();
        return conference;
    }

    // Update
    @Override
    public void update(ConferencesEntity conf) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.update(conf);
        closeTransactionSession();
    }

    // Delete
    @Override
    public void delete(ConferencesEntity conf) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.remove(conf);
        closeTransactionSession();
    }

    @Override
    public void delete(int id) throws SQLException {
        ConferencesEntity conf = getById(id);
        openTransactionSession();
        Session session = getSession();
        session.remove(conf);
        closeTransactionSession();
    }

    // Check
    @Override
    public boolean checkFull(ConferencesEntity conf) throws SQLException {
        AttendingModel attendingModel = new AttendingModel();
        List<AttendConferencesEntity> list = attendingModel.getByConfId(conf.getConferenceId());
        PlacesModel placesModel = new PlacesModel();
        PlacesEntity place = placesModel.getById(conf.getPlaceId());
        return place.getCapacity() <= list.size();
    }

    public boolean checkExistConfTime(ConferencesEntity conf){
        openTransactionSession();
        String hql = "from ConferencesEntity where placeId =: placeId";
        Query query = getSession().createQuery(hql);
        query.setParameter("placeId",conf.getPlaceId());
        List<ConferencesEntity> conferenceList = query.list();
        for(ConferencesEntity confItem : conferenceList){
            if(isOverlapping(conf.getTimeStart().toLocalDateTime(),conf.getTimeEnd().toLocalDateTime(),confItem.getTimeStart().toLocalDateTime(),confItem.getTimeEnd().toLocalDateTime()))
                return true;
        }
        return false;
    }
    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}
