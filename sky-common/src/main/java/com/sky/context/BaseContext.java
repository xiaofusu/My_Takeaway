package com.sky.context;

/**
 * ThreadLocal为每个线程单独一份存储空间，具有线程隔离的效果，
 * 只有线程内才能获取到对应的值，线程外不能访问 可以用来存储当前信息
 * 将ThreadLocal封装成工具类
 */
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
