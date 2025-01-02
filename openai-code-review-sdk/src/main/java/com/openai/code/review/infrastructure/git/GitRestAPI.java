package com.openai.code.review.infrastructure.git;

import com.alibaba.fastjson2.JSON;
import com.openai.code.review.infrastructure.git.dto.SingleCommitResponseDTO;
import com.openai.code.review.utils.DefaultHTTPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GitRestAPI implements BaseGitOperation {

    private final Logger logger = LoggerFactory.getLogger(GitRestAPI.class);

    private String reviewLogRepoURI;
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
     * 要评审的代码的仓库的url
     */
    private String codeRepoUrl;

    public GitRestAPI(String reviewLogRepoURI, String project, String branch, String author, String commitMessage, String githubToken, String codeRepoUrl) {
        this.reviewLogRepoURI = reviewLogRepoURI;
        this.project = project;
        this.branch = branch;
        this.author = author;
        this.commitMessage = commitMessage;
        this.githubToken = githubToken;
        this.codeRepoUrl = codeRepoUrl;
    }

    /**
     * 使用github commit api获取2次提交的不同代码
     *
     * @return
     * @throws IOException
     */
    @Override
    public String getDiffCode() throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Accept", "application/vnd.github+json");
        params.put("Authorization", "Bearer " + this.githubToken);
        params.put("X-GitHub-Api-Version", "2022-11-28");
        String responseJson = DefaultHTTPUtils.executeGetRequest(this.codeRepoUrl, params);
        SingleCommitResponseDTO singleCommitResponseDTO = JSON.parseObject(responseJson, SingleCommitResponseDTO.class);
        StringBuilder stringBuilder = new StringBuilder();
        SingleCommitResponseDTO.CommitFile[] files = singleCommitResponseDTO.getFiles();
        for (SingleCommitResponseDTO.CommitFile file : files) {
            stringBuilder.append("待评审的文件名称: " + file.getFilename() + "\n");
            stringBuilder.append("文件的内容: " + file.getPatch());
        }
        return stringBuilder.toString();
    }

    public String getReviewLogRepoURI() {
        return reviewLogRepoURI;
    }

    public void setReviewLogRepoURI(String reviewLogRepoURI) {
        this.reviewLogRepoURI = reviewLogRepoURI;
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
}
