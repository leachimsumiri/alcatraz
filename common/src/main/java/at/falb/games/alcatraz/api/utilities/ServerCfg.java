package at.falb.games.alcatraz.api.utilities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ServerCfg implements Serializable {

    private String name;
    private String spreaderIp;
    private String serverIp;
    private int spreaderPort;
    private int registryPort;
    private LocalDateTime startTimestamp;

    public ServerCfg() {
    }

    public ServerCfg(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpreaderIp() {
        return spreaderIp;
    }

    public void setSpreaderIp(String spreaderIp) {
        this.spreaderIp = spreaderIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getSpreaderPort() {
        return spreaderPort;
    }

    public void setSpreaderPort(int spreaderPort) {
        this.spreaderPort = spreaderPort;
    }

    public int getRegistryPort() {
        return registryPort;
    }

    public void setRegistryPort(int registryPort) {
        this.registryPort = registryPort;
    }

    public LocalDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(LocalDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void copy(ServerCfg serverCfg) {
        name = serverCfg.name;
        spreaderIp = serverCfg.spreaderIp;
        serverIp = serverCfg.serverIp;
        spreaderPort = serverCfg.spreaderPort;
        registryPort = serverCfg.registryPort;
        startTimestamp = serverCfg.startTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerCfg serverCfg = (ServerCfg) o;
        return Objects.equals(name, serverCfg.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ServerCfg{" +
                "name='" + name + '\'' +
                ", spreaderIp='" + spreaderIp + '\'' +
                ", serverIp='" + serverIp + '\'' +
                ", spreaderPort=" + spreaderPort +
                ", registryPort=" + registryPort +
                ", startTimestamp=" + startTimestamp +
                '}';
    }
}
