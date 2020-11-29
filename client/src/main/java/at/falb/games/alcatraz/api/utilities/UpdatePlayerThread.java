package at.falb.games.alcatraz.api.utilities;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.logic.InputHelper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdatePlayerThread extends Thread {
    private static final Logger LOG = LogManager.getLogger(UpdatePlayerThread.class);

    ClientInterface client;
    JFrame frame;
    JTextArea textField;
    Boolean stopThread = false;

    public UpdatePlayerThread(ClientInterface client, JFrame frame, JTextArea textField) {
        this.client = client;
        this.frame = frame;
        this.textField = textField;
    }

    public void setStopThread(Boolean stopThread) {
        this.stopThread = stopThread;
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
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (RemoteException | InterruptedException e) {
                LOG.error("Primary not available", e);
            }
        } while(!toExit && !stopThread);

    }
}
