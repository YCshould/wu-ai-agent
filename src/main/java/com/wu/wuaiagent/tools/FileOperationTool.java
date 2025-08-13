package com.wu.wuaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.wu.wuaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class FileOperationTool {

    private final String filePath= FileConstant.FILE_PATH+"/file";

    /**
     * 用hutool工具类读取文件内容
     * @return
     */
    @Tool( description = "Read file content from a file")
    public String readFile(@ToolParam( description = "file name") String fileName){
        String fileContent=filePath+"/"+fileName;
        String s = null;
        try {
            s = FileUtil.readUtf8String(fileContent);
            return s;
        } catch (IORuntimeException e) {
            return "读取文件失败"+e.getMessage();
        }

    }

    /**
     * 用hutool工具类写入文件内容
     * @return
     */
    @Tool( description = "Write file content to a file")
    public String writeFile(@ToolParam( description = "file name") String fileName
            ,@ToolParam(description = "file content") String content){
        String fileContent=filePath+"/"+fileName;
        FileUtil.mkdir(filePath);
        try {
            FileUtil.writeUtf8String(content, fileContent);
        } catch (IORuntimeException e) {
            return "写入文件失败";
        }
        return "写入文件成功";
    }
}
