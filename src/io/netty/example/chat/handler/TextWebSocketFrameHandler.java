package io.netty.example.chat.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONObject;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.example.chat.core.Message;
import io.netty.example.chat.core.dex.DynamicCodeGenerator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup sChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final String OSS_HOST = "http://oss.aliyuncs.com";
    private static final String UPLOAD_BUCKET = "cootek-dialer-download";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
            final TextWebSocketFrame msg) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : sChannels) {
            if (channel != incoming) {
                channel.writeAndFlush(new TextWebSocketFrame("[" + incoming.remoteAddress() + "]" + msg.text()));
            } else {
                channel.writeAndFlush(new TextWebSocketFrame("[you]" + msg.text()));
            }
        }
        
        final String msgContent = msg.text();
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                DynamicCodeGenerator generator = new DynamicCodeGenerator();
                boolean result = false;
                result = generator.preBuildProcess(0);
                if (!result) {
                    System.out.println("preBuildProcess fail");
                }
                result = generator.replaceDynamicCodeFile(msgContent);
                if (!result) {
                    System.out.println("replaceDynamicCodeFile fail");
                }
                String id = "123456";
                result = generator.build(id);
                if (!result) {
                    System.out.println("build fail");
                }
                String generatedFileName = id + ".jar";
                result = upload(UPLOAD_BUCKET, "chat/" + generatedFileName, generatedFileName);
                if (result) {
                    String url = OSS_HOST + "/" + UPLOAD_BUCKET + "/chat/" + generatedFileName;
                    System.out.println(url);
                    JSONObject json = new JSONObject();
                    json.put("action", Message.ACTION_NOTIFY_NEW_MESSAGE_FROM_CS);
                    JSONObject message = new JSONObject();
                    message.put("type", "CODE");
                    message.put("content", url);
                    json.put("msg", message);
                    System.out.println(json.toString());
                    for (Channel channel : ChatServerHandler.sChannels) {
                        channel.writeAndFlush(json.toString() + "\0");
                    }
                }
            }
        }).start();
        
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel incoming = ctx.channel();
        for (Channel channel : sChannels) {
            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 加入"));
        }
        sChannels.add(ctx.channel());
        System.out.println("Client:"+incoming.remoteAddress() +"加入");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel incoming = ctx.channel();
        for (Channel channel : sChannels) {
            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 离开"));
        }
        System.out.println("Client:"+incoming.remoteAddress() +"离开");
        sChannels.remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"异常");
        cause.printStackTrace();
        ctx.close();
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
