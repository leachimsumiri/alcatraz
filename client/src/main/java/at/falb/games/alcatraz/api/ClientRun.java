package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.utilities.*;
import at.falb.games.alcatraz.api.logic.InputHelper;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class ClientRun {
    private static final Logger LOG = LogManager.getLogger(ClientRun.class);

    public static void main(String[] args) {
        try {
            String serverName = args.length == 2 && StringUtils.isNotBlank(args[0]) ? args[0] : ClientValues.MAIN_SERVER;
            final ServerCfg firstServer = JsonHandler.readServerJson(serverName);

            InputHelper.getInstance().welcome();

            GamePlayer gamePlayer = InputHelper.getInstance().requestPlayerSocket();

            boolean playerRegistered = false;
            ClientInterface client = new Client();
            client.setGamePlayer(gamePlayer);
            ServerClientUtility.createRegistry(client);
            //UpdatePlayerThread thread = new UpdatePlayerThread(client);
            //thread.start();

            do {
                try {
                    String name = InputHelper.getInstance().requestPlayerName();
                    gamePlayer.setName(name);
                    int id = client.getPrimary().register(gamePlayer);
                    gamePlayer.setId(id);
                    playerRegistered = true;
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            } while (!playerRegistered);

            client.setGamePlayer(gamePlayer);

            while (client.getPrimary().getGameStatus().equals(GameStatus.NOT_STARTED)) {
                try {
                    InputHelper.getInstance().printLobbyWelcome();
                    String action = InputHelper.getInstance().printLobby();
                    switch (action) {
                        case "d":
                            client.getPrimary().deregister(gamePlayer);
                            break;
                        case "s":
                            client.getPrimary().beginGame();
                            break;
                    }
                } catch (BeginGameException | GamePlayerException e) {
                    InputHelper.getInstance().printError(e.getMessage());
                    LOG.error(e.getMessage());
                }
            }


            LOG.info("Client started: " + gamePlayer);

            LOG.info("Lookup first server from config file...");
            ServerInterface server = ServerClientUtility.lookup(firstServer);
            LOG.info("Getting primary server from registry server");
            ServerCfg primaryServerCfg = server.getMainRegistryServer();
            LOG.info("Lookup primary server stub");
            ServerClientUtility.lookup(primaryServerCfg).register(gamePlayer);
        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }
    }
}
