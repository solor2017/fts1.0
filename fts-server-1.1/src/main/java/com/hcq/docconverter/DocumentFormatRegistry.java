package com.hcq.docconverter;

/**
 * @Author: solor
 * @Since:
 * @Description:
 */
public abstract interface DocumentFormatRegistry {
    public abstract DocumentFormat getFormatByFileExtension(String paramString);

    public abstract DocumentFormat getFormatByMimeType(String paramString);
}