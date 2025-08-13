package com.wu.wuaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 扩展查询,要用在检索文档（DocumentRetriever）或者提取查询文本来改写提示词
 * 这种优化要和去重搭配用，慎用这种优化
 */
@Component
public class QueryExpand {

    private ChatClient.Builder chatClientBuilder;

    public QueryExpand(ChatModel dashscopeChatModel){
        this.chatClientBuilder=ChatClient.builder(dashscopeChatModel);
    }

    public List<Query> expandQuery(String query){
        MultiQueryExpander queryExpander=MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)
                .build();
        return queryExpander.expand(new Query(query));
    }
}
