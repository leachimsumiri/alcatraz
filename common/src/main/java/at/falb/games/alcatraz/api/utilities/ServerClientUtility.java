package at.falb.games.alcatraz.api.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
        final Optional<ServerCfg> optionalServerCfg = serverCfgList
                .stream()
                .filter(s -> s.getStartTimestamp() != null)
                .min(Comparator.comparing(ServerCfg::getStartTimestamp));
        assert optionalServerCfg.isPresent();
        return optionalServerCfg.get();
    }
}
