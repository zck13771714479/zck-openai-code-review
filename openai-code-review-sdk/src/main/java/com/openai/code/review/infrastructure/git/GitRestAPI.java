package com.openai.code.review.infrastructure.git;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.infrastructure.git.dto.CommitCommentRequestDTO;
import com.openai.code.review.infrastructure.git.dto.CommitCommentResponseDTO;
import com.openai.code.review.utils.DefaultHttpUtils;
import com.openai.code.review.utils.EnvUtils;

import java.util.HashMap;
import java.util.Map;

public class GitRestAPI implements IBaseGitOperation {
    /**
     * 项目名称
     */
    private String project;
    /**
     * 分支名称
     */
    private String branch;
    /**
     * 作者名称
     */
    private String author;
    /**
     * 提交时的消息
     */
    private String commitMessage;
    /**
     * github https的token 文档https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens
     */
    private String githubToken;

    /**
     * 写入日志的方式
     */
    private String writeType;

    /**
     * 提交的sha码
     */
    private String commitSHA;

    /**
     * 仓库拥有者
     */
    private String owner;

    public GitRestAPI(String project, String branch, String author, String commitMessage, String githubToken, String writeType, String commitSHA, String owner) {
        this.project = project;
        this.branch = branch;
        this.author = author;
        this.commitMessage = commitMessage;
        this.githubToken = githubToken;
        this.writeType = writeType;
        this.commitSHA = commitSHA;
        this.owner = owner;
    }

    /**
     * 写入评审结果到对应的commit comment
     *
     * @param reviewResult
     * @return
     */
    @Override
    public String writeResult(String reviewResult) {
        // github comment url: https://api.github.com/repos/{owner}/{repo}/commits/{commit_sha}/comments
        String url = EnvUtils.getEnv("GITHUB_API_URL") + "/repos/" + this.owner + "/"
                + this.project + "/commits/" + this.commitSHA + "/comments";
        Map<String, String> params = new HashMap<String, String>();
        params.put("Accept", "application/vnd.github+json");
        params.put("Content-Type", "application/json");
        params.put("Authorization", "Bearer " + this.githubToken);
        params.put("X-GitHub-Api-Version", "2022-11-28");
        CommitCommentRequestDTO requestDTO = new CommitCommentRequestDTO();
        requestDTO.setBody(reviewResult);
        String body = JSON.toJSONString(requestDTO);
        String response = DefaultHttpUtils.executePostRequest(url, params, body);
        CommitCommentResponseDTO commitCommentResponseDTO = JSON.parseObject(response, CommitCommentResponseDTO.class);
        return commitCommentResponseDTO.getHtmlUrl();
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String getGithubToken() {
        return githubToken;
    }

    public void setGithubToken(String githubToken) {
        this.githubToken = githubToken;
    }

    public String getWriteType() {
        return writeType;
    }

    public void setWriteType(String writeType) {
        this.writeType = writeType;
    }

    public String getCommitSHA() {
        return commitSHA;
    }

    public void setCommitSHA(String commitSHA) {
        this.commitSHA = commitSHA;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
