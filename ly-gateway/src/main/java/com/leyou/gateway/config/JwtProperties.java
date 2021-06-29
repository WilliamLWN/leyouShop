package com.leyou.gateway.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@Component
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
    private String pubKeyPath;

    private PublicKey publicKey;

    //注意：构造方法在这里不能读取到配置文件信息
    @PostConstruct // 声明为初始化方法 init-method
    public void initMethod() throws Exception {
        publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }

    //接收cookie的数据
    private CookiePojo cookie = new CookiePojo();

    @Data
    public class CookiePojo{
        private String cookieName;
    }
}
