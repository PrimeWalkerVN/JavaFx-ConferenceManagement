package javafx.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class AttendConferencesEntityPK implements Serializable {
    private int attendId;
    private int conferenceId;
    private int userId;

    @Column(name = "AttendID", nullable = false)
    @Id
    public int getAttendId() {
        return attendId;
    }

    public void setAttendId(int attendId) {
        this.attendId = attendId;
    }

    @Column(name = "ConferenceID", nullable = false)
    @Id
    public int getConferenceId() {
        return conferenceId;
    }

    public void setConferenceId(int conferenceId) {
        this.conferenceId = conferenceId;
    }

    @Column(name = "UserID", nullable = false)
    @Id
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttendConferencesEntityPK that = (AttendConferencesEntityPK) o;

        if (attendId != that.attendId) return false;
        if (conferenceId != that.conferenceId) return false;
        if (userId != that.userId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attendId;
        result = 31 * result + conferenceId;
        result = 31 * result + userId;
        return result;
    }
}
