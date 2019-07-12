package com.hcq.docconverter.openoffice.connection;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class SocketOpenOfficeConnection extends AbstractOpenOfficeConnection{

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8100;

    public SocketOpenOfficeConnection() {
        this("localhost", 8100);
    }

    public SocketOpenOfficeConnection(int port) {
        this("localhost", port);
    }

    public SocketOpenOfficeConnection(String host, int port) {
        super("socket,host=" + host + ",port=" + port + ",tcpNoDelay=1");
    }
}
