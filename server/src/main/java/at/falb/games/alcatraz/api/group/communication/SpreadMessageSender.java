package at.falb.games.alcatraz.api.group.communication;

import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.group.communication.messages.UpdateMessageFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadMessage;

import java.util.ArrayList;
import java.util.List;

public class SpreadMessageSender {

    private static final Logger LOG = LogManager.getLogger(SpreadMessageSender.class);

    private SpreadConnection spreadConnection;

    public SpreadMessageSender() {
    }

    public SpreadMessageSender(SpreadConnection spreadConnection) {
        this.spreadConnection = spreadConnection;
    }

    public void sendUpdateMessage(ArrayList<GamePlayer> gamePlayerList) {
        LOG.info("Sending UpdateMessage....");
        UpdateMessageFactory updateMessageFactory = new UpdateMessageFactory();
        try {
            SpreadMessage spreadMessage = updateMessageFactory.createMessage(gamePlayerList);
            spreadConnection.multicast(spreadMessage);
        } catch (SpreadException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
