package com.leyou.cart.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * 读取Jwt相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {

    private String pubKeyPath;//公钥路径

    private PublicKey publicKey;//公钥

    private CookiePojo cookie = new CookiePojo();

    @Data
    public class CookiePojo{
        private String cookieName;
    }


    /**
     * 读取公钥
     */
    @PostConstruct
    public void initMethod() throws Exception {
        publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
