package com.biubiu.sls.proxy;

import com.biubiu.sls.handler.MapperInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午10:34
 * @description：动态代理类 - 用于创建代理对象
 * @email: zhangyule1993@sina.com
 * @version:
 */
public class MapperProxy {

    /**
     * 将 mapper 接口 创建成代理对象
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getMapper(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new MapperInvocationHandler());
    }
}
