package com.wu.wuaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 向量存储器以及数据检索
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader documentLoader;

    @Resource
    private MyKeyWordEnricher myKeyWordEnricher;

    @Resource
    private QueryExpand queryExpand;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        //1.创建SimpleVectorStore对象，并设置embeddingModel
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        //2.你要存储就要先拿到分割后的文档
        List<Document> documents = documentLoader.loadDocument();
        //3.利用关键词增强器对文档进行关键词增强
        List<Document> documentsEnriched = myKeyWordEnricher.keyWordEnricher(documents);
        vectorStore.doAdd(documentsEnriched);

        /**
         * 文档检索是在文档插入到向量存储器中和在进行优化检索，但是这种优化要和去重搭配用，慎用这种优化
         */
//        List<Query> queries = queryExpand.expandQuery("我还是单身怎么办");
//        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
//                .vectorStore(vectorStore)
//                .similarityThreshold(0.3)
//                .topK(5)
//                .filterExpression(new FilterExpressionBuilder()
//                        .eq("status", "单身")
//                        .build())
//                .build();
//        // 直接用扩展后的查询来获取文档
//        List<Document> retrievedDocuments = documentRetriever.retrieve(queries.getLast());
//        System.out.println(retrievedDocuments);
        return vectorStore;
    }
}
