package com.biubiu.sls.annotation;

import java.lang.annotation.*;

/**
 * @description: 日志查询sql mapper
 * @author: 张音乐
 * @date: Created in 2022/8/16 20:06
 * @email: zhangyule1993@sina.com
 * @version: 1.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
public @interface SlsMapper {
}
