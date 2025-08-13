package com.wu.wuaiagent.rag;


import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 索引过滤器
 */
public class LoveAppRagCustomAdvisorFactory {

    public static Advisor createLoveAppRagCustomAdvisorFactory(VectorStore vectorStore,String status){
        Filter.Expression status1 = new FilterExpressionBuilder().eq("status", status).build();//过滤掉除status的所有文档，也就是只保留status为status的文档

        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(status1)
                .topK(3)
                .similarityThreshold(0.5)
                .build();

        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor
                .builder()
                .documentRetriever(vectorStoreDocumentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())//上下文查询增强器，ai不能回答时输出写好的回答
                .build();
        return retrievalAugmentationAdvisor;
    }
}
