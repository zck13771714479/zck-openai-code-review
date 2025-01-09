package com.openai.code.review.infrastructure.notification;

import com.openai.code.review.infrastructure.notification.strategy.INotificationStrategy;
import com.openai.code.review.infrastructure.notification.strategy.impl.WeiXinNotificationStrategy;

import java.util.Map;

/**
 * 制造通知策略的工厂类
 */
public class NotificationFactory {
    private static Map<String, INotificationStrategy> strategyMap;

    static {
        strategyMap.put(new WeiXinNotificationStrategy().typeName(), new WeiXinNotificationStrategy());
    }

    public static INotificationStrategy getStrategy(String notificationType) {
        return strategyMap.get(notificationType);
    }
}
