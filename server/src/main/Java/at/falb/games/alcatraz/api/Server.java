package at.falb.games.alcatraz.api;

import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadMessage;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Server extends UnicastRemoteObject implements ServerInterface, Serializable, AdvancedMessageListener {
    private Alcatraz Game ;
    private List<GamePlayer> PlayerList;
    private int PlayersNo ;
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
    public int Register(Client client) throws RemoteException, SpreadException  {
        int PlayerID =0;
        if (this.PlayerList.contains(client.getPlayer())){  // To avoid the dopple register from the same client (player)
            return client.getPlayer().getId();
        }
        if (PlayersNo < 4){
            for (GamePlayer P : this.PlayerList ){
                if (P.getName().equals(client.getPlayer().getName())){  // To avoid names similarity
                    return -1;
                }
            }
            PlayerID = PlayersNo ;   // the new playerID becomes the the number of already existing players
            client.getPlayer().setId(PlayerID);
            this.PlayerList.add(client.getPlayer());
            PlayersNo++;
            SpreadMessage message = new SpreadMessage();
            message.setObject(this.PlayersNo);
            message.addGroup(connection.getPrivateGroup().toString());  /////////////////////////////////////////
            message.setReliable();
            connection.multicast(message);
            return PlayerID;
        }
        else{
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
