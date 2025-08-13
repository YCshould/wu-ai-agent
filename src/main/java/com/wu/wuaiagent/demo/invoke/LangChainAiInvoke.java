package com.wu.wuaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LangChainAiInvoke {
    public static void main(String[] args) {
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                .modelName("qwen-plus")
                .apiKey(TestApiKey.API_KEY)
                .build();
        String result = qwenChatModel.chat("你好，请问你是谁！");
        log.info("结果是：{}",result);
        System.out.println(result);
    }
}
