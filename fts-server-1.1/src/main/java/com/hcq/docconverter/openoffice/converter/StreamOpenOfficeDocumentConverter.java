package com.hcq.docconverter.openoffice.converter;

import com.hcq.docconverter.DocumentFormat;
import com.hcq.docconverter.DocumentFormatRegistry;
import com.hcq.docconverter.openoffice.connection.OpenOfficeConnection;
import com.hcq.docconverter.openoffice.connection.OpenOfficeException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lib.uno.adapter.ByteArrayToXInputStreamAdapter;
import com.sun.star.lib.uno.adapter.OutputStreamToXOutputStreamAdapter;
import com.sun.star.uno.UnoRuntime;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class StreamOpenOfficeDocumentConverter extends AbstractOpenOfficeDocumentConverter {
    public StreamOpenOfficeDocumentConverter(OpenOfficeConnection connection) {
        super(connection);
    }

    public StreamOpenOfficeDocumentConverter(OpenOfficeConnection connection, DocumentFormatRegistry formatRegistry) {
        super(connection, formatRegistry);
    }

    protected void convertInternal(File inputFile, DocumentFormat inputFormat, File outputFile, DocumentFormat outputFormat) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
            outputStream = new FileOutputStream(outputFile);
            convert(inputStream, inputFormat, outputStream, outputFormat);
        } catch (FileNotFoundException fileNotFoundException) {
            throw new IllegalArgumentException(fileNotFoundException.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

    protected void convertInternal(InputStream inputStream, DocumentFormat inputFormat, OutputStream outputStream, DocumentFormat outputFormat) {
        Map exportOptions = outputFormat.getExportOptions(inputFormat.getFamily());
        try {
            synchronized (this.openOfficeConnection) {
                loadAndExport(inputStream, inputFormat.getImportOptions(), outputStream, exportOptions);
            }
        } catch (OpenOfficeException openOfficeException) {
            throw openOfficeException;
        } catch (Throwable throwable) {
            throw new OpenOfficeException("conversion failed", throwable);
        }
    }

    private void loadAndExport(InputStream inputStream, Map importOptions, OutputStream outputStream, Map exportOptions) throws Exception {
        XComponentLoader desktop = this.openOfficeConnection.getDesktop();

        Map loadProperties = new HashMap();
        loadProperties.putAll(getDefaultLoadProperties());
        loadProperties.putAll(importOptions);

        loadProperties.put("InputStream", new ByteArrayToXInputStreamAdapter(IOUtils.toByteArray(inputStream)));

        XComponent document = desktop.loadComponentFromURL("private:stream", "_blank", 0, toPropertyValues(loadProperties));
        if (document == null) {
            throw new OpenOfficeException("conversion failed: input document is null after loading");
        }

        refreshDocument(document);

        Map storeProperties = new HashMap();
        storeProperties.putAll(exportOptions);
        storeProperties.put("OutputStream", new OutputStreamToXOutputStreamAdapter(outputStream));
        try {
            XStorable storable = (XStorable) UnoRuntime.queryInterface(XStorable.class, document);
            storable.storeToURL("private:stream", toPropertyValues(storeProperties));
        } finally {
            document.dispose();
        }
    }
}