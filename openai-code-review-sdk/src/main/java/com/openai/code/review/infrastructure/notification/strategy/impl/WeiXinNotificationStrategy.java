package com.openai.code.review.infrastructure.notification.strategy.impl;

import com.openai.code.review.infrastructure.notification.strategy.INotificationStrategy;
import com.openai.code.review.infrastructure.notification.weixin.WeiXin;
import com.openai.code.review.infrastructure.notification.weixin.dto.TemplateMessageDTO;
import com.openai.code.review.utils.EnvUtils;

import java.io.IOException;

public class WeiXinNotificationStrategy implements INotificationStrategy {
    private WeiXin weiXin;

    /**
     * @return 消息通知的类型，微信公众号，飞书等等
     */
    @Override
    public String typeName() {
        return "weixin";
    }

    /**
     * 创建通知消息实例对象
     */
    @Override
    public void initData() {
        this.weiXin = new WeiXin(
                EnvUtils.getEnv("APPID"),
                EnvUtils.getEnv("SECRET"),
                EnvUtils.getEnv("TOUSER"),
                EnvUtils.getEnv("TEMPLATE_ID")
        );
    }

    /**
     * 发送微信公众号消息
     *
     * @param logUrl
     * @return
     */
    @Override
    public String sendNotificationMessage(String logUrl) {
        try {
            TemplateMessageDTO templateMessageDTO = new TemplateMessageDTO(weiXin.getTouser(), weiXin.getTemplate_id());
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.PROJECT.getCode(), EnvUtils.getEnv("PROJECT"));
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.BRANCH.getCode(), EnvUtils.getEnv("BRANCH"));
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.AUTHOR.getCode(), EnvUtils.getEnv("AUTHOR"));
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.COMMIT_MESSAGE.getCode(), EnvUtils.getEnv("COMMIT_MESSAGE"));
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.REVIEW.getCode(), logUrl);
            templateMessageDTO.setUrl(logUrl);
            return weiXin.pushTemplateMessage(templateMessageDTO);
        } catch (IOException e) {
            System.out.println("推送微信消息失败");
            throw new RuntimeException(e);
        }
    }
}
