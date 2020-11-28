package at.falb.games.alcatraz.api;

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
            final GamePlayer gamePlayer = InputHelper.getInstance().requestPlayerData();

            ClientInterface client = new Client();
            client.setGamePlayer(gamePlayer);
            ServerClientUtility.createRegistry(client);
            LOG.info("Client started: " + gamePlayer);
        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }
    }
}
