package com.wu.wuaiagent.app;


import com.wu.wuaiagent.advisor.MyLoggerAdvisor;
import com.wu.wuaiagent.chatmemory.FileBasedChatMemory;
import com.wu.wuaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.wu.wuaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {


    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    // 构造方法,使ai具有记忆能力
    public LoveApp(ChatModel dashscopeChatModel){
        //基于文件的对话持久化记忆
//        String fileName = System.getProperty("user.dir")+"/tmp/chatMemory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileName);

        //基于内存的对话持久化记忆
        ChatMemory chatMemory =new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory)
                        ,new MyLoggerAdvisor()  // 自定义日志记录拦截器
                )
                .build();
    }

    /**
     * 非结构化输出，返回一个字符串，基于对话类型
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        return text;
    }

    //Java14新特性，record类型，直接定义对象，省去了类定义
    //记录类的字段是 final的
    //内部还可以自定义方法
    //public int questionCount() {
    //        return questions.size();
    //    }
    record LoveReport(String title, List<String> questions){}


    /**
     * 结构化输出，返回一个LoveReport对象
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message,String chatId){
        LoveReport loveReport = chatClient.prompt()
                .user(message)
                .system(SYSTEM_PROMPT + "每次对话后生成一个恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表。")
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport:{}",loveReport);
        return loveReport;
    }


    /**
     * 带有rag的对话，但是向量是保存在本地的
     */
    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    public String doChatWithRag(String message, String chatId) {
        String rewriterMessage = queryRewriter.rewriterQuery(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(rewriterMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用知识库问答（基于本地文档）
                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                // 应用索引增强对话（基于云端知识库）
//                .advisors(loveAppRagCloudAdvisor)
                // 应用基于Postgresql向量的问答,将信息存储在向量数据库中
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
//                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisorFactory(loveAppVectorStore,"单身"))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    @Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * mcp对话，调用外部工具
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }



}
