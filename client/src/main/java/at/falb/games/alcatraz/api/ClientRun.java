package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.ClientValues;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.time.LocalDateTime;

public class ClientRun {
    public static final LocalDateTime START_TIMESTAMP = LocalDateTime.now();
    private static ClientCfg clientCfg;

    public static void main(String[] args) throws IOException, NotBoundException {

        String serverName = args.length == 2 && StringUtils.isNotBlank(args[1]) ? args[1] : ClientValues.MAIN_SERVER;
        clientCfg = JsonHandler.readClientJson(args[0], serverName);
        clientCfg.setStartTimestamp(START_TIMESTAMP);
        LocateRegistry.getRegistry(clientCfg.getServerCfg().getServerIp(), clientCfg.getServerCfg().getRegistryPort());

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
