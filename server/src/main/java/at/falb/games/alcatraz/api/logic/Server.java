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

    private static Server instance = null;
    private final ServerCfg serverCfg;
    private final List<ServerCfg> actualServersList = new ArrayList<>();

    private Server(SpreadConnection connection, ServerCfg serverCfg) throws RemoteException {
        this.serverCfg = serverCfg;
        this.connection = connection;
        connection.add(new SpreadMessageListener());
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
    public <M extends Serializable> void announceToGroup(M messageObject) throws SpreadException {
        SpreadMessage message = new SpreadMessage();
        message.setObject(messageObject);
        message.addGroup(ServerValues.REPLICAS_GROUP_NAME);
        message.setReliable();
        message.setSelfDiscard(true);
        instance.connection.multicast(message);
    }

    public List<ServerCfg> getActualServersList() {
        return actualServersList;
    }

    public void updateActualServersList(ServerCfg serverCfg) throws SpreadException {
        // Added this because the indexof, wasn't finding the server
        final Optional<ServerCfg> optionalServerCfg = instance.actualServersList
                .stream()
                .filter(s -> s.equals(serverCfg))
                .findAny();
        assert optionalServerCfg.isPresent() : "This should not happen, the server should exist";

        final int i = instance.actualServersList.indexOf(optionalServerCfg.get());
        instance.actualServersList.set(i, serverCfg);

        //It takes some time(nanoseconds) till all servers in the group know about each other
        if (instance.getListOfServersWithStartTimestamp().size() == instance.actualServersList.size()) {

            // One of the servers, will ask the main registry server to update all the servers in the group
            final ServerCfg mainRegistryServer = instance.getMainRegistryServer();
            announceToGroup(new UpdateGroup(mainRegistryServer));
        }
    }

    /**
     * This needs to be synchronized, because: Incorrect lazy initialization
     * Findbug is referencing a potential threading issue. In a multi thread environment,
     * there would be potential for your singleton to be created more than once with your current code.
     * https://stackoverflow.com/a/6782690
     * @param serverCfg this server configuration
     * @return an instance of {@link Server}
     * @throws RemoteException
     * @throws UnknownHostException
     * @throws SpreadException
     * @throws MalformedURLException
     */
    public static Server build(ServerCfg serverCfg) throws RemoteException, UnknownHostException, SpreadException, MalformedURLException {
        if (instance == null) {
            final SpreadConnection connection = new SpreadConnection();
            connection.connect(InetAddress.getByName(serverCfg.getSpreaderIp()),
                    serverCfg.getSpreaderPort(),
                    serverCfg.getName(),
                    false,
                    true);
            SpreadGroup group = new SpreadGroup();
            group.join(connection, ServerValues.REPLICAS_GROUP_NAME);
            instance = new Server(connection, serverCfg);
            ServerClientUtility.createRegistry(instance);
        }
        return instance;
    }

    public void updateGamePlayerList(List<GamePlayer> gamePlayerList) {
        this.gamePlayerList = gamePlayerList;
        LOG.info(gamePlayerList);
    }

    @Override
    public ServerCfg getServerCfg() {
        return serverCfg;
    }

    @Override
    public void register(GamePlayer gamePlayer) throws GamePlayerException, RemoteException, SpreadException {

        checkForNullAndEmptyName(gamePlayer);
        String errorMessage;
        if (gameStatus != GameStatus.NOT_STARTED) {
            errorMessage = "Game already started!!";
            LOG.error(errorMessage);
            throw new GamePlayerException(errorMessage);
        }

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
        announceToGroup((Serializable) gamePlayerList);
        updateClientsPlayersList(gamePlayerList);
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
    public void deregister(GamePlayer gamePlayer) throws SpreadException, GamePlayerException, RemoteException {
        checkForNullAndEmptyName(gamePlayer);
        final Optional<GamePlayer> optionalGamePlayer = gamePlayerList.stream()
                .filter(gp -> gp.getName().equals(gamePlayer.getName()))
                .findAny();
        if (optionalGamePlayer.isEmpty()) {
            String errorMessage = "GamePlayer doesn't exist!!";
            LOG.error(errorMessage);
            throw new GamePlayerException(errorMessage);
        }

        List<GamePlayer> gamePlayerListOld = new ArrayList<>(gamePlayerList);
        gamePlayerList.remove(gamePlayer);
        AtomicInteger repairId = new AtomicInteger();
        gamePlayerList.forEach(p -> p.setId(repairId.getAndIncrement()));

        announceToGroup((Serializable) gamePlayerList);
        updateClientsPlayersList(gamePlayerListOld);
        LOG.info(String.format("Player %d removed!!", gamePlayer.getId()));
    }

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
    public void beginGame() throws BeginGameException, RemoteException, SpreadException {
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

    private void updateClientsPlayersList(List<GamePlayer> gamePlayerListOld) throws RemoteException {
        for (GamePlayer gamePlayer : gamePlayerListOld) {
            final ClientInterface clientInterface = ServerClientUtility.lookup(gamePlayer);
            clientInterface.setGamePlayersList(gamePlayerList);
            clientInterface.setId(gamePlayer.getId());
        }
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public static Server getInstance() {
        return instance;
    }

    /**
     * This method will be called every time a server joins/leaves the group.
     * If this server is the main registry server, it will update the content of all the servers in the group
     * @param updateGroup the message to announce the main registry server, to update all the servers in the group
     * @throws SpreadException see {@link SpreadException}
     */
    public void updateTheGroup(UpdateGroup updateGroup) throws SpreadException {
        if (updateGroup.getTheMainServer().equals(instance.serverCfg)) {
            LOG.info(String.format("The main server: %s will update the content from all the other servers",
                    instance.serverCfg));
            announceToGroup((Serializable) instance.gamePlayerList);
            announceToGroup(instance.gameStatus);
        }
    }
}
