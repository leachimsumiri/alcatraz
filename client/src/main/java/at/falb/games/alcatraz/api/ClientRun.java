package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.logic.SocketHelper;
import at.falb.games.alcatraz.api.utilities.CommonValues;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.PlayerStatus;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import at.falb.games.alcatraz.api.utilities.UpdatePlayerThread;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class ClientRun {
    private static final Logger LOG = LogManager.getLogger(ClientRun.class);
    private static GraphicsConfiguration gc;
    private static GamePlayer gamePlayer;
    private static PlayerStatus playerStatus = PlayerStatus.NOT_REGISTERED;
    private static JTextField nameTextField;
    private static JButton submitButton;
    private static JButton startGameButton;
    private static JButton deregisterButton;
    public static JFrame frame;
    private static ClientInterface client;
    private static UpdatePlayerThread thread;
    private static JTextArea playerListTextArea;

    public static void main(String[] args) throws RemoteException {
        assert CommonValues.RESOURCE != null && CommonValues.RESOURCE.getPath() != null;
        System.setProperty(CommonValues.JAVA_SECURITY_POLICY_KEY, CommonValues.RESOURCE.toString());
        client = new Client();
        try {
            JsonHandler.readServerJson();
            gamePlayer = SocketHelper.getInstance().requestPlayerSocket();
            System.setProperty(CommonValues.JAVA_RMI_SERVER_HOSTNAME, gamePlayer.getIp());
            client.setGamePlayer(gamePlayer);
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            ServerClientUtility.createRegistry(client);
            LOG.info("Client started: " + gamePlayer);
        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }

        frame = new JFrame(gc);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setTitle("Welecome to the Alcatraz Lobby");
        frame.setSize(600, 400);
        frame.setLocation(200, 200);
        // To prevent the window from closing, when no or cancel are pressed
        frame.addWindowListener(closePlayerWindow());


        JLabel label = new JLabel();
        label.setText("Enter Name :");
        frame.add(label);

        frame.getContentPane().setLayout(new FlowLayout());
        nameTextField = new JTextField("", 10);
        frame.getContentPane().add(nameTextField);

        submitButton = new JButton("Submit");
        submitButton.setBounds(100, 100, 140, 40);
        frame.add(submitButton);

        startGameButton = new JButton("Start Game");
        startGameButton.setBounds(100, 100, 140, 40);
        frame.add(startGameButton);
        startGameButton.setEnabled(false);

        deregisterButton = new JButton("Deregister");
        deregisterButton.setBounds(100, 100, 140, 40);
        frame.add(deregisterButton);
        deregisterButton.setEnabled(false);

        playerListTextArea = new JTextArea("");
        playerListTextArea.setEditable(false);
        Font font = new Font("Verdana", Font.PLAIN, 15);
        playerListTextArea.setFont(font);
        frame.add(playerListTextArea);

        frame.setVisible(true);
        frame.setResizable(false);

        client.setFrame(frame);

        submitButton.addActionListener(a -> submitAction());
        startGameButton.addActionListener(a -> startGameAction());
        deregisterButton.addActionListener(a -> deregisterAction());
        updateVisibility();
    }

    private static void deregisterAction() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            gamePlayer = client.getGamePlayer();
            client.getPrimary().deregister(gamePlayer);
            LOG.info("Deregistered player: " + gamePlayer);
            thread.interrupt();
            playerStatus = PlayerStatus.NOT_REGISTERED;
            updateVisibility();
        } catch (SpreadException | RemoteException | GamePlayerException | NotBoundException | MalformedURLException e) {
            LOG.error("Something went wrong", e);
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
        frame.setCursor(Cursor.getDefaultCursor());
    }

    private static void startGameAction() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            client.getPrimary().beginGame();
            playerStatus = PlayerStatus.GAME_STARTED;
            thread.interrupt();
            thread = null;
            updateVisibility();
        } catch (BeginGameException e) {
            LOG.error(e.getMessage());
            JOptionPane.showMessageDialog(frame, e.getMessage());
        } catch (RemoteException | SpreadException | NotBoundException | MalformedURLException e) {
            LOG.error("Something went wrong", e);
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
        frame.setCursor(Cursor.getDefaultCursor());
    }

    private static void submitAction() {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String name = nameTextField.getText();
            gamePlayer.setName(name);
            client.getPrimary().register(gamePlayer);
            thread = new UpdatePlayerThread(client, frame, playerListTextArea);
            thread.start();
            playerStatus = PlayerStatus.REGISTERED;
            updateVisibility();
            LOG.info("Player registered: " + gamePlayer);
        } catch (IOException | SpreadException | GamePlayerException | NotBoundException e) {
            LOG.error("Something went wrong", e);
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
        frame.setCursor(Cursor.getDefaultCursor());
    }

    private static void updateVisibility() {
        nameTextField.setEnabled(playerStatus == PlayerStatus.NOT_REGISTERED);
        submitButton.setEnabled(playerStatus == PlayerStatus.NOT_REGISTERED);
        startGameButton.setEnabled(playerStatus == PlayerStatus.REGISTERED);
        deregisterButton.setEnabled(playerStatus == PlayerStatus.REGISTERED);
        playerListTextArea.setVisible(playerStatus == PlayerStatus.REGISTERED);
    }

    private static WindowAdapter closePlayerWindow() {
        return new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit?");
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }

                // in case no server is available
                frame.setTitle("You are about to quit, waiting for the primary server to deregister");
                try {
                    if (CollectionUtils.isNotEmpty(client.getGamePlayersList())) {
                        deregisterAction();
                    }
                } catch (RemoteException ex) {
                    LOG.error("Something went wrong", ex);
                }
                System.exit(0);
            }
        };
    }
}
