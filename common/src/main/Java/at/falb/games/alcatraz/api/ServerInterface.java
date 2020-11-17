package at.falb.games.alcatraz.api;

import spread.SpreadException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    public int Register (ClientInterface client ) throws RemoteException, SpreadException;
}

