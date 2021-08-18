package com.mmg.wss.config;

import com.mmg.wss.entity.SysUser;
import org.springframework.stereotype.Component;

/**
 * @Auther: fan
 * @Date: 2021/8/18
 * @Description:
 */
@Component
public class SysUserHolder {
    private static ThreadLocal<SysUser> user = new ThreadLocal<>();

    public SysUser getUser(){
        return user.get();
    }

    public void setUser(SysUser user){
        SysUserHolder.user.set(user);
    }

    public void clear(){
        user.remove();
    }
}