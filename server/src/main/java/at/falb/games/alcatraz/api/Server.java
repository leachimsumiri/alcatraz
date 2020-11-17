package at.falb.games.alcatraz.api;

import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadMessage;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements ServerInterface, AdvancedMessageListener {
    private Alcatraz Game;
    private final List<GamePlayer> PlayerList;
    private final List<ClientInterface> ClientList;
    private int PlayersNo;
    private SpreadConnection connection;

    public Server() throws RemoteException {
        super();
        PlayerList = new ArrayList<>();
        ClientList = new ArrayList<>();
        PlayersNo = 0;
        connection = new SpreadConnection();
    }

    public Server(SpreadConnection connection) throws RemoteException {
        this();
        this.connection = connection;

    }

    @Override
    public int Register(ClientInterface client) throws RemoteException, SpreadException {
        int PlayerID =0;
        //if (this.PlayerList.contains(client.getPlayer())){  // To avoid the dopple register from the same client (player)
        //if (this.ClientList.contains(client)){  // To avoid the dopple register from the same client (player)
        //    System.out.println("Client already exists!!");
        //    return client.getPlayer().getId();
        //}
        if (this.PlayersNo < 4){
            for (GamePlayer P : this.PlayerList ){
                if (P.getName().equals(client.getPlayer().getName())){  // To avoid names similarity
                    System.out.println("Player name already taken!!");
                    return -1;
                }
            }
            PlayerID = this.PlayersNo ;   // the new playerID becomes the the number of already existing players
            this.PlayerList.add(client.getPlayer());
            //this.ClientList.add(client);
            this.PlayersNo++;
            SpreadMessage message = new SpreadMessage();
            message.setObject(this.PlayersNo);
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
