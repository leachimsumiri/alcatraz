package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.*;
import at.falb.games.alcatraz.api.utilities.GameMove;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {

    private List<GamePlayer> GamePlayersList = new ArrayList<>();
    private GamePlayer player;
    private Alcatraz game = new Alcatraz();
    private MoveListener listener;

    public Client(GamePlayer player) throws RemoteException{
        super();
        this.player = player;
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
        return player;
    }

    @Override
    public void setPlayer(GamePlayer player) throws RemoteException {
        this.player = player;
    }

    @Override
    public void move(Player player, GameMove gameMove) throws RemoteException {
        System.out.println(player.getName());
        System.out.println(gameMove.getRow());

    }

/*
    @Override
    public void move(Player player, GameMove gameMove) throws RemoteException {
        System.out.println("Called Move!");

        try {
            this.game.doMove(player, gameMove.getPrisoner(), gameMove.getRowOrCol(), gameMove.getRow(),
                    gameMove.getColumn());
        } catch (IllegalMoveException e) {
            e.printStackTrace();
        }

    }

 */

    @Override
    public void nextTurn(GamePlayer player) throws RemoteException {
        System.out.println("Received next turn message");
    }


    @Override
    public void startGame(List<GamePlayer> playerList){
        this.game.init(playerList.size() + 1, this.player.getId());

        this.listener = new GameMoveListener(playerList);
        this.game.addMoveListener(this.listener);

        this.game.showWindow();
        this.game.start();
    }
}
