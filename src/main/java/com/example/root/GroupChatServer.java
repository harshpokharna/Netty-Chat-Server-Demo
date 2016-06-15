package com.example.root;

import com.example.handler.MessageDecoder;
import com.example.handler.MessageEncoder;
import com.example.service.GroupChatService;
import com.example.handler.InboundLogHandler;
import com.example.handler.GroupChatHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by harsh on 29/05/16.
 */
public class GroupChatServer {

    // Log4j
    private static final Logger logger = LogManager.getRootLogger();

    // Log Handler
    private static final InboundLogHandler INBOUND_LOG_HANDLER = new InboundLogHandler();

    // MessageDecoder
    private static final MessageDecoder MESSAGE_DECODER = new MessageDecoder();
    private static final MessageEncoder MESSAGE_ENCODER = new MessageEncoder();

    ThreadFactory bossFactory = new ThreadFactoryBuilder()
            .setNameFormat("BOSS-%d")
            .build();

    ThreadFactory workerFactory = new ThreadFactoryBuilder()
            .setNameFormat("WORKER-%d")
            .build();

    EventExecutorGroup businessLogicExecutors = new NioEventLoopGroup();

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1, bossFactory);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(10, workerFactory);

    private int port;

    public GroupChatServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {

        final GroupChatService groupChatService = GroupChatService.getInstance();

        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(INBOUND_LOG_HANDLER);
                            ch.pipeline().addLast(MESSAGE_DECODER);
                            ch.pipeline().addLast(MESSAGE_ENCODER);
                            ch.pipeline().addLast(businessLogicExecutors, new GroupChatHandler(groupChatService));

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            logger.info("Server running");

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {

        logger.info("Main method called");
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new GroupChatServer(port).run();
    }
}
