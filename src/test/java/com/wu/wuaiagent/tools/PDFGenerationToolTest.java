package com.wu.wuaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "AI.pdf";
        String content = "学习AI来406";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
