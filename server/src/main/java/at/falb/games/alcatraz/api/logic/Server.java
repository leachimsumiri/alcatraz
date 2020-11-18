package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.group.communication.SpreadListener;
import at.falb.games.alcatraz.impl.Game;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private static final Logger LOG = LogManager.getLogger(Server.class);
    private Alcatraz game;
    private final List<GamePlayer> gamePlayerList = new ArrayList<>();
    private SpreadConnection connection;
    private int playerNumber;
    private SpreadListener spreadListener;

    public Server() throws RemoteException {
        super();
        connection = new SpreadConnection();
    }

    public Server(SpreadConnection connection) throws RemoteException {
        super();
        this.connection = connection;
        this.spreadListener = new SpreadListener();
        connection.add(this.spreadListener);
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

}
