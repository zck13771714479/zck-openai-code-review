package com.openai.code.review;

import com.openai.code.review.domain.service.CodeReviewService;
import com.openai.code.review.infrastructure.git.GitCommand;
import org.junit.Test;

import java.io.IOException;

public class APITest {

    public void testParseInt() {
        int a = Integer.parseInt("123aabbcc");
    }
}
