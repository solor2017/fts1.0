package com.hcq.docconverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: solor
 * @Since:
 * @Description:
 */
public class BasicDocumentFormatRegistry implements DocumentFormatRegistry {
    private List documentFormats = new ArrayList();

    public void addDocumentFormat(DocumentFormat documentFormat) {
        this.documentFormats.add(documentFormat);
    }

    protected List getDocumentFormats() {
        return this.documentFormats;
    }

    public DocumentFormat getFormatByFileExtension(String extension) {
        if (extension == null) {
            return null;
        }
        String lowerExtension = extension.toLowerCase();
        for (Iterator it = this.documentFormats.iterator(); it.hasNext(); ) {
            DocumentFormat format = (DocumentFormat) it.next();
            if (format.getFileExtension().equals(lowerExtension)) {
                return format;
            }
        }
        return null;
    }

    public DocumentFormat getFormatByMimeType(String mimeType) {
        for (Iterator it = this.documentFormats.iterator(); it.hasNext(); ) {
            DocumentFormat format = (DocumentFormat) it.next();
            if (format.getMimeType().equals(mimeType)) {
                return format;
            }
        }
        return null;
    }
}