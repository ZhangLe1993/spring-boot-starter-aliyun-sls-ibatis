package com.biubiu.sls.annotation;

import com.biubiu.sls.SlsMapperScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午10:24
 * @description：mapperScan
 * @email: zhangyule1993@sina.com
 * @version:
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(SlsMapperScannerRegistrar.class)
public @interface SlsMapperScan {

    /**
     * 需要注册到spring ioc容器中的bean的包名数组
     * @return
     */
    String[] basePackages() default {};
}
