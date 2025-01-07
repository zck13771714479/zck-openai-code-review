package com.openai.code.review.infrastructure.git.strategy;

import com.openai.code.review.infrastructure.git.strategy.impl.CommitCommentStrategy;
import com.openai.code.review.infrastructure.git.strategy.impl.GitRemoteStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成写入策略的工厂
 */
public class WriteResultStrategyFactory {

    private static final Map<String, IWriteResultStrategy> strategyMap = new HashMap<>();

    static {
        strategyMap.put(new GitRemoteStrategy().typeName(), new GitRemoteStrategy());
        strategyMap.put(new CommitCommentStrategy().typeName(), new CommitCommentStrategy());
    }

    public static IWriteResultStrategy getStrategy(String strategyName) {
        return strategyMap.get(strategyName);
    }

}
