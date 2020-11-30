package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.Server;
import at.falb.games.alcatraz.api.utilities.CommonValues;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import spread.SpreadException;

import java.io.IOException;
import java.time.LocalDateTime;

public class ServerRun {

    public static void main(String[] arg) throws IOException, SpreadException {

        ServerCfg serverCfg = JsonHandler.readServerJson(arg[0]);
        assert CommonValues.RESOURCE != null && CommonValues.RESOURCE.getPath() != null;

        System.setProperty(CommonValues.JAVA_SECURITY_POLICY_KEY, CommonValues.RESOURCE.toString());
        System.setProperty(CommonValues.JAVA_RMI_SERVER_HOSTNAME, serverCfg.getServerIp());
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        serverCfg.setStartTimestamp(LocalDateTime.now());
        Server.build(serverCfg);
    }
}

