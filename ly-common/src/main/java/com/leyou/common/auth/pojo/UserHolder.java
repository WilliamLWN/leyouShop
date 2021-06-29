package com.leyou.common.auth.pojo;

/**
 * 用于从ThreadLocal对象中存取数据的工具
 */
public class UserHolder {
    private static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();


    /**
     * 存入数据到ThreadLocal
     */
    public static void setUser(UserInfo userInfo){
        threadLocal.set(userInfo);
    }

    /**
     * 从ThreadLocal取出对象
     */
    public static UserInfo getUser(){
        return threadLocal.get();
    }

    /**
     * 删除数据
     */
    public static void removeUser(){
        threadLocal.remove();
    }
}
