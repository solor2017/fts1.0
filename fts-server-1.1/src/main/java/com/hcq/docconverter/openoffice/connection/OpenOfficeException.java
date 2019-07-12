package com.hcq.docconverter.openoffice.connection;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class OpenOfficeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OpenOfficeException(String message) {
        super(message);
    }

    public OpenOfficeException(String message, Throwable cause) {
        super(message, cause);
    }
}
