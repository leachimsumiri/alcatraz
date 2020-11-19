package at.falb.games.alcatraz.api;

import spread.SpreadException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
    int register(GamePlayer player) throws RemoteException, SpreadException;

    void sayHello(ServerCfg serverCfg) throws RemoteException;

    List<ServerCfg> getActiveServers() throws RemoteException;

    ServerCfg getMainRegistryServer() throws RemoteException;
}

