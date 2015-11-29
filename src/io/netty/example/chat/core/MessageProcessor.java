package io.netty.example.chat.core;

import io.netty.channel.Channel;

public abstract class MessageProcessor {
    public abstract void processMessage(Message message, Channel senderChannel);
}
