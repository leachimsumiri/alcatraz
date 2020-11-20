package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.utilities.ServerCfg;
import spread.SpreadException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
    int register(GamePlayer player) throws RemoteException, SpreadException;

    List<ServerCfg> getActiveServers() throws RemoteException;

    ServerCfg getMainRegistryServer() throws RemoteException;
}

