package com.openai.code.review.infrastructure.git.strategy;

import com.openai.code.review.infrastructure.git.IBaseGitOperation;

/**
 * 评审结果写入日志策略
 */
public interface IWriteResultStrategy {
    /**
     * 显示写入策略的名字
     * @return
     */
    String typeName();

    void initialData();

    /**
     * 执行写入策略
     * @param reviewResult
     * @return
     */
    String writeResult(String reviewResult);
}
