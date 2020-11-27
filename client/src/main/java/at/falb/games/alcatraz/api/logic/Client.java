package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.*;
import at.falb.games.alcatraz.api.utilities.GameMove;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface {

    private List<GamePlayer> gamePlayersList;
    private GamePlayer gamePlayer;
    //This is the list of servers, that will be updated every x seconds.
    private final List<ServerInterface> serverList = new ArrayList<>();
    // This Server is used for the first time
    private ServerInterface mainRegistryServer;

    private Alcatraz game = new Alcatraz();
    private MoveListener listener;

    public Client(GamePlayer gamePlayer, List<GamePlayer> gamePlayersList) throws RemoteException {
        this.gamePlayer = gamePlayer;
        this.gamePlayersList = gamePlayersList;
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
    public GamePlayer getGamePlayer() throws RemoteException {
        return gamePlayer;
    }

    @Override
    public void setGamePlayer(GamePlayer gamePlayer) throws RemoteException {
        this.gamePlayer = gamePlayer;
    }


    @Override
    public void move(Player player, GameMove gameMove) throws RemoteException {
        try {
            this.game.doMove(player, gameMove.getPrisoner(), gameMove.getRowOrCol(), gameMove.getRow(),
                    gameMove.getColumn());
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextTurn(GamePlayer player) throws RemoteException {
        System.out.println("Received next turn message");
    }


    @Override
    public void startGame(){
        this.game.init(this.gamePlayersList.size() + 1, this.gamePlayer.getId());

        this.listener = new GameMoveListener(this.gamePlayersList);
        this.game.addMoveListener(this.listener);

        this.game.showWindow();
        this.game.start();
    }
}
