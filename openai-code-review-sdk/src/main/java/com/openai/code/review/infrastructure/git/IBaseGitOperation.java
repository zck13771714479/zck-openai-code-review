package com.openai.code.review.infrastructure.git;

/**
 * git基础操作接口
 */
public interface IBaseGitOperation {

    /**
     * 评审结果写入日志
     * @param reviewResult
     * @return
     */
    String writeResult(String reviewResult);
}
