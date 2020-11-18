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

        ClientInterface client_1 = new Client("192.168.1.111" , 5100);   /////// local IP
        Registry Reg_1 = LocateRegistry.createRegistry(client_1.getPlayer().getPort());      ////// local IP
        Reg_1.rebind("", client_1);
        client_1.getPlayer().setName("Player1");
    }
}
