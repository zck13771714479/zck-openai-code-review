package com.openai.code.review.infrastructure.notification.feishu;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.infrastructure.notification.feishu.dto.TextMessageRequestDTO;
import com.openai.code.review.utils.DefaultHttpUtils;

import java.util.HashMap;

/**
 * 飞书操作，发送飞书机器人消息
 */
public class FeiShu {
    /**
     * 飞书机器人webhook
     */
    private String webhook;
    /**
     * 发送请求的url
     */
    private String url;


    public FeiShu(String webhook, String url) {
        this.webhook = webhook;
        this.url = url;
    }

    /**
     * 发送飞书消息
     * @param requestDTO
     * @return
     */
    public String pushFeiShuMessage(TextMessageRequestDTO requestDTO){
        String requestUrl = this.url + "/" + this.webhook;
        return DefaultHttpUtils.executePostRequest(requestUrl,new HashMap<String,String>(), JSON.toJSONString(requestDTO));
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
