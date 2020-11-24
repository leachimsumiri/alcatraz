package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.ClientValues;
import at.falb.games.alcatraz.api.utilities.ClientCfg;
import at.falb.games.alcatraz.api.utilities.CommonValues;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
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
        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }


        //////////////////////////// Testing purposes

        List<ServerInterface> serverList = new ArrayList<ServerInterface>();

        // hier probiert der Client die Servers nacheinander zu lokalisieren,
        // falls ein Server down ist, wird die Exception ignoriert und mit dem nächsten Server probiert.
        Registry Reg = LocateRegistry.getRegistry(serverCfg.getServerIp(), serverCfg.getRegistryPort());
        try {serverList.add((ServerInterface) Reg.lookup(serverCfg.getName()));} catch (Exception ignore) { }
            serverCfg = JsonHandler.readServerJson("secondary");
            Reg = LocateRegistry.getRegistry(serverCfg.getServerIp(), serverCfg.getRegistryPort());
        try {serverList.add((ServerInterface) Reg.lookup(serverCfg.getName()));} catch (Exception ignore) { }
            serverCfg = JsonHandler.readServerJson("tertiary");
            Reg = LocateRegistry.getRegistry(serverCfg.getServerIp(), serverCfg.getRegistryPort());
        try {
            serverList.add((ServerInterface) Reg.lookup(serverCfg.getName()));
        } catch (Exception e) {
            System.out.println("Cannot locate any server!!" );
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
                    if (ID != CommonValues.SERVER_NOT_PRIMARY) break;
                }
            } catch (SpreadException e) {
                e.printStackTrace();
            }
            System.out.println("Player ID: " + ID);

        }
        else if (x == 0){
            try {
                for (ServerInterface SI : serverList){
                    ID = SI.deregister(player1);
                    if (ID != CommonValues.SERVER_NOT_PRIMARY) break;
                }
            } catch (SpreadException e) {
                e.printStackTrace();
            }
        }

        ////////////////////////////////////////// End of testing

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
