package com.openai.code.review.infrastructure.notification.strategy.impl;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.infrastructure.notification.feishu.FeiShu;
import com.openai.code.review.infrastructure.notification.feishu.dto.TextMessageRequestDTO;
import com.openai.code.review.infrastructure.notification.strategy.INotificationStrategy;
import com.openai.code.review.utils.EnvUtils;

public class FeiShuNotificationStrategy implements INotificationStrategy {
    private FeiShu feiShu;

    /**
     * @return 消息通知的类型，微信公众号，飞书等等
     */
    @Override
    public String typeName() {
        return "feishu";
    }

    /**
     * 创建通知消息实例对象
     */
    @Override
    public void initData() {
        this.feiShu = new FeiShu(EnvUtils.getEnv("FEISHU_URL"), EnvUtils.getEnv("FEISHU_WEBHOOK"));
    }

    /**
     * 发送飞书消息
     *
     * @param logUrl
     * @return
     */
    @Override
    public String sendNotificationMessage(String logUrl) {
        String message = this.buildMessage(logUrl);
        TextMessageRequestDTO textMessageRequestDTO = new TextMessageRequestDTO();
        textMessageRequestDTO.setMsgType("text");
        textMessageRequestDTO.setContent(new TextMessageRequestDTO.Content(message));
        System.out.println("方法 sendNotificationMessage：" + JSON.toJSONString(textMessageRequestDTO));
        return this.feiShu.pushFeiShuMessage(textMessageRequestDTO);
    }

    /**
     * 构建飞书消息
     * @param logUrl
     * @return
     */
    private String buildMessage(String logUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("工程项目：" + EnvUtils.getEnv("PROJECT") + "\n");
        sb.append("分支：" + EnvUtils.getEnv("BRANCH") + "\n");
        sb.append("作者：" + EnvUtils.getEnv("AUTHOR") + "\n");
        sb.append("提交信息：" + EnvUtils.getEnv("COMMIT_MESSAGE") + "\n");
        sb.append("评审结果：" + logUrl + "\n");
        return sb.toString();
    }
}
