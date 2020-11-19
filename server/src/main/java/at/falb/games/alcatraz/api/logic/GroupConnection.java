package at.falb.games.alcatraz.api.logic;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

// TODO: See if Comparable or serializable are necessary, since it is not sent nor it is being sorted automatically
public class GroupConnection implements Comparable<GroupConnection>, Serializable {

    private String id;
    private String hostname;
    private LocalDateTime startTimestamp;

    public GroupConnection() {
    }

    public GroupConnection(String id, String hostname, LocalDateTime startTimestamp) {
        this.id = id;
        this.hostname = hostname;
        this.startTimestamp = startTimestamp;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(LocalDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GroupConnection other = (GroupConnection) obj;
        return hashCode() == other.hashCode();
    }

    @Override
    public int compareTo(GroupConnection gc) {
        return startTimestamp.compareTo(gc.startTimestamp);
    }

    @Override
    public String toString() {
        return "GroupConnection{" +
                "id='" + id + '\'' +
                ", hostname='" + hostname + '\'' +
                ", startTimestamp=" + startTimestamp +
                '}';
    }
}
