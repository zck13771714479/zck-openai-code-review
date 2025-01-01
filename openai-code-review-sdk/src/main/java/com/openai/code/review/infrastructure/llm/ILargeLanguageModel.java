package com.openai.code.review.infrastructure.llm;

import com.openai.code.review.infrastructure.llm.dto.ChatCompletionRequest;

import java.io.IOException;

public interface ILargeLanguageModel {

    String completion(ChatCompletionRequest request) throws IOException;
}
