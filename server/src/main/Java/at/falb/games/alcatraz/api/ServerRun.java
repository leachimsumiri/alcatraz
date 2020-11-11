package at.falb.games.alcatraz.api;

import spread.AdvancedMessageListener;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRun {
    public static void main(String arg[]) throws RemoteException, SpreadException, UnknownHostException {
        Server S = new Server();
        Registry Reg = LocateRegistry.createRegistry(5099);
        Reg.rebind("SS", S);
        System.out.println("S Server started");
        SpreadConnection connection = new SpreadConnection();
        connection.connect(InetAddress.getByName("localhost:1099/SS"), 0, "ServerGroup", false, false);
        System.out.println(connection.getPrivateGroup());

        Server server_1 = new Server(connection);
        Reg.rebind("first", server_1);
        System.out.println("First Server started");

        Server server_2 = new Server(connection);
        Reg.rebind("second", server_2);
        System.out.println("Second Server started");

        Server server_3 = new Server(connection);
        Reg.rebind("third", server_3);
        System.out.println("Third Server started");
    }
}

