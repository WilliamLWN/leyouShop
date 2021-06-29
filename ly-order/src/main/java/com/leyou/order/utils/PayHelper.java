package com.leyou.order.utils;

import com.github.wxpay.sdk.WXPay;
import com.leyou.common.exception.pojo.ExceptionEnum;
import com.leyou.common.exception.pojo.LyException;
import com.leyou.order.config.PayConfiguration;
import com.leyou.order.config.PayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付工具类
 */
@Component
@Slf4j
public class PayHelper {

    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayProperties payProps;

    /**
     * 获取支付链接
     */
    public String getPayUrl(Long orderId,Long totalFee){
        // 请求参数：
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "乐优商城-订单支付");//描述
        data.put("out_trade_no", orderId.toString());//订单号
        data.put("total_fee", totalFee.toString());//支付金额，分
        data.put("spbill_create_ip", "123.12.12.123");
        data.put("notify_url", payProps.getNotifyUrl());//支付成功后，回调地址
        data.put("trade_type", payProps.getPayType());  // 此处指定为扫码支付

        try {
            Map<String, String> resp = wxPay.unifiedOrder(data);

            if(resp.get("return_code").equals("SUCCESS")
                    && resp.get("result_code").equals("SUCCESS")){
                log.info("【支付链接生成】链接生成成功");
                //返回支付链接
                return resp.get("code_url");
            }else{
                log.error("【支付链接生成】链接生成成功");
                throw new LyException(500,"支付链接生成失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(500,e.getMessage());
        }
    }


}


