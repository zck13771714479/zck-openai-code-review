package com.openai.code.review.infrastructure.notification.feishu;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.infrastructure.notification.feishu.dto.TextMessageRequestDTO;
import com.openai.code.review.utils.DefaultHttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 飞书操作，发送飞书机器人消息
 */
public class FeiShu {
    /**
     * 飞书机器人webhook
     */
    private String webhook;



    public FeiShu(String webhook) {
        this.webhook = webhook;
    }

    /**
     * 发送飞书消息
     *
     * @param requestDTO
     * @return
     */
    public String pushFeiShuMessage(TextMessageRequestDTO requestDTO) {
        System.out.println(JSON.toJSONString(requestDTO));
        Map<String,String> params = new HashMap<>();
        params.put("Content-Type","application/json");
        String response = DefaultHttpUtils.executePostRequest(this.webhook, params, JSON.toJSONString(requestDTO));
        System.out.println("response: " + response);
        return response;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

}
