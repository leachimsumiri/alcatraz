package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.utilities.ClientValues;
import at.falb.games.alcatraz.api.utilities.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.List;

public class ClientRun {
    public static final LocalDateTime START_TIMESTAMP = LocalDateTime.now();
    private static ClientCfg clientCfg;

    public static void main(String[] args) throws IOException, NotBoundException {
        String serverName = args.length == 2 && StringUtils.isNotBlank(args[1]) ? args[1] : ClientValues.MAIN_SERVER;
        clientCfg = JsonHandler.readClientJson(args[0]);
        clientCfg.setStartTimestamp(START_TIMESTAMP);
        ServerCfg serverCfg = JsonHandler.readServerJson(serverName);
        LocateRegistry.getRegistry(serverCfg.getServerIp(), serverCfg.getRegistryPort());

        // Create RMI registry for this client
        ClientInterface client = new Client(clientCfg.getIp(), clientCfg.getPort());
        Registry registry =  LocateRegistry.createRegistry(clientCfg.getPort());
        registry.rebind(clientCfg.getName(), client);
        System.out.println("First player client up and running");

        List<GamePlayer> playerList = null; //TODO vom server holen
        client.startGame(playerList);


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
