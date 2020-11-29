package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.*;
import at.falb.games.alcatraz.api.utilities.GameMove;
import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private static final Logger LOG = LogManager.getLogger(Client.class);
    private static final List<ServerCfg> ALL_POSSIBLE_SERVERS = ServerClientUtility.getServerCfgList();
    private List<GamePlayer> gamePlayersList = new ArrayList<>();
    private GamePlayer gamePlayer = new GamePlayer();
    //This is the list of servers, that will be updated every x seconds.
    private final List<ServerInterface> serverList = new ArrayList<>();
    // This ServergetMainRegistryServer is used for the first time
    private ServerInterface mainRegistryServer;

    private Alcatraz game = new Alcatraz();
    private MoveListener listener;

    public Client() throws RemoteException {
    }

    @Override
    public ServerInterface getPrimary(){
        for (ServerCfg serverCfg: ALL_POSSIBLE_SERVERS){
            try {
                ServerCfg primaryServer = ServerClientUtility.lookup(serverCfg).getMainRegistryServer();
                return ServerClientUtility.lookup(primaryServer);
            } catch (Exception e) {
                LOG.error("Server not available: " + serverCfg, e);
            }
        }
        return null;
    }

    @Override
    public List<GamePlayer> getGamePlayersList() {
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
            LOG.error("Something went wrong when communication move to game", e);
        }
    }

    @Override
    public void nextTurn(GamePlayer player) throws RemoteException {
        LOG.info("Received next turn message");
    }


    @Override
    public void startGame(List<GamePlayer> gamePlayersList){
        try {
            this.game.init(gamePlayersList.size(), this.gamePlayer.getId());
        } catch (Exception e) {
            LOG.error("Something went wrong when initializing game", e);
        }

        this.listener = new GameMoveListener(gamePlayersList);
        this.game.addMoveListener(this.listener);

        this.game.showWindow();
        this.game.start();
    }
}
