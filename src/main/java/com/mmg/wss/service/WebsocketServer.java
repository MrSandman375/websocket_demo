package com.mmg.wss.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmg.wss.config.ServerConfigurator;
import com.mmg.wss.config.SysUserHolder;
import com.mmg.wss.task.AsyncSaveMessage;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: fan
 * @Date: 2021/8/13
 * @Description:
 */
@Slf4j
@Component
@ServerEndpoint(value = "/socket", configurator = ServerConfigurator.class)
public class WebsocketServer {

    private static SysUserHolder sysUserHolder;
    private static AsyncSaveMessage asyncSaveMessage;

    @Autowired
    public void setWebsocketServer(SysUserHolder sysUserHolder, AsyncSaveMessage asyncSaveMessage) {
        WebsocketServer.sysUserHolder = sysUserHolder;
        WebsocketServer.asyncSaveMessage = asyncSaveMessage;
    }

    private static Map<String, WebsocketServer> socketMap = new ConcurrentHashMap<>();
    private String id;
    private Session session;

    /**
     * 连接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.id = sysUserHolder.getUser().getId();
        this.session = session;
        if (ObjectUtils.isEmpty(socketMap.get(this.id))) {
            socketMap.put(this.id, this);
        }
        sendMessage(session, "连接成功");
    }

    @OnClose
    public void onClose() {
        socketMap.remove(this.id);
        log.info("用户" + sysUserHolder.getUser().getUsername() + "下线");
    }

    /**
     * 消息格式：
     * {
     *     "message":"发送的消息",
     *     "from":"发送人",
     *     "to":"接收人"
     * }
     * @param session
     * @param message
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        if (StringUtil.isNullOrEmpty(message)) {
            return;
        }
        System.out.println(message);
        JSONObject jsonpObject = JSON.parseObject(message);
        String toUser = jsonpObject.getString("to");
        WebsocketServer websocketServer = socketMap.get(toUser);
        //保存消息
        message = jsonpObject.getString("message");
        asyncSaveMessage.saveMessage(jsonpObject.getString("from"), toUser, message);
        //如果不在线则不发送，直接结束
        if (websocketServer == null) {
            sendMessage(session, "对方不在线，存储到历史消息中");
        } else {
            System.out.println("服务端收到来自客户端的消息：" + message + "=====>需要转发给用户" + websocketServer.id);
            sendMessage(websocketServer.session, message);
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
