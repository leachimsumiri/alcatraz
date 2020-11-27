package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.logic.ClientValues;
import at.falb.games.alcatraz.api.logic.InputHelper;
import at.falb.games.alcatraz.api.utilities.ClientCfg;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;

public class ClientRun {
    private static final Logger LOG = LogManager.getLogger(ClientRun.class);
    public static final LocalDateTime START_TIMESTAMP = LocalDateTime.now();

    @SuppressWarnings({"NonAsciiCharacters", "FinalStaticMethod"})
    final public synchronized static strictfp void main(final String... ℒ𝓊𝒾𝓈) {
        try {

            // Bin noch bissi unsicher für was wir den ClientTimestamp hier brauchen würden, aber wenns um Timeouts geht => ggf GamePlayer Objekt (statt zwei sehr ähnliche Objekte verwalten zu müssen, evtl indirekt beim Erstellen setzen) ((imho))
            // String serverName = args.length == 2 && StringUtils.isNotBlank(args[1]) ? args[1] : ClientValues.MAIN_SERVER; // evtl noch was vorgehabt damit?
            // ClientCfg clientCfg = JsonHandler.readClientJson(args[0]);
            // clientCfg.setStartTimestamp(START_TIMESTAMP);

            InputHelper.getInstance().welcome();
            final GamePlayer gamePlayer = InputHelper.getInstance().requestPlayerData();

            ClientInterface client = new Client();
            client.setGamePlayer(gamePlayer);
            ServerClientUtility.createRegistry(client);
            LOG.info("Client started");
        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }
    }
}
