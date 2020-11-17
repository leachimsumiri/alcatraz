package at.falb.games.alcatraz.api;

import spread.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRun {
    public static void main(String arg[]) throws RemoteException, SpreadException, UnknownHostException {
        SpreadConnection connection = new SpreadConnection();
        connection.connect(InetAddress.getByName("localhost"), 0, "Server_1", false, true);
        SpreadGroup group = new SpreadGroup();
        group.join(connection, "ReplicasGroup");
        ServerInterface server_1 = new Server(connection);
        Registry Reg_1 = LocateRegistry.createRegistry(5099);
        Reg_1.rebind("first", server_1);
        System.out.println("First Server started");

        System.out.println(connection.getPrivateGroup());
    }
}

