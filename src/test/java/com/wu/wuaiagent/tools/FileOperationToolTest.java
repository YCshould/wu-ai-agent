package com.wu.wuaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileOperationToolTest {

    @Test
    void readFile() {
        String fielName = "FileOperationToolTest.txt";
        FileOperationTool fileOperationTool = new FileOperationTool();
        String s = fileOperationTool.readFile(fielName);
        System.out.println(s);
        Assertions.assertNotNull(s);
    }

    @Test
    void writeFile() {
        String fielName = "FileOperationToolTest.txt";
        String content = "Hello World!";
        FileOperationTool fileOperationTool = new FileOperationTool();
        String s = fileOperationTool.writeFile(fielName, content);
        Assertions.assertNotNull(s);
    }
}