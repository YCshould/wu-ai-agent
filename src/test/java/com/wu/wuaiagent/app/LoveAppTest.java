package com.wu.wuaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {
        String chatId= UUID.randomUUID().toString();

        String message1="你好我的名字叫小明";
        String answer1 = loveApp.doChat(message1, chatId);
        Assertions.assertNotNull(answer1);
        System.out.println(answer1);

        String message2="我今天心情不好都怪小红";
        String answer2 = loveApp.doChat(message2, chatId);
        Assertions.assertNotNull(answer2);
        System.out.println(answer2);

        String message3="不好意思我刚刚忘了我怪的谁，你能告诉我今天怪的是谁吗";
        String answer3 = loveApp.doChat(message3, chatId);
        Assertions.assertNotNull(answer3);
        System.out.println(answer3);

    }

    @Test
    void doChatWithReport() {
        String chatId= UUID.randomUUID().toString();

        String message1="你好我的名字叫小明,我喜欢你怎么办";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message1, chatId);
        Assertions.assertNotNull(loveReport);
        System.out.println(loveReport);


    }

    @Test
    void doChatWithRag() {
        String chatId= UUID.randomUUID().toString();

        String message="结婚后我现在遇到一些问题怎么办，夫妻关系不亲密，感情不稳定，我想问问你有什么建议";
        String answer = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
        System.out.println(answer);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
//        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
//        // 测试地图 MCP
//        String message = "我的另一半居住在成都市成华区，请帮我找到 5 公里内合适的约会地点,不要管我们什么关系，直接给我推荐真实的地址位置";
//        String answer =  loveApp.doChatWithMcp(message, chatId);
//        System.out.println(answer);

        // 测试图片搜索 MCP
        String message1 = "帮我搜索一些NBA球星欧文的照片";
        String answer1 =  loveApp.doChatWithMcp(message1, chatId);
        Assertions.assertNotNull(answer1);
    }


}