package com.wu.wuaiagent.controller;

import com.wu.wuaiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * AI聊天接口，第一种方式实现，没有流式输出
     * 前端使用Fetch API分块读取
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }

    /**
     * AI聊天接口，第二种方式实现，有流式输出，是基于 SSE 协议实现的
     * 前端使用Fetch API分块读取
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * AI聊天接口，第三种方式实现，有流式输出，是基于 SSEEmitter 实现的，实现打字机效果（服务器会主动向前端发送消息）
     * 异步非阻塞
     * 可以实现自己控制什么时候给前端发送消息，以及前端接收消息的频率
     * 前端使用Fetch API分块读取
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        loveApp.doChatByStream(message, chatId)  // 获取 Flux 数据流，Flux是Publisher, .subscribe()括号里面的就是隐式的Subscriber
                .subscribe(  // subscribe的作用就是每处理一条消息就会订阅该消息，然后处理该消息，当被订阅时（通过.subscribe()），它会开始向Subscriber推送数据片段（字符串）
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);  // 每次调用 send 方法都会发送消息到前端
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }


}
