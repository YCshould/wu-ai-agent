package com.wu.wuaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关键提展，用于文档的元数据标注优化
 */
@Component
public class MyKeyWordEnricher {

    @Resource
    private ChatModel dashscopeChatModel;

    public List<Document> keyWordEnricher(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }

}
