package com.openai.code.review.infrastructure.git;

import java.io.IOException;

public interface BaseGitOperation {
    /**
     * 获取2次提交的不同代码
     *
     * @return
     * @throws IOException
     */
    String getDiffCode() throws IOException;
}
