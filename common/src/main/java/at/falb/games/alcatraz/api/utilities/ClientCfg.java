package at.falb.games.alcatraz.api.utilities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ClientCfg implements Serializable {
    private String name;
    private String ip;
    private int port;
    private LocalDateTime startTimestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(LocalDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientCfg clientCfg = (ClientCfg) o;
        return Objects.equals(name, clientCfg.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ClientCfg{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", startTimestamp=" + startTimestamp +
                '}';
    }
}
