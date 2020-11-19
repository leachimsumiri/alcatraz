package at.falb.games.alcatraz.api;

import spread.SpreadException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDateTime;

public interface ServerInterface extends Remote {
    int register(GamePlayer player) throws RemoteException, SpreadException;

    void sayHello(String id, LocalDateTime startTimestamp) throws RemoteException;
}

