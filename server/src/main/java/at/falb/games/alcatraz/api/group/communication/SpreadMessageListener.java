package at.falb.games.alcatraz.api.group.communication;

import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import at.falb.games.alcatraz.api.logic.Server;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.AdvancedMessageListener;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.util.Arrays;
import java.util.List;

public class SpreadMessageListener implements AdvancedMessageListener {

    private static final Logger LOG = LogManager.getLogger(SpreadMessageListener.class);

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        try {
            final Object spreadMessageObject = spreadMessage.getObject();
            LOG.info("Received UpdateMessage:");
            LOG.info("Message from: " + spreadMessage.getSender());
            LOG.info("Message: " + spreadMessageObject.toString());
            if (spreadMessageObject instanceof List<?>) {
                List<?> listOfObjects = (List<?>) spreadMessageObject;
                if (CollectionUtils.isNotEmpty(listOfObjects)) {
                    final Object genericObject = listOfObjects.get(0);
                    if (genericObject instanceof GamePlayer) {
                        handleGamePlayerList((List<GamePlayer>) listOfObjects);
                    } else {
                        throw new Exception("This object type is unknown: " + spreadMessageObject.getClass().getSimpleName());
                    }
                }
            } else if (spreadMessageObject instanceof ServerCfg) {
                Server.addToActualServersList((ServerCfg) spreadMessageObject);
            } else {
                throw new Exception("This object type is unknown: " + spreadMessageObject.getClass().getSimpleName());
            }
        } catch (Exception e) {
            LOG.info("Something went wrong", e);
        }
    }

    private void handleGamePlayerList(List<GamePlayer> gamePlayerList) {
        for (GamePlayer gamePlayer : gamePlayerList) {
            LOG.info("Gameplayer: " + gamePlayer.getIp() + " Name: " + gamePlayer.getName());
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        Server.getActualServersList().clear();

        Arrays.stream(spreadMessage.getMembershipInfo().getMembers())
                .map(this::createServerCfg)
                .forEachOrdered(Server.getActualServersList()::add);

        Server.announceToGroup(Server.getServerCfg());
        LOG.info(String.format("Current Group View: %s", Server.getActualServersList()));
    }

    private ServerCfg createServerCfg(SpreadGroup spreadGroup) {
        String[] splited = spreadGroup.toString().split("#");
        if (Server.getServerCfg().getName().equals(splited[1])) {
            return Server.getServerCfg();
        } else {
            return new ServerCfg(splited[1]);
        }
    }
}
