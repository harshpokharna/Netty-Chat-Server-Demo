package com.example.handler;

import io.netty.channel.ChannelId;

/**
 * Created by harsh on 13/06/16.
 */
public interface MessageListener {
    void onMessageReceived(ChannelId id, String message);
}
