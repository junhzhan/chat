package io.netty.example.chat.core.processor;

import org.json.JSONObject;

import io.netty.channel.Channel;
import io.netty.example.chat.core.Message;
import io.netty.example.chat.core.MessageParser;
import io.netty.example.chat.core.MessageProcessor;
import io.netty.example.chat.handler.TextWebSocketFrameHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class UserSendMsgProcessor extends MessageProcessor {

    private static final String CONTENT_KEY = "content";
    private static final String USER_KEY = "user";
    @Override
    public void processMessage(Message message, Channel senderChannel) {
        JSONObject contentJson = message.msg;
        String content = contentJson.getString(CONTENT_KEY);
        String sendMsg = generateNotifyMessage("test", content);
        for (Channel channel : TextWebSocketFrameHandler.sChannels) {
            channel.writeAndFlush(new TextWebSocketFrame(sendMsg));
        }
    }
    
    private String generateNotifyMessage(String user, String message) {
        JSONObject notifyCustomService = new JSONObject();
        notifyCustomService.put(MessageParser.ACTION_KEY, Message.ACTION_NOTIFY_NEW_MESSAGE_FROM_USER);
        JSONObject notifyMsg = new JSONObject();
        notifyMsg.put(USER_KEY, "test");
        notifyMsg.put(CONTENT_KEY, message);
        notifyCustomService.put(MessageParser.MESSAGE_KEY, notifyMsg);
        return notifyCustomService.toString();
    }

}
