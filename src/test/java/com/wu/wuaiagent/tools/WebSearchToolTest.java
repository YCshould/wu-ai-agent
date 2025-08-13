package com.wu.wuaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String Api_key;

    @Test
    public void testSearchWeb() {
        WebSearchTool tool = new WebSearchTool(Api_key);
        String query = "NBA虎扑";
        String result = tool.searchWeb(query);
        assertNotNull(result);
     }
}

