package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.*;
import at.falb.games.alcatraz.api.utilities.GameMove;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
                    ClientInterface client = ServerClientUtility.lookup(current_player);
                    client.move(player, new GameMove(col, row, rowOrCol, prisoner));
                    LOG.info("Send move to player: " + current_player);
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
