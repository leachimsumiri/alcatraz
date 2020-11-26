package at.falb.games.alcatraz.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {
    List<GamePlayer> getGamePlayersList() throws RemoteException;

    void setGamePlayersList(List<GamePlayer> gamePlayersList) throws RemoteException;

    GamePlayer getGamePlayer() throws RemoteException;

    void setGamePlayer(GamePlayer gamePlayer) throws RemoteException;

    void startGame(List<GamePlayer> playerList) throws RemoteException;
}
