package com.openai.code.review.infrastructure.weixin;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.domain.model.WxTemplateMessageRequest;
import com.openai.code.review.domain.service.CodeReviewService;
import com.openai.code.review.infrastructure.weixin.dto.TemplateMessageDTO;
import com.openai.code.review.utils.WXAccessTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 微信公众平台交互
 */
public class WeiXin {
    private final Logger logger = LoggerFactory.getLogger(WeiXin.class);

    private String appid = "wx83124e4c316e9f4e";
    private String secret = "5d403559eae7c593a6ece6c19f92c6d8";
    private String touser = "ouCDp626vpOiobgSUL8eAoGk0SSI";
    private String template_id = "PZEEfW9IbIqwIqlSYjADHjUK7xHwGMS7Y_XTOy7U7VQ";

    public WeiXin(String appid, String secret, String touser, String template_id) {
        this.appid = appid;
        this.secret = secret;
        this.touser = touser;
        this.template_id = template_id;
    }



    /**
     * 推送微信模板消息通知
     *
     * @param request
     * @return
     * @throws IOException
     */
    public String pushTemplateMessage(TemplateMessageDTO request) throws IOException {
        //获取access token
        WXAccessTokenUtil.AccessToken accessToken = WXAccessTokenUtil.getAccessToken(this.appid, this.secret);
        logger.info("AccessToken: " + accessToken);
        if (accessToken == null) {
            return null;
        }
        URL url = new URL(String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken.getAccess_token()));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        bufferedWriter.write(JSON.toJSONString(request));
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
            logger.info("推送通知响应结果：" + builder.toString());
        }
        return builder.toString();
    }


    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }
}
