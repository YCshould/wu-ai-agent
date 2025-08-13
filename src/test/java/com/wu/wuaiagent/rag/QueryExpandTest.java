package com.wu.wuaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QueryExpandTest {

    @Resource
    private QueryExpand queryExpand;

    @Test
    void expandQuery() {
        String query="程序员黑马是谁";
        List<Query> queries = queryExpand.expandQuery(query);
        System.out.println(queries);
    }
}