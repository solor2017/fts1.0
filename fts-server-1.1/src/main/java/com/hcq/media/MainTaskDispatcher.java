package com.hcq.media;

import com.hcq.fts.conf.Config;
import com.hcq.media.flv.FlvConvertorWorker;
import com.hcq.media.office.OfficeConvertorWorker;
import com.hcq.media.rm.RmConvertorWorker;
import com.hcq.media.thumb.ImageConvertorWorker;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class MainTaskDispatcher {
    private TasksQuene queue;
    private int FL_CNT = 1;
    public int RM_CNT = 1;
    public int OF_CNT = 1;
    public int IM_CNT = 1;

    public MainTaskDispatcher() {
        Config localResourceConfig = Config.getInstance();
        this.OF_CNT = localResourceConfig.getInteger("task.office.cnt");
        this.FL_CNT = localResourceConfig.getInteger("task.flv.cnt");
        this.RM_CNT = localResourceConfig.getInteger("task.rmvb.cnt");
        this.IM_CNT = localResourceConfig.getInteger("task.img.cnt");
        this.queue = TasksQuene.newInstance();
    }

    private void startOfficeConvertor() {
        for (int i = 0; i < this.OF_CNT; i++)
            new OfficeConvertorWorker(this.queue, i + 1).start();
    }

    private void startImageConvertor() {
        for (int i = 0; i < this.IM_CNT; i++)
            new ImageConvertorWorker(this.queue, i + 1).start();
    }

    private void startRmConvertor() {
        for (int i = 0; i < this.RM_CNT; i++)
            new RmConvertorWorker(this.queue, i + 1).start();
    }

    private void startFlvConvertor() {
        for (int i = 0; i < this.FL_CNT; i++)
            new FlvConvertorWorker(this.queue, i + 1).start();
    }

    public void startAllConvertor() {
        System.err.println("[JFTS]:Starting file convertor Queue...");
        startOfficeConvertor();
        startRmConvertor();
        startFlvConvertor();
        startImageConvertor();
    }

    public boolean addTask(String paramString1, String paramString2) {
        return this.queue.addTask(paramString1, paramString2);
    }

    public TasksQuene getQueue() {
        return this.queue;
    }
}
