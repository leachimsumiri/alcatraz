package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.ServerInterface;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {

    private List<GamePlayer> gamePlayersList = new ArrayList<>();
    private GamePlayer Player = new GamePlayer();
    //This is the list of servers, that will be updated every x seconds.
    private final List<ServerInterface> serverList = new ArrayList<>();
    // This Server is used for the first time
    private ServerInterface mainRegistryServer;

    protected Client() throws RemoteException {
    }
    //


    @Override
    public List<GamePlayer> getGamePlayersList() throws RemoteException {
        return gamePlayersList;
    }

    @Override
    public void setGamePlayersList(List<GamePlayer> gamePlayersList) throws RemoteException {
        this.gamePlayersList = gamePlayersList;
    }

    @Override
    public GamePlayer getPlayer() throws RemoteException {
        return Player;
    }

    @Override
    public void setPlayer(GamePlayer player) throws RemoteException {
        Player = player;
    }

   @Override
    public void startGame(List<GamePlayer> playerList){
        Alcatraz game = new Alcatraz();
        game.init(playerList.size(), this.Player.getId());

        MoveListener listener = new GameMoveListener(playerList);
        game.addMoveListener(listener);

        game.showWindow();
        game.start();
    }


}
