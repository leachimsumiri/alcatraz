package at.falb.games.alcatraz.api.utilities;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerInterface;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class ServerClientUtility {
    private static final Logger LOG = LogManager.getLogger(ServerClientUtility.class);
    //These lists have the configurations of the clients and servers
    private static final List<ServerCfg> SERVER_CFG_LIST = new ArrayList<>();

    // How many times will the sender, try to reach the other RMI server
    private static final int MAX_RETRIES = 4;

    private enum RmiType {
        // This is the client
        PLAYER("client"),
        SERVER("server");
        public final String name;

        RmiType(String name) {
            this.name = name;
        }
    }

    private ServerClientUtility() {

    }

    public static List<ServerCfg> getServerCfgList() {
        return SERVER_CFG_LIST;
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
     */
    public static ClientInterface lookup(GamePlayer gamePlayer) throws RemoteException {
        return lookup(gamePlayer.getIp(), gamePlayer.getPort(), gamePlayer.getName(), RmiType.PLAYER);
    }

    /**
     * Look for server RMI registry server
     * @param serverCfg instance of type {@link ServerCfg}
     * @return the remote of type {@link ServerInterface}
     * @throws RemoteException see {@link LocateRegistry#getRegistry(String, int)}
     */
    public static ServerInterface lookup(ServerCfg serverCfg) throws RemoteException {
        return lookup(serverCfg.getServerIp(), serverCfg.getRegistryPort(), serverCfg.getName(), RmiType.SERVER);
    }

    /**
     * This method will try for 2 minutes(12 * 10sec) to connect with its target RMI server.
     * If it fails, it will throw a {@link RemoteException}
     * @param <T> the type of interface returned
     * @param ip the static url
     * @param port the port used by RMI
     * @param name the name of the server or player
     * @param type the type of RMI server -> Client(aka Player) or Server
     * @return an instance of type {@link Remote}
     * @throws RemoteException see {@link RemoteException}
     */
    private static <T extends Remote> T lookup(String ip, int port, String name, RmiType type) throws RemoteException {
        final String completedRmiUrl = completesTheRmiUrl(ip, port);
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                Registry registry = LocateRegistry.getRegistry(ip, port);
                return (T) registry.lookup(type == RmiType.SERVER ? "server" : "client");
            } catch (RemoteException | NotBoundException e) {
                LOG.error(String.format("Try %d: try to connect with RMI. %s:%d ", i, ip, port), e);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    // Ignore this annoying exception
                }
            }
        }
        throw new RemoteException(String.format("The %s with the name %s wasn't reachable", type.name(), name));
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
        createRegistry(clientInterface, gamePlayer.getPort(), RmiType.PLAYER);
    }

    private static void createRegistry(Remote remote, int port, RmiType type) throws RemoteException {
        LocateRegistry.createRegistry(port).rebind(type.name, remote);
    }

    /**
     * Clients a server RMI registry Server
     * @param serverInterface an instance of type {@link ServerInterface}
     * @throws RemoteException see {@link RemoteException}
     * @throws MalformedURLException see {@link MalformedURLException}
     */
    public static void createRegistry(ServerInterface serverInterface) throws RemoteException, MalformedURLException {
        final ServerCfg serverCfg = serverInterface.getServerCfg();
        createRegistry(serverInterface, serverCfg.getRegistryPort(), RmiType.SERVER);
    }
}
