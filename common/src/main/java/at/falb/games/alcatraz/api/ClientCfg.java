package at.falb.games.alcatraz.api;

import java.util.Objects;

public class ClientCfg {
    private String name;
    private String ip;
    private int port;
    private ServerCfg serverCfg;

    public ClientCfg() {
    }

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

    public ServerCfg getServerCfg() {
        return serverCfg;
    }

    public void setServerCfg(ServerCfg serverCfg) {
        this.serverCfg = serverCfg;
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
}
