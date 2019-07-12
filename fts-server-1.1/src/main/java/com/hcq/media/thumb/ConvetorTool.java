package com.hcq.media.thumb;

import com.hcq.fts.conf.Config;

import java.io.File;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class ConvetorTool {
    private int destWidth;
    private int destHeight;
    private static ConvetorTool instance;

    private ConvetorTool() {
        String str = Config.getInstance().getThumbSize().toLowerCase();
        String[] arrayOfString = str.split("x");
        this.destWidth = Integer.parseInt(arrayOfString[0]);
        this.destHeight = Integer.parseInt(arrayOfString[1]);
    }

    public static ConvetorTool newInstance() {
        return instance == null ? (ConvetorTool.instance = new ConvetorTool()) : instance;
    }

    public void convert(String paramString) {
        convert(new File(paramString));
    }

    public void convert(File paramFile) {
        String str = paramFile.getName();
        File localFile = new File(paramFile.getParentFile(), str.substring(0, str.lastIndexOf(".")) + "_thumb.jpg");
        new ThumbServer().process(paramFile, localFile, this.destWidth, this.destHeight);
    }

    public void convert(String paramString1, String paramString2) {
        convert(new File(paramString1), new File(paramString2));
    }

    public void convert(File paramFile1, File paramFile2) {
        new ThumbServer().process(paramFile1, paramFile2, this.destWidth, this.destHeight);
    }
}
