package at.falb.games.alcatraz.api;

import spread.SpreadException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    public int Register (Client client ) throws RemoteException, SpreadException;
}

