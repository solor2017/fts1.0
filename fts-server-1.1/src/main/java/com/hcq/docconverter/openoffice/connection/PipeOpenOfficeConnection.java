package com.hcq.docconverter.openoffice.connection;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class PipeOpenOfficeConnection extends AbstractOpenOfficeConnection{

    public static final String DEFAULT_PIPE_NAME = "jodconverter";

    public PipeOpenOfficeConnection() {
        this("jodconverter");
    }

    public PipeOpenOfficeConnection(String pipeName) {
        super("pipe,name=" + pipeName);
    }
}
