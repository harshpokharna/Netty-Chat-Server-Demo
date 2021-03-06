package com.example.handler;

import com.example.service.GroupChatService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ConcurrentSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by harsh on 30/05/16.
 */
public class GroupChatHandler extends ChannelInboundHandlerAdapter implements MessageListener {

    // Constants
    private static final int INTENTION_JOIN_ROOM = 0;
    private static final int INTENTION_LEAVE_ROOM = 1;
    private static final int INTENTION_CHAT = 2;
    private static final String LEAVE_ROOM = "leave:";
    private static final String JOIN_ROOM = "join:";

    // Log4j
    private static final Logger logger = LogManager.getLogger();

    private boolean isRoomJoined;
    public String roomName;
    private GroupChatService groupChatService;

    private ChannelHandlerContext ctx;

    public GroupChatHandler(GroupChatService groupChatService) {
        super();
        this.groupChatService = groupChatService;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("Exception - " + cause.getMessage());
        cause.getCause().printStackTrace();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        this.ctx = ctx;
        logger.info("Chat Handler ChannelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

        logger.info("Chat Handler ChannelUnregistered");
        if (roomName != null) {
            groupChatService.removeChannelHandler(this, roomName);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        logger.info("Chat Handler ChannelRead");
        try {

            String message;

            if (msg instanceof String) {
                message = (String) msg;
            } else {
                throw new IllegalArgumentException("Chat Handler requires a String as input");
            }

            int messageIntention = getIntentionFromMessage(message);
            handleMessage(message, messageIntention, ctx);

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void writeChatMessageToSelf(String message) {
        logger.debug("GroupChatHandler writeCHatMessageToSelf");
        message = "[ME] --- " + message;

        ctx.writeAndFlush(message);
    }

    private void writeChatMessageToOtherChannels(String message) {
        logger.debug("GroupChatHandler writeMessageToOtherChannels");
        message = "[" + ctx.channel().id().asShortText() + "] --- " + message;

        ConcurrentSet<MessageListener> messageListeners = new ConcurrentSet<MessageListener>();

        if (roomName != null) {

            messageListeners = groupChatService.getActiveChannelHandlers(roomName);
        }

        for (MessageListener messageListener : messageListeners) {

            messageListener.onMessageReceived(ctx.channel().id().asShortText(), message);
        }
    }

    private void writeMessageToSelf(String message) {
        logger.debug("GroupChatHandler writeMessageToSelf");

        ctx.writeAndFlush(message);
    }

    private void handleMessage(String message, int messageIntention, ChannelHandlerContext ctx) {

        if (isRoomJoined) {
            if (messageIntention == INTENTION_JOIN_ROOM) {
                writeMessageToSelf("Please leave the room first!");

            } else if (messageIntention == INTENTION_LEAVE_ROOM) {
                if (message.substring(6).equals(roomName)) {
                    isRoomJoined = false;
                    writeMessageToSelf(roomName + " left!");
                    writeChatMessageToOtherChannels(ctx.channel().id().asShortText() + " has left the room!");
                    ctx.close();

                } else {
                    writeMessageToSelf("Invalid request");
                }

            } else if (messageIntention == INTENTION_CHAT) {
                writeChatMessageToOtherChannels(message);
                writeChatMessageToSelf(message);
            }

        } else {
            if (messageIntention == INTENTION_LEAVE_ROOM) {
                writeMessageToSelf("You should join a room first!");

            } else if (messageIntention == INTENTION_CHAT) {
                writeMessageToSelf("You should join a room first!");

            } else if (messageIntention == INTENTION_JOIN_ROOM) {
                isRoomJoined = true;
                roomName = message.substring(5);

                groupChatService.addChannelHandler(this, roomName);
                writeMessageToSelf(roomName + " joined!");
                writeChatMessageToOtherChannels(ctx.channel().id().asShortText() + " has joined the room!");
            }
        }
    }

    private int getIntentionFromMessage(String message) {

        if (message.startsWith(JOIN_ROOM)) {
            return INTENTION_JOIN_ROOM;
        } else if (message.startsWith(LEAVE_ROOM)) {
            return INTENTION_LEAVE_ROOM;
        } else {
            return INTENTION_CHAT;
        }
    }

    public void onMessageReceived(String id, String message) {

        if (!id.equals(ctx.channel().id().asShortText())) {
            ctx.writeAndFlush(message);
        }
    }
}
