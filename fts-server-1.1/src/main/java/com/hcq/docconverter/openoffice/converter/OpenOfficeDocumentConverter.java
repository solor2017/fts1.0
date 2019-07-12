package com.hcq.docconverter.openoffice.converter;

import com.hcq.docconverter.DocumentFormat;
import com.hcq.docconverter.DocumentFormatRegistry;
import com.hcq.docconverter.openoffice.connection.OpenOfficeConnection;
import com.hcq.docconverter.openoffice.connection.OpenOfficeException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.sdbc.SQLException;
import com.sun.star.sdbc.XCloseable;
import com.sun.star.task.ErrorCodeIOException;
import com.sun.star.ucb.XFileIdentifierConverter;
import com.sun.star.uno.UnoRuntime;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class OpenOfficeDocumentConverter extends AbstractOpenOfficeDocumentConverter {
    private static final Logger logger = LoggerFactory.getLogger(OpenOfficeDocumentConverter.class);

    public OpenOfficeDocumentConverter(OpenOfficeConnection connection) {
        super(connection);
    }

    public OpenOfficeDocumentConverter(OpenOfficeConnection connection, DocumentFormatRegistry formatRegistry) {
        super(connection, formatRegistry);
    }

    protected void convertInternal(InputStream inputStream, DocumentFormat inputFormat, OutputStream outputStream, DocumentFormat outputFormat) {
        File inputFile = null;
        File outputFile = null;
        try {
            inputFile = File.createTempFile("document", "." + inputFormat.getFileExtension());
            OutputStream inputFileStream = null;
            try {
                inputFileStream = new FileOutputStream(inputFile);
                IOUtils.copy(inputStream, inputFileStream);
            } finally {
                IOUtils.closeQuietly(inputFileStream);
            }

            outputFile = File.createTempFile("document", "." + outputFormat.getFileExtension());
            convert(inputFile, inputFormat, outputFile, outputFormat);
            Object outputFileStream = null;
            try {
                outputFileStream = new FileInputStream(outputFile);
                IOUtils.copy((InputStream) outputFileStream, outputStream);
            } finally {
                IOUtils.closeQuietly((InputStream) outputFileStream);
            }
        } catch (java.io.IOException ioException) {
            throw new OpenOfficeException("conversion failed", ioException);
        } finally {
            if (inputFile != null) {
                inputFile.delete();
            }
            if (outputFile != null)
                outputFile.delete();
        }
    }

    protected void convertInternal(File inputFile, DocumentFormat inputFormat, File outputFile, DocumentFormat outputFormat) {
        Map loadProperties = new HashMap();
        loadProperties.putAll(getDefaultLoadProperties());
        loadProperties.putAll(inputFormat.getImportOptions());

        Map storeProperties = outputFormat.getExportOptions(inputFormat.getFamily());

        synchronized (this.openOfficeConnection) {
            XFileIdentifierConverter fileContentProvider = this.openOfficeConnection.getFileContentProvider();
            String inputUrl = fileContentProvider.getFileURLFromSystemPath("", inputFile.getAbsolutePath());
            String outputUrl = fileContentProvider.getFileURLFromSystemPath("", outputFile.getAbsolutePath());

            loadAndExport(inputUrl, loadProperties, outputUrl, storeProperties);
        }
    }

    private void loadAndExport(String inputUrl, Map loadProperties, String outputUrl, Map storeProperties) throws OpenOfficeException {
        XComponent document = null;
        try {
            document = loadDocument(inputUrl, loadProperties);
        } catch (ErrorCodeIOException errorCodeIOException) {

            throw new OpenOfficeException("conversion failed: could not load input document; OOo errorCode: " + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (Exception otherException) {
            throw new OpenOfficeException("conversion failed: could not load input document", otherException);
        }
        if (document == null) {
            throw new OpenOfficeException("conversion failed: could not load input document");
        }

        refreshDocument(document);
        try {
            storeDocument(document, outputUrl, storeProperties);
        } catch (ErrorCodeIOException errorCodeIOException) {
            throw new OpenOfficeException("conversion failed: could not save output document; OOo errorCode: " + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (Exception otherException) {
            throw new OpenOfficeException("conversion failed: could not save output document", otherException);
        }
    }

    private XComponent loadDocument(String inputUrl, Map loadProperties) throws com.sun.star.io.IOException, IllegalArgumentException, IllegalArgumentException {
        XComponentLoader desktop = this.openOfficeConnection.getDesktop();
        return desktop.loadComponentFromURL(inputUrl, "_blank", 0, toPropertyValues(loadProperties));
    }

    private void storeDocument(XComponent document, String outputUrl, Map storeProperties)
            throws com.sun.star.io.IOException, SQLException {

        try {
            XStorable storable = (XStorable) UnoRuntime.queryInterface(XStorable.class, document);
            storable.storeToURL(outputUrl, toPropertyValues(storeProperties));
        } finally {
            XCloseable closeable = (XCloseable) UnoRuntime.queryInterface(XCloseable.class, document);
            if (closeable != null) {
                closeable.close();
            } else {
                document.dispose();
            }

        }
    }
}