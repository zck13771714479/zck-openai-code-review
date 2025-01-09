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


    public FeiShu(String url, String webhook) {
        this.url = url;
        this.webhook = webhook;
    }

    /**
     * 发送飞书消息
     * @param requestDTO
     * @return
     */
    public String pushFeiShuMessage(TextMessageRequestDTO requestDTO){
        String requestUrl = this.url + "/" + this.webhook;
        System.out.println(requestUrl);
        System.out.println(JSON.toJSONString(requestDTO));
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
