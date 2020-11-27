package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.*;
import at.falb.games.alcatraz.api.utilities.GameMove;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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
            try {
                if (player.getId() != current_player.getId()) {
                    ClientInterface client = ServerClientUtility.lookup(current_player);
                    client.move(player, new GameMove(col, row, rowOrCol, prisoner));
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            System.out.println("Send move to player: " + current_player);
        }
    }

    @Override
    public void gameWon(Player player) {
        System.out.println("Player " + player.getId() + " wins.");
    }
}
