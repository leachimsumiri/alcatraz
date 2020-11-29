package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.utilities.GameStatus;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private static final Logger LOG = LogManager.getLogger(Server.class);

    private List<GamePlayer> gamePlayerList = new ArrayList<>();
    private GameStatus gameStatus = GameStatus.NOT_STARTED;
    private final SpreadConnection connection;

    private static Server thisServer;
    private final ServerCfg serverCfg;
    private final SpreadMessageListener spreadMessageListener;
    private static final List<ServerCfg> actualServersList = new ArrayList<>();

    private Server(SpreadConnection connection, ServerCfg serverCfg) throws RemoteException {
        this.serverCfg = serverCfg;
        this.connection = connection;
        this.spreadMessageListener = new SpreadMessageListener();
        connection.add(this.spreadMessageListener);
    }

    /**
     * It will announce the group any change.
     * <ul>
     *     <li>A new server joined this spread group</li>
     *     <li>The server left this spread group</li>
     *     <li>The gameplayer register or deregister</li>
     * </ul>
     * @param messageObject
     * @param <M>
     * @throws SpreadException
     */
    public static <M extends Serializable> void announceToGroup(M messageObject) throws SpreadException {
        SpreadMessage message = new SpreadMessage();
        message.setObject(messageObject);
        message.addGroup(ServerValues.REPLICAS_GROUP_NAME);
        message.setReliable();
        message.setSelfDiscard(true);
        thisServer.connection.multicast(message);
    }

    public static List<ServerCfg> getActualServersList() {
        return actualServersList;
    }

    public static void updateActualServersList(ServerCfg serverCfg) throws SpreadException {
        // Added this because the indexof, wasn't finding the server
        final Optional<ServerCfg> optionalServerCfg = actualServersList
                .stream()
                .filter(s -> s.equals(serverCfg))
                .findAny();

        assert optionalServerCfg.isPresent() : "This should not happen, the server should exist";

        final int i = actualServersList.indexOf(optionalServerCfg.get());

        actualServersList.set(i, serverCfg);

        //It takes some time(nanoseconds) till all servers in the group know about each other
        if (thisServer.getListOfServersWithStartTimestamp().size() == actualServersList.size()) {

            // One of the servers, will ask the main registry server to update all the servers in the group
            final ServerCfg mainRegistryServer = thisServer.getMainRegistryServer();
            announceToGroup(new UpdateGroup(mainRegistryServer));
        }
    }

    public static Server build(ServerCfg serverCfg) throws RemoteException, UnknownHostException, SpreadException, MalformedURLException {
        if (thisServer == null) {
            build(serverCfg, new SpreadConnection());
            ServerClientUtility.createRegistry(thisServer);
        }
        return thisServer;
    }

    /**
     * It is needed for testing
     * @param serverCfg a normal server configuration
     * @param connection a mocked SpreadConnection
     * @return
     * @throws RemoteException
     * @throws UnknownHostException
     * @throws SpreadException
     */
    public static Server build(ServerCfg serverCfg, SpreadConnection connection) throws RemoteException, UnknownHostException, SpreadException {
        if (thisServer == null) {
            connection.connect(InetAddress.getByName(serverCfg.getSpreaderIp()),
                    serverCfg.getSpreaderPort(),
                    serverCfg.getName(),
                    false,
                    true);
            SpreadGroup group = new SpreadGroup();
            group.join(connection, ServerValues.REPLICAS_GROUP_NAME);
            thisServer = new Server(connection, serverCfg);
        }
        return thisServer;
    }

    public static void updateGamePlayerList(List<GamePlayer> gamePlayerList) {
        thisServer.gamePlayerList = gamePlayerList;
        LOG.info(gamePlayerList);
    }

    /**
     * This is used only for testing
     * @param thisServer
     */
    public static void setThisServer(Server thisServer) {
        Server.thisServer = thisServer;
    }

    @Override
    public ServerCfg getServerCfg() throws RemoteException {
        return serverCfg;
    }

    @Override
    public void register(GamePlayer gamePlayer) throws SpreadException, GamePlayerException, RemoteException, NotBoundException, MalformedURLException {
        checkForNullAndEmptyName(gamePlayer);
        String errorMessage;

        final int size = gamePlayerList.size();
        if (size >= ServerValues.MAX_PLAYERS) {
            errorMessage = "Max players reached!!";
            LOG.error(errorMessage);
            throw new GamePlayerException(errorMessage);
        }

        final Optional<GamePlayer> optionalGamePlayer = gamePlayerList.stream()
                .filter(gp -> gp.getName().equals(gamePlayer.getName()))
                .findAny();
        if (optionalGamePlayer.isPresent()) {
            errorMessage = "Name is already taken!!";
            LOG.error(errorMessage);
            throw new GamePlayerException(errorMessage);
        }
        gamePlayer.setId(gamePlayerList.size());
        gamePlayerList.add(gamePlayer);
        updateClientsPlayersList();
        LOG.info(String.format("Player %s registered!!", gamePlayer));
    }

    private void checkForNullAndEmptyName(GamePlayer gamePlayer) throws GamePlayerException {
        String errorMessage;
        if (gamePlayer == null) {
            errorMessage = "A null object was sent!!";
            LOG.error(errorMessage);
            throw new GamePlayerException(errorMessage);
        }

        if (StringUtils.isBlank(gamePlayer.getName())) {
            errorMessage = "The name is empty!!";
            LOG.error(errorMessage);
            throw new GamePlayerException(errorMessage);
        }
    }

    @Override
    public void deregister(GamePlayer gamePlayer) throws SpreadException, GamePlayerException, RemoteException, NotBoundException, MalformedURLException {
        checkForNullAndEmptyName(gamePlayer);
        final Optional<GamePlayer> optionalGamePlayer = gamePlayerList.stream()
                .filter(gp -> gp.getName().equals(gamePlayer.getName()))
                .findAny();
        if (optionalGamePlayer.isEmpty()) {
            String errorMessage = "GamePlayer doesn't exist!!";
            LOG.error(errorMessage);
            throw new GamePlayerException(errorMessage);
        }

        gamePlayerList.remove(gamePlayer);
        AtomicInteger repairId = new AtomicInteger();
        gamePlayerList.forEach(p -> p.setId(repairId.getAndIncrement()));

        announceToGroup((Serializable) gamePlayerList);
        updateClientsPlayersList();
        LOG.info(String.format("Player %d removed!!", gamePlayer.getId()));
    }

    @Override
    public List<ServerCfg> getListOfServersWithStartTimestamp() {
        final List<ServerCfg> activeServers = actualServersList
                .stream()
                .filter(s -> s.getStartTimestamp() != null)
                .collect(Collectors.toList());
        LOG.info("Updated registers:" + actualServersList);
        return activeServers;
    }

    @Override
    public ServerCfg getMainRegistryServer() {
        final Optional<ServerCfg> optionalServerCfg = actualServersList
                .stream()
                .filter(s -> s.getStartTimestamp() != null)
                .min(Comparator.comparing(ServerCfg::getStartTimestamp));
        assert optionalServerCfg.isPresent();
        LOG.info("Main register server: " + optionalServerCfg.get());
        return optionalServerCfg.get();
    }

    @Override
    public void beginGame() throws BeginGameException, RemoteException, NotBoundException, SpreadException, MalformedURLException {
        if (gamePlayerList.size() < 2) {
            throw new BeginGameException("Not enough players are register");
        }

        for (GamePlayer gamePlayer : gamePlayerList) {
            final ClientInterface clientInterface = ServerClientUtility.lookup(gamePlayer);
            clientInterface.setId(gamePlayer.getId());
            clientInterface.startGame(gamePlayerList);
        }
        gameStatus = GameStatus.STARTED;
        announceToGroup(gameStatus);
    }

    private void updateClientsPlayersList() throws RemoteException, MalformedURLException, NotBoundException {
        for (GamePlayer gamePlayer : gamePlayerList) {
            final ClientInterface clientInterface = ServerClientUtility.lookup(gamePlayer);
            clientInterface.setGamePlayersList(gamePlayerList);
            clientInterface.setId(gamePlayer.getId());
        }
    }

    public static void setGameStatus(GameStatus gameStatus) {
        thisServer.gameStatus = gameStatus;
    }

    @Override
    public List<GamePlayer> getGamePlayersList() {
        return gamePlayerList;
    }

    @Override
    public GameStatus getGameStatus() throws RemoteException {
        return gameStatus;
    }

    public static Server getThisServer() {
        return thisServer;
    }

    /**
     * This method will be called every time a server joins/leaves the group.
     * If this server is the main registry server, it will update the content of all the servers in the group
     * @param updateGroup the message to announce the main registry server, to update all the servers in the group
     * @throws SpreadException see {@link SpreadException}
     */
    public static void updateTheGroup(UpdateGroup updateGroup) throws SpreadException {
        if (updateGroup.getTheMainServer().equals(thisServer.serverCfg)) {
            LOG.info(String.format("The main server: %s will update the content from all the other servers",
                    thisServer.serverCfg));
            announceToGroup((Serializable) thisServer.gamePlayerList);
            announceToGroup(thisServer.gameStatus);
        }
    }
}
