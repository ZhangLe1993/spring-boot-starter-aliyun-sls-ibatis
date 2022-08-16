package com.biubiu.sls.annotation;

import java.lang.annotation.*;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午10:24
 * @description：日志查询注解
 * @email: zhangyule1993@sina.com
 * @version:
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SlsSelect {

    /**
     * 传入SQL 语句
     * @return
     */
    String value();
}
