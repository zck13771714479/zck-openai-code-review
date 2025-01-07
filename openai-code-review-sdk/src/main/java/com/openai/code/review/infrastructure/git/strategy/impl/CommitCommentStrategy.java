package com.openai.code.review.infrastructure.git.strategy.impl;

import com.openai.code.review.infrastructure.git.GitRestAPI;
import com.openai.code.review.infrastructure.git.IBaseGitOperation;
import com.openai.code.review.infrastructure.git.strategy.IWriteResultStrategy;
import com.openai.code.review.utils.EnvUtils;

public class CommitCommentStrategy implements IWriteResultStrategy {

    private IBaseGitOperation baseGitOperation;

    /**
     * 显示写入策略的名字
     *
     * @return
     */
    @Override
    public String typeName() {
        return "commitComment";
    }

    @Override
    public void initialData() {
        this.baseGitOperation = new GitRestAPI(
                EnvUtils.getEnv("PROJECT"),
                EnvUtils.getEnv("BRANCH"),
                EnvUtils.getEnv("AUTHOR"),
                EnvUtils.getEnv("COMMIT_MESSAGE"),
                EnvUtils.getEnv("GITHUB_TOKEN"),
                EnvUtils.getEnv("WRITE_TYPE"),
                EnvUtils.getEnv("COMMIT_SHA"),
                EnvUtils.getEnv("OWNER")
        );
    }

    /**
     * 执行写入策略
     *
     * @param reviewResult
     * @return
     */
    @Override
    public String writeResult(String reviewResult) {
        GitRestAPI gitRestAPI = (GitRestAPI) baseGitOperation;
        return gitRestAPI.writeResult(reviewResult);
    }
}
