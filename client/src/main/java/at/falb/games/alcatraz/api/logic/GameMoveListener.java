package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Player;
import at.falb.games.alcatraz.api.Prisoner;

import java.util.List;

public class GameMoveListener implements MoveListener {
    private final List<GamePlayer> other_players;

    public GameMoveListener(List<GamePlayer> other_players) {
        this.other_players = other_players;
    }

    @Override
    public void moveDone(Player player, Prisoner prisoner, int rowOrCol, int row, int col) {
        System.out.println("moving " + prisoner + " to " + (rowOrCol == Alcatraz.ROW ? "row" : "col") + " " + (rowOrCol == Alcatraz.ROW ? row : col));
        for (GamePlayer current_player : other_players) {
            // TODO send move to other players via RMI
            System.out.println("Send move to player: " + current_player);
        }
    }

    @Override
    public void gameWon(Player player) {
        System.out.println("Player " + player.getId() + " wins.");
    }
}
