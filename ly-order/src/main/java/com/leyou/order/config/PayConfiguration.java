package com.leyou.order.config;

import com.github.wxpay.sdk.PayConfig;
import com.github.wxpay.sdk.WXPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化微信支付需要的对象
 */
@Configuration
public class PayConfiguration {
    @Autowired
    private PayProperties payProps;

    @Bean
    public WXPay wxPay() throws Exception {
        PayConfig payConfig = new PayConfig();
        payConfig.setAppID(payProps.getAppId());
        payConfig.setMchID(payProps.getMchId());
        payConfig.setKey(payProps.getKey());
        return new WXPay(payConfig);
    }

}

