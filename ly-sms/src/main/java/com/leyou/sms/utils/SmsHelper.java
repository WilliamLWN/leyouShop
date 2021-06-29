package com.leyou.sms.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 短信工具类
 */
@Component
@Slf4j
public class SmsHelper {
    @Autowired
    private Client client;
    @Autowired
    private SmsProperties smsProps;
    /**
     * 发送短信
     */
    public void sendVerifySms(String phone,String code){
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(smsProps.getSignName())
                .setTemplateCode(smsProps.getVerifyCodeTemplate())
                .setTemplateParam("{\""+smsProps.getCode()+"\":\""+code+"\"}");
        try {
            SendSmsResponse sendResp = client.sendSms(sendSmsRequest);
            //取出响应的Code
            String respCode = sendResp.body.code;
            String message = sendResp.body.message;
            if(respCode.equals("OK")){
                log.info("【短信发送】短信发送成功");
            }else{
                log.error("【短信发送】短信发送失败，原因："+message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【短信发送】短信发送失败，原因："+e.getMessage());
        }
    }
}

