package at.falb.games.alcatraz.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerClientUtility {
    //These lists have the configurations of the clients and servers
    private static final List<ServerCfg> serverCfgList = new ArrayList<>();
    private static final List<ClientCfg> clientCfgList = new ArrayList<>();

    private ServerClientUtility() {

    }

    public static List<ServerCfg> getTheOtherServers(ServerCfg serverCfg) {
        return serverCfgList.stream().filter(s -> !s.equals(serverCfg)).collect(Collectors.toList());
    }

    public static List<ClientCfg> getTheOtherClients(ClientCfg clientCfg) {
        return clientCfgList.stream().filter(s -> !s.equals(clientCfg)).collect(Collectors.toList());
    }

    public static List<ServerCfg> getServerCfgList() {
        return serverCfgList;
    }

    public static List<ClientCfg> getClientCfgList() {
        return clientCfgList;
    }

    /**
     * It will return the server running for the longest time
     * @param serverCfgList a list of {@link ServerCfg}
     * @return the server or null if any has a {@link ServerCfg#getStartTimestamp()}
     */
    public static ServerCfg getMainRegistryServer(List<ServerCfg> serverCfgList) {
        ServerCfg oldestGC = null;
        for (ServerCfg gc : serverCfgList) {
            if (gc.getStartTimestamp() != null && (oldestGC == null || oldestGC.getStartTimestamp().compareTo(gc.getStartTimestamp()) > 0)) {
                oldestGC = gc;
            }
        }
        return oldestGC;
    }
}
