package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.utilities.GameMove;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {
    List<GamePlayer> getGamePlayersList() throws RemoteException;
    void setGamePlayersList(List<GamePlayer> gamePlayersList) throws RemoteException;
    GamePlayer getPlayer() throws RemoteException;
    void setPlayer(GamePlayer player) throws RemoteException;

    void move(Player player, GameMove gameMove) throws RemoteException;
    void nextTurn(GamePlayer player) throws RemoteException;

    void startGame(List<GamePlayer> playerList) throws RemoteException;
}
