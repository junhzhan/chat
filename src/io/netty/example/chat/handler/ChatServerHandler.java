package io.netty.example.chat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.example.chat.core.Message;
import io.netty.example.chat.core.MessageDispatcher;
import io.netty.example.chat.core.MessageParser;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup sChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)
            throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : sChannels) {
            if (channel == incoming) {
                channel.writeAndFlush("[you]" + msg + "\0");
            } else {
                channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + msg + "\0");
            }
        }
        
        System.out.println("receive user message");
        System.out.println(msg);
        Message message = MessageParser.parseMessage(msg);
        MessageDispatcher.getInst().dispatchMessage(message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:" + incoming.remoteAddress() + " online");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        ChannelFuture writeFuture = incoming.writeAndFlush("Message after inactive");
        writeFuture.addListener(new ChannelFutureListener() {
            
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("write message in channelInactive result " + future.isSuccess());
            }
        });
        System.out.println("SimpleChatClient:" + incoming.remoteAddress() + " offline");
        ChannelFuture future = ctx.close();
        future.addListener(new ChannelFutureListener() {
            
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("close future result " + future.isSuccess());
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : sChannels) {
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " participate \0");
        }
        sChannels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : sChannels) {
            channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " leave \0");
        }
        sChannels.remove(incoming);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient:" + incoming.remoteAddress() + " exeception");
        cause.printStackTrace();
        ctx.close();
    }
    
    

}
