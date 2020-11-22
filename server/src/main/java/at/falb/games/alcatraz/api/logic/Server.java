package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.utilities.ClientCfg;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.group.communication.SpreadMessageListener;
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
import java.util.stream.Collectors;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private static final Logger LOG = LogManager.getLogger(Server.class);
    private Alcatraz game;
    private final List<GamePlayer> gamePlayerList = new ArrayList<>();
    private final List<ClientInterface> ClientList = new ArrayList<>();
    private final SpreadConnection connection;
    private static Server thisServer;
    private final ServerCfg serverCfg;
    private int playerNumber;
    private final SpreadMessageListener spreadMessageListener;
    private static final List<ServerCfg> actualServersList = new ArrayList<>();

    private Server(SpreadConnection connection, ServerCfg serverCfg) throws RemoteException {
        super();
        this.serverCfg = serverCfg;
        this.connection = connection;
        this.spreadMessageListener = new SpreadMessageListener();
        connection.add(this.spreadMessageListener);
    }

    public static <M extends Serializable> void announceToGroup(M messageObject) {
        try {
            SpreadMessage message = new SpreadMessage();
            message.setObject(messageObject);
            message.addGroup(ServerValues.REPLICAS_GROUP_NAME);
            message.setReliable();
            message.setSelfDiscard(true);
            thisServer.connection.multicast(message);
        } catch (SpreadException e) {
            LOG.error("The Server information could not be spread", e);
        }
    }

    public static List<ServerCfg> getActualServersList() {
        return actualServersList;
    }

    public static ServerCfg getServerCfg() {
        return thisServer.serverCfg;
    }

    public static void updateActualServersList(ServerCfg serverCfg) {
        final int i = actualServersList.indexOf(serverCfg);
        assert i < 0 : "This should not happen, the server should exist";
        actualServersList.set(i, serverCfg);
        thisServer.getActiveServers();
        thisServer.getMainRegistryServer();
    }

    public static Server build(ServerCfg serverCfg) throws RemoteException, UnknownHostException, SpreadException {
        if (thisServer == null) {
            final SpreadConnection connection = new SpreadConnection();
            connection.connect(InetAddress.getByName(serverCfg.getSpreaderIp()),
                    serverCfg.getSpreaderPort(),
                    serverCfg.getName(),
                    false,
                    true);
            SpreadGroup group = new SpreadGroup();
            group.join(connection, ServerValues.REPLICAS_GROUP_NAME);
            thisServer = new Server(connection, serverCfg);
            Registry registry = LocateRegistry.createRegistry(serverCfg.getRegistryPort());
            registry.rebind(serverCfg.getName(), thisServer);
        }
        return thisServer;
    }

    @Override
    public int register(ClientInterface client) throws RemoteException, SpreadException {
        int PlayerID =0;
        //if (this.PlayerList.contains(client.getPlayer())){  // To avoid the dopple register from the same client (player)
        //if (this.ClientList.contains(client)){  // To avoid the dopple register from the same client (player)
        //    System.out.println("Client already exists!!");
        //    return client.getPlayer().getId();
        //}
            if (this.playerNumber < 4){
            for (GamePlayer P : this.gamePlayerList ){
                if (P.getName().equals(client.getPlayer().getName())){  // To avoid names similarity
                    System.out.println("Player name already taken!!");
                    return -1;
                }
            }
            PlayerID = this.playerNumber ;   // the new playerID becomes the the number of already existing players
            this.gamePlayerList.add(client.getPlayer());
            //this.ClientList.add(client);
            this.playerNumber++;
            SpreadMessage message = new SpreadMessage();
            message.setObject(this.playerNumber);
            message.addGroup("ReplicasGroup");  /////////////////////////////////////////
            message.setReliable();
            connection.multicast(message);
            System.out.println("New Player!!");
            return PlayerID;
        }
            else{
            System.out.println("Max players reached!!");
            return -2;
        }
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
