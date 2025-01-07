package com.openai.code.review.infrastructure.git.dto;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * 写入commit comment的响应dto
 */
public class CommitCommentResponseDTO {
    /**
     * commit comment所在的url
     */
    @JSONField(name = "html_url")
    private String htmlUrl;
    private Long id;


    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
