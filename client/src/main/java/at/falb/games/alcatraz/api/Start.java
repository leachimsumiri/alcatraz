package at.falb.games.alcatraz.api;

import spread.SpreadException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
        //GamePlayer player1 =  new GamePlayer(0);
        //player1.setName("Player1");
        //player1.setPort(123);
        //player1.setIP("127.0.0.1");
        //playerList.add(player1);
        //GamePlayer player2 =  new GamePlayer(1);
        //player2.setPort(123);
        //player2.setIP("127.0.0.1");
        //player2.setName("Player2");
        //playerList.add(player2);
        //GamePlayer player3 =  new GamePlayer(2);
        //player3.setPort(123);
        //player3.setIP("127.0.0.1");
        //player3.setName("Player3");
        //playerList.add(player3);
        //GamePlayer player4 =  new GamePlayer(3);
        //player4.setPort(123);
        //player4.setIP("127.0.0.1");
        //player4.setName("Player4");
        //playerList.add(player4);
        //System.setProperty("java.rmi.server.hostname", "192.168.1.111");

        Registry Reg = LocateRegistry.getRegistry("192.168.1.111",5099);
        ServerInterface service = (ServerInterface) Reg.lookup("first");

        ClientInterface client_1 = new Client("192.168.1.111" , 5100);   /////// local IP
        Registry Reg_1 = LocateRegistry.createRegistry(client_1.getPlayer().getPort());      ////// local IP
        Reg_1.rebind("", client_1);
        client_1.getPlayer().setName("Player1");

        ClientInterface client_2 = new Client("192.168.1.111" , 5200);
        Registry Reg_2 = LocateRegistry.createRegistry(client_2.getPlayer().getPort());
        Reg_2.rebind("", client_2);
        client_2.getPlayer().setName("Player2");

        ClientInterface client_3 = new Client("192.168.1.111" , 5300);
        Registry Reg_3 = LocateRegistry.createRegistry(client_3.getPlayer().getPort());
        Reg_2.rebind("", client_3);
        client_3.getPlayer().setName("Player2");

       int playerID = 0;
       playerID = service.register(client_1.getPlayer());
        System.out.println(playerID);
       playerID = service.register(client_2.getPlayer());
        System.out.println(playerID);
       playerID = service.register(client_3.getPlayer());

        System.out.println(playerID);



        //int amt_players = playerList.size();
        //Alcatraz game = new Alcatraz();
        //game.init(amt_players, player_id);
        //MoveListener listener = new GameMoveListener(playerList);
        //game.addMoveListener(listener);
//
        //game.showWindow();
        //game.start();
    }
}
