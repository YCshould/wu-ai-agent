package com.wu.wuaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceDownloadToolTest {

    @Test
    public void testDownloadResource() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "https://pic4.zhimg.com/v2-8cdbd35b0ceefbbff66e9a9c3369dfff_r.jpg";
        String fileName = "kri.png";
        String result = tool.downloadResource(url, fileName);
        assertNotNull(result);
    }
}
