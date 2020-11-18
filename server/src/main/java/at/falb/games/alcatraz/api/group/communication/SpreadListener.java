package at.falb.games.alcatraz.api.group.communication;

import spread.AdvancedMessageListener;
import spread.SpreadException;
import spread.SpreadMessage;

public class SpreadListener implements AdvancedMessageListener {



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
          System.out.println("MessageType: " + spreadMessage.getType());

          if(spreadMessage.getMembershipInfo().isRegularMembership()) {

          } else if(spreadMessage.getMembershipInfo().isCausedByDisconnect()) {

          } else if(spreadMessage.getMembershipInfo().isCausedByJoin()) {

          } else if(spreadMessage.getMembershipInfo().isCausedByLeave()) {

          } else if(spreadMessage.getMembershipInfo().isCausedByNetwork()) {

          } else if(spreadMessage.getMembershipInfo().isSelfLeave()) {

          } else if(spreadMessage.getMembershipInfo().isTransition()) {

          }

    }
}
