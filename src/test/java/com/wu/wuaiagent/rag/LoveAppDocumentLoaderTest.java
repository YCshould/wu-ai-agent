package com.wu.wuaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppDocumentLoaderTest {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Test
    void loadDocument() {
        List<Document> documents = loveAppDocumentLoader.loadDocument();
        assertNotNull(documents);
        System.out.println(documents);
    }
}