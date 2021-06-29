package com.leyou.common.constants;

/**
 * @author 黑马程序员
 */
public abstract class MQConstants {

    public static final class Topic {
        /**
         * 商品服务Topic名称
         */
        public static final String ITEM_TOPIC_NAME = "ly-item-topic";
        /**
         * 短信服务Topic名称
         */
        public static final String SMS_TOPIC_NAME = "ly-sms-topic";
    }

    public static final class Tag {
        /**
         * 商品上架的Tag
         */
        public static final String ITEM_UP_TAG = "item-up";
        /**
         * 商品下架的Tag
         */
        public static final String ITEM_DOWN_TAG = "item-down";
    }

}