package com.openai.code.review.domain.service;

import com.openai.code.review.domain.model.valobj.GLMModel;
import com.openai.code.review.infrastructure.git.GitCommand;
import com.openai.code.review.infrastructure.git.strategy.IWriteResultStrategy;
import com.openai.code.review.infrastructure.git.strategy.WriteResultStrategyFactory;
import com.openai.code.review.infrastructure.llm.ILargeLanguageModel;
import com.openai.code.review.infrastructure.llm.dto.ChatCompletionRequest;
import com.openai.code.review.infrastructure.notification.NotificationFactory;
import com.openai.code.review.infrastructure.notification.strategy.INotificationStrategy;
import com.openai.code.review.utils.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class CodeReviewService extends AbstractCodeReviewService {

    private final Logger logger = LoggerFactory.getLogger(CodeReviewService.class);

    public CodeReviewService(GitCommand gitCommand, ILargeLanguageModel llm) {
        super(gitCommand, llm);
    }

    /**
     * 获取最近2次提交不同的代码
     *
     * @return
     */
    @Override
    protected String getDiffCode() {
        try {
            return gitCommand.getDiffCode();
        } catch (IOException e) {
            logger.error("获取不同代码出错");
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据不同的代码进行代码评审
     *
     * @param diffCode
     * @return
     */
    @Override
    protected String codeReview(String diffCode) {
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel(GLMModel.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>() {
            {
                this.add(new ChatCompletionRequest.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
                this.add(new ChatCompletionRequest.Prompt("user", diffCode));
            }
        });
        try {
            return llm.completion(chatCompletionRequest);
        } catch (IOException e) {
            logger.error("代码评审失败");
            throw new RuntimeException(e);
        }
    }

    /**
     * 评审结果写入日志
     *
     * @param reviewResult
     * @return
     */
    @Override
    protected String writeLog(String reviewResult) {
        try {
            //运用策略模式将评审结果写入日志
            String writeType = EnvUtils.getEnv("WRITE_TYPE");
            IWriteResultStrategy strategy = WriteResultStrategyFactory.getStrategy(writeType);
            strategy.initialData();
            return strategy.writeResult(reviewResult);
        } catch (Exception e) {
            logger.error("写入日志仓库失败");
            throw new RuntimeException(e);
        }
    }

    /**
     * 推送微信公众号通知，模板消息
     *
     * @param logUrl
     */
    @Override
    protected void pushNotification(String logUrl) {
        try {
            INotificationStrategy notificationStrategy = NotificationFactory.getStrategy(EnvUtils.getEnv("NOTIFICATION_TYPE"));
            notificationStrategy.initData();
            notificationStrategy.sendNotificationMessage(logUrl);
        } catch (Exception e) {
            logger.error("推送通知失败");
            throw new RuntimeException(e);
        }
    }
}
