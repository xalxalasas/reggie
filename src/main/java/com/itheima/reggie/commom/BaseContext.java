package com.itheima.reggie.commom;


/*
* 基于Theardloca（线程的局部变量）的封装类
* 用来获取和保存当前用户的id
*工具方记得设置为静态的
* 作用域是线程*/
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
