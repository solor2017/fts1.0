package com.hcq.media;

import java.io.File;

/**
 * @Author: solor
 * @Since:1.1
 * @Description:
 */
public class ContentType {
    public static final int TYPE_IMG = 4001;
    public static final int TYPE_VIDEO = 4002;
    public static final int TYPE_AUDIO = 4003;
    public static final int TYPE_OFFICE = 4004;
    public static final int TYPE_TXT = 4005;
    public static final int TYPE_UNSUPPORT = 4015;
    public static String[] IMG_EXTENSIONS = {".jpg", ".gif", ".png", ".jpeg"};
    public static String[] VIDEO_EXTENSIONS = {".avi", ".mpg", ".wmv", ".3gp", ".mp4", ".asf", ".asx", ".flv", ".rm", ".rmvb"};
    public static String[] AUDIO_EXTENSIONS = {".wma", ".wav", ".mp3", ".cdi"};
    public static String[] OFFICE_EXTENSIONS = {".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx", ".tif", ".pdf"};
    public static String[] TXT_EXTENSIONS = {".txt"};
    private static ContentType instance;

    public static ContentType newInstance() {
        return instance == null ? (ContentType.instance = new ContentType()) : instance;
    }

    private String getExtension(String paramString) {
        int i = paramString.lastIndexOf(".");
        if (i > -1)
            return paramString.substring(i);
        return "";
    }

    private boolean checkFile(String[] paramArrayOfString, String paramString) {
        paramString = paramString.toLowerCase();
        for (String str : paramArrayOfString)
            if (str.equalsIgnoreCase(paramString))
                return true;
        return false;
    }

    public int getType(String paramString) {
        String str = getExtension(paramString);
        if (checkFile(IMG_EXTENSIONS, str))
            return 4001;
        if (checkFile(VIDEO_EXTENSIONS, str))
            return 4002;
        if (checkFile(OFFICE_EXTENSIONS, str))
            return 4004;
        if (checkFile(TXT_EXTENSIONS, str))
            return 4005;
        if (checkFile(AUDIO_EXTENSIONS, str))
            return 4003;
        return 4015;
    }

    public boolean isDoc(String paramString) {
        return checkFile(new String[]{".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx"}, getExtension(paramString));
    }

    public boolean isPdf(String paramString) {
        return checkFile(new String[]{".pdf"}, getExtension(paramString));
    }

    public boolean isTif(String paramString) {
        return checkFile(new String[]{".tif"}, getExtension(paramString));
    }

    public boolean isRmvb(String paramString) {
        return checkFile(new String[]{".rm", ".rmvb"}, getExtension(paramString));
    }

    public boolean isImage(String paramString) {
        return checkFile(new String[]{".jpg", ".png", ".gif"}, getExtension(paramString));
    }

    public boolean isFlv(String paramString) {
        return checkFile(new String[]{".avi", ".mpg", ".wmv", ".3gp", ".mo", ".mp4", ".asf", ".asx", ".flv"}, getExtension(paramString));
    }

    public boolean isAudio(String paramString) {
        return checkFile(AUDIO_EXTENSIONS, getExtension(paramString));
    }

    public String getFileShortName(String paramString) {
        String str = new File(paramString).getName();
        return str.substring(0, str.lastIndexOf("."));
    }
}