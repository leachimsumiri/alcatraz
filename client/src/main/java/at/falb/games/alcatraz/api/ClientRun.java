package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.ClientValues;
import at.falb.games.alcatraz.api.utilities.ClientCfg;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.rmi.registry.LocateRegistry;
import java.time.LocalDateTime;

public class ClientRun {
    private static final Logger LOG = LogManager.getLogger(ClientRun.class);
    public static final LocalDateTime START_TIMESTAMP = LocalDateTime.now();
    private static ClientCfg clientCfg;

    public static void main(String[] args) {

        try {
            String serverName = args.length == 2 && StringUtils.isNotBlank(args[1]) ? args[1] : ClientValues.MAIN_SERVER;
            clientCfg = JsonHandler.readClientJson(args[0]);
            clientCfg.setStartTimestamp(START_TIMESTAMP);
            ServerCfg serverCfg = JsonHandler.readServerJson(serverName);
            LocateRegistry.getRegistry(serverCfg.getServerIp(), serverCfg.getRegistryPort());
        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }

// TODO to be removed
//        ServerInterface service = (ServerInterface) Reg.lookup(ClientValues.MAIN_SERVER);
//        ClientInterface client_1 = new Client(clientCfg.getIp() , clientCfg.getPort());
//        Registry Reg_1 = LocateRegistry.createRegistry(client_1.getPlayer().getPort());
//        Reg_1.rebind("", client_1);
//        client_1.getPlayer().setName(clientCfg.getName());

    }

    public static ClientCfg getClientCfg() {
        return clientCfg;
    }
}
