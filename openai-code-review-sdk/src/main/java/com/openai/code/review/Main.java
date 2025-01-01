package com.openai.code.review;

import com.openai.code.review.domain.service.CodeReviewService;
import com.openai.code.review.domain.service.ICodeReviewService;
import com.openai.code.review.infrastructure.git.GitCommand;
import com.openai.code.review.infrastructure.llm.ILargeLanguageModel;
import com.openai.code.review.infrastructure.llm.impl.ChatGLM;
import com.openai.code.review.infrastructure.weixin.WeiXin;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, GitAPIException {
        //对于需要评审的github代码仓库，先在其中配置正确的variables或者secrets
        //仓库的settings-> Secrets and variables -> Actions
        GitCommand gitCommand = new GitCommand(
                getEnv("LOG_REPOSITORY_URL"),
                getEnv("PROJECT"),
                getEnv("BRANCH"),
                getEnv("AUTHOR"),
                getEnv("COMMIT_MESSAGE"),
                getEnv("GITHUB_TOKEN")
        );
        ILargeLanguageModel llm = new ChatGLM(
                getEnv("API_URL"),
                getEnv("API_KEY")
        );
        WeiXin weiXin = new WeiXin(
                getEnv("APPID"),
                getEnv("SECRET"),
                getEnv("TOUSER"),
                getEnv("TEMPLATE_ID")
        );
        ICodeReviewService codeReviewService = new CodeReviewService(gitCommand,llm,weiXin);
        codeReviewService.exec();
    }

    public static String getEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            System.out.println(key + " is empty，请正确github配置variables或者secrets");
            return null;
        }
        return value;
    }

}