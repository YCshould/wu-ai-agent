package com.wu.wuaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TerminalOperationToolTest {

    @Test
    public void testExecuteTerminalCommand_cmd() {
        TerminalOperationTool tool = new TerminalOperationTool();
        String result = tool.executeTerminalCommand("dir");
        assertNotNull(result);
        System.out.println(result);
    }
}
