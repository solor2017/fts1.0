package com.hcq.media.rm;

import com.hcq.fts.conf.Config;
import com.hcq.media.ContentType;
import com.hcq.media.ProcessTask;
import com.hcq.media.TasksQuene;
import com.hcq.media.thumb.ConvetorTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class RmConvertorWorker extends Thread {
    private static String MEN_COMMAND = "mencoder";
    private static String FF_COMMAND = "ffmpeg";
    private static String FLV_DIR = "server.home";
    private ContentType contentType;
    private String flvDir;
    private String flvOutputFilePath;
    private TasksQuene queue;
    private int threadId;

    public RmConvertorWorker(TasksQuene paramTasksQuene, int paramInt) {
        this.queue = paramTasksQuene;
        this.threadId = paramInt;
        this.flvDir = Config.getInstance().getString(FLV_DIR);
        File localFile = new File(this.flvDir, "apps/arch");
        if (!localFile.exists())
            localFile.mkdirs();
        this.flvDir = localFile.getAbsolutePath();
        this.contentType = ContentType.newInstance();
    }

    public void run() {
        while (true) {
            process();
            try {
                sleep(500L);
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }

    private void process() {
        ProcessTask localProcessTask = this.queue.getNextRmTask();
        if (localProcessTask != null) {
            System.out.println("Starting[RM-Thread-" + this.threadId + ":" + this.queue.getRmLength() + "]:" + localProcessTask.getFilePath());
            convertor(localProcessTask.getFilePath(), localProcessTask.getThumbFilePath());
            localProcessTask.removeSelf();
        }
    }

    private void convertor(String paramString1, String paramString2) {
        if (this.contentType.isRmvb(paramString1)) {
            rmvb2flv(paramString1);
            createVideoCover(this.flvOutputFilePath, paramString2);
        }
    }

    private void rmvb2flv(final String paramString) {
        this.flvOutputFilePath = new File(new File(this.flvDir), this.contentType.getFileShortName(paramString) + ".flv").getPath();
        String str = getMencoderCoverCommands(paramString, this.flvOutputFilePath);
        try {
            Process localProcess = Runtime.getRuntime().exec(str);
            InputStream localInputStream1 = localProcess.getErrorStream();
            InputStream localInputStream2 = localProcess.getInputStream();
            final BufferedReader localBufferedReader1 = new BufferedReader(new InputStreamReader(localInputStream1));
            final  BufferedReader localBufferedReader2 = new BufferedReader(new InputStreamReader(localInputStream2));
            new Thread() {
                public void run() {
                    try {
                        while (localBufferedReader1 != null) ;
                        localBufferedReader1.close();
                    } catch (Exception localException) {
                        localException.printStackTrace();
                    }
                }
            }
                    .start();
            new Thread() {
                public void run() {
                    try {
                        String str1 = null;
                        String str2 = new File(paramString).getName();
                        Object localObject = "";
                        while ((str1 = localBufferedReader2.readLine()) != null) {
                            int i = str1.indexOf("(") + 1;
                            int j = str1.indexOf(")");
                            if ((i > 0) && (j > 0) && (j > i)) {
                                String str3 = str1.substring(i, j);
                                if ((str3.indexOf("%") > -1) && (!((String) localObject).equals(str3))) {
                                    localObject = str3;
                                    System.err.println("[RM-Thread-" + RmConvertorWorker.this.threadId + ":" + RmConvertorWorker.this.queue.getRmLength() + "]:[" + str2 + "]:" + str3);
                                }
                            }
                        }
                        localBufferedReader2.close();
                    } catch (Exception localException) {
                        localException.printStackTrace();
                    }
                }
            }
                    .start();
            localProcess.waitFor();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private String createVideoCover(String paramString1, String paramString2) {
        File localFile1 = new File(paramString1);
        String str1 = localFile1.getName();
        File localFile2 = new File(paramString2, str1.substring(0, str1.lastIndexOf(".")) + ".jpg");
        String str2 = getImageCoverCommands(paramString1, localFile2.getAbsolutePath());
        try {
            Process localProcess = Runtime.getRuntime().exec(str2);
            InputStream localInputStream1 = localProcess.getErrorStream();
            InputStream localInputStream2 = localProcess.getInputStream();
            final BufferedReader localBufferedReader1 = new BufferedReader(new InputStreamReader(localInputStream1));
            final BufferedReader localBufferedReader2 = new BufferedReader(new InputStreamReader(localInputStream2));
            new Thread() {
                public void run() {
                    try {
                        while (localBufferedReader1.readLine() != null) ;
                        localBufferedReader1.close();
                    } catch (Exception localException) {
                        localException.printStackTrace();
                    }
                }
            }
                    .start();
            new Thread() {
                public void run() {
                    try {
                        while (localBufferedReader2.readLine() != null) ;
                        localBufferedReader2.close();
                    } catch (Exception localException) {
                        localException.printStackTrace();
                    }
                }
            }
                    .start();
            localProcess.waitFor();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        ConvetorTool.newInstance().convert(localFile2);
        return localFile2.getAbsolutePath();
    }

    private String getImageCoverCommands(String paramString1, String paramString2) {
        String str = " ";
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(FF_COMMAND).append(str).append("-i").append(str).append("\"" + paramString1 + "\"").append(str).append("-y").append(str).append("-f").append(str).append("image2").append(str).append("-ss").append(str).append("8").append(str).append("-t").append(str).append("2").append(str).append("\"" + paramString2 + "\"");
        return localStringBuffer.toString();
    }

    private String getMencoderCoverCommands(String paramString1, String paramString2) {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(MEN_COMMAND + " \"" + paramString1 + "\" -o \"" + paramString2 + "\" -of lavf -oac mp3lame -lameopts abr:br=32:mode=2 -ovc lavc -lavcopts vcodec=flv:vbitrate=600:mbd=2:mv0:trell:v4mv:cbp:last_pred=3 -srate 22050 -vf scale=450:-3 -sws 2");
        return localStringBuffer.toString();
    }
}
