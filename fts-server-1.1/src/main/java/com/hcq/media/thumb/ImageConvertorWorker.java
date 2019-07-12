package com.hcq.media.thumb;

import com.hcq.media.ContentType;
import com.hcq.media.ProcessTask;
import com.hcq.media.TasksQuene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class ImageConvertorWorker extends Thread {
    private ContentType contentType;
    private TasksQuene queue;
    private int threadId;

    public ImageConvertorWorker(TasksQuene paramTasksQuene, int paramInt) {
        this.queue = paramTasksQuene;
        this.threadId = paramInt;
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
        ProcessTask localProcessTask = this.queue.getNextImageTask();
        if (localProcessTask != null) {
            System.out.println("Starting[IMG-Thread-" + this.threadId + ":" + this.queue.getRmLength() + "]:" + localProcessTask.getFilePath());
            convertor(localProcessTask.getFilePath(), localProcessTask.getThumbFilePath());
            localProcessTask.removeSelf();
        }
    }

    private void convertor(String paramString1, String paramString2) {
        if (this.contentType.isImage(paramString1)) {
            File localFile1 = new File(paramString1);
            File localFile2 = new File(paramString2, localFile1.getName());
            copyFile(localFile1, localFile2);
            ConvetorTool.newInstance().convert(localFile2);
        }
    }

    private void copyFile(File paramFile1, File paramFile2) {
        try {
            FileInputStream localFileInputStream = new FileInputStream(paramFile1);
            FileOutputStream localFileOutputStream = new FileOutputStream(paramFile2);
            byte[] arrayOfByte = new byte[1024];
            int i = 0;
            while ((i = localFileInputStream.read(arrayOfByte)) > 0)
                localFileOutputStream.write(arrayOfByte, 0, i);
            localFileOutputStream.flush();
            localFileOutputStream.close();
            localFileInputStream.close();
        } catch (Exception localException) {
        }
    }
}
