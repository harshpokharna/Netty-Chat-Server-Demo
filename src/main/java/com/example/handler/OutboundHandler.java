package com.example.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketAddress;

/**
 * Created by harsh on 03/06/16.
 */
public class OutboundHandler extends ChannelOutboundHandlerAdapter {

    // Log4j
    private static final Logger logger = LogManager.getLogger();

    public OutboundHandler() {
        super();

        logger.info("OutBoundHandler Constructor");
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        logger.info("OutBoundHandler read");
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("OutBoundHandler write");

        if (msg instanceof String) {
            ByteBuf byteBuf = ctx.channel().alloc().buffer();
            byteBuf.writeBytes(((String)msg).getBytes());

            ctx.write(byteBuf);

        } else {
            throw new IllegalArgumentException("Outbound Handler should get String as input");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("OutBoundHandler exception - " + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
