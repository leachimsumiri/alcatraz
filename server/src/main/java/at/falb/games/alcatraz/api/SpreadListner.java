package at.falb.games.alcatraz.api;

import spread.AdvancedMessageListener;
import spread.SpreadException;
import spread.SpreadMessage;

public class SpreadListner implements AdvancedMessageListener {
    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        try {
            System.out.println("Message from: " + spreadMessage.getSender() + "\nMessage: " + spreadMessage.getObject().toString());
        } catch (SpreadException e) {
            System.out.println("No Object can be found!");
            e.printStackTrace();
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
          System.out.println("Membership message: " + spreadMessage.getMembershipInfo());
    }
}
