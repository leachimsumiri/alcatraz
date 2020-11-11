package at.falb.games.alcatraz.api;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface {

    private List<GamePlayer> GamePlayersList = new ArrayList<>();
    private GamePlayer Player = new GamePlayer(0);

    public Client() throws RemoteException{
        super();
    }

    public List<GamePlayer> getGamePlayersList() {
        return GamePlayersList;
    }

    public void setGamePlayersList(List<GamePlayer> gamePlayersList) {
        GamePlayersList = gamePlayersList;
    }

    public GamePlayer getPlayer() {
        return Player;
    }

    public void setPlayer(GamePlayer player) {
        Player = player;
    }
}
