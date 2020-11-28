package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.utilities.GameMove;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {
    ServerInterface getPrimary() throws RemoteException;

    List<GamePlayer> getGamePlayersList() throws RemoteException;

    void setGamePlayersList(List<GamePlayer> gamePlayersList) throws RemoteException;

    GamePlayer getGamePlayer() throws RemoteException;

    void setGamePlayer(GamePlayer gamePlayer) throws RemoteException;

    void move(Player player, GameMove gameMove) throws RemoteException;
    void nextTurn(GamePlayer player) throws RemoteException;

    void startGame(List<GamePlayer> gamePlayersList) throws RemoteException;
}
