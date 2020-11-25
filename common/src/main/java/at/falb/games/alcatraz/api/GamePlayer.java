package at.falb.games.alcatraz.api;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class GamePlayer extends Player {
    private String ip;
    private int port;

    /**
     * This will set the player with -1, since int cannot be null
     */
    public GamePlayer() {
        this(-1);
    }

    public GamePlayer(String name) {
        this();
        super.setName(name);
    }


    public GamePlayer(int id) {
        super(id);
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            Player p = (Player) obj;
            if (StringUtils.isAnyBlank(getName(), p.getName())) {
                return false;
            }
            return this.getName().equals(p.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return StringUtils.isNotBlank(getName()) ? -1 : Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", " + super.toString() +
                '}';
    }
}
