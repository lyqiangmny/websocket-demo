package com.lyqiang.websocket.server;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lyqiang
 */

@Component
@ServerEndpoint(value = "/websocketTest/{userId}")
public class TestServer {


    private static ConcurrentHashMap<String, TestServer> webSocketSession = new ConcurrentHashMap<>();

    private Session session;

    private String userId;

    /**
     * 连接
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) throws IOException {
        this.session = session;
        webSocketSession.put(userId, this);
        this.userId = userId;
        System.out.println("新连接：{}" + userId + ":" + this);
    }

    /**
     * 关闭时执行
     */
    @OnClose
    public void onClose() {
        System.out.println("连接：{} 关闭" + this.userId);
    }

    /**
     * 收到消息时执行
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        System.out.println("收到用户{}的消息{}" + this.userId + " - " + message);
        session.getBasicRemote().sendText("收到 " + this.userId + " 的消息 ");
    }

    /**
     * 连接错误时执行
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("用户id为：{}的连接发送错误" + this.userId);
        error.printStackTrace();
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        Optional<TestServer> optional = Optional.ofNullable(webSocketSession.get(userId));
        optional.ifPresent(c -> {
            try {
                c.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

}
