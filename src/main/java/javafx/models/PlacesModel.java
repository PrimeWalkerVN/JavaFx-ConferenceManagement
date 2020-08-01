package javafx.models;

import javafx.DAO.PlacesDao;
import javafx.entities.PlacesEntity;
import javafx.utils.SessionUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.util.List;

public class PlacesModel extends SessionUtil implements PlacesDao {

    @Override
    public void add(PlacesEntity place) throws SQLException {
        //open session with a transaction
        openTransactionSession();
        Session session = getSession();
        session.save(place);
        //close session with a transaction
        closeTransactionSession();
    }

    @Override
    public List<PlacesEntity> getAll() throws SQLException {
        openTransactionSession();
        String hql = "from PlacesEntity ";
        Query query = getSession().createQuery(hql);
        List<PlacesEntity> placesList = query.list();
        closeTransactionSession();
        return placesList;
    }

    @Override
    public PlacesEntity getById(int id) throws SQLException {
        openTransactionSession();
        PlacesEntity place = getSession().get(PlacesEntity.class, id);
        closeTransactionSession();
        return place;
    }

    @Override
    public void update(PlacesEntity place) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.update(place);
        closeTransactionSession();
    }

    @Override
    public void delete(PlacesEntity place) throws SQLException {
        openTransactionSession();
        Session session = getSession();
        session.remove(place);
        closeTransactionSession();
    }

    public boolean checkNamePlace(String name) throws SQLException{
        openTransactionSession();
        String hql = "from PlacesEntity where name =: namePlace";
        Query query = getSession().createQuery(hql);
        query.setParameter("namePlace",name);
        List<PlacesEntity> placesList = query.list();
        closeTransactionSession();
        return placesList.size() <1;
    }
}
