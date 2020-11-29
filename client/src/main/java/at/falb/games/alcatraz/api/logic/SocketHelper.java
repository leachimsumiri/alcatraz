package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.GamePlayer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketHelper {

    private final Logger Log = LogManager.getLogger(SocketHelper.class);

    public static final int PORT_FROM = 5000;
    public static final int PORT_TO = 5100;

    private SocketHelper() {
    }

    private static SocketHelper instance;

    public static SocketHelper getInstance() {
        if (instance == null)
            instance = new SocketHelper();
        return instance;
    }

    public GamePlayer requestPlayerSocket() throws IOException {
        final GamePlayer playa = new GamePlayer();

        final String ip = getIpAddress();
        final int port = findAvailablePort();

        playa.setPort(port);
        playa.setIP(ip);

        return playa;
    }

    /**
     * Public in order to use for testing
     * @return {@link InetAddress#getLocalHost()#getHostAddress()}
     * @throws UnknownHostException
     */
    public String getIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    /**
     * Public in order to use for testing
     * @return a available port
     */
    public int findAvailablePort() {
        int initialPort = PORT_FROM;
        do {
            try (Socket ignored = new Socket(getIpAddress(), initialPort)) {
                initialPort++;
            } catch (IOException e) {
                Log.trace("Free port was found: " + initialPort, e);
                break;
            }
        } while (initialPort < PORT_TO);
        return initialPort;
    }
}
