package at.falb.games.alcatraz.api;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface , Serializable {

    private List<GamePlayer> GamePlayersList = new ArrayList<>();
    private GamePlayer Player = new GamePlayer(0);

    public Client(String IP, int port) throws RemoteException{
        super();
        this.Player.setIP(IP); ;
        this.Player.setPort(port);
    }

    @Override
    public List<GamePlayer> getGamePlayersList() throws RemoteException {
        return GamePlayersList;
    }

    @Override
    public void setGamePlayersList(List<GamePlayer> gamePlayersList) throws RemoteException {
        GamePlayersList = gamePlayersList;
    }

    @Override
    public GamePlayer getPlayer() throws RemoteException {
        return Player;
    }

    @Override
    public void setPlayer(GamePlayer player) throws RemoteException {
        Player = player;
    }

}
