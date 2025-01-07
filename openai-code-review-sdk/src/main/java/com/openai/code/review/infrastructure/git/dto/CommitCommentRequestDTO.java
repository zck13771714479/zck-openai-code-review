package com.openai.code.review.infrastructure.git.dto;

//参考 https://docs.github.com/zh/rest/commits/comments?apiVersion=2022-11-28#create-a-commit-comment
public class CommitCommentRequestDTO {
    private String body;
    private String path;
    private Integer line;
    private Integer position;

    public CommitCommentRequestDTO() {
    }

    public CommitCommentRequestDTO(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
