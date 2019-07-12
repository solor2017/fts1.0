package com.hcq.docconverter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public abstract interface DocumentConverter {
    public abstract void convert(InputStream paramInputStream, DocumentFormat paramDocumentFormat1, OutputStream paramOutputStream, DocumentFormat paramDocumentFormat2);

    public abstract void convert(File paramFile1, DocumentFormat paramDocumentFormat1, File paramFile2, DocumentFormat paramDocumentFormat2);

    public abstract void convert(File paramFile1, File paramFile2, DocumentFormat paramDocumentFormat);

    public abstract void convert(File paramFile1, File paramFile2);
}
