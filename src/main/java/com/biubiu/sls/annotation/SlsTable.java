package com.biubiu.sls.annotation;

import java.lang.annotation.*;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午10:24
 * @description：日志查询数据源注解
 * @email: zhangyule1993@sina.com
 * @version:
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SlsTable {

    /**
     * sls project 例如： dms-app
     * @return
     */
    String project();

    /**
     * sls logstore 例如： dms-app-prod
     * @return
     */
    String logStore();
}
