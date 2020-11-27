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

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client extends UnicastRemoteObject implements ClientInterface {

    private List<GamePlayer> gamePlayersList;
    private GamePlayer gamePlayer;
    private static final Logger LOG = LogManager.getLogger(Client.class);
    private static final List<ServerCfg> ALL_POSSIBLE_SERVERS = ServerClientUtility.getServerCfgList();
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

    private ServerInterface getPrimary(){
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

    // https://stackoverflow.com/questions/2258066/java-run-a-function-after-a-specific-number-of-seconds
    @Override
    public List<GamePlayer> getGamePlayersList() throws RemoteException {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            gamePlayersList = Objects.requireNonNull(getPrimary()).getGamePlayersList();
                        } catch (RemoteException e) {
                            LOG.error("Cannot get current Players", e);
                        }
                    }
                },
                5000
        );
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
