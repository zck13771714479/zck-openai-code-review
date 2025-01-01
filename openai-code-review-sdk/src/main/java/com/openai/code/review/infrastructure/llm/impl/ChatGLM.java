package com.openai.code.review.infrastructure.llm.impl;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.infrastructure.llm.ILargeLanguageModel;
import com.openai.code.review.infrastructure.llm.dto.ChatCompletionRequest;
import com.openai.code.review.infrastructure.llm.dto.ChatCompletionSyncResponse;
import com.openai.code.review.infrastructure.weixin.WeiXin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ChatGLM交互，发送请求
 */
public class ChatGLM implements ILargeLanguageModel {

    private final Logger logger = LoggerFactory.getLogger(WeiXin.class);

    private String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
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
        URL url = new URL(this.API_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + this.API_KEY);
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //写入body参数
        String body = JSON.toJSONString(request);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
        writer.write(body);
        writer.flush();
        writer.close();
        //发送请求
        httpURLConnection.connect();
        //获取响应
        int responseCode = httpURLConnection.getResponseCode();
        logger.info("Response code: " + responseCode);
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        bufferedReader.close();
        ChatCompletionSyncResponse chatCompletionSyncResponse = JSON.parseObject(builder.toString(), ChatCompletionSyncResponse.class);
        return chatCompletionSyncResponse.getChoices().get(0).getMessage().getContent();
    }
}
