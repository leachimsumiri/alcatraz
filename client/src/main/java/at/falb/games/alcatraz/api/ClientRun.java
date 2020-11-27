package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.utilities.*;
import at.falb.games.alcatraz.api.logic.ClientValues;
import at.falb.games.alcatraz.api.logic.InputHelper;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientRun {
    private static final Logger LOG = LogManager.getLogger(ClientRun.class);

    public static void main(String[] args) {
        try {

            String serverName = args.length == 2 && StringUtils.isNotBlank(args[0]) ? args[0] : ClientValues.MAIN_SERVER;
            final ServerCfg firstServer = JsonHandler.readServerJson(serverName);
            final List<ServerCfg> allPossibleServers = ServerClientUtility.getServerCfgList();

            InputHelper.getInstance().welcome();
            final GamePlayer gamePlayer = InputHelper.getInstance().requestPlayerData();

            ClientInterface client = new Client();
            client.setGamePlayer(gamePlayer);
            ServerClientUtility.createRegistry(client);
            LOG.info("Client started: " + gamePlayer);
        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }
        try {
            LocalDateTime START_TIMESTAMP = LocalDateTime.now();
            String serverName = args.length == 2 && StringUtils.isNotBlank(args[1]) ? args[1] : ClientValues.MAIN_SERVER;
            ClientCfg clientCfg = JsonHandler.readClientJson(args[0]);
            clientCfg.setStartTimestamp(START_TIMESTAMP);
            final GamePlayer gamePlayer = new GamePlayer();
            gamePlayer.setName(clientCfg.getName());
            gamePlayer.setIP(clientCfg.getIp());
            gamePlayer.setPort(clientCfg.getPort());

            ArrayList<GamePlayer> otherPlayers = new ArrayList<>();
            GamePlayer player2 = new GamePlayer();
            GamePlayer player3 = new GamePlayer();
            if (clientCfg.getName().equals("player1")) {
                gamePlayer.setId(0);
                ClientCfg clientCfg2 = JsonHandler.readClientJson("player2");
                clientCfg2.setStartTimestamp(START_TIMESTAMP);
                player2.setName(clientCfg2.getName());
                player2.setPort(clientCfg2.getPort());
                otherPlayers.add(player2);

                ClientCfg clientCfg3 = JsonHandler.readClientJson("player3");
                clientCfg3.setStartTimestamp(START_TIMESTAMP);
                player3.setName(clientCfg3.getName());
                player3.setPort(clientCfg3.getPort());
                otherPlayers.add(player3);
            } else if (clientCfg.getName().equals("player2")){
                gamePlayer.setId(1);
                ClientCfg clientCfg2 = JsonHandler.readClientJson("player1");
                clientCfg2.setStartTimestamp(START_TIMESTAMP);
                player2.setName(clientCfg2.getName());
                player2.setPort(clientCfg2.getPort());
                otherPlayers.add(player2);

                ClientCfg clientCfg3 = JsonHandler.readClientJson("player3");
                clientCfg3.setStartTimestamp(START_TIMESTAMP);
                player3.setName(clientCfg3.getName());
                player3.setPort(clientCfg3.getPort());
                otherPlayers.add(player3);
            } else {
                gamePlayer.setId(2);
                ClientCfg clientCfg2 = JsonHandler.readClientJson("player1");
                clientCfg2.setStartTimestamp(START_TIMESTAMP);
                player2.setName(clientCfg2.getName());
                player2.setPort(clientCfg2.getPort());
                otherPlayers.add(player2);

                ClientCfg clientCfg3 = JsonHandler.readClientJson("player2");
                clientCfg3.setStartTimestamp(START_TIMESTAMP);
                player3.setName(clientCfg3.getName());
                player3.setPort(clientCfg3.getPort());
                otherPlayers.add(player3);
            }

            ClientInterface client = new Client(gamePlayer, otherPlayers);
            ServerClientUtility.createRegistry(client);
            LOG.info("Client started");
            client.startGame();

        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }
    }
}
