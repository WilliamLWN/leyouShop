package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Oss初始化工具类对象配置
 */
@Configuration
public class OssConfig {
    /*
    @Autowired
    private OssProperties ossProps*/;

    /**
     * 注意：在@Bean的方法的参数上面可以直接注入IOC容器的对象，不需要任何注解
     * @param ossProps
     * @return
     */
    @Bean
    public OSS createOss(OssProperties ossProps){
        return new OSSClientBuilder().build(
                ossProps.getEndpoint(),
                ossProps.getAccessKeyId(),
                ossProps.getAccessKeySecret());
    }

}
