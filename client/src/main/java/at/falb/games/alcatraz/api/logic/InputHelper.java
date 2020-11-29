package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.GamePlayer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class InputHelper {

    private final Logger Log = LogManager.getLogger(InputHelper.class);

    public static final int PORT_FROM = 5000;
    public static final int PORT_TO   = 5100;


    private InputHelper() {}
    private static InputHelper instance;

    public static InputHelper getInstance() {
        if (instance==null)
            instance = new InputHelper();
        return instance;
    }

    public GamePlayer requestPlayerSocket()  throws IOException {
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
