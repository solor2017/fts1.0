package com.hcq.fts;

import com.flazr.rtmp.server.RtmpServer;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public class MainServer {
    public static void runJftsServer() {
        new Thread() {
            public void run() {
                try {
                    new FTSServer().runService();
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
        }.start();
    }

    public static void runRtmpServer(final String[] args) {
        new Thread() {
            public void run() {
                try {
                    RtmpServer.main(args);
                } catch (Exception localException) {
                    localException.printStackTrace();
                }
            }
        }
                .start();
    }

    public static void main(String[] args)
            throws Exception {
        System.err.println("==============================================================");
        System.err.println("\tWelcome to to use JFTS Server V2.1");
        System.err.println("==============================================================");
        runJftsServer();
        runRtmpServer(args);
    }
}
