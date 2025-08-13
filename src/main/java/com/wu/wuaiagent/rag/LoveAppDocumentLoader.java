package com.wu.wuaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RAG文档加载器
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {

    private  final ResourcePatternResolver resourcePatternResolver;

    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver=resourcePatternResolver;
    }

    public List<Document> loadDocument() {
        List<Document> documents=new ArrayList<Document>();
        try {
            //1.读取原始的document数据
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            //2.分割document数据
            //参考官方文档https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html#_markdown
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                String status = filename.substring(filename.length() - 6, filename.length() - 4);
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .withAdditionalMetadata("status", status)
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                documents.addAll(reader.read());
            }
        } catch (IOException e) {
            log.error("读取原始的document数据失败",e);
        }
        return documents;
    }
}
