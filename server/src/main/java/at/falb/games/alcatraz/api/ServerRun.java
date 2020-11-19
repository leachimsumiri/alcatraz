package at.falb.games.alcatraz.api;


import at.falb.games.alcatraz.api.group.communication.SpreadListener;
import at.falb.games.alcatraz.api.logic.GroupConnection;
import at.falb.games.alcatraz.api.logic.Server;
import at.falb.games.alcatraz.api.logic.ServerCfg;
import at.falb.games.alcatraz.api.logic.ServerValues;
import at.falb.games.alcatraz.api.logic.YamlHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.Optional;

public class ServerRun {
    private static final Logger LOG = LogManager.getLogger(ServerRun.class);
    public static final LocalDateTime START_TIMESTAMP = LocalDateTime.now();
    private static ServerCfg serverCfg;

    public static void main(String[] arg) throws IOException, SpreadException {

        serverCfg = YamlHandler.readYaml(arg[0]);

        SpreadConnection connection = new SpreadConnection();
        connection.connect(InetAddress.getByName(serverCfg.getSpreaderIp()),
                serverCfg.getSpreaderPort(),
                serverCfg.getName(),
                false,
                true);

        SpreadGroup group = new SpreadGroup();
        group.join(connection, ServerValues.REPLICAS_GROUP_NAME);

        ServerInterface server = new Server(connection);
        Registry registry = LocateRegistry.createRegistry(serverCfg.getRegistryPort());
        registry.rebind(serverCfg.getName(), server);

        LOG.info("Start Timestamp: " + START_TIMESTAMP);

    }

    public static ServerCfg getServerCfg() {
        return serverCfg;
    }

    public static void sayHi() {
        YamlHandler.getServerCfgList().forEach(s -> {
            final Optional<GroupConnection> optionalGroupConnection = SpreadListener.getGroupConnectionList()
                    .stream()
                    .filter(gc -> gc.getId().equals(s.getName())).findAny();
            if (optionalGroupConnection.isPresent()) {
                try {
                    Registry neighbor = LocateRegistry.getRegistry(s.getServerIp(), s.getRegistryPort());
                    ServerInterface neighbourBinding = (ServerInterface) neighbor.lookup(s.getName());
                    neighbourBinding.sayHello(serverCfg.getName(), START_TIMESTAMP);
                } catch (RemoteException | NotBoundException e) {
                    LOG.error(String.format("Cannot locate the %s ", s.getName()), e);
                }
            }
        });
    }
}

