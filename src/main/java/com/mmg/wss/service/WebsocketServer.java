package com.mmg.wss.service;

import com.mmg.wss.entity.SysUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: fan
 * @Date: 2021/8/13
 * @Description:
 */
@Data
@Slf4j
@Component
@ServerEndpoint(value = "/socket/{token}/{to}")
public class WebsocketServer {


    private static RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setWebsocketServer(RedisTemplate<String, Object> redisTemplate) {
        WebsocketServer.redisTemplate = redisTemplate;
    }

    private static Map<String, WebsocketServer> socketMap = new ConcurrentHashMap<>();
    private String id;
    private Session session;

    /**
     * 连接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        SysUser sysUser = (SysUser) redisTemplate.opsForValue().get("login_" + token);
        if (ObjectUtils.isEmpty(sysUser)) {
            sendMessage(session, "请先登录");
            try {
                session.close();
            } catch (IOException e) {
                log.info("session关闭失败{}", e.getMessage());
            }
        } else {
            this.id = sysUser.getId();
            this.session = session;
            socketMap.put(sysUser.getId(), this);
            sendMessage(session, "连接成功");
            System.out.println(socketMap);
        }

    }

    @OnClose
    public void onClose(@PathParam("token") String token) {
        socketMap.remove(this.id);
        log.info("用户" + token + "下线");
        System.out.println(socketMap);
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("token") String token, @PathParam("to") String to, String message) {
        WebsocketServer websocketServer = socketMap.get(to);
        //保存消息
        //************
        //如果不在线则不发送，直接结束
        if (websocketServer == null) {
            sendMessage(session, "对方不在线，存储到历史消息中");
        } else {
            System.out.println("服务端收到来自客户端的消息：" + message + "=====>需要转发给用户" + websocketServer.getId());
            sendMessage(websocketServer.getSession(), message);
            sendMessage(session, "发送成功");
        }

    }

    public void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.info("消息发送异常,info：{}", e.getMessage());
        }
    }

}
