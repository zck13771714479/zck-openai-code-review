package com.openai.code.review.utils;

import com.alibaba.fastjson2.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 获取微信access token的工具类
 * 获取access token的文档https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Get_access_token.html
 */
public class WXAccessTokenUtil {
    //常量为默认的，需要使用时通过外部调用参数传递，或者可以修改为自己的


    //appid和appsecret的获取https://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index
    private static final String CLIENT_CREDENTIAL = "client_credential";
    private static final String APPID = "wx83124e4c316e9f4e";
    private static final String SECRET = "5d403559eae7c593a6ece6c19f92c6d8";
    //发送获取access token的请求的模板字符串url
    private static final String URL_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/token?grant_type=%s&appid=%s&secret=%s";


    public static AccessToken getAccessToken() throws IOException {
        return getAccessToken(APPID, SECRET);
    }

    /**
     * 获取微信access token
     *
     * @return
     * @throws IOException
     */
    public static AccessToken getAccessToken(String appid, String secret) throws IOException {
        URL url = new URL(String.format(URL_TEMPLATE, CLIENT_CREDENTIAL, appid, secret));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoInput(true);
        connection.connect();

        int responseCode = connection.getResponseCode();
        System.out.println("Get WX access token responseCode:" + responseCode);
        StringBuilder result = new StringBuilder();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            return JSON.parseObject(result.toString(), AccessToken.class);
        }
        System.out.println("access token fail");
        return null;
    }

    public static class AccessToken {
        private String access_token;
        private Integer expires_in;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public Integer getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(Integer expires_in) {
            this.expires_in = expires_in;
        }
    }
}
