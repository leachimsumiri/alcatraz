package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.utilities.ClientValues;
import at.falb.games.alcatraz.api.utilities.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadException;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

            List<ServerInterface> serverList = new ArrayList<>();

            // hier probiert der Client die Servers nacheinander zu lokalisieren,
            // falls ein Server down ist, wird die Exception ignoriert und mit dem nächsten Server probiert.
            Registry Reg = LocateRegistry.getRegistry(serverCfg.getServerIp(), serverCfg.getRegistryPort());
            try {
                serverList.add((ServerInterface) Reg.lookup(serverCfg.getName()));
            } catch (Exception ignore) {
            }
            serverCfg = JsonHandler.readServerJson("secondary");
            Reg = LocateRegistry.getRegistry(serverCfg.getServerIp(), serverCfg.getRegistryPort());
            try {
                serverList.add((ServerInterface) Reg.lookup(serverCfg.getName()));
            } catch (Exception ignore) {
            }
            serverCfg = JsonHandler.readServerJson("tertiary");
            Reg = LocateRegistry.getRegistry(serverCfg.getServerIp(), serverCfg.getRegistryPort());
            try {
                serverList.add((ServerInterface) Reg.lookup(serverCfg.getName()));
            } catch (Exception e) {
                System.out.println("Cannot locate any server!!");
            }
            GamePlayer player1 = new GamePlayer(0);
            player1.setName(args[0]);
            int ID = 0;
            System.out.println("Press 1 for register or 0 for deregister:");
            Scanner in = new Scanner(System.in);
            int x = in.nextInt();
            if (x == 1) {
                try {
                    for (ServerInterface SI : serverList) {
                        ID = SI.register(player1);
                    }
                } catch (SpreadException e) {
                    e.printStackTrace();
                }
                System.out.println("Player ID: " + ID);

            } else if (x == 0) {
                for (ServerInterface SI : serverList) {
                    SI.deregister(player1);
                }
            }

        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }

        // Create RMI registry for this client
        ClientInterface client = new Client(clientCfg.getIp(), clientCfg.getPort());
        Registry registry =  LocateRegistry.createRegistry(clientCfg.getPort());
        registry.rebind(clientCfg.getName(), client);
        System.out.println("First player client up and running");

        List<GamePlayer> playerList = null; //TODO vom server holen
        client.startGame(playerList);


        //////////////////////////// Testing purposes



        ////////////////////////////////////////// End of testing


    }

    public static ClientCfg getClientCfg() {
        return clientCfg;
    }
}
