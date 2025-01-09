package com.openai.code.review.infrastructure.notification.weixin;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.infrastructure.notification.weixin.dto.TemplateMessageDTO;
import com.openai.code.review.utils.DefaultHttpUtils;
import com.openai.code.review.utils.WXAccessTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众平台交互
 */
public class WeiXin {
    private final Logger logger = LoggerFactory.getLogger(WeiXin.class);

    private String appid;
    private String secret;
    private String touser;
    private String template_id;

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
        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s", accessToken.getAccess_token());
        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "application/json; utf-8");
        params.put("Accept", "application/json");
        return DefaultHttpUtils.executePostRequest(url, params, JSON.toJSONString(request));
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
