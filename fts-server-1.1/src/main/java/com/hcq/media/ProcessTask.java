package com.hcq.media;

import java.io.Serializable;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public abstract interface ProcessTask extends Serializable {
    public abstract String getFilePath();

    public abstract String getThumbFilePath();

    public abstract void setStatus(boolean paramBoolean);

    public abstract boolean getStatus();

    public abstract String getTaskId();

    public abstract void removeSelf();
}