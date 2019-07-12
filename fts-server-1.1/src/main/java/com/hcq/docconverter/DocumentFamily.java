package com.hcq.docconverter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class DocumentFamily
{
    public static final DocumentFamily TEXT = new DocumentFamily("Text");
    public static final DocumentFamily SPREADSHEET = new DocumentFamily("Spreadsheet");
    public static final DocumentFamily PRESENTATION = new DocumentFamily("Presentation");
    public static final DocumentFamily DRAWING = new DocumentFamily("Drawing");

    private static Map FAMILIES = new HashMap();
    private String name;

    static
    {
        FAMILIES.put(TEXT.name, TEXT);
        FAMILIES.put(SPREADSHEET.name, SPREADSHEET);
        FAMILIES.put(PRESENTATION.name, PRESENTATION);
        FAMILIES.put(DRAWING.name, DRAWING);
    }

    private DocumentFamily(String name)
    {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static DocumentFamily getFamily(String name) {
        DocumentFamily family = (DocumentFamily)FAMILIES.get(name);
        if (family == null) {
            throw new IllegalArgumentException("invalid DocumentFamily: " + name);
        }
        return family;
    }
}