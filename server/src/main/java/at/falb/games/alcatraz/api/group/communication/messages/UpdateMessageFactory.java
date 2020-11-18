package at.falb.games.alcatraz.api.group.communication.messages;

import at.falb.games.alcatraz.api.GamePlayer;
import spread.MessageFactory;
import spread.SpreadException;
import spread.SpreadMessage;

import java.util.ArrayList;

public class UpdateMessageFactory extends MessageFactory {

    public UpdateMessageFactory(SpreadMessage message) {
        super(message);
    }

    public UpdateMessageFactory() {
        super(null);
    }

    public SpreadMessage createMessage(ArrayList<GamePlayer> gamePlayers) throws SpreadException {
        SpreadMessage message = super.createMessage();
        message.setObject(gamePlayers);
        return message;
    }
}
