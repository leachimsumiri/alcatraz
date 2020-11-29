package at.falb.games.alcatraz.api;

import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.logic.InputHelper;
import at.falb.games.alcatraz.api.utilities.JsonHandler;
import at.falb.games.alcatraz.api.utilities.PlayerStatus;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
import at.falb.games.alcatraz.api.utilities.UpdatePlayerThread;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadException;

import javax.swing.*;
import java.awt.*;
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
    private static JFrame frame;
    private static ClientInterface client;
    private static UpdatePlayerThread thread;
    private static JTextArea playerListTextArea;

    public static void main(String[] args) throws RemoteException {
        client = new Client();
        try {
            JsonHandler.readServerJson();
            gamePlayer = InputHelper.getInstance().requestPlayerSocket();
            client.setGamePlayer(gamePlayer);
            ServerClientUtility.createRegistry(client);
            LOG.info("Client started: " + gamePlayer);
        } catch (Exception e) {
            LOG.error("It wasn't possible to start the client", e);
        }

        frame = new JFrame(gc);
        frame.setTitle("Welecome to the Alcatraz Lobby");
        frame.setSize(600, 400);
        frame.setLocation(200, 200);

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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        client.setFrame(frame);

        submitButton.addActionListener(a -> submitAction());
        startGameButton.addActionListener(a -> startGameAction());
        deregisterButton.addActionListener(a -> deregisterAction());

        updateVisibility();
    }

    private static void deregisterAction() {
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
    }

    private static void startGameAction() {
        try {
            client.getPrimary().beginGame();
            playerStatus = PlayerStatus.GAME_STARTED;
            thread.interrupt();
            updateVisibility();
        } catch (BeginGameException e) {
            LOG.error(e.getMessage());
            JOptionPane.showMessageDialog(frame, e.getMessage());
        } catch (RemoteException | SpreadException | NotBoundException | MalformedURLException e) {
            LOG.error("Something went wrong", e);
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
    }

    private static void submitAction() {
        try {
            String name = nameTextField.getText();
            gamePlayer.setName(name);
            int id = client.getPrimary().register(gamePlayer);
            gamePlayer.setId(id);
            client.setGamePlayer(gamePlayer);
            LOG.info("Player registered: " + gamePlayer);
            thread = new UpdatePlayerThread(client, frame, playerListTextArea);
            thread.start();
            playerStatus = PlayerStatus.REGISTERED;
            updateVisibility();
        } catch (IOException | SpreadException | GamePlayerException | NotBoundException e) {
            LOG.error("Something went wrong", e);
            JOptionPane.showMessageDialog(frame, e.getMessage());
        }
    }

    private static void updateVisibility() {
        nameTextField.setEnabled(playerStatus == PlayerStatus.NOT_REGISTERED);
        submitButton.setEnabled(playerStatus == PlayerStatus.NOT_REGISTERED);
        startGameButton.setEnabled(playerStatus == PlayerStatus.REGISTERED);
        deregisterButton.setEnabled(playerStatus == PlayerStatus.REGISTERED);
        playerListTextArea.setVisible(playerStatus == PlayerStatus.REGISTERED);
    }
}
