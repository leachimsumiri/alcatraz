package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.utilities.ClientCfg;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Run this test only if one Server and a Client are running
 */
public class ClientSystemTests {
    private static final List<String> GAME_PLAYER_NAMES = List.of("player1", "player2", "player3", "player4");
    private GamePlayer gamePlayer;
    private ServerCfg serverCfg;

    @BeforeEach
    void setUp() throws IOException {
        final ClientCfg clientCfg = JsonHandler.readClientJson(GAME_PLAYER_NAMES.get(0));
        serverCfg = JsonHandler.readServerJson("primary");
        gamePlayer = new GamePlayer();
        gamePlayer.setIP(clientCfg.getIp());
        gamePlayer.setPort(clientCfg.getPort());
        gamePlayer.setName(clientCfg.getName());
    }

    @Test
    void checkIfPossibleToLocateRemote() throws RemoteException, NotBoundException, MalformedURLException {
        final GamePlayer actualGamePlayer = ServerClientUtility.locateRegistryAndLookup(this.gamePlayer).getGamePlayer();
        final ServerCfg actualServerCfg = ServerClientUtility.locateRegistryAndLookup(this.serverCfg).getServerCfg();
        assertEquals(serverCfg, actualServerCfg);
        assertEquals(gamePlayer, actualGamePlayer);
    }

    @Test
    void checkBeginThrowsError() {
        assertThrows(BeginGameException.class, () -> ServerClientUtility.locateRegistryAndLookup(serverCfg).beginGame());
    }
}
