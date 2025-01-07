package com.openai.code.review.utils;

/**
 * 获取环境变量的工具类
 */
public class EnvUtils {
    public static String getEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            System.out.println(key + " is empty，请正确github配置variables或者secrets");
            return null;
        }
        return value;
    }
}
