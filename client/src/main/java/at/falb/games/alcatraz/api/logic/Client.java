package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.IllegalMoveException;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.utilities.GameMove;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private static final Logger LOG = LogManager.getLogger(Client.class);
    private static final List<ServerCfg> ALL_POSSIBLE_SERVERS = ServerClientUtility.getServerCfgList();
    private List<GamePlayer> gamePlayersList = new ArrayList<>();
    private GamePlayer gamePlayer = new GamePlayer();
    //This is the list of servers, that will be updated every x seconds.
    private final List<ServerInterface> serverList = new ArrayList<>();
    // This ServergetMainRegistryServer is used for the first time
    private ServerCfg mainRegistryServer;
    private JFrame frame;

    private final Alcatraz game = new Alcatraz();
    private MoveListener listener;

    public Client() throws RemoteException {
    }

    @Override
    public ServerInterface getPrimary() throws RemoteException {
        if (mainRegistryServer == null) {
            for (ServerCfg serverCfg : ALL_POSSIBLE_SERVERS) {
                try {
                    return getServerInterface(serverCfg);
                } catch (Exception e) {
                    LOG.error("Server not available: " + serverCfg, e);
                }
            }
        } else {
            try {
                return getServerInterface(mainRegistryServer);
            } catch (Exception e) {
                LOG.warn("Actual primary server not available: " + mainRegistryServer, e);
                mainRegistryServer = null;
                getPrimary();
            }
        }
        throw new RemoteException("No remote registry server is available");
    }

    private ServerInterface getServerInterface(ServerCfg serverCfg) throws RemoteException {
        ServerCfg primaryServer = ServerClientUtility.lookup(serverCfg).getMainRegistryServer();
        final ServerInterface serverInterface = ServerClientUtility.lookup(primaryServer);
        mainRegistryServer = primaryServer;
        return serverInterface;
    }

    @Override
    public void setFrame(JFrame frame) {
        this.frame = frame;
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
            LOG.info("Received move from player: " + player);
            this.game.doMove(player, gameMove.getPrisoner(), gameMove.getRowOrCol(), gameMove.getRow(),
                    gameMove.getColumn());
            if (this.gamePlayer.getId() != (player.getId() + 1) % gamePlayersList.size()) {
                for (GamePlayer current : gamePlayersList) {
                    if (current.getId() == (player.getId() + 1) % gamePlayersList.size()) {
                        LOG.info("Wait for player " + current.getName() + " to play");
                    }
                }
            } else {
                LOG.info("It's your turn!");
            }
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
            for (int idx = 0 ; idx < gamePlayersList.size(); idx++ ){
                this.game.getPlayer(idx).setName(gamePlayersList.get(idx).getName());
            }
        } catch (Exception e) {
            LOG.error("Something went wrong when initializing game", e);
        }

        this.listener = new GameMoveListener(gamePlayersList);
        this.game.addMoveListener(this.listener);

        this.game.showWindow();
        this.game.start();
        LOG.info("Started game with player: " + gamePlayersList);
        if (this.gamePlayer.getId() != 0) {
            for (GamePlayer current:gamePlayersList) {
                if (current.getId() == 0) {
                    LOG.info("Wait for player " + current.getName() + " to play");
                }
            }
        } else {
            LOG.info("It's your turn!");
        }
        this.frame.setVisible(false);
    }

    @Override
    public void setId(int id) throws RemoteException {
        gamePlayer.setId(id);
    }
}
