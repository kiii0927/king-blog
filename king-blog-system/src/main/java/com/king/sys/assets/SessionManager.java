package com.king.sys.assets;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *    session manager
 * </p>
 *
 * @author king
 * @version 1.0
 * @since 2023-05-16
 **/
public class SessionManager {

    /**
     * 保存连接 session 的地方
     */
    public static final Map<String, Session> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 添加 session
     *
     * @param key key
     */
    public static void add(String key, Session session) {
        if (Objects.isNull(SESSION_POOL.get(key))) {
            SESSION_POOL.put(key, session);
        }
    }

    /**
     * 删除 session,会返回删除的 session
     *
     * @param key key
     * @return WebSocketSession
     */
    public static Session remove(String key) {
        return SESSION_POOL.remove(key);
    }

    /**
     * 删除并同步关闭连接
     *
     * @param key key
     */
    public static void removeAndClose(String key) {
        Session session = remove(key);
        if (session != null) {
            try {
                // 关闭连接
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获得 session
     *
     * @param key key
     * @return WebSocketSession
     */
    public static Session get(String key) {
        return SESSION_POOL.get(key);
    }
}
