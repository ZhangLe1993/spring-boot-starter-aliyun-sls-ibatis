package com.biubiu.sls.mapper;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 下午12:46
 * @description：测试对象
 * @email: zhangyule1993@sina.com
 * @version:
 */
public class User {

    private String id;

    private String name;

    public User() {
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
