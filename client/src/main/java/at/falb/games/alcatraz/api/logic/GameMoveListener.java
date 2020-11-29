package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.*;
import at.falb.games.alcatraz.api.utilities.GameMove;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class GameMoveListener implements MoveListener {
    private static final Logger LOG = LogManager.getLogger(GameMoveListener.class);
    private final List<GamePlayer> other_players;
    private final int MAX_RETRIES = 12;

    public GameMoveListener(List<GamePlayer> other_players) {
        this.other_players = other_players;
    }

    @Override
    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        LOG.info("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
        for (GamePlayer current_player : other_players) {
            try {
                if (player.getId() != current_player.getId()) {
                    ClientInterface client = null;
                    int retries = 0;

                    while(retries < MAX_RETRIES) {
                      try {
                          client = ServerClientUtility.lookup(current_player);
                          break;
                      } catch(RemoteException | NotBoundException | MalformedURLException exception) {
                          LOG.error("Client Interface from player: " + current_player.getName() + " not available");
                          retries++;
                          Thread.sleep(10000);
                      }
                    }

                    if(client != null) {
                        client.move(player, new GameMove(col, row, rowOrCol, prisoner));
                        LOG.info("Send move to player: " + current_player);
                    } else {
                        LOG.error("Retry mechanism failed....");
                    }
                }
            } catch (Exception e) {
                LOG.error("Something went wrong when sending move to player: " + current_player, e);
            }
        }
    }

    @Override
    public void gameWon(Player player) {
        LOG.info("Player " + player.getId() + " wins.");
    }
}
