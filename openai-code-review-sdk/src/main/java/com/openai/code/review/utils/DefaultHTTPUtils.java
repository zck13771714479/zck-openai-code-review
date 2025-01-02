package com.openai.code.review.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 发送HTTP请求的工具类
 */
public class DefaultHTTPUtils {

    /**
     * GET请求封装类
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String executeGetRequest(String url, Map<String, String> params) throws IOException {
        URL getUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        //设置请求头，参数
        params.forEach(connection::setRequestProperty);
        connection.connect();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return response.toString();
    }


}
