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
import java.util.Scanner;

public class InputHelper {

    private final Logger Log = LogManager.getLogger(InputHelper.class);
    private final Scanner scanner = new Scanner(System.in);
    private final PrintStream Out = System.out;

    public static final int PORT_FROM = 5000;
    public static final int PORT_TO   = 5100;


    private InputHelper() {}
    private static InputHelper instance;

    public static InputHelper getInstance() {
        if (instance==null)
            instance = new InputHelper();
        return instance;
    }

    public GamePlayer requestPlayerData() throws IOException {

        final GamePlayer playa = new GamePlayer();

        final String name = requestName();
        final String ip = getIpAddress();
        final int port = findAvailablePort();

        playa.setName(URLEncoder.encode(name, StandardCharsets.UTF_8));
        playa.setPort(port);
        playa.setIP(ip);

        return playa;
    }


    @SuppressWarnings("SameParameterValue")
    private String getInput(final String requestInfos) {
        Out.print(requestInfos + ": ");
        return scanner.nextLine();
    }

    private void sendOutput(final String message) {
        Out.println(message);
    }

    private String getIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    private int findAvailablePort() {
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


    /***
     * Warm welcoming message for our friend the player
     */
    public void welcome() {
        sendOutput("/////////////////////////////////");
        sendOutput(" Welcome to Aaaaaaaahhhhhlcatraz ");
        sendOutput("/////////////////////////////////");
        sendOutput("");
    }

    /**
     * Request a player name
     * @return Name the player chose for him/her/itself
     */
    public String requestName() {
        sendOutput("What would you like to be called?");
        return getInput("Name");
    }
}
