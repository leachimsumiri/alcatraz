package at.falb.games.alcatraz.api.utilities;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.logic.InputHelper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdatePlayerThread extends Thread {
    private static final Logger LOG = LogManager.getLogger(UpdatePlayerThread.class);

    ClientInterface client;

    public UpdatePlayerThread(ClientInterface client) {
        this.client = client;
    }

    public void run() {
        List<GamePlayer> lastGamePlayersList = new ArrayList<>();
        boolean toExit = false;
        do {
            try {
                toExit = client.getPrimary().getGameStatus().equals(GameStatus.STARTED);
                List<GamePlayer> currentGamePlayersList = client.getGamePlayersList();
                if (!lastGamePlayersList.equals(currentGamePlayersList)) {
                    InputHelper.getInstance().printPlayerList(currentGamePlayersList);
                    lastGamePlayersList = currentGamePlayersList;
                }
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (RemoteException | InterruptedException e) {
                LOG.error("Primary not available", e);
            }
        } while(!toExit);
    }
}
