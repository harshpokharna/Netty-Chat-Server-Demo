package com.example.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by harsh on 01/06/16.
 */
@ChannelHandler.Sharable
public class MessageDecoder extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger();

    public MessageDecoder() {
        super();

        logger.info("MessageDecoder Constructor");
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        logger.info("MessageDecoder ChannelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("MessageDecoder ChannelUnRegistered");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info("MessageDecoder ChannelRead");
            String message = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);

            ctx.fireChannelRead(message);

        } finally {

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getCause().printStackTrace();
    }
}
