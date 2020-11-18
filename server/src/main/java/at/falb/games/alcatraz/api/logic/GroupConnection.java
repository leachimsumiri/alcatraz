package at.falb.games.alcatraz.api.logic;

import java.util.Objects;

public class GroupConnection {

    private String id;
    private String hostname;

    public GroupConnection() {
    }

    public GroupConnection(String id, String hostname) {
        this.id = id;
        this.hostname = hostname;
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

    @Override
    public int hashCode() {
        String hashString = id + hostname;
        return Objects.hash(hashString);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        GroupConnection other = (GroupConnection) obj;
        return hashCode() == other.hashCode();
    }
}
