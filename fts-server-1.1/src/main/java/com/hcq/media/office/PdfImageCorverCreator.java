package com.hcq.media.office;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class PdfImageCorverCreator {

    public void createCover(String paramString1, String paramString2) {
        FileOutputStream localFileOutputStream = null;
        try {
            PDDocument localPDDocument = PDDocument.load(paramString1);
            int i = localPDDocument.getNumberOfPages();
            if (i > 0) {
                PDPage localPDPage = (PDPage) localPDDocument.getDocumentCatalog().getAllPages().get(0);
                BufferedImage localBufferedImage = localPDPage.convertToImage();
                Iterator localIterator = ImageIO.getImageWritersBySuffix("jpg");
                ImageWriter localImageWriter = (ImageWriter) localIterator.next();
                File localFile = new File(paramString2);
                localFileOutputStream = new FileOutputStream(localFile);
                ImageOutputStream localImageOutputStream = ImageIO.createImageOutputStream(localFileOutputStream);
                localImageWriter.setOutput(localImageOutputStream);
                localImageWriter.write(new IIOImage(localBufferedImage, null, null));
                localFileOutputStream.flush();
            }
            localPDDocument.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (localFileOutputStream != null)
                    localFileOutputStream.close();
                localFileOutputStream = null;
            } catch (Exception localException4) {
            }
        }
    }
}
