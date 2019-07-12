package com.hcq.fts;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class PolicyThread extends Thread {
    private Socket inSocket;
    private PrintStream ps;
    private static final String SECURITY_FILE = "<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>";

    public PolicyThread(Socket paramSocket) {
        this.inSocket = paramSocket;
    }

    public void run() {
        try {
            this.ps = new PrintStream(this.inSocket.getOutputStream());
            this.ps.print("<?xml version=\"1.0\"?><cross-domain-policy><site-control permitted-cross-domain-policies=\"all\"/><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>");
            this.ps.flush();
            this.ps.close();
            this.ps = null;
            interrupt();
            System.gc();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }
}
