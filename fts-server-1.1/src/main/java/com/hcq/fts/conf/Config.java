package com.hcq.fts.conf;

import java.util.ResourceBundle;

/**
 * @Author: solor
 * @Since: 1.1
 * @Description:
 */
public enum Config {

    SINGLETON_INSTANCE(ResourceBundle.getBundle("jfts-server"));

    private ResourceBundle bundle;
    public static final String JFTS_SERVER="jfts.host";
    public static final String JFTS_PORT="jfts.port";
    public static final String JFTS_WORKDIR="jfts.workdir";
    public static final String JFTS_ENCODING="jfts.encoding";
    public static final String JFTS_LOG="jfts.log";
    public static final String JFTS_THUMB_DIR = "jfts.thumb.dir";
    public static final String JFTS_THUMB_SIZE = "jfts.thumb.size";

    public String getString(String paramString)
    {
        return this.bundle.getString(paramString);
    }

    public int getInteger(String paramString)
    {
        return Integer.parseInt(this.bundle.getString(paramString));
    }

    public String getServer(){
        return this.getKey(Config.JFTS_SERVER);
    }
    public boolean showLog(){
        return "on".equalsIgnoreCase(this.getKey(JFTS_LOG));
    }
    public int getServerPort(){
        return Integer.valueOf(this.getKey(Config.JFTS_PORT));
    }
    public String getEncoding(){
        return this.getKey(Config.JFTS_ENCODING);
    }
    public String getWorkdir(){
        return this.getKey(Config.JFTS_WORKDIR);
    }
    public String getThumbdir(){
        return this.getKey(Config.JFTS_THUMB_DIR);
    }
    public String getThumbSize()
    {
        return getKey("jfts.thumb.size");
    }
    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String getKey(String key){
        return bundle.getString(key);
    }




    Config(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public static Config getInstance() {
        return SINGLETON_INSTANCE;
    }

}
