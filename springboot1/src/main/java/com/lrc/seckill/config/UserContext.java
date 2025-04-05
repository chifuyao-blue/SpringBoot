package com.lrc.seckill.config;

import com.lrc.seckill.pojo.User;

/**
 * 类的简要描述
 *
 * @author 33182
 * @Description:
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();
    public static void setUser(User user){
        userHolder.set(user);
    }
    public static User getUser(){
        return userHolder.get();
    }
}
