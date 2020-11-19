package at.falb.games.alcatraz.api.group.communication;

import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerCfg;
import at.falb.games.alcatraz.api.ServerClientUtility;
import at.falb.games.alcatraz.api.ServerInterface;
import at.falb.games.alcatraz.api.ServerRun;
import at.falb.games.alcatraz.api.logic.Server;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.AdvancedMessageListener;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class SpreadMessageListener implements AdvancedMessageListener {

    private static final Logger LOG = LogManager.getLogger(SpreadMessageListener.class);

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        try {
            LOG.info("Received UpdateMessage:");
            LOG.info("Message from: " + spreadMessage.getSender() + "\nMessage: " + spreadMessage.getObject().toString());
            ArrayList<GamePlayer> gamePlayerArrayList = (ArrayList<GamePlayer>) spreadMessage.getObject();

            for (GamePlayer gamePlayer : gamePlayerArrayList) {
                LOG.info("Gameplayer: " + gamePlayer.getIp() + " Name: " + gamePlayer.getName());
            }

        } catch (SpreadException e) {
            LOG.info("No Object can be found!");
            e.printStackTrace();
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        Server.getActualServersList().clear();

        Arrays.stream(spreadMessage.getMembershipInfo().getMembers())
                .map(this::createServerCfg)
                .forEachOrdered(Server.getActualServersList()::add);
        LOG.info(String.format("Current Group View: %s", Server.getActualServersList()));
        introduceToTheOthers();
    }

    private ServerCfg createServerCfg(SpreadGroup spreadGroup) {
        String[] splited = spreadGroup.toString().split("#");
        if (ServerRun.getServerCfg().getName().equals(splited[1])) {
            return ServerRun.getServerCfg();
        } else {
            return new ServerCfg(splited[1]);
        }
    }

    private void introduceToTheOthers() {
        if (Server.getActualServersList().size() > 1) {
            try {
                // Wait a some time, otherwise this server will get a connection refused
                Thread.sleep(500);
                ServerClientUtility.getTheOtherServers(ServerRun.getServerCfg()).forEach(s -> {
                    final Optional<ServerCfg> optionalServerCfg = Server.getActualServersList()
                            .stream()
                            .filter(sc -> sc.equals(s))
                            .findAny();

                    // Only if the other server is only, will this server introduce a it self
                    if (optionalServerCfg.isPresent()) {
                        try {
                            Registry neighbor = LocateRegistry.getRegistry(s.getServerIp(), s.getRegistryPort());
                            ServerInterface neighbourBinding = (ServerInterface) neighbor.lookup(s.getName());
                            neighbourBinding.sayHello(ServerRun.getServerCfg());
                        } catch (RemoteException | NotBoundException e) {
                            LOG.error(String.format("Cannot locate the %s ", s.getName()), e);
                        }
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
