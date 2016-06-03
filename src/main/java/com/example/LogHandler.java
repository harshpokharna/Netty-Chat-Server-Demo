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

        logger.info("Log Handler ChannelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("Log Handler ChannelUnRegistered");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info("Log handler ChannelRead");
            ctx.fireChannelRead(msg);

        } finally {
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getCause().printStackTrace();
    }
}
