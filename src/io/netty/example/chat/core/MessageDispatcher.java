package io.netty.example.chat.core;

import java.util.HashMap;

import org.json.JSONObject;

import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class MessageDispatcher {
    
    
    
    private static final HashMap<Integer, Class<? extends MessageProcessor>> sProcessorMap = new HashMap<Integer, Class<? extends MessageProcessor>>();
    
    static {
    }
    private MessageDispatcher() {
        
    }
    private static class SingletonHolder {
        static MessageDispatcher sInst = new MessageDispatcher();
    }
    public static MessageDispatcher getInst() {
        return SingletonHolder.sInst;
    }
    
    private DefaultEventExecutorGroup mDefaultExecutor = new DefaultEventExecutorGroup(1);
    
    public void dispatchMessage(Message message) {
        int action = message.action;
        JSONObject content = message.content;
        
    }
    
}
