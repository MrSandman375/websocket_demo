package com.mmg.wss.config;

import com.mmg.wss.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @Auther: fan
 * @Date: 2021/8/18
 * @Description: websocket鉴权
 */
@Slf4j
@Component
public class ServerConfigurator extends ServerEndpointConfig.Configurator {


    private static RedisTemplate<String, Object> redisTemplate;
    private static SysUserHolder sysUserHolder;

    @Autowired
    public void setServerConfiguration(RedisTemplate<String, Object> redisTemplate, SysUserHolder sysUserHolder) {
        ServerConfigurator.redisTemplate = redisTemplate;
        ServerConfigurator.sysUserHolder = sysUserHolder;
    }

    /**
     * token鉴权认证
     *
     * @param originHeaderValue
     * @return
     */
    @Override
    public boolean checkOrigin(String originHeaderValue) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String token = request.getHeader("token");
        Object o = redisTemplate.opsForValue().get("login_" + token);
        if (!ObjectUtils.isEmpty(o)) {
            //把当前用户set到线程中
            sysUserHolder.setUser((SysUser) o);
            return super.checkOrigin(originHeaderValue);
        }
        return false;
    }

    /**
     * Modify the WebSocket handshake response
     * 修改websocket 返回值
     *
     * @param sec
     * @param request
     * @param response
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        super.modifyHandshake(sec, request, response);
    }


}