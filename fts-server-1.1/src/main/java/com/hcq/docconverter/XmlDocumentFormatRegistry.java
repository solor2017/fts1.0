package com.hcq.docconverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Iterator;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class XmlDocumentFormatRegistry extends BasicDocumentFormatRegistry
        implements DocumentFormatRegistry {
    private static final String DEFAULT_CONFIGURATION = "/" + XmlDocumentFormatRegistry.class.getPackage().getName().replace('.', '/') +
            "/document-formats.xml";

    /**
     * @deprecated
     */
    public XmlDocumentFormatRegistry() {
        load(getClass().getResourceAsStream(DEFAULT_CONFIGURATION));
    }

    public XmlDocumentFormatRegistry(InputStream inputStream) {
        load(inputStream);
    }

    private void load(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("inputStream is null");
        }
        XStream xstream = createXStream();
        try {
            ObjectInputStream in = xstream.createObjectInputStream(new InputStreamReader(inputStream));
            try {
                while (true)
                    addDocumentFormat((DocumentFormat) in.readObject());
            } catch (EOFException localEOFException) {
            }
        } catch (Exception exception) {
            throw new RuntimeException("invalid registry configuration", exception);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static XStream createXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.setMode(1001);
        xstream.alias("document-format", DocumentFormat.class);
        xstream.aliasField("mime-type", DocumentFormat.class, "mimeType");
        xstream.aliasField("file-extension", DocumentFormat.class, "fileExtension");
        xstream.aliasField("export-filters", DocumentFormat.class, "exportFilters");
        xstream.aliasField("export-options", DocumentFormat.class, "exportOptions");
        xstream.aliasField("import-options", DocumentFormat.class, "importOptions");
        xstream.alias("family", DocumentFamily.class);
        xstream.registerConverter(new AbstractSingleValueConverter() {
            public boolean canConvert(Class type) {
                return type.equals(DocumentFamily.class);
            }

            public Object fromString(String name) {
                return DocumentFamily.getFamily(name);
            }

            public String toString(Object object) {
                DocumentFamily family = (DocumentFamily) object;
                return family.getName();
            }
        });
        return xstream;
    }

    public static void main(String[] args)throws IOException {
        DefaultDocumentFormatRegistry registry = new DefaultDocumentFormatRegistry();
        XStream xstream = createXStream();
        ObjectOutputStream outputStream = xstream.createObjectOutputStream(
                new OutputStreamWriter(System.out), "document-formats");
        for (Iterator iterator = registry.getDocumentFormats().iterator(); iterator.hasNext(); ) {
            outputStream.writeObject(iterator.next());
        }
        outputStream.close();
    }
}