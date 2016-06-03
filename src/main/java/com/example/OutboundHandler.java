package com.example;

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
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        logger.info("OutBoundHandler bind");
        super.bind(ctx, localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        logger.info("OutBoundHandler connect");
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        logger.info("OutBoundHandler disconnect");
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        logger.info("OutBoundHandler close");
        super.close(ctx, promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        logger.info("OutBoundHandler deregister");
        super.deregister(ctx, promise);
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

            ctx.channel().write(byteBuf);

        } else {
            throw new IllegalArgumentException("Outbound Handler should get String as input");
        }

        super.write(ctx, msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        logger.info("OutBoundHandler flush");
        super.flush(ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("OutBoundHandler handlerAdded");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        logger.info("OutBoundHandler handlerRemoved");
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("OutBoundHandler exception - " + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }
}
