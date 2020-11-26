package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import spread.SpreadException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
    /**
     * Register the {@link GamePlayer} and if successful, it will return the new id for the player.
     * @param player an instance of type {@link GamePlayer}
     * @return the new GamePlayer id
     * @throws SpreadException when its not possible to announce to the other servers, that a new GamePlayer wants to register
     * @throws GamePlayerException when the GamePlayer cannot register, because the maximum number of player was reached or the name already exists
     */
    int register(GamePlayer player) throws SpreadException, RemoteException, GamePlayerException;

    void deregister(GamePlayer player) throws SpreadException, RemoteException, GamePlayerException;

    List<ServerCfg> getActiveServers() throws RemoteException;

    ServerCfg getMainRegistryServer() throws RemoteException;

    void beginGame() throws RemoteException, SpreadException;
}

