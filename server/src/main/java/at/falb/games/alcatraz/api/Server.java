package at.falb.games.alcatraz.api;

import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements ServerInterface, AdvancedMessageListener {
    private Alcatraz Game;
    private final List<GamePlayer> PlayerList;
    private int PlayersNo;
    private SpreadConnection connection;

    public Server() throws RemoteException {
        super();
        PlayerList = new ArrayList<>();
        PlayersNo = 0;
        connection = new SpreadConnection();
    }

    public Server(SpreadConnection connection) throws RemoteException {
        this();
        this.connection = connection;

    }

    @Override
    public int register(GamePlayer player) throws RemoteException, SpreadException {
        int PlayerID =0;
        if (this.PlayersNo < 4){
            for (GamePlayer P : this.PlayerList ){
                if (P.getIp().equals(player.getIp()) && P.getPort() == player.getPort()) {  // avoid duplicated register from the same host
                    System.out.println("Client already exists!!");
                    return player.getId();
                }

                if (P.getName().equals(player.getName())){  // To avoid names similarity
                    System.out.println("Name is already taken!!");
                    return -1;
                }
            }
            PlayerID = this.PlayersNo ;   // the new playerID becomes the the number of already existing players
            player.setId(PlayerID);
            this.PlayerList.add(player);
            this.PlayersNo++;
            SpreadMessage message = new SpreadMessage();
            message.setObject(this.PlayersNo);
            message.addGroup("ReplicasGroup");  /////////////////////////////////////////
            message.setReliable();
            connection.multicast(message);
            System.out.println("New Player!!");
            return PlayerID;
        } else {
            System.out.println("Max players reached!!");
            return -2;
        }
    }

    @Override
    public void sayHello(String id, LocalDateTime startTimestamp) throws RemoteException {
        // TODO: Wee need to select one Server implementation
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {

    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        try {
            this.PlayersNo = (int) spreadMessage.getObject();
        } catch (SpreadException e) {
            e.printStackTrace();
        }
    }
}
