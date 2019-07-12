package com.hcq.docconverter.openoffice.connection;

import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.XBridge;
import com.sun.star.bridge.XBridgeFactory;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.connection.NoConnectException;
import com.sun.star.connection.XConnection;
import com.sun.star.connection.XConnector;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XEventListener;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.ucb.XFileIdentifierConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public abstract class AbstractOpenOfficeConnection implements OpenOfficeConnection, XEventListener {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private String connectionString;
    private XComponent bridgeComponent;
    private XMultiComponentFactory serviceManager;
    private XComponentContext componentContext;
    private XBridge bridge;
    private boolean connected = false;
    private boolean expectingDisconnection = false;

    protected AbstractOpenOfficeConnection(String connectionString) {
        this.connectionString = connectionString;
    }

    public synchronized void connect() throws ConnectException {
        this.logger.debug("connecting");
        try {
            XComponentContext localContext = Bootstrap.createInitialComponentContext(null);
            XMultiComponentFactory localServiceManager = localContext.getServiceManager();
            XConnector connector = (XConnector) UnoRuntime.queryInterface(XConnector.class,
                    localServiceManager.createInstanceWithContext("com.sun.star.connection.Connector", localContext));
            XConnection connection = connector.connect(this.connectionString);
            XBridgeFactory bridgeFactory = (XBridgeFactory) UnoRuntime.queryInterface(XBridgeFactory.class,
                    localServiceManager.createInstanceWithContext("com.sun.star.bridge.BridgeFactory", localContext));
            this.bridge = bridgeFactory.createBridge("", "urp", connection, null);
            this.bridgeComponent = ((XComponent) UnoRuntime.queryInterface(XComponent.class, this.bridge));
            this.bridgeComponent.addEventListener(this);
            this.serviceManager =
                    ((XMultiComponentFactory) UnoRuntime.queryInterface(XMultiComponentFactory.class,
                            this.bridge.getInstance("StarOffice.ServiceManager")));
            XPropertySet properties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, this.serviceManager);
            this.componentContext =
                    ((XComponentContext) UnoRuntime.queryInterface(XComponentContext.class,
                            properties.getPropertyValue("DefaultContext")));
            this.connected = true;

            System.err.println("[DOC->PDF]:Starting Converting....");
        } catch (NoConnectException connectException) {
            throw new ConnectException("connection failed: " + this.connectionString + ": " + connectException.getMessage());
        } catch (Exception exception) {
            throw new OpenOfficeException("connection failed: " + this.connectionString, exception);
        }
    }

    public synchronized void disconnect() {
        this.logger.debug("disconnecting");
        this.expectingDisconnection = true;
        this.bridgeComponent.dispose();
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void disposing(EventObject event) {
        this.connected = false;
        if (this.expectingDisconnection) {
            System.err.println("[DOC->PDF]:Convert Finished.");
        } else this.logger.error("disconnected unexpectedly");

        this.expectingDisconnection = false;
    }

    void simulateUnexpectedDisconnection() {
        disposing(null);
        this.bridgeComponent.dispose();
    }

    private Object getService(String className) {
        try {
            if (!this.connected) {
                this.logger.info("trying to (re)connect");
                connect();
            }
            return this.serviceManager.createInstanceWithContext(className, this.componentContext);
        } catch (Exception exception) {
            throw new OpenOfficeException("could not obtain service: " + className, exception);
        }

    }

    public XComponentLoader getDesktop() {
        return (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class,
                getService("com.sun.star.frame.Desktop"));
    }

    public XFileIdentifierConverter getFileContentProvider() {
        return (XFileIdentifierConverter) UnoRuntime.queryInterface(XFileIdentifierConverter.class,
                getService("com.sun.star.ucb.FileContentProvider"));
    }

    public XBridge getBridge() {
        return this.bridge;
    }

    public XMultiComponentFactory getRemoteServiceManager() {
        return this.serviceManager;
    }

    public XComponentContext getComponentContext() {
        return this.componentContext;
    }
}