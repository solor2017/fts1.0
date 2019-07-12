package com.hcq.fts;

import com.hcq.fts.conf.Config;
import com.hcq.media.ContentType;
import com.hcq.media.MainTaskDispatcher;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class FTSServer {
    private Config config;
    private int port;
    private String encoding;
    private String workDir;
    private String thumbDir;
    public static final int OP_UPLOAD = 3841;
    public static final int OP_DOWNLOAD = 3842;
    public static final int OP_DELETE = 3843;
    public static final int OP_CHECK = 3844;
    public static final int OP_COPY = 3845;
    public static final int OP_RENAME = 3846;
    public static final int OP_MOVE = 3847;
    public static final int OP_SWF = 3848;
    public static final int OP_THUMB = 3849;
    public static final int PAKER_SIZE = 1024;
    public static final String LINE = "\r\n";
    public static final String SPLITER = "!)($#%)#(%&#)##%)_#%_#%_#((@(@)#@$$%%^^!@@+==!!!";
    private MainTaskDispatcher taskDispatcher = new MainTaskDispatcher();

    public FTSServer()
            throws Exception {
        this.taskDispatcher.startAllConvertor();
        this.config = Config.getInstance();
        this.port = this.config.getServerPort();
        this.encoding = this.config.getEncoding();
        this.workDir = this.config.getWorkdir();
        this.thumbDir = this.config.getThumbdir();
    }

    public void setConfig(Map<String, String> paramMap)
            throws Exception {
        this.port = Integer.parseInt((String) paramMap.get("port"));
        this.encoding = ((String) paramMap.get("encoding"));
        this.workDir = ((String) paramMap.get("workDir"));
        this.thumbDir = ((String) paramMap.get("thumbDir"));
    }

    public void runService()
            throws UnknownHostException, IOException, ConnectException {
        ServerSocket localServerSocket = new ServerSocket();
        InetSocketAddress localInetSocketAddress = new InetSocketAddress(this.port);
        new PolicyService().start();
        localServerSocket.bind(localInetSocketAddress);
        log("JFTSServer Running At[" + localServerSocket.getInetAddress().getHostAddress() + ":" + this.port + "],waiting for task...");
        while (true) {
            Socket localSocket = localServerSocket.accept();
            processThread(localSocket);
        }
    }

    private void log(String paramString) {
        if (this.config.showLog())
            System.err.println(paramString);
    }

    public void processThread(final Socket client) {
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    InputStream localInputStream = client.getInputStream();
                    OutputStream localOutputStream = client.getOutputStream();
                    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
                    List localList = FTSServer.this.parseInfoType(localInputStream, localByteArrayOutputStream);
                    if (localList.size() == 0) {
                        localInputStream.close();
                        localOutputStream.close();
                        client.close();
                        return;
                    }
                    int i = Integer.parseInt((String) localList.get(0));
                    switch (i) {
                        case 3841:
                            FTSServer.this.processUploadFile(localInputStream, (String) localList.get(1), (String) localList.get(2), localByteArrayOutputStream);
                            break;
                        case 3842:
                            FTSServer.this.processDownUploadFile(localOutputStream, (String) localList.get(1), (String) localList.get(2));
                            break;
                        case 3843:
                            FTSServer.this.processDeleteFile((String) localList.get(1), (String) localList.get(2));
                            break;
                        case 3844:
                            FTSServer.this.processCheckFileExist(localOutputStream, (String) localList.get(1), (String) localList.get(2));
                            break;
                        case 3845:
                            FTSServer.this.processCopyFile((String) localList.get(1), (String) localList.get(2), (String) localList.get(3), (String) localList.get(4));
                            break;
                        case 3846:
                            FTSServer.this.processRenameFile((String) localList.get(1), (String) localList.get(2), (String) localList.get(3));
                            break;
                        case 3847:
                            FTSServer.this.processMoveFile((String) localList.get(1), (String) localList.get(2), (String) localList.get(3));
                            break;
                        case 3848:
                            FTSServer.this.processSwfFile(localOutputStream, (String) localList.get(1), (String) localList.get(2));
                            break;
                        case 3849:
                            FTSServer.this.processThumbFile(localOutputStream, (String) localList.get(1), (String) localList.get(2));
                    }
                    localInputStream.close();
                    client.close();
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
        };
        Thread localThread = new Thread(runnable);
        localThread.start();
    }

    private List<String> parseInfoType(InputStream paramInputStream, ByteArrayOutputStream paramByteArrayOutputStream)
            throws Exception {
        ArrayList localArrayList = new ArrayList();
        byte[] arrayOfByte1 = new byte[1024];
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        int i = 0;
        while ((i = paramInputStream.read(arrayOfByte1)) > 0) {
            localByteArrayOutputStream.write(arrayOfByte1, 0, i);
            byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
            String str1 = new String(arrayOfByte2, this.encoding);
            int j = str1.indexOf("!)($#%)#(%&#)##%)_#%_#%_#((@(@)#@$$%%^^!@@+==!!!");
            if (j > 0) {
                String str2 = str1.substring(0, j);
                String[] arrayOfString1 = str2.split("\r\n");
                for (String str3 : arrayOfString1)
                    localArrayList.add(str3);
                int k = Integer.parseInt(arrayOfString1[0]);
                if (3841 != k)
                    break;
                paramByteArrayOutputStream.reset();
                int start=str2.getBytes(encoding).length+SPLITER.length();
                int size=arrayOfByte2.length-start;
                paramByteArrayOutputStream.write(arrayOfByte2, start,size);
                break;
            }
        }
        localByteArrayOutputStream.close();
        return localArrayList;
    }

    private void processCopyFile(String paramString1, String paramString2, String paramString3, String paramString4)
            throws Exception {
        File localFile1 = new File(this.workDir, paramString3);
        localFile1 = new File(localFile1, paramString1);
        if (!localFile1.exists()) {
            log("文件不存在:" + localFile1.getPath());
            return;
        }
        log("拷贝文件：" + localFile1.getPath());
        File localFile2 = new File(this.workDir, paramString4);
        if (!localFile2.exists())
            localFile2.mkdirs();
        FileInputStream localFileInputStream = new FileInputStream(localFile1);
        localFile2 = new File(localFile2, paramString2);
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
        byte[] arrayOfByte = new byte[1024];
        int i = 0;
        while ((i = localFileInputStream.read(arrayOfByte)) > 0)
            localFileOutputStream.write(arrayOfByte, 0, i);
        localFileOutputStream.close();
        localFileInputStream.close();
    }

    private void processUploadFile(InputStream paramInputStream, String paramString1, String paramString2, ByteArrayOutputStream paramByteArrayOutputStream)
            throws Exception {
        File localFile1 = new File(this.workDir, paramString1);
        File localFile2 = new File(this.thumbDir, paramString1);
        if (!localFile1.exists())
            localFile1.mkdirs();
        if (!localFile2.exists())
            localFile2.mkdirs();
        localFile1 = new File(localFile1, paramString2);
        log("文件保存路径:" + localFile1.getPath());
        FileOutputStream localFileOutputStream = new FileOutputStream(localFile1);
        localFileOutputStream.write(paramByteArrayOutputStream.toByteArray());
        byte[] arrayOfByte = new byte[1024];
        int i = 0;
        while ((i = paramInputStream.read(arrayOfByte)) > 0)
            localFileOutputStream.write(arrayOfByte, 0, i);
        localFileOutputStream.flush();
        localFileOutputStream.close();
        paramByteArrayOutputStream.close();
        this.taskDispatcher.addTask(localFile1.getPath(), localFile2.getAbsolutePath());
    }

    private void processCheckFileExist(OutputStream paramOutputStream, String paramString1, String paramString2)
            throws Exception {
        File localFile = new File(this.workDir, paramString1);
        localFile = new File(localFile, paramString2);
        log("检测文件:" + localFile.getPath());
        if (!localFile.exists()) {
            log("文件不存在:" + localFile.getPath());
            paramOutputStream.write("no".getBytes(this.encoding));
        } else {
            paramOutputStream.write("yes".getBytes(this.encoding));
        }
        paramOutputStream.flush();
        paramOutputStream.close();
    }

    private void processDeleteFile(String paramString1, String paramString2) {
        File localFile = new File(this.workDir, paramString1);
        localFile = new File(localFile, paramString2);
        if (!localFile.exists()) {
            log("文件不存在:" + localFile.getPath());
            return;
        }
        log("删除文件:" + localFile.getPath());
        localFile.delete();
    }

    private void processDownUploadFile(OutputStream paramOutputStream, String paramString1, String paramString2)
            throws Exception {
        File localFile = new File(this.workDir, paramString1);
        localFile = new File(localFile, paramString2);
        if (!localFile.exists()) {
            log("文件不存在:" + localFile.getPath());
            return;
        }
        log("下载文件:" + localFile.getPath());
        FileInputStream localFileInputStream = new FileInputStream(localFile);
        byte[] arrayOfByte = new byte[1024];
        int i = 0;
        while ((i = localFileInputStream.read(arrayOfByte)) > 0)
            paramOutputStream.write(arrayOfByte, 0, i);
        paramOutputStream.flush();
        paramOutputStream.close();
    }

    private void processThumbFile(OutputStream paramOutputStream, String paramString1, String paramString2)
            throws Exception {
        String str = paramString2.substring(0, paramString2.lastIndexOf(".")) + "_thumb.jpg";
        File localFile = new File(this.thumbDir, paramString1);
        localFile = new File(localFile, str);
        if (!localFile.exists()) {
            log("缩略图文件不存在:" + localFile.getPath());
            ContentType localObject1 = ContentType.newInstance();
            String localObject2 = "/insource/unknown.png";
            if (((ContentType) localObject1).isAudio(paramString2))
                localObject2 = "/insource/music.png";
            else if (((ContentType) localObject1).isDoc(paramString2))
                localObject2 = "/insource/office.png";
            else if (((ContentType) localObject1).isFlv(paramString2))
                localObject2 = "/insource/movie.png";
            else if (((ContentType) localObject1).isPdf(paramString2))
                localObject2 = "/insource/office.png";
            else if (((ContentType) localObject1).isRmvb(paramString2))
                localObject2 = "/insource/movie.png";
            else if (((ContentType) localObject1).isTif(paramString2))
                localObject2 = "/insource/office.png";
            InputStream localInputStream = FTSServer.class.getResourceAsStream((String) localObject2);
            byte[] arrayOfByte = new byte[1024];
            int j = 0;
            while ((j = localInputStream.read(arrayOfByte)) > 0)
                paramOutputStream.write(arrayOfByte, 0, j);
            paramOutputStream.flush();
            paramOutputStream.close();
            return;
        }
        log("下载缩略图文件:" + localFile.getPath());
        FileInputStream fileInputStream = new FileInputStream(localFile);
        byte[] bytes = new byte[1024];
        int i = 0;
        while ((i = (fileInputStream.read(bytes))) > 0)
            paramOutputStream.write(bytes, 0, i);
        paramOutputStream.flush();
        paramOutputStream.close();
    }

    private void processRenameFile(String paramString1, String paramString2, String paramString3)
            throws Exception {
        File localFile1 = new File(this.workDir, paramString3);
        localFile1 = new File(localFile1, paramString1);
        if (!localFile1.exists()) {
            log("文件不存在:" + localFile1.getPath());
            return;
        }
        File localFile2 = new File(localFile1.getParent(), paramString2);
        localFile1.renameTo(localFile2);
        log("重命名文件:" + localFile1.getPath() + "->" + localFile2.getPath());
    }

    private String getShortFileName(String paramString) {
        int i = paramString.lastIndexOf(".");
        return paramString.substring(0, i);
    }

    private void processSwfFile(OutputStream paramOutputStream, String paramString1, String paramString2)
            throws Exception {
        File localFile1 = new File(this.workDir, paramString1);
        File localFile2 = new File(localFile1, getShortFileName(paramString2));
        localFile1 = new File(localFile1, paramString2);
        log("查看原文:" + localFile1.getPath());
        if (!localFile1.exists()) {
            log("文件不存在:" + localFile1.getPath());
            paramOutputStream.write("no".getBytes(this.encoding));
        } else if (localFile2.exists()) {
            ArrayList localArrayList = new ArrayList();
            String[] arrayOfString = localFile2.list();
            for (String localObject2 : arrayOfString) {
                if (!localObject2.toLowerCase().endsWith(".swf"))
                    continue;
                localArrayList.add(Integer.valueOf(localObject2.substring(0, localObject2.length() - 4)));
            }
            sortFileIndex(localArrayList);
            StringBuffer stringBuffer = new StringBuffer();
            Iterator localIterator = localArrayList.iterator();
            while (localIterator.hasNext()) {
                Integer localInteger = (Integer) localIterator.next();
                if (stringBuffer.length() > 0)
                stringBuffer.append(",")
                .append(localInteger);
            }
            paramOutputStream.write(stringBuffer.toString().getBytes(this.encoding));
            log("获得原文:" + (stringBuffer.toString()));
        } else {
            System.out.println("swf不存在:" + localFile2.getPath());
            paramOutputStream.write("no".getBytes(this.encoding));
        }
        paramOutputStream.flush();
        paramOutputStream.close();
    }

    private void sortFileIndex(List<Integer> paramList) {
        for (int i = 0; i < paramList.size(); i++)
            for (int j = i + 1; j < paramList.size(); j++) {
                if (((Integer) paramList.get(i)).intValue() <= ((Integer) paramList.get(j)).intValue())
                    continue;
                int k = ((Integer) paramList.get(i)).intValue();
                paramList.set(i, paramList.get(j));
                paramList.set(j, Integer.valueOf(k));
            }
    }

    private void processMoveFile(String paramString1, String paramString2, String paramString3)
            throws Exception {
        File localFile1 = new File(this.workDir, paramString2);
        localFile1 = new File(localFile1, paramString1);
        if (!localFile1.exists()) {
            log("文件不存在:" + localFile1.getPath());
            return;
        }
        File localFile2 = new File(this.workDir, paramString3);
        if (!localFile2.exists())
            localFile2.mkdirs();
        localFile2 = new File(localFile2, paramString1);
        localFile1.renameTo(localFile2);
        log("移动文件:" + localFile1.getPath() + "->" + localFile2.getPath());
    }


}