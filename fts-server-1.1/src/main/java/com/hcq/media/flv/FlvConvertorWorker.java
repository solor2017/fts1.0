package com.hcq.media.flv;

import com.hcq.fts.conf.Config;
import com.hcq.media.ContentType;
import com.hcq.media.ProcessTask;
import com.hcq.media.TasksQuene;
import com.hcq.media.thumb.ConvetorTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class FlvConvertorWorker extends Thread {
    private static String FF_COMMAND = "ffmpeg";
    private static String FLV_DIR = "server.home";
    private TasksQuene queue;
    private ContentType contentType;
    private String flvDir;
    private int threadId;

    public FlvConvertorWorker(TasksQuene paramTasksQuene, int paramInt) {
        this.queue = paramTasksQuene;
        this.threadId = paramInt;
        Config localResourceConfig = Config.getInstance();
        this.flvDir = localResourceConfig.getString(FLV_DIR);
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

    private String getFlvCommands(String paramString1, String paramString2, String paramString3) {
        String str = " ";
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(FF_COMMAND).append(str).append("-i").append(str).append("\"").append(paramString1).append("\"").append(str).append("-y").append(str).append("-ab").append(str).append("56").append(str).append("-ar").append(str).append("22050").append(str).append("-b").append(str).append("500").append(str).append("-r").append(str).append("25").append(str).append("-qscale").append(str).append("5").append(str).append("-s").append(str).append(paramString3).append(str).append("\"").append(paramString2).append("\"");
        return localStringBuffer.toString();
    }

    private String getAudioCommand(String paramString1, String paramString2) {
        String str = " ";
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(FF_COMMAND).append(str).append("-i").
                append(str).append("\"").append(paramString1).append("\"").
                append(str).append("-y").append(str).append("\"").append(paramString2).append("\"");
        return localStringBuffer.toString();
    }

    private String getImageCoverCommands(String paramString1, String paramString2) {
        String str = " ";
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(FF_COMMAND).append(str).append("-i").append(str).append("\"" + paramString1 + "\"").append(str).append("-y").append(str).append("-f").append(str).append("image2").append(str).append("-ss").append(str).append("8").append(str).append("-t").append(str).append("2").append(str).append("\"" + paramString2 + "\"");
        return localStringBuffer.toString();
    }

    private void process() {
        ProcessTask localProcessTask = this.queue.getNextFlvTask();
        if (localProcessTask != null) {
            System.out.println("Flv[Thread-" + this.threadId + ":" + this.queue.getFlLength() + "]:" + localProcessTask.getFilePath());
            convertor(localProcessTask.getFilePath(), localProcessTask.getThumbFilePath());
            localProcessTask.removeSelf();
        }
    }

    private void convertor(String paramString1, String paramString2) {
        if (this.contentType.isFlv(paramString1)) {
            String str = createVideoCover(paramString1, paramString2);
            ConvetorTool.newInstance().convert(str);
            flv2flv(paramString1, str);
        } else if (this.contentType.isAudio(paramString1)) {
            mp32Flv(paramString1);
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
            }.start();
            new Thread() {
                public void run() {
                    try {
                        while (localBufferedReader2.readLine() != null) ;
                        localBufferedReader2.close();
                    } catch (Exception localException) {
                        localException.printStackTrace();
                    }
                }
            }.start();
            localProcess.waitFor();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return localFile2.getAbsolutePath();
    }

    private void mp32Flv(final String paramString) {
        String str1 = new File(new File(this.flvDir), this.contentType.getFileShortName(paramString) + ".flv").getPath();
        final String str2 = getAudioCommand(paramString, str1);
        try {
            Process localProcess = Runtime.getRuntime().exec(str2);
            InputStream localInputStream1 = localProcess.getErrorStream();
            InputStream localInputStream2 = localProcess.getInputStream();
            final BufferedReader localBufferedReader1 = new BufferedReader(new InputStreamReader(localInputStream1));
            final BufferedReader localBufferedReader2 = new BufferedReader(new InputStreamReader(localInputStream2));
            new Thread() {
                public void run() {
                    try {
                        String str1 = null;
                        String str2 = new File(paramString).getName();
                        Object localObject = "";
                        while ((str1 = localBufferedReader1.readLine()) != null) {
                            int i = str1.indexOf("time=");
                            if (i > 0) {
                                int j = str1.indexOf(" ", i);
                                if (j > 0) {
                                    String str3 = str1.substring(i, j);
                                    int k = str3.lastIndexOf(".");
                                    if (k > 0) {
                                        str3 = str3.substring(0, k);
                                        if (!((String) localObject).equals(str3)) {
                                            localObject = str3;
                                            System.err.println("[Flv-Thread-" + FlvConvertorWorker.this.threadId + ":" +
                                                    FlvConvertorWorker.this.queue.getRmLength() + "]:[" + str2 + "]:" + str3);
                                        }
                                    }
                                }
                            }
                        }
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
    }

    private String getTargetSize(String paramString) {
        String str = "600*450";
        File localFile = new File(paramString);
        if (localFile.exists())
            try {
                ImageReader localImageReader = (ImageReader) ImageIO.getImageReadersByFormatName("jpg").next();
                ImageInputStream localImageInputStream = ImageIO.createImageInputStream(localFile);
                localImageReader.setInput(localImageInputStream, true);
                str = newSize(600, 450, localImageReader.getWidth(0), localImageReader.getHeight(0));
                localImageInputStream.close();
            } catch (IOException localIOException) {
                localIOException.printStackTrace();
            }
        return str;
    }

    private String newSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        String str = paramInt1 + "*" + paramInt2;
        int i = paramInt3;
        int j = paramInt4;
        if (paramInt3 > paramInt1) {
            i = paramInt1;
            j = i * paramInt4 / paramInt3;
        }
        paramInt3 = i;
        paramInt4 = j;
        if (paramInt4 > paramInt2) {
            j = paramInt2;
            i = paramInt3 * j / paramInt4;
        }
        paramInt3 = i;
        paramInt4 = j;
        str = paramInt3 + "*" + paramInt4;
        return str;
    }

    private void flv2flv(final String paramString1, String paramString2) {
        String str1 = new File(new File(this.flvDir), this.contentType.getFileShortName(paramString1) + ".flv").getPath();
        final String str2 = getFlvCommands(paramString1, str1, getTargetSize(paramString2));
        try {
            Process localProcess = Runtime.getRuntime().exec(str2);
            InputStream localInputStream1 = localProcess.getErrorStream();
            InputStream localInputStream2 = localProcess.getInputStream();
            final BufferedReader localBufferedReader1 = new BufferedReader(new InputStreamReader(localInputStream1));
            final BufferedReader localBufferedReader2 = new BufferedReader(new InputStreamReader(localInputStream2));
            new Thread() {
                public void run() {
                    try {
                        String str1 = null;
                        String str2 = new File(paramString1).getName();
                        Object localObject = "";
                        while ((str1 = localBufferedReader1.readLine()) != null) {
                            int i = str1.indexOf("time=");
                            if (i > 0) {
                                int j = str1.indexOf(" ", i);
                                if (j > 0) {
                                    String str3 = str1.substring(i, j);
                                    int k = str3.lastIndexOf(".");
                                    if (k > 0) {
                                        str3 = str3.substring(0, k);
                                        if (!((String) localObject).equals(str3)) {
                                            localObject = str3;
                                            System.err.println("[Flv-Thread-" + FlvConvertorWorker.this.threadId + ":" +
                                                    FlvConvertorWorker.this.queue.getRmLength() + "]:[" + str2 + "]:" + str3);
                                        }
                                    }
                                }
                            }
                        }
                        localBufferedReader1.close();
                    } catch (Exception localException) {
                        localException.printStackTrace();
                    }
                }
            }.start();
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
    }
}