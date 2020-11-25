package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.group.communication.SpreadMessageListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private static final Logger LOG = LogManager.getLogger(Server.class);

    private List<GamePlayer> gamePlayerList = new ArrayList<>();
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

    public static ServerCfg getServerCfg() {
        return thisServer.serverCfg;
    }

    public static void updateActualServersList(ServerCfg serverCfg) {
        // Added this because the indexof, wasn't finding the server
        final Optional<ServerCfg> optionalServerCfg = actualServersList
                .stream()
                .filter(s -> s.equals(serverCfg))
                .findAny();

        assert optionalServerCfg.isPresent() : "This should not happen, the server should exist";

        final int i = actualServersList.indexOf(optionalServerCfg.get());

        actualServersList.set(i, serverCfg);
        thisServer.getActiveServers();
        thisServer.getMainRegistryServer();
    }

    public static Server build(ServerCfg serverCfg) throws RemoteException, UnknownHostException, SpreadException {
        if (thisServer == null) {
            build(serverCfg, new SpreadConnection());
            Registry registry = LocateRegistry.createRegistry(serverCfg.getRegistryPort());
            registry.rebind(serverCfg.getName(), thisServer);
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

    public List<GamePlayer> getGamePlayerList() {
        return gamePlayerList;
    }

    /**
     * This is used only for testing
     * @param thisServer
     */
    public static void setThisServer(Server thisServer) {
        Server.thisServer = thisServer;
    }

    @Override
    public int register(GamePlayer gamePlayer) throws SpreadException, GamePlayerException {
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
        int freePort = getFreePort(gamePlayer);
        announceToGroup((Serializable) gamePlayerList);
        LOG.info(String.format("Player %d registered!!", freePort));
        return freePort;
    }

    /**
     * Just like the ports in the Nintendo 64, if a port is free, this user will get it
     * @param gamePlayer an instance of {@link GamePlayer}
     * @return an id between 0 and 3
     */
    private int getFreePort(GamePlayer gamePlayer) {
        int freePort = -1;
        gamePlayer.setId(freePort);// to make sure, that the user will not pass a player with an id already
        gamePlayerList.add(gamePlayer);
        for (int i = 0; i < gamePlayerList.size(); i++) {
            int finalI = i;// This is from intellij, i wanted this filter(gp -> gp.getId() == i)
            final Optional<GamePlayer> gamePlayerOptional = gamePlayerList
                    .stream()
                    .filter(gp -> gp.getId() == finalI)
                    .findAny();
            if (gamePlayerOptional.isEmpty()) {
                freePort = i;
                break;
            }
        }
        gamePlayer.setId(freePort);
        gamePlayerList.set(freePort, gamePlayer);
        return freePort;
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
    public void deregister(GamePlayer gamePlayer) throws SpreadException, GamePlayerException {
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
        announceToGroup((Serializable) gamePlayerList);
        LOG.info(String.format("Player %d removed!!", gamePlayer.getId()));
    }

    @Override
    public List<ServerCfg> getActiveServers() {
        final List<ServerCfg> activeServers = actualServersList
                .stream()
                .filter(s -> s.getStartTimestamp() != null)
                .collect(Collectors.toList());
        LOG.info("Updated registers:" + actualServersList);
        return activeServers;
    }

    @Override
    public ServerCfg getMainRegistryServer() {
        final ServerCfg mainRegistryServer = ServerClientUtility.getMainRegistryServer(getActiveServers());
        LOG.info("Main register server: " + mainRegistryServer);
        return mainRegistryServer;
    }

    @Override
    public void beginGame() {
        if(playerNumber >= 2) {
            this.ClientList.forEach(clientInterface -> {
                try {
                    clientInterface.startGame(this.gamePlayerList);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
