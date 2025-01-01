package com.openai.code.review.infrastructure.weixin.dto;

import java.util.HashMap;
import java.util.Map;

public class TemplateMessageDTO {
    private String touser = "ouCDp626vpOiobgSUL8eAoGk0SSI";
    private String template_id = "PZEEfW9IbIqwIqlSYjADHjUK7xHwGMS7Y_XTOy7U7VQ";
    private String url = "https://weixin.qq.com";
    private Map<String, Map<String, String>> data = new HashMap<>();

    public TemplateMessageDTO(String touser, String template_id) {
        this.touser = touser;
        this.template_id = template_id;
    }

    public void put(String key, String value) {
        data.put(key, new HashMap<String, String>() {
            {
                put("value", value);
            }
        });
    }

    public static enum TemplateKey {
        PROJECT("project", "工程项目"),
        BRANCH("branch", "分支"),
        AUTHOR("author", "作者"),
        COMMIT_MESSAGE("commitMessage", "提交信息"),
        REVIEW("review", "评审结果"),
        ;

        private String code;
        private String info;

        TemplateKey(String code, String info) {
            this.code = code;
            this.info = info;
        }

        public String getCode() {
            return code;
        }

        public String getInfo() {
            return info;
        }
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, String>> data) {
        this.data = data;
    }
}
