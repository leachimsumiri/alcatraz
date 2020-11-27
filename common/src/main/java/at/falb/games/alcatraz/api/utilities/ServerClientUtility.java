package at.falb.games.alcatraz.api.utilities;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class ServerClientUtility {
    //These lists have the configurations of the clients and servers
    private static final List<ServerCfg> serverCfgList = new ArrayList<>();
    private static final List<ClientCfg> clientCfgList = new ArrayList<>();

    // The servers and clients differentiate by their socket
    private static final String RMI_URL = "rmi://localhost:%d";
    private static final String CLIENT_URL = RMI_URL + "/client";
    private static final String SERVER_URL = RMI_URL + "/server";

    private ServerClientUtility() {

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
     * Look for client RMI registry server
     * @param gamePlayer instance of type {@link GamePlayer}
     * @return the remote of type {@link ClientInterface}
     * @throws RemoteException see {@link LocateRegistry#getRegistry(String, int)}
     * @throws NotBoundException see {@link Registry#lookup(String)}
     */
    public static ClientInterface lookup(GamePlayer gamePlayer) throws RemoteException, NotBoundException, MalformedURLException {
        return (ClientInterface) Naming.lookup(completesTheRmiUrl(CLIENT_URL, gamePlayer.getPort()));
    }

    /**
     * Look for server RMI registry server
     * @param serverCfg instance of type {@link ServerCfg}
     * @return the remote of type {@link ServerInterface}
     * @throws RemoteException see {@link LocateRegistry#getRegistry(String, int)}
     * @throws NotBoundException see {@link Registry#lookup(String)}
     */
    public static ServerInterface lookup(ServerCfg serverCfg) throws RemoteException, NotBoundException, MalformedURLException {
        return (ServerInterface) Naming.lookup(completesTheRmiUrl(SERVER_URL, serverCfg.getRegistryPort()));
    }

    private static String completesTheRmiUrl(String url, int port) {
        return String.format(url, port);
    }

    /**
     * Creates a client RMI registry Server
     * @param clientInterface an instance of type {@link ClientInterface}
     * @throws RemoteException see {@link RemoteException}
     * @throws MalformedURLException see {@link MalformedURLException}
     */
    public static void createRegistry(ClientInterface clientInterface) throws RemoteException, MalformedURLException {
        final GamePlayer gamePlayer = clientInterface.getGamePlayer();
        LocateRegistry.createRegistry(gamePlayer.getPort());
        Naming.rebind(completesTheRmiUrl(CLIENT_URL, gamePlayer.getPort()), clientInterface);
    }

    /**
     * Clients a server RMI registry Server
     * @param serverInterface an instance of type {@link ServerInterface}
     * @throws RemoteException see {@link RemoteException}
     * @throws MalformedURLException see {@link MalformedURLException}
     */
    public static void createRegistry(ServerInterface serverInterface) throws RemoteException, MalformedURLException {
        final ServerCfg serverCfg = serverInterface.getServerCfg();
        LocateRegistry.createRegistry(serverCfg.getRegistryPort()).rebind("server", serverInterface);
    }
}
