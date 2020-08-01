package javafx.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "AttendConferences", schema = "Conference_Management")
@IdClass(AttendConferencesEntityPK.class)
public class AttendConferencesEntity {
    private int attendId;
    private int conferenceId;
    private int userId;
    private Timestamp timeRegister;
    private boolean approved;



    @Id
    @Column(name = "AttendID", nullable = false)
    public int getAttendId() {
        return attendId;
    }

    public void setAttendId(int attendId) {
        this.attendId = attendId;
    }

    @Id
    @Column(name = "ConferenceID", nullable = false)
    public int getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(int conferenceId) {
        this.conferenceId = conferenceId;
    }

    @Id
    @Column(name = "UserID", nullable = false)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "TimeRegister", nullable = false)
    public Timestamp getTimeRegister() {
        return timeRegister;
    }

    public void setTimeRegister(Timestamp timeRegister) {
        this.timeRegister = timeRegister;
    }

    @Basic
    @Column(name = "approved", nullable = false)
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {

        this.approved = approved;
        if(approved){
            setApprovedString("Approved");
        }else {
            setApprovedString("Not Approved");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendConferencesEntity that = (AttendConferencesEntity) o;

        if (attendId != that.attendId) return false;
        if (conferenceId != that.conferenceId) return false;
        if (userId != that.userId) return false;
        if (approved != that.approved) return false;
        if (timeRegister != null ? !timeRegister.equals(that.timeRegister) : that.timeRegister != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attendId;
        result = 31 * result + conferenceId;
        result = 31 * result + userId;
        result = 31 * result + (timeRegister != null ? timeRegister.hashCode() : 0);
        result = 31 * result + (approved ? 1 : 0);
        return result;
    }


    private String nameConf;
    private String namePlace;
    private Timestamp timeEnd;
    private Timestamp timeStart;
    private String approvedString;
    private String userName;
    private String userFullName;

    public String getNameConf() {
        return nameConf;
    }

    public void setNameConf(String nameConf) {
        this.nameConf = nameConf;
    }

    public String getNamePlace() {
        return namePlace;
    }

    public void setNamePlace(String namePlace) {
        this.namePlace = namePlace;
    }

    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Timestamp getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Timestamp timeStart) {
        this.timeStart = timeStart;
    }

    public String getApprovedString() {
        return approvedString;
    }

    public void setApprovedString(String approvedString) {
        this.approvedString = approvedString;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
