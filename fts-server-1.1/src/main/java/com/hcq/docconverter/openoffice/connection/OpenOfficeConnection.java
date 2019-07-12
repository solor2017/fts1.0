package com.hcq.docconverter.openoffice.connection;

import com.sun.star.bridge.XBridge;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.ucb.XFileIdentifierConverter;
import com.sun.star.uno.XComponentContext;

import java.net.ConnectException;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public interface OpenOfficeConnection {

    public abstract void connect()throws ConnectException;

    public abstract void disconnect();

    public abstract boolean isConnected();

    public abstract XComponentLoader getDesktop();

    public abstract XFileIdentifierConverter getFileContentProvider();

    public abstract XBridge getBridge();

    public abstract XMultiComponentFactory getRemoteServiceManager();

    public abstract XComponentContext getComponentContext();
}
