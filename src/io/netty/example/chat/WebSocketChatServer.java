package io.netty.example.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebSocketChatServer {
    private int mPort;
    
    public WebSocketChatServer(int port) {
        mPort = port;
    }
    
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.channel(NioServerSocketChannel.class)
            .group(bossGroup, workerGroup)
            .childHandler(new WebSocketChatServerInitializer())
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
            System.out.print("WebSocketChatServer started");
            
            ChannelFuture f = server.bind(mPort).sync();
            
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("WebSocketChatServer closed");
        }
    }
    
}
