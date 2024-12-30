package com.openai.code.review;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.domain.constant.Constant;
import com.openai.code.review.domain.model.ChatCompletionRequest;
import com.openai.code.review.domain.model.ChatCompletionSyncResponse;
import com.openai.code.review.domain.model.Model;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("开始代码评审");
        //1. 获取2次提交的不同 git diff
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));
        Process process = processBuilder.start();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder diffCode = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            diffCode.append(line);
        }
        int exitValue = process.waitFor();
        System.out.println("Exit value: " + exitValue);
        System.out.println("Git diff " + diffCode.toString());
        //2. AI代码自动评审
        String reviewResult = codeReview(diffCode.toString());
        System.out.println(reviewResult);
    }

    /**
     * chatglm代码评审方法
     * @param diffCode
     * @throws IOException
     */
    public static String codeReview(String diffCode) throws IOException {
        // 创建请求，配置header
        URL url = new URL(Constant.API_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Authorization","Bearer " + Constant.API_KEY);
        // 设置模型，提示词
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>(){
            private static final long serialVersionUID = 545664644649799L;
            {
                add(new ChatCompletionRequest.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
                add(new ChatCompletionRequest.Prompt("user", diffCode));
            }
        });
        //写入body参数
        String body = JSON.toJSONString(chatCompletionRequest);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
        writer.write(body);
        writer.flush();
        writer.close();
        httpURLConnection.connect();
        int responseCode = httpURLConnection.getResponseCode();
        System.out.println("Response code: " + responseCode);
        //获取响应
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