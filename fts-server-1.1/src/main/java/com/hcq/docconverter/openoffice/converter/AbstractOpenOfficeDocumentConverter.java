package com.hcq.docconverter.openoffice.converter;

import com.hcq.docconverter.DefaultDocumentFormatRegistry;
import com.hcq.docconverter.DocumentConverter;
import com.hcq.docconverter.DocumentFormat;
import com.hcq.docconverter.DocumentFormatRegistry;
import com.hcq.docconverter.openoffice.connection.OpenOfficeConnection;
import com.sun.star.beans.PropertyValue;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XRefreshable;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public abstract class AbstractOpenOfficeDocumentConverter implements DocumentConverter {

    private final Map defaultLoadProperties;
    protected OpenOfficeConnection openOfficeConnection;
    private DocumentFormatRegistry documentFormatRegistry;

    public AbstractOpenOfficeDocumentConverter(OpenOfficeConnection connection) {
        this(connection, new DefaultDocumentFormatRegistry());
    }

    public AbstractOpenOfficeDocumentConverter(OpenOfficeConnection openOfficeConnection, DocumentFormatRegistry documentFormatRegistry) {
        this.openOfficeConnection = openOfficeConnection;
        this.documentFormatRegistry = documentFormatRegistry;

        this.defaultLoadProperties = new HashMap();
        this.defaultLoadProperties.put("Hidden", Boolean.TRUE);
        this.defaultLoadProperties.put("ReadOnly", Boolean.TRUE);
    }

    public void setDefaultLoadProperty(String name, Object value) {
        this.defaultLoadProperties.put(name, value);
    }

    protected Map getDefaultLoadProperties() {
        return this.defaultLoadProperties;
    }

    protected DocumentFormatRegistry getDocumentFormatRegistry() {
        return this.documentFormatRegistry;
    }

    public void convert(File inputFile, File outputFile) {
        convert(inputFile, outputFile, null);
    }

    public void convert(File inputFile, File outputFile, DocumentFormat outputFormat) {
        convert(inputFile, null, outputFile, outputFormat);
    }

    public void convert(InputStream inputStream, DocumentFormat inputFormat, OutputStream outputStream, DocumentFormat outputFormat) {
        ensureNotNull("inputStream", inputStream);
        ensureNotNull("inputFormat", inputFormat);
        ensureNotNull("outputStream", outputStream);
        ensureNotNull("outputFormat", outputFormat);
        convertInternal(inputStream, inputFormat, outputStream, outputFormat);
    }

    public void convert(File inputFile, DocumentFormat inputFormat, File outputFile, DocumentFormat outputFormat) {
        ensureNotNull("inputFile", inputFile);
        ensureNotNull("outputFile", outputFile);

        if (!inputFile.exists()) {
            throw new IllegalArgumentException("inputFile doesn't exist: " + inputFile);
        }
        if (inputFormat == null) {
            inputFormat = guessDocumentFormat(inputFile);
        }
        if (outputFormat == null) {
            outputFormat = guessDocumentFormat(outputFile);
        }
        if (!inputFormat.isImportable()) {
            throw new IllegalArgumentException("unsupported input format: " + inputFormat.getName());
        }
        if (!inputFormat.isExportableTo(outputFormat)) {
            throw new IllegalArgumentException("unsupported conversion: from " + inputFormat.getName() + " to " + outputFormat.getName());
        }
        convertInternal(inputFile, inputFormat, outputFile, outputFormat);
    }

    protected abstract void convertInternal(InputStream paramInputStream, DocumentFormat paramDocumentFormat1, OutputStream paramOutputStream, DocumentFormat paramDocumentFormat2);

    protected abstract void convertInternal(File paramFile1, DocumentFormat paramDocumentFormat1, File paramFile2, DocumentFormat paramDocumentFormat2);

    private void ensureNotNull(String argumentName, Object argumentValue) {
        if (argumentValue == null)
            throw new IllegalArgumentException(argumentName + " is null");
    }

    private DocumentFormat guessDocumentFormat(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        DocumentFormat format = getDocumentFormatRegistry().getFormatByFileExtension(extension);
        if (format == null) {
            throw new IllegalArgumentException("unknown document format for file: " + file);
        }
        return format;
    }

    protected void refreshDocument(XComponent document) {
        XRefreshable refreshable = (XRefreshable) UnoRuntime.queryInterface(XRefreshable.class, document);
        if (refreshable != null)
            refreshable.refresh();
    }

    protected static PropertyValue property(String name, Object value) {
        PropertyValue property = new PropertyValue();
        property.Name = name;
        property.Value = value;
        return property;
    }

    protected static PropertyValue[] toPropertyValues(Map properties) {
        PropertyValue[] propertyValues = new PropertyValue[properties.size()];
        int i = 0;
        for (Iterator iter = properties.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object value = entry.getValue();
            if ((value instanceof Map)) {
                Map subProperties = (Map) value;
                value = toPropertyValues(subProperties);
            }
            propertyValues[(i++)] = property((String) entry.getKey(), value);
        }
        return propertyValues;
    }
}
