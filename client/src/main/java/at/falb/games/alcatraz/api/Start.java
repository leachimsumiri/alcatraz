package at.falb.games.alcatraz.api;

import spread.SpreadException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class Start {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, SpreadException {
        // TODO user interface to type in player name
        // TODO Connect to server and send player name
        // TODO Get assigned Player ID from server
        // TODO Ask for list of player repeatedly and update names in UI
        // TODO Start game button if 2-4 players are registered
        // TODO receive list of all players when game starts

        //int player_id = 0; // TODO change to ID received from server
        //List<GamePlayer> playerList = new ArrayList<>(); // TODO change to player list received from server
        /*GamePlayer player1 =  new GamePlayer(0);
        player1.setName("Player1");
        player1.setPort(123);
        player1.setIP("127.0.0.1");
        playerList.add(player1);
        GamePlayer player2 =  new GamePlayer(1);
        player2.setPort(123);
        player2.setIP("127.0.0.1");
        player2.setName("Player2");
        playerList.add(player2);
        GamePlayer player3 =  new GamePlayer(2);
        player3.setPort(123);
        player3.setIP("127.0.0.1");
        player3.setName("Player3");
        playerList.add(player3);
        GamePlayer player4 =  new GamePlayer(3);
        player4.setPort(123);
        player4.setIP("127.0.0.1");
        player4.setName("Player4");
        playerList.add(player4);
        System.setProperty("java.rmi.server.hostname", "192.168.1.111");
        Registry Reg = LocateRegistry.getRegistry("192.168.1.111",5099);
        ServerInterface service = (ServerInterface) Reg.lookup("first");
        ServerInterface service = (ServerInterface) Naming.lookup("rmi://localhost:5099/first");
        System.out.println("connection Done!!");
        ClientInterface client = new Client();
        ClientInterface client_2 = new Client();
        UnicastRemoteObject.exportObject(client, 0);
        UnicastRemoteObject.exportObject(client_2, 1);
        int playerID = service.register(client);
        System.out.println(playerID);*/

        Registry Reg = LocateRegistry.getRegistry("192.168.1.111",5099);
        ServerInterface service = (ServerInterface) Reg.lookup("first");

        Client client = new Client();
        client.getPlayer().setId(service.register(client));

        //at Click on what? idk
        //service.beginGame();
    }
}
