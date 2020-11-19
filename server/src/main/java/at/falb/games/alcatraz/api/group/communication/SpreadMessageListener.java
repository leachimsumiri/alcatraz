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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

public class SpreadMessageListener implements AdvancedMessageListener {

    private static final Logger LOG = LogManager.getLogger(SpreadMessageListener.class);

    private static TreeSet<GroupConnection> groupConnectionList = new TreeSet<>();

    public static TreeSet<GroupConnection> getGroupConnectionList() {
        return groupConnectionList;
    }

    /**
     * Dummy Sorting, since the sorted TreeSet isn't sorting it self
     * @return the oldest GroupConnection
     */
    public static GroupConnection getMainRegistryServer() {
        GroupConnection oldestGC = null;
        for (GroupConnection gc : groupConnectionList) {
            if (oldestGC == null || oldestGC.compareTo(gc) > 0) {
                oldestGC = gc;
            }
        }
        return oldestGC;
    }

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
        groupConnectionList = new TreeSet<>();

        Arrays.stream(spreadMessage.getMembershipInfo().getMembers())
                .map(this::createGroupConnection)
                .forEachOrdered(groupConnectionList::add);
        LOG.info(String.format("Current Group View: %s", groupConnectionList));
        if (getGroupConnectionList().size() > 1) {
            try {
                // Wait a second, otherwise this server will get a connection refused
                Thread.sleep(1000);
                ServerRun.sayHi();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            LOG.info("Main register server: " + SpreadMessageListener.getMainRegistryServer());
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

    public GroupConnection createGroupConnection(SpreadGroup spreadGroup) {
        String[] splited = spreadGroup.toString().split("#");
        LocalDateTime startTimestamp = ServerRun.getServerCfg().getName().equals(splited[1]) ?
                ServerRun.START_TIMESTAMP :
                LocalDateTime.now();
        return new GroupConnection(splited[1], splited[2], startTimestamp);
    }
}
