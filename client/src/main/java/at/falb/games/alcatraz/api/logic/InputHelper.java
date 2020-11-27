package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.ClientRun;
import at.falb.games.alcatraz.api.GamePlayer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InputHelper {

    private final Logger Log = LogManager.getLogger(InputHelper.class);
    private final Scanner Scanner = new Scanner(System.in);
    private final PrintStream Out = System.out;

    public static final int PORT_FROM = 5000;
    public static final int PORT_TO   = 5100;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // InputHelper (Singleton)
    ////////////////////////////////////////////////////////////////////////////////////////////////

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
        final String ip = getIPAddress();                               // throws UnknownHost
        final int port = findAvailablePort(PORT_FROM, PORT_TO);         // throws IOException

        // URL Encode zB um Leerzeichen zu erlauben (bzgl. späteres rmi://...playername...)
        playa.setName(URLEncoder.encode(name, StandardCharsets.UTF_8)); // throws UnsupportedEncodingEx
        playa.setPort(port);
        playa.setIP(ip);

        return playa;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("SameParameterValue")
    private String getInput(final String requestInfos) {
        Out.print(requestInfos+": "); // ohne Linebreak!
        return Scanner.nextLine();
    }

    private void sendOutput(final String message) {
        Out.println(message);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String getIPAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @SuppressWarnings("SameParameterValue")
    private int findAvailablePort(int from, int to) throws IOException {
        ServerSocket sock;
        for (int i=from; i <= to; i++) try {
            sock = new ServerSocket(i);
            sock.setReuseAddress(true);
            Log.info("Port "+i+" verfügbar und ausgewählt.");
            sock.close();
            return i;
        } catch (IOException ex) {
            if (i==to)
                throw new IOException("Kein lokaler freier Port gefunden (von "+from+" bis "+to+")");
        }
        throw new IOException("Hatte keine Chance irgendeinen Port zu probieren :-(");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

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
