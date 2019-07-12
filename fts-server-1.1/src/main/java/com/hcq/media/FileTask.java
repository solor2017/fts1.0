package com.hcq.media;

/**
 * @Author: solor
 * @Since:
 * @Description:
 */
public final class FileTask
        implements ProcessTask {
    private String filePath;
    private boolean converting = false;
    private TasksQuene queue;
    private String taskId;
    private String taskType;
    private String thumbFilePath;

    public FileTask(String paramString1, String paramString2, TasksQuene paramTasksQuene, String paramString3, String paramString4) {
        this.filePath = paramString2;
        this.queue = paramTasksQuene;
        this.taskId = paramString1;
        this.taskType = paramString3;
        this.thumbFilePath = paramString4;
    }

    public String getThumbFilePath() {
        return this.thumbFilePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setStatus(boolean paramBoolean) {
        this.converting = paramBoolean;
    }

    public boolean getStatus() {
        return this.converting;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void removeSelf() {
        this.queue.removeTask(this, this.taskType);
    }
}