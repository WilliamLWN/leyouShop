package com.leyou.sms.mq;

import com.leyou.common.constants.MQConstants;
import com.leyou.common.pojo.SmsData;
import com.leyou.sms.utils.SmsHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 接收短信监听器
 *   topic: 消息主题
 *   selectorExpression：消息标签
 *   consumerGroup： 消费组
 *   messageModel: 消息模式
 *       集群 : MessageModel.CLUSTERING
 *       广播 ：MessageModel.BROADCASTING
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQConstants.Topic.SMS_TOPIC_NAME,
        consumerGroup = "SmsConsumerGroup",
        messageModel = MessageModel.CLUSTERING
)
public class SmsListener implements RocketMQListener<SmsData> {

    @Autowired
    private SmsHelper smsHelper;

    @Override
    public void onMessage(SmsData smsData) { //商品ID
        smsHelper.sendVerifySms(smsData.getPhone(),smsData.getCode());
    }
}
