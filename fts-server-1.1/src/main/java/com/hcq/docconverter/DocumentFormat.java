package com.hcq.docconverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: solor
 * @Since:
 * @Description:
 */
public class DocumentFormat
{
    private static final String FILTER_NAME = "FilterName";
    private String name;
    private DocumentFamily family;
    private String mimeType;
    private String fileExtension;
    private Map exportOptions = new HashMap();
    private Map importOptions = new HashMap();

    public DocumentFormat()
    {
    }

    public DocumentFormat(String name, String mimeType, String extension) {
        this.name = name;
        this.mimeType = mimeType;
        this.fileExtension = extension;
    }

    public DocumentFormat(String name, DocumentFamily family, String mimeType, String extension) {
        this.name = name;
        this.family = family;
        this.mimeType = mimeType;
        this.fileExtension = extension;
    }

    public String getName() {
        return this.name;
    }

    public DocumentFamily getFamily() {
        return this.family;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    private String getExportFilter(DocumentFamily family) {
        return (String)getExportOptions(family).get("FilterName");
    }

    public boolean isImportable() {
        return this.family != null;
    }

    public boolean isExportOnly() {
        return !isImportable();
    }

    public boolean isExportableTo(DocumentFormat otherFormat) {
        return otherFormat.isExportableFrom(this.family);
    }

    public boolean isExportableFrom(DocumentFamily family) {
        return getExportFilter(family) != null;
    }

    public void setExportFilter(DocumentFamily family, String filter) {
        getExportOptions(family).put("FilterName", filter);
    }

    public void setExportOption(DocumentFamily family, String name, Object value) {
        Map options = (Map)this.exportOptions.get(family);
        if (options == null) {
            options = new HashMap();
            this.exportOptions.put(family, options);
        }
        options.put(name, value);
    }

    public Map getExportOptions(DocumentFamily family) {
        Map options = (Map)this.exportOptions.get(family);
        if (options == null) {
            options = new HashMap();
            this.exportOptions.put(family, options);
        }
        return options;
    }

    public void setImportOption(String name, Object value) {
        this.importOptions.put(name, value);
    }

    public Map getImportOptions() {
        if (this.importOptions != null) {
            return this.importOptions;
        }
        return Collections.EMPTY_MAP;
    }
}