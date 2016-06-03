package com.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by harsh on 30/05/16.
 */
public class ChatHandler extends ChannelInboundHandlerAdapter {

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

    public ChatHandler(GroupChatService groupChatService) {
        super();
        this.groupChatService = groupChatService;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("Exception - " + cause.getMessage());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        logger.info("Channel Registered -- Chat Handler");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

        logger.info("Channel UnRegistered -- Chat Handler");
        groupChatService.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        logger.info("Channel Read -- Chat Handler");
        try {

            String message;

            if(msg instanceof String) {
                message = (String) msg;
            }else{
                throw new IllegalArgumentException("Chat Handler requires a String as input");
            }

            int messageIntention = getIntentionFromMessage(message);
            handleMessage(message, messageIntention, ctx);

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void writeChatMessageToSelf(Channel channel, String message) {

        message = "[ME] --- " + message;
        ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(message.getBytes());

        channel.writeAndFlush(byteBuf);
    }

    private void writeChatMessageToOtherChannels(Channel channel, String message) {

        message = "[" + channel.id().asShortText() + "] --- " + message;
        ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(message.getBytes());

        ChannelGroup channelGroup = groupChatService.getActiveChannelContainers();

        for (Channel ch : channelGroup) {
            if (ch.id() != channel.id()) {
                String handlerRoomName = ((ChatHandler) ch.pipeline().last()).roomName;
                if (handlerRoomName.equals(roomName)) {
                    ch.writeAndFlush(byteBuf);
                }
            }
        }
    }

    private void writeMessageToSelf(String message, Channel channel) {
        ByteBuf byteBuf = channel.alloc().buffer();
        byteBuf.writeBytes(message.getBytes());

        channel.writeAndFlush(byteBuf);
    }

    private void handleMessage(String message, int messageIntention, ChannelHandlerContext ctx) {

        if (isRoomJoined) {
            if (messageIntention == INTENTION_JOIN_ROOM) {
                writeMessageToSelf("Please leave the room first!", ctx.channel());

            } else if (messageIntention == INTENTION_LEAVE_ROOM) {
                if (message.substring(6).equals(roomName)) {
                    isRoomJoined = false;
                    writeMessageToSelf(roomName + " left!", ctx.channel());
                    writeChatMessageToOtherChannels(ctx.channel(), ctx.channel().id().asShortText() + " has left the room!");
                    ctx.close();

                } else {
                    writeMessageToSelf("Invalid request", ctx.channel());
                }

            } else if (messageIntention == INTENTION_CHAT) {
                writeChatMessageToOtherChannels(ctx.channel(), message);
                writeChatMessageToSelf(ctx.channel(), message);
            }

        } else {
            if (messageIntention == INTENTION_LEAVE_ROOM) {
                writeMessageToSelf("You should join a room first!", ctx.channel());

            } else if (messageIntention == INTENTION_CHAT) {
                writeMessageToSelf("You should join a room first!", ctx.channel());

            } else if (messageIntention == INTENTION_JOIN_ROOM) {
                isRoomJoined = true;
                roomName = message.substring(5);

                groupChatService.addChannel(ctx.channel());
                writeMessageToSelf(roomName + " joined!", ctx.channel());
                writeChatMessageToOtherChannels(ctx.channel(), ctx.channel().id().asShortText() + " has joined the room!");
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
}
