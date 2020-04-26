package io.agora.mainClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmMessage;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.rtm.RtmChannel;
import io.agora.rtm.RtmChannelListener;
import io.agora.rtm.RtmChannelMember;
import io.agora.rtm.RtmStatusCode;
import io.agora.rtm.RtmChannelAttribute;

import javax.swing.*;

public class RtmJavaDemo {
    private RtmClient mRtmClient;
    private RtmChannel mRtmChannel;
    private boolean loginStatus = false;
    private Scanner scn;

    public void init() {
        try {
            mRtmClient = RtmClient.createInstance(APPID.APP_ID,
                    new RtmClientListener() {
                        @Override
                        public void onConnectionStateChanged(int state, int reason) {
                            System.out.println("on connection state changed to "
                                    + state + " reason: " + reason);
                        }

                        @Override
                        public void onMessageReceived(RtmMessage rtmMessage, String peerId) {
                            String msg = rtmMessage.getText();
                            System.out.println("Receive message: " + msg
                                    + " from " + peerId);
                        }

                        @Override
                        public void onTokenExpired() {
                        }

                        @Override
                        public void onPeersOnlineStatusChanged(Map<String, Integer> peersStatus) {
                        }
                    });
        } catch (Exception e) {
            System.out.println("Rtm sdk init fatal error!");
            throw new RuntimeException("Need to check rtm sdk init process");
        }
        scn = new Scanner(System.in);
    }

    public boolean login(MainFrame mainFrame) {
        String userId = mainFrame.askUserString("Please enter userID (literal \"null\" or starting " + "with space is not allowed, no more than 64 charaters!):");
        if (userId.equals("") ||
                userId.startsWith(" ") ||
                userId.equals("null")) {
            System.out.println("Invalid userID detected!");
            return false;
        }
        mRtmClient.login(null, userId, new ResultCallback<Void>() {
            //@Override
            public void onSuccess(Void responseInfo) {
                loginStatus = true;
                System.out.println("login success!");
            }

            //@Override
            public void onFailure(ErrorInfo errorInfo) {
                loginStatus = false;
                System.out.println("login failure!");
            }
        });
        return true;
    }

    public void logout() {
        loginStatus = false;
        mRtmClient.logout(null);
    }

    public void p2pChat(String dst) {
        String msg;
        while (true) {
            System.out.println("please input message you want to send," +
                    " or input \'quit\' " + " to leave p2pChat");
            msg = scn.nextLine();
            if (msg.equals("quit")) {
                return;
            } else {
                sendPeerMessage(dst, msg);
            }
        }
    }

    public void groupChat(String channel, MainFrame mainFrame) {
        String msg;
        int counter = 0;
        mRtmChannel = mRtmClient.createChannel(channel,
                new ChannelListener(channel));
        if (mRtmChannel == null) {
            System.out.println("channel created failed!");
            return;
        }
        mRtmChannel.join(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void responseInfo) {
                System.out.println("join channel success!");
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                System.out.println("join channel failure! errorCode = "
                        + errorInfo.getErrorCode());
            }
        });

//        while(true) {
//            System.out.println("please input message you want to send,"+
//                    " or input \'quit\' " + " to leave groupChat, " +
//                    "or input \'members\' to list members");
//            msg = scn.nextLine();
//            if (msg.equals("quit")) {
//                mRtmChannel.leave(null);
//                mRtmChannel.release();
//                mRtmChannel = null;
//                return;
//            } else if (msg.equals("members")) {
//                getChannelMemberList();
//            } else {
//                sendChannelMessage(msg);
//            }
//        }

        while (true) {
            if (counter == 0){
                mainFrame.addMessage("please input message you want to send," + " or input \'quit\' " + " to leave groupChat, " + "or input \'members\' to list members");
                counter++;
            }
//            msg = mainFrame.getMessage();

            if (!mainFrame.getMessage().equals("")){
                System.out.println("MSG: " + mainFrame.getMessage());
                if (mainFrame.getMessage().equals("quit")) {
                    mRtmChannel.leave(null);
                    mRtmChannel.release();
                    mRtmChannel = null;
                    return;
                } else if (mainFrame.getMessage().equals("members")) {
                    getChannelMemberList();
                } else{
                    sendChannelMessage(mainFrame.getMessage());
                    counter = 0;
                    mainFrame.clearMessage();
                    msg = mainFrame.getMessage();
                }
            } else{
                System.out.println("");
            }


        }
    }

    public void sendPeerMessage(String dst, String message) {
        RtmMessage msg = mRtmClient.createMessage();
        msg.setText(message);

        mRtmClient.sendMessageToPeer(dst, msg, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                final int errorCode = errorInfo.getErrorCode();
                System.out.println("Send Message to peer failed, errorCode = "
                        + errorCode);
            }
        });
    }

    public void getChannelMemberList() {
        mRtmChannel.getMembers(new ResultCallback<List<RtmChannelMember>>() {
            @Override
            public void onSuccess(final List<RtmChannelMember> responseInfo) {
                for (int i = 0; i < responseInfo.size(); i++) {
                    System.out.println("memberlist[" + i + "]" + ": "
                            + responseInfo.get(i).getUserId());
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                System.out.println("failed to get channel members, errCode = "
                        + errorInfo.getErrorCode());
            }
        });
    }

    public void sendChannelMessage(String msg) {
        RtmMessage message = mRtmClient.createMessage();
        message.setText(msg);


        mRtmChannel.sendMessage(message, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                final int errorCode = errorInfo.getErrorCode();
                System.out.println("Send Message to channel failed, erroCode = "
                        + errorCode);
            }
        });
    }

    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////
        MainFrame mainFrame = new MainFrame("FriendMe");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = mainFrame;
                frame.setSize(500, 400);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });

        ////////////////////////////////////////////////////////////

        RtmJavaDemo client_ = new RtmJavaDemo();
        client_.init();
        while (true) {
            if (!client_.loginStatus) {
                if (!client_.login(mainFrame))
                    continue;
            }

            int choice = mainFrame.askUserInt("1: peer to peer chat\n" + "2: group chat\n" + "3: logout" + "\nplease input your choice:");

            if (choice == 1) {
                String dst = mainFrame.askUserString("please input your destination user ID:");
                client_.p2pChat(dst);
            } else if (choice == 2) {
                String channel = mainFrame.askUserString("please input your channel ID:");
                client_.groupChat(channel, mainFrame);
            } else if (choice == 3) {
                client_.logout();
                String quit = mainFrame.askUserString("quit the demo? yes/no");
                if (quit.equals("yes")) {
                    break;
                }
            }
//            } else {
//                continue;
//            }
        }
        System.out.println("leaving demo...");
        System.exit(0);
    }
}
