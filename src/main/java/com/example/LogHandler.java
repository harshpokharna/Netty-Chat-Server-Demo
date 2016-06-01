package com.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by harsh on 01/06/16.
 */
@ChannelHandler.Sharable
public class LogHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger();

    public LogHandler() {
        super();

        logger.info("Log Handler Constructor");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        logger.info("Log Handler Channel Registered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("Log Handler Channel UnRegistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Log Handler Channel Active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Log Handler Channel InActive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String message = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);

            logger.info("Log Handler Channel Read with message - " + message);
            logger.info("Message Recd - " + message);
            ctx.fireChannelRead(message);

        } finally {
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        logger.info("Log Handler Channel Read Complete");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("Log Handler Channel UserEventTriggered");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        logger.info("Log Handler Channel Writability Changed");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getCause().printStackTrace();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("Log Handler Added");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("Log Handler Removed");
    }
}
