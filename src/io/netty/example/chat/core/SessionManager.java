package io.netty.example.chat.core;

import java.util.HashMap;

import io.netty.channel.Channel;

public class SessionManager {
    private HashMap<String, Session> mSessionMap = new HashMap<String, Session>();
    
    
    private SessionManager() {
        
    }
    public Session getSession(String id) {
        return mSessionMap.get(id);
    }
    
    /**
     * register a session to the system
     * @param userId id of user
     * @param serviceId id of service
     * @param userChannel channel of user 
     * @param serviceChannel channel of service
     * @return true if session is created. If the user or the service is already bound with a session, the register operation fails resulting in false.
     */
    public boolean registerSession(String userId, String serviceId, Channel userChannel, Channel serviceChannel) {
        if (mSessionMap.get(userId) != null || mSessionMap.get(serviceId) != null) {
            return false;
        }
        Session session = new Session(userId, serviceId, userChannel, serviceChannel);
        mSessionMap.put(userId, session);
        mSessionMap.put(serviceId, session);
        return true;
    }
    
    /**
     * unregister a session bound with provided id
     * @param userOrServiceId id for a user or a service
     * @return true if the operation succeed. Otherwise, false
     */
    public boolean unregisterSession(String userOrServiceId) {
        if (mSessionMap.get(userOrServiceId) == null) {
            return false;
        }
        Session session = mSessionMap.get(userOrServiceId);
        String otherId = session.getUserId().equals(userOrServiceId) ? session.getServiceId() : session.getUserId();
        mSessionMap.remove(userOrServiceId);
        mSessionMap.remove(otherId);
        return true;
    }
    
    private static class SingletonHolder {
        static final SessionManager sInst = new SessionManager();
    }
    public SessionManager getInst() {
        return SingletonHolder.sInst;
    }
}
