package com.openai.code.review;

import com.openai.code.review.domain.service.CodeReviewService;
import com.openai.code.review.infrastructure.git.GitCommand;
import org.junit.Test;

import java.io.IOException;

public class APITest {

    public void testParseInt() {
        int a = Integer.parseInt("123aabbcc");
    }

//    @Test
//    public void testDiffCode() {
//        GitCommand gitCommand = new GitCommand("", "", "", "", "", "");
//        CodeReviewService codeReviewService = new CodeReviewService(gitCommand, null, null);
//        String diffCode = codeReviewService.getDiffCode();
//        System.out.println(diffCode);
//    }

    @Test
    public void testCodeReview() throws IOException {
        String diffCode = "    @Test\n" +
                "    public void testParseInt(){\n" +
                "       int a = Integer.parseInt(\"abc123\");\n" +
                "    }";
        String s = Main.codeReview(diffCode);
        System.out.println("评审结果" + s);
    }

    @Test
    public void testPushWXMessage() throws IOException {
        String url = "https://github.com/zck13771714479/zck-openai-code-review-logs/blob/main/2024-12-30/Ul7qMmjCZxQs.md";
        Main.pushWxNotification(url);
    }

}
