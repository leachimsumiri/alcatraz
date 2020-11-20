package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.logic.Server;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import spread.SpreadException;

import java.io.IOException;
import java.time.LocalDateTime;

public class ServerRun {

    public static void main(String[] arg) throws IOException, SpreadException {
        ServerCfg serverCfg = JsonHandler.readServerJson(arg[0]);
        serverCfg.setStartTimestamp(LocalDateTime.now());
        Server.build(serverCfg);
    }
}

