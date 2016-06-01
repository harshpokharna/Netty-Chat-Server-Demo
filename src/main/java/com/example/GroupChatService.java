package com.example;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by harsh on 30/05/16.
 */
public class GroupChatService {

    private static GroupChatService instance;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ChannelGroup activeChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static GroupChatService getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new GroupChatService(new NioEventLoopGroup(), new NioEventLoopGroup());
            return instance;
        }
    }

    private GroupChatService(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    public EventLoopGroup getBossGroup() {
        return bossGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public void addChannel(Channel channel) {
        activeChannels.add(channel);
    }

    public void removeChannel(Channel channel) {
        activeChannels.remove(channel);
    }

    public ChannelGroup getActiveChannelContainers() {
        return activeChannels;
    }

}
