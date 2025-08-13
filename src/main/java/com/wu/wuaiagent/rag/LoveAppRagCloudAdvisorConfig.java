package com.wu.wuaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoveAppRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String modelKey;

    private final String modelName = "恋爱秘籍";

    @Bean
    public Advisor loveAppRagCloudAdvisor() {
        //1.调用大模型API
        DashScopeApi dashScopeApi = new DashScopeApi(modelKey);
        //2.创建文档检索器
        DocumentRetriever retriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(modelName)
                        .build());
        //3.创建检索增强器
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .build();
    }
}
