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
import java.util.List;

public interface ServerInterface extends Remote {

    ServerCfg getServerCfg() throws RemoteException;

    /**
     * Register the {@link GamePlayer} and if successful, it will return the new id for the player.
     * @param player an instance of type {@link GamePlayer}
     * @throws SpreadException when its not possible to announce to the other servers, that a new GamePlayer wants to register
     * @throws GamePlayerException when the GamePlayer cannot register, because the maximum number of player was reached or the name already exists
     */
    void register(GamePlayer player) throws SpreadException, RemoteException, GamePlayerException, NotBoundException, MalformedURLException;

    void deregister(GamePlayer player) throws SpreadException, RemoteException, GamePlayerException, NotBoundException, MalformedURLException;

    /**
     * All the register servers with {@link ServerCfg#getStartTimestamp()} != null
     * @return a list of {@link ServerCfg}
     * @throws RemoteException see {@link RemoteException}
     */
    List<ServerCfg> getListOfServersWithStartTimestamp() throws RemoteException;

    /**
     * The main register server ist the oldest server in the list {@link ServerInterface#getListOfServersWithStartTimestamp()}
     * @return the main register server
     * @throws RemoteException see {@link RemoteException}
     */
    ServerCfg getMainRegistryServer() throws RemoteException;

    /**
     * The {@link GamePlayer} asks for the game to begin and every player will receive the actual list of players
     * @throws RemoteException
     * @throws SpreadException
     * @throws BeginGameException
     */
    void beginGame() throws RemoteException, SpreadException, BeginGameException, NotBoundException, MalformedURLException;

    List<GamePlayer> getGamePlayersList() throws RemoteException;

    GameStatus getGameStatus() throws RemoteException;
}

