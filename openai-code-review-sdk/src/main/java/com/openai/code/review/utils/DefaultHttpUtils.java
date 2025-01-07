package com.openai.code.review.utils;

import com.alibaba.fastjson2.JSON;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * http请求工具类
 */
public class DefaultHttpUtils {

    /**
     * 发送get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String executeGetRequest(String url, Map<String, String> params) {
        try {
            URL requestURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            params.forEach(connection::setRequestProperty);
            connection.connect();
            return getResponseBody(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param body
     * @return
     */
    public static String executePostRequest(String url, Map<String, String> params, String body) {
        try {
            URL requestURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            params.forEach(connection::setRequestProperty);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(body);
            writer.flush();
            writer.close();
            connection.connect();
            return getResponseBody(connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取响应结果
     * @param connection
     * @return
     */
    private static String getResponseBody(HttpURLConnection connection) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
