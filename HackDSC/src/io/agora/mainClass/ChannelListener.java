package io.agora.mainClass;

import io.agora.rtm.RtmChannelAttribute;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmMessage;

import java.util.List;

class ChannelListener implements RtmChannelListener {
    private String channel_;
    public MainFrame mainFrame;
    public ChannelListener(String channel, MainFrame mainFrame) {
        channel_ = channel;
        this.mainFrame = mainFrame;
    }

    @Override
    public void onMemberCountUpdated(int memberCount) {
    }

    @Override
    public void onAttributesUpdated(List<RtmChannelAttribute> attribute) {
    }

    @Override
    public void onMessageReceived(
            final RtmMessage message, final RtmChannelMember fromMember) {
        String account = fromMember.getUserId();
        String msg = message.getText();

        mainFrame.addMessage("\n\n" + account + ": " + msg + "\n");

//        System.out.println("\n\n\n" + account + ": " + msg);
    }

    @Override
    public void onMemberJoined(RtmChannelMember member) {
        String account = member.getUserId();
        mainFrame.addMessage("\n\n" + account + " joined the channel!!\n");
    }

    @Override
    public void onMemberLeft(RtmChannelMember member) {
        String account = member.getUserId();
        mainFrame.addMessage("\n\n" + account + " left the channel!!\n");
    }
}
