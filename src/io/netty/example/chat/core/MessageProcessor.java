package io.netty.example.chat.core;

public abstract class MessageProcessor {
    public abstract void processMessage(Message message);
}
