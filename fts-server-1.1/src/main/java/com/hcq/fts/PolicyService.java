package com.hcq.fts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class PolicyService extends Thread {
    private ServerSocket server;
    public static final int PORT = 843;

    public PolicyService() {
        try {
            this.server = new ServerSocket(843);
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true)
                handleIncoming(this.server.accept());
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    private void handleIncoming(Socket paramSocket) {
        try {
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramSocket.getInputStream()));
            char[] arrayOfChar = new char[22];
            localBufferedReader.read(arrayOfChar, 0, 22);
            StringBuffer localStringBuffer = new StringBuffer();
            for (int i = 0; i < arrayOfChar.length; i++)
                localStringBuffer.append(arrayOfChar[i]);
            String str = localStringBuffer.toString();
            if (str.indexOf("<policy-file-request/>") != -1) {
                new PolicyThread(paramSocket).start();
            } else {
                localBufferedReader.close();
                localBufferedReader = null;
                arrayOfChar = null;
                localStringBuffer = null;
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }
}