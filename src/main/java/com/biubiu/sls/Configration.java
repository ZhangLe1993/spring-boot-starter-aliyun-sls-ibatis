package com.biubiu.sls;

import com.aliyun.openservices.log.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：张音乐
 * @date ：Created in 2021/5/30 上午11:11
 * @description：
 * @email: zhangyule1993@sina.com
 * @version:
 */
@Configuration
public class Configration {

    @Value("${sls.ibatis.endpoint}")
    private String endpoint;

    @Value("${sls.ibatis.accessKeyId}")
    private String accessKeyId;

    @Value("${sls.ibatis.accessKeySecret}")
    private String accessKeySecret;

    @Bean(name="slsClient")
    public Client slsClient() {
        return new Client(endpoint, accessKeyId, accessKeySecret);
    }


}
