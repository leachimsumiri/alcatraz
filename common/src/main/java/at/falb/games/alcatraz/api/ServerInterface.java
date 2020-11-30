package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.utilities.GameStatus;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import spread.SpreadException;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {

    ServerCfg getServerCfg() throws RemoteException;

    /**
     * Register the {@link GamePlayer} and if successful, it will set the id for the new player.
     * @param player which will be registered
     * @throws SpreadException when its not possible to announce to the other servers, that a new GamePlayer wants to register
     * @throws GamePlayerException when the GamePlayer cannot register, because the maximum number of player was reached or the name already exists
     */
    void register(GamePlayer player) throws SpreadException, RemoteException, GamePlayerException, NotBoundException, MalformedURLException;

    /**
     * Deregister the @link GamePlayer}, update the id of all players and update the id to all players
     * @param player which will be deregister
     * @throws SpreadException see {@link SpreadException}
     * @throws RemoteException see {@link RemoteException}
     * @throws GamePlayerException see {@link GamePlayerException}
     * @throws NotBoundException see {@link NotBoundException}
     * @throws MalformedURLException see {@link MalformedURLException}
     */
    void deregister(GamePlayer player) throws SpreadException, RemoteException, GamePlayerException, NotBoundException, MalformedURLException;

    /**
     * The main register server ist the oldest server in the list Server#getListOfServersWithStartTimestamp()
     * @return the main register server
     * @throws RemoteException see {@link RemoteException}
     */
    ServerCfg getMainRegistryServer() throws RemoteException;

    /**
     * The {@link GamePlayer} asks for the game to begin and every player will receive the actual list of players
     * @throws RemoteException see {@link RemoteException}
     * @throws SpreadException see {@link SpreadException}
     * @throws BeginGameException see {@link BeginGameException}
     */
    void beginGame() throws RemoteException, SpreadException, BeginGameException, NotBoundException, MalformedURLException;

    /**
     * The actual game status.
     * @return {@link GameStatus#NOT_STARTED} or {@link GameStatus#STARTED}
     * @throws RemoteException
     */
    GameStatus getGameStatus() throws RemoteException;
}

