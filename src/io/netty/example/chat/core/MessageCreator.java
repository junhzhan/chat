package io.netty.example.chat.core;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageCreator {
    
    private static final String ACTION_KEY = "action";
    private static final String MESSAGE_KEY = "msg";
    public static Message parseMessage(String rawMessage) {
        Message result = null;
        try {
            JSONObject json = new JSONObject(rawMessage);
            int action = json.getInt(ACTION_KEY);
            JSONObject content = json.getJSONObject(MESSAGE_KEY);
            result = new Message(action, content);
        } catch (JSONException e) {
            
        }
        return result;
    }
}
