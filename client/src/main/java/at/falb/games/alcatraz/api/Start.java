package at.falb.games.alcatraz.api;

import spread.SpreadException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Start {
    public static void main(String[] args) throws RemoteException, NotBoundException, SpreadException {
        Registry Reg = LocateRegistry.getRegistry("192.168.1.111", 5099);
        ServerInterface service = (ServerInterface) Reg.lookup("first");
        ClientInterface client = new Client();
        int playerID;
        playerID = service.register(client);
        System.out.println(playerID);
    }
}
