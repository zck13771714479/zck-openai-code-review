package com.openai.code.review.domain.service;

import java.io.IOException;

/**
 * 代码评审服务接口
 */
public interface ICodeReviewService {

    /**
     * 执行代码评审
     */
    void exec() throws IOException;

}
