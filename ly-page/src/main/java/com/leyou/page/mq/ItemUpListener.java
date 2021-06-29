package com.leyou.page.mq;

import com.leyou.common.constants.MQConstants;
import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 上架-生成静态页
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
        topic = MQConstants.Topic.ITEM_TOPIC_NAME,
        consumerGroup = "itemUpConsumerGroup",
        selectorExpression = MQConstants.Tag.ITEM_UP_TAG,
        messageModel = MessageModel.BROADCASTING
)
public class ItemUpListener implements RocketMQListener<Long> {

    @Autowired
    private PageService pageService;

    @Override
    public void onMessage(Long spuId) { //商品ID
        try {
            pageService.createStaticPage(spuId);
            log.info("【静态页同步】静态页创建成功，ID="+spuId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【静态页同步】静态页创建失败，ID="+spuId);
        }

    }
}
