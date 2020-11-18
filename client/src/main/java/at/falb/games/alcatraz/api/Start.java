package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.ClientValues;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Start {
    public static void main(String[] args) throws IOException, NotBoundException {

        final ClientCfg clientCfg = JsonHandler.readClientJson(args[0], ClientValues.MAIN_SERVER);

        Registry Reg = LocateRegistry.getRegistry(clientCfg.getServerCfg().getServerIp(), clientCfg.getServerCfg().getRegistryPort());
        ServerInterface service = (ServerInterface) Reg.lookup(ClientValues.MAIN_SERVER);

// TODO to be removed
//        ClientInterface client_1 = new Client(clientCfg.getIp() , clientCfg.getPort());
//        Registry Reg_1 = LocateRegistry.createRegistry(client_1.getPlayer().getPort());
//        Reg_1.rebind("", client_1);
//        client_1.getPlayer().setName(clientCfg.getName());

    }
}
