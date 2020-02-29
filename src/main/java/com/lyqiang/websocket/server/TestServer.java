package com.lyqiang.websocket.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lyqiang
 */

@Component
@Slf4j
@ServerEndpoint(value = "/websocketTest/{userId}")
public class TestServer {

    /**
     * 用 list 维护，处理用户打开多个标签页
     */
    private static ConcurrentHashMap<String, Set<TestServer>> webSocketSession = new ConcurrentHashMap<>();

    private Session session;

    private String userId;

    /**
     * 连接
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) throws IOException {
        this.session = session;
        if (webSocketSession.containsKey(userId)) {
            webSocketSession.get(userId).add(this);
        } else {
            Set<TestServer> set = new HashSet<>();
            set.add(this);
            webSocketSession.put(userId, set);
        }
        this.userId = userId;
        log.info("新连接：{}, this:{}", userId, this);
    }

    /**
     * 关闭时执行
     */
    @OnClose
    public void onClose() {
        webSocketSession.get(userId).remove(this);
        log.info("连接：{} 关闭", this.userId);
    }

    /**
     * 收到消息时执行
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到用户 {} 的消息 {} , this: {}", this.userId, message, this);
        session.getBasicRemote().sendText("你好，服务端已经收到 " + this.userId + " 的消息： " + message);
    }

    /**
     * 连接错误时执行
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户id为：{}的连接发送错误", this.userId, error);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, String userId) {
        Set<TestServer> serverSet = webSocketSession.get(userId);
        if (serverSet != null && !serverSet.isEmpty()) {
            serverSet.forEach(s -> s.sendMessage(message));
        }
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("sendMsg error", e);
        }
    }
}
