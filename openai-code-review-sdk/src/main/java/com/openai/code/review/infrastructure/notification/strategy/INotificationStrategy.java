package com.openai.code.review.infrastructure.notification.strategy;

import java.util.Map;

/**
 * 发送评审结果消息通知的接口
 */
public interface INotificationStrategy {

    /**
     * @return 消息通知的类型，微信公众号，飞书等等
     */
    String typeName();

    /**
     * 创建通知消息实例对象
     */
    void initData();

    /**
     * 发送消息
     * @param logUrl
     *
     * @return
     */
    String sendNotificationMessage(String logUrl);
}
