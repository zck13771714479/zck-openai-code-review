package com.openai.code.review.infrastructure.notification.feishu.dto;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 发送文本消息请求DTO
 */
public class TextMessageRequestDTO {


    @JSONField(name = "msg_type")
    private String msgType = "text";

    private Content content;


    public TextMessageRequestDTO() {
    }

    public TextMessageRequestDTO(String msgType, Content content) {
        this.msgType = msgType;
        this.content = content;
    }



    public static class Content{
        private String text;

        public Content(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }


    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
