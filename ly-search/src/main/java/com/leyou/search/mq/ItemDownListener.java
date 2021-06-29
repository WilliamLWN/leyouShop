package com.leyou.search.mq;

import com.leyou.common.constants.MQConstants;
import com.leyou.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 下架-删除索引库
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
        consumerGroup = "itemDownConsumerGroup",
        selectorExpression = MQConstants.Tag.ITEM_DOWN_TAG,
        messageModel = MessageModel.BROADCASTING
)
public class ItemDownListener implements RocketMQListener<Long> {

    @Autowired
    private SearchService searchService;

    @Override
    public void onMessage(Long spuId) { //商品ID
        try {
            searchService.deleteIndex(spuId);
            log.info("【索引库同步】索引删除成功，ID="+spuId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【索引库同步】索引删除失败，ID="+spuId);
        }

    }
}
