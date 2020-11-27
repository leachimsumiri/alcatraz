package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Run this test only if one Server and a Client are running
 */
public class ClientSystemTests {
    private GamePlayer gamePlayer;
    private ServerCfg serverCfg;

    @BeforeEach
    void setUp() throws IOException {
        serverCfg = JsonHandler.readServerJson("primary");
        gamePlayer = new GamePlayer();
        gamePlayer.setPort(5000);
    }

    @Test
    void checkIfPossibleToLocateRemote() throws RemoteException, NotBoundException, MalformedURLException {
        final GamePlayer actualGamePlayer = ServerClientUtility.lookup(gamePlayer).getGamePlayer();
        final ServerCfg actualServerCfg = ServerClientUtility.lookup(this.serverCfg).getServerCfg();
        assertEquals(serverCfg, actualServerCfg);
        assertEquals(gamePlayer.getPort(), actualGamePlayer.getPort());
    }

    @Test
    void checkBeginThrowsError() {
        assertThrows(BeginGameException.class, () -> ServerClientUtility.lookup(serverCfg).beginGame());
    }
}
