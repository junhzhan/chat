package io.netty.example.chat.core;

import io.netty.channel.Channel;

public class Session {
    private final String mUserId;
    private final String mServiceId;
    private final Channel mUserChannel;
    private final Channel mServiceChannel;
    
    public Session(String userId, String serviceId, Channel userChannel, Channel serviceChannel) {
        mUserId = userId;
        mServiceId = serviceId;
        mUserChannel = userChannel;
        mServiceChannel = serviceChannel;
    }
    
    public String getUserId() {
        return mUserId;
    }
    
    public String getServiceId() {
        return mServiceId;
    }
    
    public Channel getUserChannel() {
        return mUserChannel;
    }
    
    public Channel getServiceChannel() {
        return mServiceChannel;
    }
}
