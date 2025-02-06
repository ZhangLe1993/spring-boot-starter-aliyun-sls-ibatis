package com.biubiu.sls;

import com.biubiu.sls.proxy.MapperProxy;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午10:20
 * @description：
 * @email: zhangyule1993@sina.com
 * @version:
 */
public class SlsMapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    public SlsMapperFactoryBean() {
    }

    public SlsMapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        return MapperProxy.getMapper(this.mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}