package at.falb.games.alcatraz.api.utilities;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ServerClientUtility {
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

    /**
     * Bounds to the specified server/client
     * @param ip of the registry
     * @param port of the registry
     * @param name of the registry
     * @param <T> the type of remote will be return
     * @return the remote of specified interface
     * @throws RemoteException see {@link LocateRegistry#getRegistry(String, int)}
     * @throws NotBoundException see {@link Registry#lookup(String)}
     */
    public static <T> T locateRegistryAndLookup(String ip, int port, String name) throws RemoteException, NotBoundException {
        final Registry registry = LocateRegistry.getRegistry(ip, port);
        return (T) registry.lookup(name);
    }

    /**
     * See {@link ServerClientUtility#locateRegistryAndLookup(String, int, String)}
     * @param gamePlayer instance of type {@link GamePlayer}
     * @return the remote of type {@link ClientInterface}
     * @throws RemoteException see {@link LocateRegistry#getRegistry(String, int)}
     * @throws NotBoundException see {@link Registry#lookup(String)}
     */
    public static ClientInterface locateRegistryAndLookup(GamePlayer gamePlayer) throws RemoteException, NotBoundException {
        return locateRegistryAndLookup(gamePlayer.getIp(), gamePlayer.getPort(), gamePlayer.getName());
    }

    /**
     * See {@link ServerClientUtility#locateRegistryAndLookup(String, int, String)}
     * @param serverCfg instance of type {@link ServerCfg}
     * @return the remote of type {@link ServerInterface}
     * @throws RemoteException see {@link LocateRegistry#getRegistry(String, int)}
     * @throws NotBoundException see {@link Registry#lookup(String)}
     */
    public static ServerInterface locateRegistryAndLookup(ServerCfg serverCfg) throws RemoteException, NotBoundException {
        return locateRegistryAndLookup(serverCfg.getServerIp(), serverCfg.getRegistryPort(), serverCfg.getName());
    }
}
