package io.netty.example.chat.core.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

import io.netty.channel.Channel;
import io.netty.example.chat.core.Message;
import io.netty.example.chat.core.MessageParser;
import io.netty.example.chat.core.MessageProcessor;
import io.netty.example.chat.core.dex.DynamicCodeGenerator;
import io.netty.example.chat.handler.ChatServerHandler;

public class CSSendMsgProcessor extends MessageProcessor {

    
    private static final String MESSAGE_TYPE_CODE = "CODE";
    private static final String MESSAGE_TYPE_STRING = "TEXT";
    
    private static final String MSG_TYPE_KEY = "type";
    private static final String MSG_CONTENT_KEY = "content";
    
    private static final String SEND_MSG_TYPE_KEY = "type";
    private static final String SEND_MSG_CONTENT_KEY = "content";
    
    private static final String OSS_HOST = "http://oss.aliyuncs.com";
    private static final String UPLOAD_BUCKET = "cootek-dialer-download";
    @Override
    public void processMessage(Message message, Channel senderChannel) {
        JSONObject msgJson = message.msg;
        String type = msgJson.getString(MSG_TYPE_KEY);
        String content = msgJson.getString(MSG_CONTENT_KEY);
        if (MESSAGE_TYPE_CODE.equalsIgnoreCase(type)) {
            String sendMsg = generateCodeNotification(content);
            if (sendMsg != null) {
                for (Channel channel : ChatServerHandler.sChannels) {
                    channel.writeAndFlush(sendMsg + "\0");
                }
            }
        } else if (MESSAGE_TYPE_STRING.equalsIgnoreCase(type)) {
            String sendMsg = generateCommonNotification(content);
            if (sendMsg != null) {
                for (Channel channel : ChatServerHandler.sChannels) {
                    channel.writeAndFlush(sendMsg + "\0");
                }
            }
        }
    }
    
    private String generateCommonNotification(String messageContent) {
        JSONObject notification = new JSONObject();
        notification.put(MessageParser.ACTION_KEY, Message.ACTION_NOTIFY_NEW_MESSAGE_FROM_CS);
        JSONObject msg = new JSONObject();
        msg.put(SEND_MSG_TYPE_KEY, MESSAGE_TYPE_STRING);
        msg.put(SEND_MSG_CONTENT_KEY, messageContent);
        notification.put(MessageParser.MESSAGE_KEY, msg);
        return notification.toString();
    }
    
    private String generateCodeNotification(String code) {
        DynamicCodeGenerator generator = new DynamicCodeGenerator();
        boolean result = false;
        result = generator.preBuildProcess(0);
        if (!result) {
            System.out.println("preBuildProcess fail");
            return null;
        }
        result = generator.replaceDynamicCodeFile(code);
        if (!result) {
            System.out.println("replaceDynamicCodeFile fail");
            return null;
        }
        String id = "123456";
        result = generator.build(id);
        if (!result) {
            System.out.println("build fail");
            return null;
        }
        String generatedFileName = id + ".jar";
        result = upload(UPLOAD_BUCKET, "chat/" + generatedFileName, generatedFileName);
        if (result) {
            String url = OSS_HOST + "/" + UPLOAD_BUCKET + "/chat/" + generatedFileName;
            System.out.println(url);
            JSONObject json = new JSONObject();
            json.put(MessageParser.ACTION_KEY, Message.ACTION_NOTIFY_NEW_MESSAGE_FROM_CS);
            JSONObject message = new JSONObject();
            message.put(SEND_MSG_TYPE_KEY, MESSAGE_TYPE_CODE);
            message.put(SEND_MSG_CONTENT_KEY, url);
            json.put(MessageParser.MESSAGE_KEY, message);
            System.out.println(json.toString());
            return json.toString();
        }
        return null;
    }
    
    private boolean upload(String bucketName, String path, String localFilePath) {
        String accessId = "tA8pC6ItuicFZyEM";
        String accessSecretKey = "Ds0EcOV9UDLwqLXgLvFfVIybDE4aUK";
        OSSClient client = new OSSClient(OSS_HOST, accessId, accessSecretKey);
        File dexFile = new File(localFilePath);
        boolean result = false;
        try {
            InputStream content = new FileInputStream(dexFile);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(dexFile.length());
            PutObjectResult putResult = client.putObject(bucketName, path, content, meta);
            result = putResult.getETag() != null && putResult.getETag().length() > 0;
        } catch (IOException e) {
            
        }
        return result;
    }

}
