package at.falb.games.alcatraz.api.logic;

import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.utilities.GameStatus;
import at.falb.games.alcatraz.api.utilities.ServerCfg;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.AdvancedMessageListener;
import spread.SpreadException;
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
            LOG.info(String.format("Received UpdateMessage from: %s content: %s",
                    spreadMessage.getSender(),
                    spreadMessageObject.toString()));
            if (spreadMessageObject instanceof List<?>) {
                List<?> listOfObjects = (List<?>) spreadMessageObject;
                if (CollectionUtils.isNotEmpty(listOfObjects)) {
                    final Object genericObject = listOfObjects.get(0);
                    if (genericObject instanceof GamePlayer) {
                        Server.getInstance().updateGamePlayerList((List<GamePlayer>) listOfObjects);
                    } else {
                        throw new Exception("This object type is unknown: " + spreadMessageObject.getClass().getSimpleName());
                    }
                }
            } else if (spreadMessageObject instanceof ServerCfg) {
                Server.getInstance().updateActualServersList((ServerCfg) spreadMessageObject);
            } else if (spreadMessageObject instanceof GameStatus) {
                Server.getInstance().setGameStatus((GameStatus) spreadMessageObject);
            } else if (spreadMessageObject instanceof UpdateGroup) {
                Server.getInstance().updateTheGroup((UpdateGroup) spreadMessageObject);
            } else {
                throw new Exception("This object type is unknown: " + spreadMessageObject.getClass().getSimpleName());
            }
        } catch (Exception e) {
            LOG.info("Something went wrong", e);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
        Server.getInstance().getActualServersList().clear();

        Arrays.stream(spreadMessage.getMembershipInfo().getMembers())
                .map(this::createServerCfg)
                .forEachOrdered(Server.getInstance().getActualServersList()::add);
        try {
            Server.getInstance().announceToGroup(Server.getInstance().getServerCfg());
            LOG.info(String.format("Current Group View: %s", Server.getInstance().getActualServersList()));
        } catch (SpreadException e) {
            LOG.error("It wasn't possible to announce the group about that a new server is running", e);
        }
    }

    private ServerCfg createServerCfg(SpreadGroup spreadGroup) {
        String[] splited = spreadGroup.toString().split("#");
        if (Server.getInstance().getServerCfg().getName().equals(splited[1])) {
            return Server.getInstance().getServerCfg();
        }
        return new ServerCfg(splited[1]);
    }
}
