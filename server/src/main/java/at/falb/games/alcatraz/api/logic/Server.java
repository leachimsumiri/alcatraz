package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerCfg;
import at.falb.games.alcatraz.api.ServerClientUtility;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.group.communication.SpreadMessageListener;
import at.falb.games.alcatraz.api.group.communication.SpreadMessageSender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadConnection;
import spread.SpreadException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private static final Logger LOG = LogManager.getLogger(Server.class);
    private Alcatraz game;
    private final List<GamePlayer> gamePlayerList = new ArrayList<>();
    private final SpreadConnection connection;
    private int playerNumber;
    private SpreadMessageListener spreadMessageListener;
    private final SpreadMessageSender spreadMessageSender;
    private static final List<ServerCfg> actualServersList = new ArrayList<>();

    public Server() throws RemoteException {
        super();
        connection = new SpreadConnection();
        spreadMessageSender = new SpreadMessageSender(connection);
    }

    public Server(SpreadConnection connection) throws RemoteException {
        super();
        this.connection = connection;
        this.spreadMessageListener = new SpreadMessageListener();
        this.spreadMessageSender = new SpreadMessageSender(connection);
        connection.add(this.spreadMessageListener);
    }

    public static List<ServerCfg> getActualServersList() {
        return actualServersList;
    }

    @Override
    public int register(GamePlayer gamePlayer) throws RemoteException, SpreadException {
        return 0;
    }

    @Override
    public void sayHello(ServerCfg serverCfg) {
        actualServersList.forEach(gc -> {
            if (gc.equals(serverCfg)) {
                gc.copy(serverCfg);
            }
        });
        getActiveServers();
        getMainRegistryServer();
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
}
