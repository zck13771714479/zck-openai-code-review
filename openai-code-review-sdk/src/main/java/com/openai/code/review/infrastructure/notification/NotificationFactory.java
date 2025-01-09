package com.openai.code.review.infrastructure.notification;

import com.openai.code.review.infrastructure.notification.strategy.INotificationStrategy;
import com.openai.code.review.infrastructure.notification.strategy.impl.FeiShuNotificationStrategy;
import com.openai.code.review.infrastructure.notification.strategy.impl.WeiXinNotificationStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 制造通知策略的工厂类
 */
public class NotificationFactory {
    private static final Map<String, INotificationStrategy> strategyMap = new HashMap<>();

    static {
        strategyMap.put(new WeiXinNotificationStrategy().typeName(), new WeiXinNotificationStrategy());
        strategyMap.put(new FeiShuNotificationStrategy().typeName(), new FeiShuNotificationStrategy());
    }

    public static INotificationStrategy getStrategy(String notificationType) {
        return strategyMap.get(notificationType);
    }
}
