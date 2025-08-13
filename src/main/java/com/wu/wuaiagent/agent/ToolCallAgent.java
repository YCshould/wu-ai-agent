package com.wu.wuaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;


import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理工具调用的基础代理类，实现具体的act和think方法
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent{

    //可用的工具
    private final ToolCallback[] availableToolsCall;

    //工具调用的响应
    private ChatResponse toolChatResponse;

    //工具调用的配置
    private final ChatOptions chatOptions;

    //工具调用管理者
    private final ToolCallingManager toolCallingManager;

    public ToolCallAgent(ToolCallback[] tollCallback){
        super();
        this.availableToolsCall = tollCallback;
        this.toolCallingManager=ToolCallingManager.builder().build();
        this.chatOptions= DashScopeChatOptions
                .builder()
                .withProxyToolCalls(true) //设计为true表示不用springAi的内置工具调用机制，直接使用自定义的工具调用机制
                .build();
    }


    /**
     * 执行具体的思考方法，判断是否需要行动，需要调用工具
     * @return
     */
    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        if (getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList = getMessageList();
        // 构造提示信息，将融合了前后消息的消息列表和配置信息传入
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            // 获取带工具选项的响应
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableToolsCall)
                    .call()
                    .chatResponse();
            // 记录响应，用于 Act
            this.toolChatResponse = chatResponse;
            // 解析响应，获取助手消息（响应结果），助手消息后面也会加入到消息列表中
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 输出提示信息
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info(getName() + "的思考: " + result);
            log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s，参数：%s",
                            toolCall.name(),
                            toolCall.arguments())
                    )
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            if (toolCallList.isEmpty()) {
                // 只有不调用工具时，才记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            } else {
                // 需要调用工具时，无需记录助手消息，因为调用工具时会自动记录
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考过程遇到了问题: " + e.getMessage());
            getMessageList().add(
                    new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            return false;
        }
    }


    /**
     * 执行工具调用列表，将工具的响应添加到消息列表中
     * @return
     */
    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        if (!toolChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        // 调用工具
        // toolCallingManager可以实现手动调用工具，根据prompt, toolChatResponse找到对应的工具
        // 要实现自主工具调用，还要有个chatOptions的配置告诉springAi禁用内置的工具调用机制，自己实现
        Prompt prompt = new Prompt(getMessageList(), chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolChatResponse);
        // 记录消息上下文，conversationHistory 已经包含了助手消息和工具调用返回的结果
        setMessageList(toolExecutionResult.conversationHistory());
        // 当前工具调用的结果展示
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务！结果: " + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);
        // 是否需要终止对话
        boolean isTerminate = toolResponseMessage.getResponses().stream().anyMatch(response -> "doTerminate".equals(response.name()));
        if (isTerminate) {
            setState(AgentState.FINISHED);
        }
        return results;
    }

}
