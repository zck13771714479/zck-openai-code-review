package com.openai.code.review.infrastructure.llm.impl;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.infrastructure.llm.ILargeLanguageModel;
import com.openai.code.review.infrastructure.llm.dto.ChatCompletionRequest;
import com.openai.code.review.infrastructure.llm.dto.ChatCompletionSyncResponse;
import com.openai.code.review.infrastructure.weixin.WeiXin;
import com.openai.code.review.utils.DefaultHttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * ChatGLM交互，发送请求
 */
public class ChatGLM implements ILargeLanguageModel {

    private final Logger logger = LoggerFactory.getLogger(WeiXin.class);

    private String API_URL;
    private String API_KEY = "";

    public ChatGLM(String API_URL, String API_KEY) {
        this.API_URL = API_URL;
        this.API_KEY = API_KEY;
    }


    /**
     * 向LLM发送请求
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public String completion(ChatCompletionRequest request) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/json");
        params.put("Authorization", "Bearer " + this.API_KEY);
        params.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //写入body参数
        String body = JSON.toJSONString(request);
        //获取响应
        String response = DefaultHttpUtils.executePostRequest(this.API_URL, params, body);
        ChatCompletionSyncResponse chatCompletionSyncResponse = JSON.parseObject(response, ChatCompletionSyncResponse.class);
        return chatCompletionSyncResponse.getChoices().get(0).getMessage().getContent();
    }
}
