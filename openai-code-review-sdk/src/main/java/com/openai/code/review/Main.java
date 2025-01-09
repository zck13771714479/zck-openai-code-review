package com.openai.code.review;

import com.openai.code.review.domain.service.CodeReviewService;
import com.openai.code.review.domain.service.ICodeReviewService;
import com.openai.code.review.infrastructure.git.GitCommand;
import com.openai.code.review.infrastructure.llm.ILargeLanguageModel;
import com.openai.code.review.infrastructure.llm.impl.ChatGLM;
import com.openai.code.review.utils.EnvUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, GitAPIException {
        //对于需要评审的github代码仓库，先在其中配置正确的variables或者secrets
        //仓库的settings-> Secrets and variables -> Actions
        GitCommand gitCommand = new GitCommand(
                EnvUtils.getEnv("PROJECT"),
                EnvUtils.getEnv("BRANCH"),
                EnvUtils.getEnv("AUTHOR"),
                EnvUtils.getEnv("COMMIT_MESSAGE"),
                EnvUtils.getEnv("GITHUB_TOKEN"),
                EnvUtils.getEnv("WRITE_TYPE"),
                EnvUtils.getEnv("COMMIT_SHA"),
                EnvUtils.getEnv("OWNER")
        );
        ILargeLanguageModel llm = new ChatGLM(
                EnvUtils.getEnv("API_URL"),
                EnvUtils.getEnv("API_KEY")
        );
        ICodeReviewService codeReviewService = new CodeReviewService(gitCommand,llm);
        codeReviewService.exec();
    }



}