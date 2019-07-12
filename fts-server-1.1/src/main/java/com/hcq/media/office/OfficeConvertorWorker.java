package com.hcq.media.office;

import com.hcq.docconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.hcq.docconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.hcq.fts.conf.Config;
import com.hcq.media.ContentType;
import com.hcq.media.ProcessTask;
import com.hcq.media.TasksQuene;
import com.hcq.media.thumb.ConvetorTool;
import com.sun.media.jai.codec.*;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author: solor
 * @Since:
 * @Description:
 */
public class OfficeConvertorWorker extends Thread {
    private TasksQuene queue;
    private String host;
    private int port;
    private String clearPdfFilePath = null;
    public static final String LANGUAGE_DIR_KEY = "lang.dir";
    private int threadId;

    public OfficeConvertorWorker(TasksQuene paramTasksQuene, int paramInt) {
        this.queue = paramTasksQuene;
        this.threadId = paramInt;
        Config localResourceConfig = Config.getInstance();
        this.host = localResourceConfig.getString("oo.ip");
        this.port = localResourceConfig.getInteger("oo.port");
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
        ProcessTask localProcessTask = this.queue.getNextOfficeTask();
        if (localProcessTask != null) {
            System.out.println("Office[Thread-" + this.threadId + ":" + this.queue.getOfLength() + "]:" + localProcessTask.getFilePath());
            convertor(localProcessTask.getFilePath(), localProcessTask.getThumbFilePath());
            localProcessTask.removeSelf();
        }
    }

    private void convertor(String paramString1, String paramString2) {
        ContentType localContentType = ContentType.newInstance();
        if (localContentType.isDoc(paramString1))
            doc2pdf(paramString1, paramString2);
        else if (localContentType.isPdf(paramString1))
            pdf2swf(paramString1, paramString2);
        else if (localContentType.isTif(paramString1))
            tif2swf(paramString1, paramString2);
    }

    private void doc2pdf(String paramString1, String paramString2) {
        File localFile1 = new File(paramString1);
        String str = localFile1.getName();
        File localFile2 = new File(localFile1.getParentFile(), str.substring(0, str.lastIndexOf(".")) + ".pdf");
        this.clearPdfFilePath = localFile2.getAbsolutePath();
        try {
            System.err.println("[DOC->PDF]:" + paramString1);
            SocketOpenOfficeConnection localSocketOpenOfficeConnection = new SocketOpenOfficeConnection(this.host, this.port);
            localSocketOpenOfficeConnection.connect();
            OpenOfficeDocumentConverter localOpenOfficeDocumentConverter = new OpenOfficeDocumentConverter(localSocketOpenOfficeConnection);
            localOpenOfficeDocumentConverter.convert(localFile1, localFile2);
            localSocketOpenOfficeConnection.disconnect();
            pdf2swf(localFile2.getAbsolutePath(), paramString2);
        } catch (Exception localException) {
            System.err.println("转换出错：" + localException.getMessage());
        }
    }

    private void pdf2swf(String paramString1, String paramString2) {
        String str1 = new File(paramString1).getName();
        File localFile1 = new File(paramString2, str1.substring(0, str1.lastIndexOf(".")) + ".jpg");
        new PdfImageCorverCreator().createCover(paramString1, localFile1.getAbsolutePath());
        ConvetorTool.newInstance().convert(localFile1);
        System.out.println("图片路径：" + localFile1.getAbsolutePath());
        try {
            System.err.println("[PDF->SWF]:" + paramString1);
            Runtime localRuntime = Runtime.getRuntime();
            File localFile2 = new File(paramString1.substring(0, paramString1.lastIndexOf(".")));
            localFile2.mkdirs();
            String str2 = System.getProperty("lang.dir");
            String str3 = "pdf2swf -o \"" + localFile2.getPath() + File.separatorChar + "%.swf\" -s flashversion=9 \"" + paramString1 + "\"";
            if ((str2 != null) && (str2.trim().length() > 0))
                str3 = "pdf2swf -o \"" + localFile2.getPath() + File.separatorChar + "%.swf\" -s flashversion=9 -s languagedir=\"" + str2 + "\" \"" + paramString1 + "\"";
            Process localProcess = localRuntime.exec(str3);
            final InputStream localInputStream1 = localProcess.getInputStream();
            new Thread() {
                public void run() {
                    try {
                        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream1));
                        while (localBufferedReader.readLine() != null) ;
                        localBufferedReader.close();
                    } catch (Exception localException) {
                    }
                }
            }
                    .start();
            final InputStream localInputStream2 = localProcess.getErrorStream();
            new Thread() {
                public void run() {
                    try {
                        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream2));
                        while (localBufferedReader.readLine() != null) ;
                        localBufferedReader.close();
                    } catch (Exception localException) {
                    }
                }
            }
                    .start();
            localProcess.waitFor();
            if (this.clearPdfFilePath != null) {
                File localFile3 = new File(this.clearPdfFilePath);
                localFile3.delete();
                this.clearPdfFilePath = null;
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void tif2swf(String paramString1, String paramString2) {
        File localFile1 = new File(paramString1);
        String str = paramString1.substring(0, paramString1.lastIndexOf("."));
        new File(str).mkdirs();
        try {
            System.err.println("[TIF->SWF]:" + paramString1);
            File localFile2 = new File(str, "tmp");
            if (!localFile2.exists())
                localFile2.mkdirs();
            FileSeekableStream localFileSeekableStream = new FileSeekableStream(localFile1);
            TIFFEncodeParam localTIFFEncodeParam = new TIFFEncodeParam();
            JPEGEncodeParam localJPEGEncodeParam = new JPEGEncodeParam();
            ImageDecoder localImageDecoder = ImageCodec.createImageDecoder("tiff", localFileSeekableStream, null);
            int i = localImageDecoder.getNumPages();
            localTIFFEncodeParam.setCompression(4);
            localTIFFEncodeParam.setLittleEndian(false);
            for (int j = 0; j < i; j++) {
                RenderedImage localRenderedImage = localImageDecoder.decodeAsRenderedImage(j);
                File localFile3 = new File(localFile2, j + ".jpg");
                ParameterBlock localParameterBlock = new ParameterBlock();
                localParameterBlock.addSource(localRenderedImage);
                localParameterBlock.add(localFile3.toString());
                localParameterBlock.add("JPEG");
                localParameterBlock.add(localJPEGEncodeParam);
                RenderedOp localRenderedOp = JAI.create("filestore", localParameterBlock);
                localRenderedOp.dispose();
                jpg2swf(j + 1, localFile3, new File(str));
                System.err.println("[TIF->SWF]:" + i + "->" + (j + 1));
                if (j == 0)
                    moveImageFile(localFile3, localFile1.getName(), paramString2);
                else
                    localFile3.delete();
            }
            localFile2.delete();
            localFileSeekableStream.close();
        } catch (Exception localException) {
        }
    }

    private void moveImageFile(File paramFile, String paramString1, String paramString2) {
        File localFile = new File(paramString2, paramString1.substring(0, paramString1.lastIndexOf(".")) + ".jpg");
        paramFile.renameTo(localFile);
        ConvetorTool.newInstance().convert(localFile);
    }

    private void jpg2swf(int paramInt, File paramFile1, File paramFile2)
            throws Exception {
        Runtime localRuntime = Runtime.getRuntime();
        File localFile = new File(paramFile2, paramInt + ".swf");
        String str = "jpeg2swf \"" + paramFile1.getAbsolutePath() + "\" -o \"" + localFile.getAbsolutePath() + "\"";
        Process localProcess = localRuntime.exec(str);
        localProcess.waitFor();
    }
}