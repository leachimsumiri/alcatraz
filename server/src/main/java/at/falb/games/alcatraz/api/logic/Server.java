package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.group.communication.SpreadMessageListener;
import at.falb.games.alcatraz.api.group.communication.SpreadMessageSender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadConnection;
import spread.SpreadException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private static final Logger LOG = LogManager.getLogger(Server.class);
    private Alcatraz game;
    private final List<GamePlayer> gamePlayerList = new ArrayList<>();
    private final SpreadConnection connection;
    private int playerNumber;
    private SpreadMessageListener spreadMessageListener;
    private final SpreadMessageSender spreadMessageSender;

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

    @Override
    public int register(GamePlayer gamePlayer) throws RemoteException, SpreadException {
/*        if (gamePlayerList.size() > ServerValues.MAX_PLAYERS) {
            String msg = "Max players reached!!";
            LOG.error(msg);
            return -2;
        }
        if (gamePlayerList.contains(gamePlayer.getPlayer())) {
            String msg = "Player name already taken!!";
            LOG.error(msg);
            return -1;
        }

        gamePlayerList.add(gamePlayer.getPlayer());
        final int size = gamePlayerList.size();
        SpreadMessage message = new SpreadMessage();
        message.setObject(size);
        message.addGroup(ServerValues.REPLICAS_GROUP_NAME);
        message.setReliable();
        connection.multicast(message);
        LOG.info("New Player!!");
        return size;*/
        return 0;
    }

    @Override
    public void sayHello(String id, LocalDateTime startTimestamp) throws RemoteException {
        // TODO: Wee need to select one Server implementation
        SpreadMessageListener.getGroupConnectionList().forEach(gc -> {
            if (gc.getId().equals(id)) {
                gc.setStartTimestamp(startTimestamp);
            }
        });
        LOG.info("Updated registers:" + SpreadMessageListener.getGroupConnectionList());
        LOG.info("Main register server: " + SpreadMessageListener.getMainRegistryServer());
    }

}
