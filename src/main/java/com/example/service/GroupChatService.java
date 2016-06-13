package com.example.service;

import com.example.handler.MessageListener;
import io.netty.util.internal.ConcurrentSet;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by harsh on 30/05/16.
 */
public class GroupChatService {

    private static GroupChatService instance;

    private ConcurrentMap<String, ConcurrentSet<MessageListener>> activeChannels = new ConcurrentHashMap<String, ConcurrentSet<MessageListener>>();

    public static GroupChatService getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new GroupChatService();
            return instance;
        }
    }

    private GroupChatService() {
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
