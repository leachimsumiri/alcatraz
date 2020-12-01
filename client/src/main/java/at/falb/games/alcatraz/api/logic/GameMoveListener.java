package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.ClientRun;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;
import at.falb.games.alcatraz.api.utilities.GameMove;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.List;

public class GameMoveListener implements MoveListener {
    private static final Logger LOG = LogManager.getLogger(GameMoveListener.class);
    private final List<GamePlayer> other_players;

    public GameMoveListener(List<GamePlayer> other_players) {
        this.other_players = other_players;
    }

    @Override
    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        LOG.info("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
        for (GamePlayer current_player : other_players) {
            try {
                if (player.getId() != current_player.getId()) {
                    ClientInterface client = ServerClientUtility.lookup(current_player, ServerClientUtility.UNLIMITED_RETRIES);
                    client.move(player, new GameMove(col, row, rowOrCol, prisoner));
                    LOG.info("Send move to player: " + current_player);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(ClientRun.frame, e.getMessage());
                LOG.error("Something went wrong when sending move to player: " + current_player, e);
            }
        }
        for (GamePlayer current : other_players) {
            if (current.getId() == (player.getId() + 1) % other_players.size()) {
                LOG.info("Wait for player " + current.getName() + " to play");
            }
        }
    }

    @Override
    public void gameWon(Player player) {
        LOG.info("Player " + player.getId() + " wins.");
    }
}
