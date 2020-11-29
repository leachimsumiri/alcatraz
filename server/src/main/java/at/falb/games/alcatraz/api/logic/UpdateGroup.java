package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.utilities.ServerCfg;

import java.io.Serializable;

public class UpdateGroup implements Serializable {
    private ServerCfg theMainServer;

    public UpdateGroup(ServerCfg theMainServer) {
        this.theMainServer = theMainServer;
    }

    public ServerCfg getTheMainServer() {
        return theMainServer;
    }

    public void setTheMainServer(ServerCfg theMainServer) {
        this.theMainServer = theMainServer;
    }
}
