package com.example.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by harsh on 15/06/16.
 */
public class BossThreadHandler extends ChannelHandlerAdapter {

    // Log4j
    private static final Logger logger = LogManager.getLogger();
    public BossThreadHandler() {

        super();
        logger.debug("BossThreadHandler Constructor");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);

        logger.debug("BossThreadHandler Added");
    }
}
