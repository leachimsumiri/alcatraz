package at.falb.games.alcatraz.api.group.communication;

import at.falb.games.alcatraz.api.GamePlayer;
import at.falb.games.alcatraz.api.ServerRun;
import at.falb.games.alcatraz.api.logic.GroupConnection;
import at.falb.games.alcatraz.api.logic.Server;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spread.AdvancedMessageListener;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpreadListener implements AdvancedMessageListener {

    private static final Logger LOG = LogManager.getLogger(SpreadListener.class);

    private List<GroupConnection> groupConnectionList = new ArrayList<>();

    public List<GroupConnection> getGroupConnectionList() {
        return groupConnectionList;
    }

    @Override
    public void regularMessageReceived(SpreadMessage spreadMessage) {
        try {
            LOG.info("Received UpdateMessage:");
            LOG.info("Message from: " + spreadMessage.getSender() + "\nMessage: " + spreadMessage.getObject().toString());
            ArrayList<GamePlayer> gamePlayerArrayList = (ArrayList<GamePlayer>) spreadMessage.getObject();

            for(GamePlayer gamePlayer : gamePlayerArrayList) {
                LOG.info("Gameplayer: "+ gamePlayer.getIp() + " Name: " + gamePlayer.getName());
            }

        } catch (SpreadException e) {
            LOG.info("No Object can be found!");
            e.printStackTrace();
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage spreadMessage) {
          //LOG.debug("Membership message: " + spreadMessage.getMembershipInfo());
          //LOG.debug("Message from Name: " + spreadMessage.getMembershipInfo().getMembers());
        ArrayList<GroupConnection> currentGroupConnections = new ArrayList<>();

        spreadMessage.getMembershipInfo().getMembers();
        for (SpreadGroup sg: spreadMessage.getMembershipInfo().getMembers()) {
            currentGroupConnections.add(createGroupConnection(sg.toString()));
        }
        updateGroupView(currentGroupConnections);

        LOG.info("Current Group View:");
        for (GroupConnection gc : groupConnectionList) {
            LOG.info(gc.getId() + " " + gc.getHostname());
        }

        /*if(spreadMessage.getMembershipInfo().isRegularMembership()) {
          LOG.debug("Regular Membership Message: " + spreadMessage.getMembershipInfo().getJoined());
          addToGoupView(createGroupConnection(spreadMessage.getMembershipInfo().getJoined().toString()));
        } else if(spreadMessage.getMembershipInfo().isCausedByDisconnect()) {
          LOG.debug("Disconnect Message: "+ spreadMessage.getMembershipInfo().getLeft());
          removeFromGroupView(createGroupConnection(spreadMessage.getMembershipInfo().getLeft().toString()));
        } else if(spreadMessage.getMembershipInfo().isCausedByJoin()) {
          LOG.debug("Join Message: " + spreadMessage.getMembershipInfo().getJoined());
          addToGoupView(createGroupConnection(spreadMessage.getMembershipInfo().getJoined().toString()));
        } else if(spreadMessage.getMembershipInfo().isCausedByLeave()) {
          LOG.debug("Leave Message: " + spreadMessage.getMembershipInfo().getLeft());
          removeFromGroupView(createGroupConnection(spreadMessage.getMembershipInfo().getLeft().toString()));
        } else if(spreadMessage.getMembershipInfo().isCausedByNetwork()) {
          LOG.debug("Network Error Message:" + spreadMessage.getMembershipInfo().getLeft());
          removeFromGroupView(createGroupConnection(spreadMessage.getMembershipInfo().getLeft().toString()));
        } else if(spreadMessage.getMembershipInfo().isSelfLeave()) {
          LOG.debug("Self Leave Message" + spreadMessage.getMembershipInfo().getLeft());
          removeFromGroupView(createGroupConnection(spreadMessage.getMembershipInfo().getLeft().toString()));
        } else if(spreadMessage.getMembershipInfo().isTransition()) {
          LOG.debug("Transition Message");
        }*/

    }

    public void updateGroupView(Collection<GroupConnection> groupConnections) {
        groupConnectionList.clear();
        groupConnectionList.addAll(groupConnections);
    }

    public void addToGoupView(GroupConnection groupConnection) {
        LOG.debug("add To Group View ID: " + groupConnection.getId() + " Hostname:" + groupConnection.getHostname());
        groupConnectionList.add(groupConnection);
    }

    public void removeFromGroupView(GroupConnection groupConnection) {
        LOG.debug("remove from Group View ID: " + groupConnection.getId() + " Hostname:" + groupConnection.getHostname());
        groupConnectionList.remove(groupConnection);
    }

    public GroupConnection createGroupConnection(String groupConnectionString) {
        //LOG.debug("create GroupConneciton");
        //LOG.debug(groupConnectionString);

        String[] splited = groupConnectionString.split("#");

        return new GroupConnection(splited[1], splited[2]);
    }
}
