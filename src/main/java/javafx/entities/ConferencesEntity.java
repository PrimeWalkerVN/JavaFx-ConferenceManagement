package javafx.entities;

import javafx.models.PlacesModel;

import javax.persistence.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "Conferences", schema = "Conference_Management", catalog = "")
public class ConferencesEntity {
    private int conferenceId;
    private String name;
    private String shortDescription;
    private String detailDescription;
    private Timestamp timeStart;
    private Timestamp timeEnd;
    private int placeId;
    private int capacity;
    private String dateStartFormat;
    private String dateEndFormat;
    private byte[] image;


    @Id
    @Column(name = "ConferenceID", nullable = false)
    public int getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(int conferenceId) {
        this.conferenceId = conferenceId;
    }

    @Basic
    @Column(name = "Name", nullable = false, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "ShortDescription", nullable = false, length = 50)
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @Basic
    @Column(name = "DetailDescription", nullable = false, length = -1)
    public String getDetailDescription() {
        return detailDescription;
    }

    public void setDetailDescription(String detailDescription) {
        this.detailDescription = detailDescription;
    }

    @Basic
    @Column(name = "TimeStart", nullable = false)
    public Timestamp getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {

        this.timeStart = timeStart;
        this.dateStartFormat = timestampAsString(timeStart);
    }

    @Basic
    @Column(name = "TimeEnd", nullable = false)
    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
        this.dateEndFormat = timestampAsString(timeEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConferencesEntity that = (ConferencesEntity) o;

        if (conferenceId != that.conferenceId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (shortDescription != null ? !shortDescription.equals(that.shortDescription) : that.shortDescription != null)
            return false;
        if (detailDescription != null ? !detailDescription.equals(that.detailDescription) : that.detailDescription != null)
            return false;
        if (timeStart != null ? !timeStart.equals(that.timeStart) : that.timeStart != null) return false;
        if (timeEnd != null ? !timeEnd.equals(that.timeEnd) : that.timeEnd != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = conferenceId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (shortDescription != null ? shortDescription.hashCode() : 0);
        result = 31 * result + (detailDescription != null ? detailDescription.hashCode() : 0);
        result = 31 * result + (timeStart != null ? timeStart.hashCode() : 0);
        result = 31 * result + (timeEnd != null ? timeEnd.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "PlaceID", nullable = false)
    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        PlacesModel model = new PlacesModel();
        try {
            PlacesEntity place = model.getById(placeId);
            this.capacity = place.getCapacity();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        this.placeId = placeId;
    }
    @Basic
    @Column(name = "Image", nullable = true)
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDateStartFormat() {
        return dateStartFormat;
    }

    public void setDateStartFormat(String dateStartFormat) {
        this.dateStartFormat = dateStartFormat;
    }

    public String timestampAsString(Timestamp timestamp) {
        Date date = new Date();
        date.setTime(timestamp.getTime());
        return new SimpleDateFormat("yyyy-MM-dd h:mm a").format(date);
    }

    public String getDateEndFormat() {
        return dateEndFormat;
    }

    public void setDateEndFormat(String dateEndFormat) {
        this.dateEndFormat = dateEndFormat;
    }


}
