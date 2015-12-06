package io.netty.example.chat.core;

import java.util.HashMap;

import io.netty.example.chat.core.processor.CSSendMsgProcessor;
import io.netty.example.chat.core.processor.UserSendMsgProcessor;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class MessageDispatcher {
    
    
    
    private static final HashMap<Integer, Class<? extends MessageProcessor>> sProcessorMap = new HashMap<Integer, Class<? extends MessageProcessor>>();
    
    static {
        sProcessorMap.put(Message.ACTION_USER_SEND_MESSAGE, UserSendMsgProcessor.class);
        sProcessorMap.put(Message.ACTION_CS_SEND_MESSAGE, CSSendMsgProcessor.class);
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
    
    public void dispatchMessage(final Message message) {
        mDefaultExecutor.execute(new Runnable() {
            
            @Override
            public void run() {
                int action = message.action;
                Class<? extends MessageProcessor> processorClazz = sProcessorMap.get(action);
                try {
                    System.out.println("execute in thread " + Thread.currentThread().getId());
                    MessageProcessor processor = processorClazz.newInstance();
                    processor.processMessage(message, null);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        
    }
    
}
