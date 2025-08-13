package com.wu.wuaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// 取消注释即可在 SpringBoot 项目启动时执行
@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) throws Exception {
        /**
         * springai调用示例1 chatModel
         */
//        AssistantMessage output = dashscopeChatModel.call(new Prompt("你好，我是wu"))
//                .getResult()
//                .getOutput();
//        System.out.println(output.getText());

        /**
         * springai调用示例2 chatClient
         */
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem("你是一个情感大师，帮助客户解决生活中的各种问题。")
                .build();
        String text = chatClient.prompt().user("你好，我是wu,我心情不好").call().content();
        System.out.println(text);
    }
}
