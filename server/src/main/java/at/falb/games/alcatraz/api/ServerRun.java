package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.Server;
import at.falb.games.alcatraz.api.logic.ServerValues;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;

public class ServerRun {
    private static final Logger LOG = LogManager.getLogger(ServerRun.class);
    public static final LocalDateTime START_TIMESTAMP = LocalDateTime.now();
    private static ServerCfg serverCfg;

    public static void main(String[] arg) throws IOException, SpreadException {

        serverCfg = JsonHandler.readServerJson(arg[0]);
        serverCfg.setStartTimestamp(START_TIMESTAMP);

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
}

