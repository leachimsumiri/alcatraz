package at.falb.games.alcatraz.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {
    public List<GamePlayer> getGamePlayersList() throws RemoteException;
    public void setGamePlayersList(List<GamePlayer> gamePlayersList) throws RemoteException;
    public GamePlayer getPlayer() throws RemoteException;
    public void setPlayer(GamePlayer player) throws RemoteException;
}
