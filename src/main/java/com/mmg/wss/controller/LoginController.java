package com.mmg.wss.controller;

import com.mmg.wss.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Auther: fan
 * @Date: 2021/8/17
 * @Description:
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/login/{id}/{username}")
    public String login(@PathVariable("id") String id, @PathVariable("username") String username) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        SysUser sysUser = SysUser.builder()
                .id(id)
                .username(username)
                .build();
        redisTemplate.opsForValue().set("login_" + token, sysUser);
        return token;
    }


}
