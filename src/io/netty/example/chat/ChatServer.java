package io.netty.example.chat;

public class ChatServer {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    new WebSocketChatServer(8082).run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    new ClientChatServer(8081).run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
    }
}
