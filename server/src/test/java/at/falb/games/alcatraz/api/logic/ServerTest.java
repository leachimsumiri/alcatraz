package at.falb.games.alcatraz.api.logic;


import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadMessage;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ServerTest {

    @Mock
    private SpreadConnection connection;

    private Server server;

    private static final ServerCfg serverCfg = new ServerCfg();

    static {
        serverCfg.setName("primary");
        serverCfg.setSpreaderIp("127.0.0.1");
        serverCfg.setServerIp("primary");
        serverCfg.setSpreaderPort(4803);
        serverCfg.setRegistryPort(5010);
    }

    @BeforeEach
    void setUp() throws RemoteException, SpreadException, UnknownHostException {
        MockitoAnnotations.initMocks(this);
        Server.setThisServer(null);
        server = Server.build(serverCfg, connection);
        Server.getActualServersList().clear();
        server.getGamePlayerList().clear();
    }

    @Test
    void announceToGroup() throws SpreadException {
        // It will be called when it starts, but I cannot debug it
        verify(connection).multicast((SpreadMessage) any());
    }

    @Test
    void updateActualServersList() {
        final ServerCfg server2 = new ServerCfg();
        server2.setName("server2");
        //This is to simulate the at.falb.games.alcatraz.api.group.communication.SpreadMessageListener.membershipMessageReceived when adding a new server
        Server.getActualServersList().add(serverCfg);
        Server.getActualServersList().add(server2);

        server2.setSpreaderIp("127.0.0.1");
        server2.setServerIp("127.0.0.1");
        server2.setSpreaderPort(4803);
        server2.setRegistryPort(5011);

        Server.updateActualServersList(server2);
        assertEquals(2, Server.getActualServersList().size());
        assertTrue(Server.getActualServersList().contains(server2), "The collection should contain server2");
    }

    @Test
    void registerAllPlayersSuccessfully() throws SpreadException, GamePlayerException {
        registerAllUsersAndAssert();
    }

    private void registerAllUsersAndAssert() throws SpreadException, GamePlayerException {
        for (int i = 0; i < ServerValues.MAX_PLAYERS; i++) {
            final GamePlayer gamePlayer = new GamePlayer();
            gamePlayer.setName("game" + i);
            final int id = server.register(gamePlayer);
            verify(connection, times(2 + i)).multicast((SpreadMessage) any());
            assertEquals(i, id);
        }
    }

    @Test
    void registerMaxPlayersReached() {
        for (int i = 0; i < ServerValues.MAX_PLAYERS; i++) {
            final GamePlayer gamePlayer = new GamePlayer();
            gamePlayer.setName("game" + i);
            server.getGamePlayerList().add(gamePlayer);
        }
        final GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setName("game0");
        assertThrows(GamePlayerException.class, () -> server.register(gamePlayer));
    }

    @ParameterizedTest
    @MethodSource
    void registerPlayersFail(GamePlayer gamePlayer) {
        server.getGamePlayerList().add(new GamePlayer("game"));
        assertThrows(GamePlayerException.class, () -> server.register(gamePlayer));
    }

    private static Stream<GamePlayer> registerPlayersFail() {
        return Stream.of(null,
                new GamePlayer(),
                new GamePlayer("game"));
    }

    @Test
    void deregisterAnExistingGamePlayer() throws SpreadException, GamePlayerException {
        final GamePlayer gamePlayer = new GamePlayer();
        gamePlayer.setName("game");
        server.getGamePlayerList().add(gamePlayer);

        server.deregister(gamePlayer);

        verify(connection, times(2)).multicast((SpreadMessage) any());
    }

    @Test
    void deregisterNonExistingGamePlayer() {
        assertThrows(GamePlayerException.class, () -> server.deregister(new GamePlayer("game1")));
    }

    @ParameterizedTest
    @MethodSource("registerPlayersFail")
    void deregisterPlayersFailed(GamePlayer gamePlayer) {
        assertThrows(GamePlayerException.class, () -> server.deregister(gamePlayer));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void registerAPlayerWhenThePortIsFree(int id) throws SpreadException, GamePlayerException {
        registerAllUsersAndAssert();
        final GamePlayer gamePlayerToDeregister = server.getGamePlayerList().get(id);
        server.deregister(gamePlayerToDeregister);

        final int actualId = server.register(new GamePlayer("game" + id));

        assertEquals(id, actualId);
    }
}
