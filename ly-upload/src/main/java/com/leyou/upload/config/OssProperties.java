package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取Oss相关配置
 *  让@ConfigurationProperties注解生效两种方法：
 *     1）在启动类使用@EnableConfigurationProperties(OssProperties.class)开启配置读取
 *     2）在当前类上直接使用@Component注解
 */
@Data
@Component
@ConfigurationProperties(prefix = "ly.oss")
public class OssProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String host;
    private String endpoint;
    private String dir;
    private Long expireTime;
    private Long maxFileSize;
}

