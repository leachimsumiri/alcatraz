package at.falb.games.alcatraz.api.ui;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.ClientRun;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.logic.InputHelper;
import at.falb.games.alcatraz.api.utilities.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class JFrameImpl {
    private static final Logger LOG = LogManager.getLogger(ClientRun.class);
    static GraphicsConfiguration gc;

    public void paint(Graphics g){
        g.drawString("Hello to JavaTutorial.net", 10, 10);
    }

    public static void main(String[] args) throws RemoteException {
        try {
            String serverName = args.length == 2 && StringUtils.isNotBlank(args[0]) ? args[0] : ClientValues.MAIN_SERVER;
            final ServerCfg firstServer = JsonHandler.readServerJson(serverName);
        } catch (IOException e) {
            LOG.error("Something went wrong", e);
        }

        ClientInterface client = new Client();

        // https://javatutorial.net/swing-jframe-basics-create-jframe
        JFrame frame= new JFrame(gc);
        frame.setTitle("Welecome to the Alcatraz Lobby");
        frame.setSize(600, 400);
        frame.setLocation(200, 200);

        JLabel label = new JLabel();
        label.setText("Enter Name :");
        frame.add(label);

        frame.getContentPane().setLayout(new FlowLayout());
        JTextField textfield1 = new JTextField("", 10);
        frame.getContentPane().add(textfield1);

        // https://javatutorial.net/jframe-buttons-listeners-text-fields
        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(100,100,140, 40);
        frame.add(submitButton);

        JButton startGameButton = new JButton("Start Game");
        startGameButton.setBounds(100,100,140, 40);
        frame.add(startGameButton);
        startGameButton.setEnabled(false);

        JButton deregisterButton = new JButton("Deregister");
        deregisterButton.setBounds(100,100,140, 40);
        frame.add(deregisterButton);
        deregisterButton.setEnabled(false);

        JLabel label1 = new JLabel();
        label1.setBounds(10, 110, 200, 100);
        frame.add(label1);

        JTextArea txtPlayerList = new JTextArea("");
        txtPlayerList.setEditable(false);
        Font font = new Font("Verdana", Font.PLAIN, 15);
        txtPlayerList.setFont(font);
        frame.add(txtPlayerList);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        UpdatePlayerThread thread = new UpdatePlayerThread(client, frame, txtPlayerList);

        //action listener
        submitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Submit name
                String name;
                GamePlayer gamePlayer = null;
                try {
                    gamePlayer = InputHelper.getInstance().requestPlayerSocket();
                    client.setGamePlayer(gamePlayer);
                    ServerClientUtility.createRegistry(client);
                    name = textfield1.getText();
                    if (StringUtils.isEmpty(name)) {
                        name = "MaxMustermann";
                    }
                    gamePlayer.setName(name);
                    int id = client.getPrimary().register(gamePlayer);
                    gamePlayer.setId(id);
                    LOG.info("Player registered: " + name);
                    thread.start();

                    // layout
                    textfield1.setText(name);
                    textfield1.setEnabled(false);
                    submitButton.setEnabled(false);
                    startGameButton.setEnabled(true);
                    deregisterButton.setEnabled(true);
                } catch (IOException | SpreadException | GamePlayerException | NotBoundException e) {
                    LOG.error("Something went wrong", e);
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }
            }
        });

        startGameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Start game
                try {
                    client.getPrimary().beginGame();
                    frame.setVisible(false);
                    frame.dispose();
                } catch (BeginGameException e) {
                    LOG.error(e.getMessage());
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }catch (RemoteException | SpreadException | NotBoundException | MalformedURLException e) {
                    LOG.error("Something went wrong", e);
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }
            }
        });

        deregisterButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Deregister name
                try {
                    GamePlayer gamePlayer = client.getGamePlayer();
                    client.getPrimary().deregister(gamePlayer);
                    LOG.info("Deregistered player: " + gamePlayer.getName());
                } catch (SpreadException | RemoteException | GamePlayerException | NotBoundException | MalformedURLException e) {
                    LOG.error("Something went wrong", e);
                    JOptionPane.showMessageDialog(frame, e.getMessage());
                }
                thread.setStopThread(true);
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        });
    }
}
