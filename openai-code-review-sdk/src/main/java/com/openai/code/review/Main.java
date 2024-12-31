package com.openai.code.review;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.domain.constant.Constant;
import com.openai.code.review.domain.model.ChatCompletionRequest;
import com.openai.code.review.domain.model.ChatCompletionSyncResponse;
import com.openai.code.review.domain.model.Model;
import com.openai.code.review.domain.model.WxTemplateMessageRequest;
import com.openai.code.review.domain.utils.WXAccessTokenUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, GitAPIException {
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
        System.out.println("评审结果：" + reviewResult);
        //3. 评审结果写入日志
        String token = System.getenv("GITHUB_TOKEN");
        if (token == null || token.isEmpty()) {
            System.out.println("Github token is empty");
            return;
        }
        String logUrl = writeReviewResultLogs(token, reviewResult);
        //4. 评审结果，微信通知
        pushWxNotification(logUrl);

    }

    /**
     * 评审结果推送微信通知
     * @param logUrl
     * @return
     * @throws IOException
     */
    public static String pushWxNotification(String logUrl) throws IOException {
        //获取access token
        WXAccessTokenUtil.AccessToken accessToken = WXAccessTokenUtil.getAccessToken();
        System.out.println("AccessToken: " + accessToken);
        if (accessToken == null) {
            return null;
        }
        //发送微信模板消息通知请求
        WxTemplateMessageRequest messageRequest = new WxTemplateMessageRequest();
        messageRequest.setUrl(logUrl);
        messageRequest.put("project", "openai-code-review");
        messageRequest.put("review", logUrl);
        URL url = new URL(String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken.getAccess_token()));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bufferedWriter.write(JSON.toJSONString(messageRequest));
        bufferedWriter.flush();
        connection.connect();
        int responseCode = connection.getResponseCode();
        StringBuilder builder = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            System.out.println("推送响应结果：" + builder.toString());
        }
        return builder.toString();
    }

    /**
     * chatglm代码评审方法
     *
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
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + Constant.API_KEY);
        // 设置模型，提示词
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>() {
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

    /**
     * 代码评审结果写入日志仓库
     *
     * @param token        github token， 生成token的文档在https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens
     * @param reviewResult AI代码评审的结果
     * @return
     * @throws GitAPIException
     */
    public static String writeReviewResultLogs(String token, String reviewResult) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setURI(Constant.LOG_REPOSITORY_URI)
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .call();
        String folderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File folder = new File("repo/" + folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String fileName = generateRandomName(12) + ".md";
        File logFile = new File(folder, fileName);
        try (FileWriter writer = new FileWriter(logFile)) {
            writer.write(reviewResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        git.add().addFilepattern(folderName + "/" + fileName).call();
        git.commit().setMessage("Code Review Log").call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();
        return "https://github.com/zck13771714479/zck-openai-code-review-logs/blob/main/" + folderName + "/" + fileName;
    }

    /**
     * 生产随机字符串
     *
     * @return
     */
    private static String generateRandomName(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}