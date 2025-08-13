package com.wu.wuaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 提问重写器，用于将原始问题转化为适合于机器人回答的形式
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;

    private final ChatClient.Builder chatClientBuilder;

    public QueryRewriter(ChatModel dashscopeChatModel){
        chatClientBuilder= ChatClient.builder(dashscopeChatModel);
        queryTransformer=RewriteQueryTransformer
                .builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }

    public String rewriterQuery(String query){
        Query transform = queryTransformer.transform(new Query(query));
        String text = transform.text();
        return text;
    }
}
