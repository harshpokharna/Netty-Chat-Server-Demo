package com.example.service;

import com.example.handler.MessageListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.internal.ConcurrentSet;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by harsh on 30/05/16.
 */
public class GroupChatService {

    private static GroupChatService instance;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ConcurrentMap<String, ConcurrentSet<MessageListener>> activeChannels = new ConcurrentHashMap<String, ConcurrentSet<MessageListener>>();

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

    public void addChannelHandler(MessageListener messageListener, String roomName) {

        if (activeChannels.containsKey(roomName)) {
            activeChannels.get(roomName).add(messageListener);
        } else {

            ConcurrentSet<MessageListener> handlers = new ConcurrentSet<MessageListener>();
            handlers.add(messageListener);
            activeChannels.put(roomName, handlers);
        }
    }

    public void removeChannelHandler(MessageListener messageListener, String roomName) {

        if (activeChannels.containsKey(roomName)) {
            activeChannels.get(roomName).remove(messageListener);

            if (activeChannels.get(roomName).isEmpty()) {
                activeChannels.remove(roomName);
            }
        }
    }

    public ConcurrentSet<MessageListener> getActiveChannelHandlers(String roomName) {
        return activeChannels.get(roomName);
    }

}
