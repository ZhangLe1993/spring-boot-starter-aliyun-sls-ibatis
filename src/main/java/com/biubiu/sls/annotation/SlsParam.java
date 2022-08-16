package com.biubiu.sls.annotation;

import java.lang.annotation.*;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午10:24
 * @description：SlsParam
 * @email: zhangyule1993@sina.com
 * @version:
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface SlsParam {
    /**
     * 参数驼峰映射名称
     * @return
     */
    String value();
}
