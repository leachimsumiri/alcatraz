package at.falb.games.alcatraz.api.utilities;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdatePlayerThread extends Thread {
    private static final Logger LOG = LogManager.getLogger(UpdatePlayerThread.class);

    private final ClientInterface client;
    private final JFrame frame;
    private final JTextArea textField;

    public UpdatePlayerThread(ClientInterface client, JFrame frame, JTextArea textField) {
        this.client = client;
        this.frame = frame;
        this.textField = textField;
    }

    public void run() {
        List<GamePlayer> lastGamePlayersList = new ArrayList<>();
        boolean toExit = false;

        do {
            try {
                toExit = client.getPrimary().getGameStatus().equals(GameStatus.STARTED);
                List<GamePlayer> currentGamePlayersList = client.getGamePlayersList();

                if (!lastGamePlayersList.equals(currentGamePlayersList)) {
                    StringBuilder playerList = new StringBuilder();
                    playerList.append("Current registered players:\n");
                    int counter = 1;
                    for (GamePlayer gamePlayer : currentGamePlayersList) {
                        playerList.append("Player ").append(counter).append(": ").append(gamePlayer.getName()).append('\n');
                        counter++;
                    }
                    this.textField.setText(playerList.toString());
                    frame.setVisible(true);
                    lastGamePlayersList = currentGamePlayersList;
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (RemoteException e) {
                LOG.error("Primary not available", e);
            } catch (InterruptedException e) {
                // A timeout happens, when the thread is interrupted
            }
        } while (!toExit);
    }
}
