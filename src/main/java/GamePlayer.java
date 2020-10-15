import at.falb.games.alcatraz.api.Player;

public class GamePlayer extends Player {
    private String ip;
    private int port = -1;

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
        if (obj instanceof GamePlayer) {
            GamePlayer p = (GamePlayer)obj;
            if (p.getId() == this.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "Player[" + this.getId();
        if (this.getName() != null) {
            result = result + ", " + this.getName();
        }
        if (this.ip != null) {
            result = result + ", " + this.ip;
        }
        if (this.port != -1) {
            result = result + ", " + this.port;
        }

        result = result + "]";
        return result;
    }
}
