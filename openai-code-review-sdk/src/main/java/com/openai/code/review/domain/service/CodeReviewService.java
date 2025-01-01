package com.openai.code.review.domain.service;

import com.openai.code.review.domain.model.GLMModel;
import com.openai.code.review.infrastructure.git.GitCommand;
import com.openai.code.review.infrastructure.llm.ILargeLanguageModel;
import com.openai.code.review.infrastructure.llm.dto.ChatCompletionRequest;
import com.openai.code.review.infrastructure.weixin.WeiXin;
import com.openai.code.review.infrastructure.weixin.dto.TemplateMessageDTO;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class CodeReviewService extends AbstractCodeReviewService {

    private final Logger logger = LoggerFactory.getLogger(CodeReviewService.class);

    public CodeReviewService(GitCommand gitCommand, ILargeLanguageModel llm, WeiXin weiXin) {
        super(gitCommand, llm, weiXin);
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
            return gitCommand.commitAndPush(reviewResult);
        } catch (GitAPIException e) {
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
    protected void pushWxNotification(String logUrl) {
        try {
            //设置代码相关信息到模板消息
            TemplateMessageDTO templateMessageDTO = new TemplateMessageDTO(weiXin.getTouser(), weiXin.getTemplate_id());
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.PROJECT.getCode(), gitCommand.getProject());
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.BRANCH.getCode(), gitCommand.getBranch());
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.AUTHOR.getCode(), gitCommand.getAuthor());
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.COMMIT_MESSAGE.getCode(), gitCommand.getCommitMessage());
            templateMessageDTO.put(TemplateMessageDTO.TemplateKey.REVIEW.getCode(), logUrl);
            weiXin.pushTemplateMessage(templateMessageDTO);
        } catch (IOException e) {
            logger.error("推送微信通知失败");
            throw new RuntimeException(e);
        }
    }
}