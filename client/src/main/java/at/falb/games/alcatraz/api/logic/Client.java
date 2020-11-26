package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.*;
import at.falb.games.alcatraz.api.logic.GameMoveListener;
import at.falb.games.alcatraz.api.utilities.GameMove;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {

    private List<GamePlayer> GamePlayersList = new ArrayList<>();
    private GamePlayer Player = new GamePlayer(0);
    private Alcatraz game = new Alcatraz();
    private MoveListener listener;

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

    @Override
    public void move(GamePlayer player, GameMove gameMove) throws RemoteException {
        try {
            this.game.doMove(player, gameMove.getPrisoner(), gameMove.getRowOrCol().ordinal(), gameMove.getRow(),
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
    public void startGame(List<GamePlayer> playerList){
        this.game.init(playerList.size(), this.Player.getId());

        this.listener = new GameMoveListener(playerList);
        this.game.addMoveListener(this.listener);

        this.game.showWindow();
        this.game.start();
    }
}
