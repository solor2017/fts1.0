package com.hcq.media;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: solor
 * @Since:
 * @Description:
 */
public class TasksQuene implements Serializable {
    private static TasksQuene instance;
    public static final String rmTaskFile = "rm-task.ser";
    public static final String flTaskFile = "fl-task.ser";
    public static final String ofTaskFile = "of-task.ser";
    public static final String imgTaskFile = "img-task.ser";
    private Map<String, ProcessTask> rmTaskList;
    private Map<String, ProcessTask> flTaskList;
    private Map<String, ProcessTask> ofTaskList;
    private Map<String, ProcessTask> imgTaskList;

    public static TasksQuene newInstance() {
        return instance == null ? (TasksQuene.instance = new TasksQuene()) : instance;
    }

    private TasksQuene() {
        readTaskConfig();
    }

    private void readTaskConfig() {
        File localFile1 = new File("rm-task.ser");
        File localFile2 = new File("fl-task.ser");
        File localFile3 = new File("of-task.ser");
        File localFile4 = new File("img-task.ser");
        try {
            if (localFile1.exists()) {
                this.rmTaskList = readTask(localFile1);
                if (this.rmTaskList == null)
                    this.rmTaskList = new LinkedHashMap();
            } else {
                this.rmTaskList = new LinkedHashMap();
            }
            resetTaskStatus(this.rmTaskList);
            saveTask("rm-task.ser", this.rmTaskList);
            if (localFile2.exists()) {
                this.flTaskList = readTask(localFile2);
                if (this.flTaskList == null)
                    this.flTaskList = new LinkedHashMap();
            } else {
                this.flTaskList = new LinkedHashMap();
            }
            resetTaskStatus(this.flTaskList);
            saveTask("fl-task.ser", this.flTaskList);
            if (localFile3.exists()) {
                this.ofTaskList = readTask(localFile3);
                if (this.ofTaskList == null)
                    this.ofTaskList = new LinkedHashMap();
            } else {
                this.ofTaskList = new LinkedHashMap();
            }
            resetTaskStatus(this.ofTaskList);
            saveTask("of-task.ser", this.ofTaskList);
            if (localFile4.exists()) {
                this.imgTaskList = readTask(localFile4);
                if (this.imgTaskList == null)
                    this.imgTaskList = new LinkedHashMap();
            } else {
                this.imgTaskList = new LinkedHashMap();
            }
            resetTaskStatus(this.imgTaskList);
            saveTask("img-task.ser", this.imgTaskList);
            System.out.println("[Task Office Queue]->[Cnt:" + this.ofTaskList.size() + "]");
            System.out.println("[Task Rmvb Queue]->[Cnt:" + this.rmTaskList.size() + "]");
            System.out.println("[Task Flv Queue]->[Cnt:" + this.flTaskList.size() + "]");
            System.out.println("[Task Image Queue]->[Cnt:" + this.imgTaskList.size() + "]");
        } catch (Exception localException) {
            System.out.println("读取任务失败");
        }
    }

    public int getOfLength() {
        return this.ofTaskList.size();
    }

    public int getRmLength() {
        return this.rmTaskList.size();
    }

    public int getFlLength() {
        return this.flTaskList.size();
    }

    public int getImgLength() {
        return this.imgTaskList.size();
    }

    private void resetTaskStatus(Map<String, ProcessTask> paramMap) {
        Iterator localIterator = paramMap.keySet().iterator();
        while (localIterator.hasNext())
            ((ProcessTask) paramMap.get(localIterator.next())).setStatus(false);
    }

    private Map<String, ProcessTask> readTask(File paramFile) {
        Map localMap = null;
        try {
            FileInputStream localFileInputStream = new FileInputStream(paramFile);
            ObjectInputStream localObjectInputStream = new ObjectInputStream(localFileInputStream);
            Object localObject = localObjectInputStream.readObject();
            localObjectInputStream.close();
            localFileInputStream.close();
            if ((localObject instanceof Map))
                localMap = (Map) localObject;
        } catch (Exception localException) {
            System.out.println("读取保存对象失败！" + localException.getMessage());
        }
        return localMap;
    }

    synchronized void saveTask(String paramString, Map<String, ProcessTask> paramMap) {
        try {
            new File(paramString).delete();
            FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
            ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localFileOutputStream);
            localObjectOutputStream.writeObject(paramMap);
            localObjectOutputStream.flush();
            localObjectOutputStream.close();
            localFileOutputStream.close();
        } catch (Exception localException) {
            System.out.println("保存任务失败!" + localException.getMessage());
        }
    }

    public synchronized boolean addTask(String paramString1, String paramString2) {
        ContentType localContentType = ContentType.newInstance();
        int i = localContentType.getType(paramString1);
        String str = newTaskId();
        int j = 0;
        boolean b = false;
        if (4004 == i) {
            this.ofTaskList.put(str, new FileTask(str, paramString1, this, "of-task.ser", paramString2));
            saveTask("of-task.ser", this.ofTaskList);
            j = 1;
            b = true;
        } else if (4002 == i) {
            if (localContentType.isRmvb(paramString1)) {
                this.rmTaskList.put(str, new FileTask(str, paramString1, this, "rm-task.ser", paramString2));
                saveTask("rm-task.ser", this.rmTaskList);
            } else {
                this.flTaskList.put(str, new FileTask(str, paramString1, this, "fl-task.ser", paramString2));
                saveTask("fl-task.ser", this.flTaskList);
            }
            j = 1;
            b = true;
        } else if (4003 == i) {
            this.flTaskList.put(str, new FileTask(str, paramString1, this, "fl-task.ser", paramString2));
            saveTask("fl-task.ser", this.flTaskList);
            j = 1;
            b = true;
        } else if (4001 == i) {
            this.imgTaskList.put(str, new FileTask(str, paramString1, this, "img-task.ser", paramString2));
            saveTask("img-task.ser", this.imgTaskList);
            j = 1;
            b = true;
        }
        return b;
    }

    private String newTaskId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public synchronized ProcessTask getNextOfficeTask() {
        Iterator localIterator = this.ofTaskList.keySet().iterator();
        while (localIterator.hasNext()) {
            ProcessTask localProcessTask = (ProcessTask) this.ofTaskList.get(localIterator.next());
            if (!localProcessTask.getStatus()) {
                localProcessTask.setStatus(true);
                saveTask("of-task.ser", this.ofTaskList);
                return localProcessTask;
            }
        }
        return null;
    }

    public void clearAllTask() {
        try {
            saveTask("of-task.ser", this.ofTaskList = new LinkedHashMap());
            saveTask("fl-task.ser", this.flTaskList = new LinkedHashMap());
            saveTask("rm-task.ser", this.rmTaskList = new LinkedHashMap());
            saveTask("img-task.ser", this.imgTaskList = new LinkedHashMap());
        } catch (Exception localException) {
        }
    }

    public synchronized ProcessTask getNextImageTask() {
        Iterator localIterator = this.imgTaskList.keySet().iterator();
        while (localIterator.hasNext()) {
            ProcessTask localProcessTask = (ProcessTask) this.imgTaskList.get(localIterator.next());
            if (!localProcessTask.getStatus()) {
                localProcessTask.setStatus(true);
                saveTask("img-task.ser", this.imgTaskList);
                return localProcessTask;
            }
        }
        return null;
    }

    public synchronized ProcessTask getNextRmTask() {
        Iterator localIterator = this.rmTaskList.keySet().iterator();
        while (localIterator.hasNext()) {
            ProcessTask localProcessTask = (ProcessTask) this.rmTaskList.get(localIterator.next());
            if (!localProcessTask.getStatus()) {
                localProcessTask.setStatus(true);
                saveTask("rm-task.ser", this.rmTaskList);
                return localProcessTask;
            }
        }
        return null;
    }

    public synchronized ProcessTask getNextFlvTask() {
        Iterator localIterator = this.flTaskList.keySet().iterator();
        while (localIterator.hasNext()) {
            ProcessTask localProcessTask = (ProcessTask) this.flTaskList.get(localIterator.next());
            if (!localProcessTask.getStatus()) {
                localProcessTask.setStatus(true);
                saveTask("fl-task.ser", this.flTaskList);
                return localProcessTask;
            }
        }
        return null;
    }

    public synchronized void removeTask(ProcessTask paramProcessTask, String paramString) {
        if ("fl-task.ser".equals(paramString)) {
            this.flTaskList.remove(paramProcessTask.getTaskId());
            System.err.println("[Remove Task:Flv]:left->" + this.flTaskList.size() + " Tasks.");
            saveTask("fl-task.ser", this.flTaskList);
        } else if ("of-task.ser".equals(paramString)) {
            this.ofTaskList.remove(paramProcessTask.getTaskId());
            System.err.println("[Remove Task:Office]:left->" + this.ofTaskList.size() + " Tasks.");
            saveTask("of-task.ser", this.ofTaskList);
        } else if ("rm-task.ser".equals(paramString)) {
            this.rmTaskList.remove(paramProcessTask.getTaskId());
            System.err.println("[Remove Task:RM]:left->" + this.rmTaskList.size() + " Tasks.");
            saveTask("rm-task.ser", this.rmTaskList);
        } else if ("img-task.ser".equals(paramString)) {
            this.imgTaskList.remove(paramProcessTask.getTaskId());
            System.err.println("[Remove Task:IMG]:left->" + this.imgTaskList.size() + " Tasks.");
            saveTask("img-task.ser", this.imgTaskList);
        }
    }
}