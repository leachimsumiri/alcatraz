package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.ServerInterface;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private static final Logger LOG = LogManager.getLogger(Client.class);
    private List<GamePlayer> gamePlayersList = new ArrayList<>();
    private GamePlayer gamePlayer = new GamePlayer();
    //This is the list of servers, that will be updated every x seconds.
    private final List<ServerInterface> serverList = new ArrayList<>();
    // This Server is used for the first time
    private ServerInterface mainRegistryServer;

    public Client() throws RemoteException {
    }

    // https://stackoverflow.com/questions/2258066/java-run-a-function-after-a-specific-number-of-seconds
    @Override
    public List<GamePlayer> getGamePlayersList() throws RemoteException {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            Registry neighbor = LocateRegistry.getRegistry("127.0.0.1", 5010);
                            ServerInterface neighbourBinding = (ServerInterface) neighbor.lookup("primary");
                            gamePlayersList = neighbourBinding.getGamePlayersList();
                        } catch (RemoteException | NotBoundException e) {
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
    public void startGame(List<GamePlayer> playerList) {
        Alcatraz game = new Alcatraz();
        game.init(playerList.size(), this.gamePlayer.getId());

        MoveListener listener = new GameMoveListener(playerList);
        game.addMoveListener(listener);

        game.showWindow();
        game.start();
    }


}
