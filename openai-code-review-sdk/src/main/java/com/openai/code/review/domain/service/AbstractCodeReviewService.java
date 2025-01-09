package com.openai.code.review.domain.service;

import com.openai.code.review.infrastructure.git.GitCommand;
import com.openai.code.review.infrastructure.llm.ILargeLanguageModel;

import java.io.IOException;

public abstract class AbstractCodeReviewService implements ICodeReviewService {

    protected GitCommand gitCommand;
    protected ILargeLanguageModel llm;


    public AbstractCodeReviewService(GitCommand gitCommand, ILargeLanguageModel llm) {
        this.gitCommand = gitCommand;
        this.llm = llm;
    }

    /**
     * 执行代码评审
     */
    @Override
    public void exec() throws IOException {
        String diffCode = this.getDiffCode();
        String reviewResult = this.codeReview(diffCode);
        String logUrl = this.writeLog(reviewResult);
        this.pushNotification(logUrl);
    }


    /**
     * 获取最近2次提交不同的代码
     *
     * @return
     */
    protected abstract String getDiffCode();

    /**
     * 根据不同的代码进行代码评审
     *
     * @param diffCode
     * @return
     */
    protected abstract String codeReview(String diffCode);


    /**
     * 评审结果写入日志
     *
     * @param reviewResult
     * @return
     */
    protected abstract String writeLog(String reviewResult);


    /**
     * 推送通知
     *
     * @param logUrl
     */
    protected abstract void pushNotification(String logUrl);


}
