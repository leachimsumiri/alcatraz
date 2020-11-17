package at.falb.games.alcatraz.api;

import spread.SpreadException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    int Register(ClientInterface client) throws RemoteException, SpreadException;
}

