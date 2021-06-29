package com.leyou.config;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.leyou.sms.config.SmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化短信API的对象
 */
@Configuration
public class SmsConfig {

    @Bean
    public Client createClient(SmsProperties smsProps) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(smsProps.getAccessKeyID())
                // 您的AccessKey Secret
                .setAccessKeySecret(smsProps.getAccessKeySecret());
        // 访问的域名
        config.endpoint = smsProps.getEndpoint();
        return new Client(config);
    }

}
