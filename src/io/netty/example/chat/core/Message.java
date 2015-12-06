package io.netty.example.chat.core;

import org.json.JSONObject;

public class Message {
    
    public static final int ACTION_USER_CONNECT = 1000;
    public static final int ACTION_USER_SEND_MESSAGE = 1001;
    public static final int ACTION_NOTIFY_CONNECT_RESULT = 2000;
    public static final int ACTION_NOTIFY_NEW_MESSAGE_FROM_CS = 2001;
    public static final int ACTION_CS_SEND_MESSAGE = 3000;
    public static final int ACTION_NOTIFY_NEW_MESSAGE_FROM_USER = 4001;
    
    
    public final int action;
    public final JSONObject msg;
    
    public Message(int action, JSONObject content) {
        this.action = action;
        this.msg = content;
    }
}
