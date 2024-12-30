package com.openai.code.review;

import org.junit.Test;

import java.io.IOException;

public class APITest {
    @Test
    public void testParseInt(){
       int a = Integer.parseInt("123");
    }

    @Test
    public void testCodeReview() throws IOException {
        String diffCode = "    @Test\n" +
                "    public void testParseInt(){\n" +
                "       int a = Integer.parseInt(\"abc123\");\n" +
                "    }";
        String s = Main.codeReview(diffCode);
        System.out.println("评审结果" + s);
    }
}
