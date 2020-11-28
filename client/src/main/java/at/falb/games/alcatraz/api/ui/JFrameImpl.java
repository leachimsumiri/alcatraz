package at.falb.games.alcatraz.api.ui;

import at.falb.games.alcatraz.api.ClientInterface;
import at.falb.games.alcatraz.api.ClientRun;
import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.exceptions.BeginGameException;
import at.falb.games.alcatraz.api.exceptions.GamePlayerException;
import at.falb.games.alcatraz.api.logic.Client;
import at.falb.games.alcatraz.api.logic.InputHelper;
import at.falb.games.alcatraz.api.utilities.ServerClientUtility;
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

        ClientInterface client = new Client();

        // https://javatutorial.net/swing-jframe-basics-create-jframe
        JFrame frame= new JFrame(gc);
        frame.setTitle("Welecome to JavaTutorial.net");
        frame.setSize(600, 400);
        frame.setLocation(200, 200);

        JLabel label = new JLabel();
        label.setText("Enter Name :");
        frame.add(label);

        frame.getContentPane().setLayout(new FlowLayout());
        JTextField textfield1 = new JTextField("", 10);
        frame.getContentPane().add(textfield1);

        // https://javatutorial.net/jframe-buttons-listeners-text-fields
        JButton b = new JButton("Submit");
        b.setBounds(100,100,140, 40);
        frame.add(b);

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

        String[] data = {"Player 1", "Player 2", "Player 3", "Player 4"};
        frame.add(new JList(data));

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        //action listener
        b.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                String name;
                GamePlayer gamePlayer = null;
                try {
                    //TODO: logic - working?
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

                    // layout
                    textfield1.setText(name);
                    textfield1.setEnabled(false);
                    b.setEnabled(false);
                    startGameButton.setEnabled(true);
                    deregisterButton.setEnabled(true);

                } catch (IOException | SpreadException | GamePlayerException | NotBoundException e) {
                    e.printStackTrace();
                }
            }
        });

        startGameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                //TODO: logic start game - working?
                try {
                    client.getPrimary().beginGame();
                } catch (RemoteException | SpreadException | BeginGameException | NotBoundException | MalformedURLException e) {
                    e.printStackTrace();
                }

                //close Jframe window
                frame.setVisible(false);
                frame.dispose();

            }
        });

        deregisterButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                // TODO: logic
                /*try {
                    client.getPrimary().deregister(gamePlayer);
                } catch (SpreadException | RemoteException | GamePlayerException | NotBoundException | MalformedURLException e) {
                    e.printStackTrace();
                }*/

                // graphics
                textfield1.setText(null);
                textfield1.setEnabled(true);
                b.setEnabled(true);
                startGameButton.setEnabled(false);
                deregisterButton.setEnabled(false);

            }
        });
    }
}
