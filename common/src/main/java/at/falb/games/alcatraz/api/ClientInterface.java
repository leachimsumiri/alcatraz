package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.utilities.GameMove;

import javax.swing.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {
    /**
     * It searches for the instance from the main server.
     * <ul>
     *     <li>when the main registry server was found, it will save it, till it isn't available anymore</li>
     *     <li>if the saved main registry server isn't available anymore, it will search for a new registry server</li>
     * </ul>
     * @return the new main registry server
     * @throws RemoteException see {@link RemoteException}
     */
    ServerInterface getPrimary() throws RemoteException;

    void setFrame(JFrame frame) throws RemoteException;

    List<GamePlayer> getGamePlayersList() throws RemoteException;

    void setGamePlayersList(List<GamePlayer> gamePlayersList) throws RemoteException;

    GamePlayer getGamePlayer() throws RemoteException;

    void setGamePlayer(GamePlayer gamePlayer) throws RemoteException;

    void move(Player player, GameMove gameMove) throws RemoteException;

    void nextTurn(GamePlayer player) throws RemoteException;

    void startGame(List<GamePlayer> gamePlayersList) throws RemoteException;

    void setId(int id) throws RemoteException;
}
