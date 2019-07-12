package com.hcq.fts;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ResourceBundle;
/**
 * @Author: solor
 * @Since: 2012/3/14
 * @Description:
 */
public class FTSClient {
	private String host;
	private int port;
	private String encoding;
	
	public static final int OP_UPLOAD = 0x000F01;
	public static final int OP_DOWNLOAD = 0x000F02;
	public static final int OP_DELETE = 0x000F03;
	public static final int OP_CHECK = 0x000F04;
	public static final int OP_COPY = 0x000F05;
	public static final int OP_RENAME = 0x000F06;
	public static final int OP_MOVE = 0x000F07;
	public static final int OP_SWF = 0x000F08;
	public static final int OP_THUMB= 0x000F09;
	
	public static final int PAKER_SIZE=1024;
	public static final String LINE="\r\n";
	public static final String SPLITER="!)($#%)#(%&#)##%)_#%_#%_#((@(@)#@$$%%^^!@@+==!!!";
	
	public FTSClient() {
		Config config=new Config();
		host=config.getServer();
		port=config.getServerPort();
		encoding=config.getEncoding();
	}
	/**
	 * 提交文件到JFTS服务器
	 * @param localFileFullPath
	 * 本地文件（完整路径），例如：C:/files/myfile.doc
	 * @param remoteFileName
	 * 远程文件名称，例如：f43298dsf09fdas0fds9fa.doc
	 * @param remoteFilePath
	 * 保存到远程文件的相对路径，例如：/2012/11/
	 * @return
	 * 上传是否成功
	 */
	public boolean uploadFile(String localFileFullPath, String remoteFileName,String remoteFilePath){
		boolean result=false;
		int type=OP_UPLOAD;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			String buffer=type+LINE+remoteFilePath+LINE+remoteFileName+SPLITER;
			os.write(buffer.getBytes(encoding));
			InputStream ins=new FileInputStream(localFileFullPath);
			byte b[]=new byte[PAKER_SIZE];
			int len=0;
			while((len=ins.read(b))>0){
				os.write(b, 0, len);
			}
			os.flush();
			os.close();
			ins.close();
			socket.close();
			result=true;
		}
		catch (Exception e) {
			System.out.println("FTS远程服务器未开启");
		}
		return result;
	}
	
	/**
	 * 从JFTS服务器下载文件
	 * @param localFileFullPath
	 * 下载到本地的完整文件路径，例如：C:/myfile.doc
	 * @param remoteFileName
	 * 远程文件名称，例如：f43298dsf09fdas0fds9fa.doc
	 * @param remoteFilePath
	 * 远程文件相对路径，例如：/2012/11/
	 */
	public boolean downLoadFile(String localFileFullPath, String remoteFileName, String remoteFilePath){
		boolean result=false;
		int type=OP_DOWNLOAD;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			InputStream ins=socket.getInputStream();
			
			String buffer=type+LINE+remoteFilePath+LINE+remoteFileName+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			FileOutputStream fos=new FileOutputStream(localFileFullPath);
			byte b[]=new byte[PAKER_SIZE];
			int len=0;
			while((len=ins.read(b))>0){
				fos.write(b, 0, len);
			}
			fos.flush();
			fos.close();
			os.close();
			ins.close();
			socket.close();
			result=true;
		}
		catch (Exception e) {
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 从JFTS服务器下载文件
	 * @param remoteFileName
	 * 远程文件名称，例如：f43298dsf09fdas0fds9fa.doc
	 * @param remoteFilePath
	 * 远程文件相对路径，例如：/2012/11/
	 * @param ios
	 * 输出文件流对象
	 * @return
	 */
	public boolean downLoadFile(String remoteFileName, String remoteFilePath,OutputStream ios){
		boolean result=false;
		int type=OP_DOWNLOAD;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			InputStream ins=socket.getInputStream();
			
			String buffer=type+LINE+remoteFilePath+LINE+remoteFileName+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			byte b[]=new byte[PAKER_SIZE];
			int len=0;
			while((len=ins.read(b))>0){
				ios.write(b, 0, len);
			}
			ios.flush();
			os.close();
			ins.close();
			socket.close();
			result=true;
		}
		catch (Exception e) {
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return result;
	}
	/**
	 * 从JFTS服务器下载缩略图
	 * @param remoteFileName
	 * 远程文件名称，例如：f43298dsf09fdas0fds9fa.doc
	 * @param remoteFilePath
	 * 远程文件相对路径，例如：/2012/11/
	 * @param ios
	 * 输出文件流对象
	 * @return
	 */
	public boolean thumbFile(String remoteFileName, String remoteFilePath,OutputStream ios){
		boolean result=false;
		int type=OP_THUMB;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			InputStream ins=socket.getInputStream();
			
			String buffer=type+LINE+remoteFilePath+LINE+remoteFileName+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			byte b[]=new byte[PAKER_SIZE];
			int len=0;
			while((len=ins.read(b))>0){
				ios.write(b, 0, len);
			}
			ios.flush();
			os.close();
			ins.close();
			socket.close();
			result=true;
		}
		catch (Exception e) {
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 从JFTS服务器上删除文件
	 * @param remoteFileName
	 * 要删除的文件名称，例如：f43298dsf09fdas0fds9fa.doc
	 * @param remoteFilePath
	 * 文件相对路径，例如：/2012/11/
	 */
	public boolean deleteFileFromServer(String remoteFileName,String remoteFilePath){
		boolean result=false;
		int type=OP_DELETE;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			String buffer=type+LINE+remoteFilePath+LINE+remoteFileName+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			os.close();
			socket.close();
			result=true;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 检测文件是否存在
	 * 从JFTS服务器上删除文件
	 * @param remoteFileName
	 * 远程文件名称，例如：f43298dsf09fdas0fds9fa.doc
	 * @param remoteFilePath
	 * 远程文件相对路径，例如：/2012/11/
	 */
	public boolean fileExist(String remoteFileName,String remoteFilePath){
		boolean hasFile=true;
		int type=OP_CHECK;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			InputStream ins=socket.getInputStream();
			
			String buffer=type+LINE+remoteFilePath+LINE+remoteFileName+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			byte b[]=new byte[PAKER_SIZE];
			int len=0;
			while((len=ins.read(b))>0){
				baos.write(b, 0, len);
			}
			String returnValue=new String(baos.toByteArray(),encoding);
			if(!"yes".equalsIgnoreCase(returnValue)){
				hasFile=false;
			}
			os.close();
			ins.close();
			socket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return hasFile;
	}
	
	/**
	 * 获得远程文件列表编号
	 * @param remoteFileName
	 * 远程文件名称，例如：f43298dsf09fdas0fds9fa.doc
	 * @param remoteFilePath
	 * 远程文件相对路径，例如：/2012/11/
	 * @return
	 * 返回例如1,2,3,4,5的文件编号字符串
	 */
	public String swfFiles(String remoteFileName,String remoteFilePath){
		String result=null;
		int type=OP_SWF;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			InputStream ins=socket.getInputStream();
			
			String buffer=type+LINE+remoteFilePath+LINE+remoteFileName+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			byte b[]=new byte[PAKER_SIZE];
			int len=0;
			while((len=ins.read(b))>0){
				baos.write(b, 0, len);
			}
			result=new String(baos.toByteArray(),encoding);
			os.close();
			ins.close();
			socket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return result;
	}
	
	/**
	 * JFTS远程文件拷贝
	 * @param oldRemoteFileName
	 * 旧远程文件名
	 * @param oldRemoteFilePath
	 * 旧远程文件相对路径
	 * @param newRemoteFileName
	 * 新远程文件名
	 * @param newRemoteFilePath
	 * 新远程文件相对路径
	 */
	public boolean copyFiles(String oldRemoteFileName,String newRemoteFileName,String oldRemoteFilePath,String newRemoteFilePath){
		boolean result=false;
		int type=OP_COPY;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			String buffer=type+LINE+oldRemoteFileName+LINE+oldRemoteFilePath+LINE+newRemoteFileName+LINE+newRemoteFilePath+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			os.close();
			socket.close();
			result=true;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return result;
	}
	/**
	 * 修改远程文件名称
	 * @param oldRemoteFileName
	 * 旧远程文件名
	 * @param newRemoteFileName
	 * 新远程文件名
	 * @param remoteFilePath
	 * 文件保存相对路径
	 */
	public boolean changeFileName(String oldRemoteFileName, String newRemoteFileName, String remoteFilePath){
		boolean result=false;
		int type=OP_RENAME;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			String buffer=type+LINE+oldRemoteFileName+LINE+newRemoteFileName+LINE+remoteFilePath+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			os.close();
			socket.close();
			result=true;
		}
		catch (Exception e) {
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return result;
	}
	
	/**
	 * 移动远程JFTS服务器上的文件
	 * @param remoteFileName
	 * 要移动的远程文件名
	 * @param oldRemoteFilePath
	 * 旧文件路径
	 * @param newRemoteFilePath
	 * 新文件路径
	 */
	public boolean moveFile(String remoteFileName,String oldRemoteFilePath,String newRemoteFilePath){
		boolean result=false;
		int type=OP_MOVE;
		try{
			Socket socket=new Socket();
			SocketAddress endpoint=new InetSocketAddress(host,port);
			socket.connect(endpoint);
			OutputStream os=socket.getOutputStream();
			String buffer=type+LINE+remoteFileName+LINE+oldRemoteFilePath+LINE+newRemoteFilePath+SPLITER;
			os.write(buffer.getBytes(encoding));
			os.flush();
			os.close();
			socket.close();
			result=true;
		}
		catch (Exception e) {
			System.out.println("FTS远程服务器未开启："+e.getMessage());
		}
		return result;
	}

	
	public static class Config{
		private ResourceBundle bundle;
		public static final String JFTS_FILE="jfts-client";
		public static final String JFTS_SERVER="jfts.host";
		public static final String JFTS_PORT="jfts.port";
		public static final String JFTS_ENCODING="jfts.encoding";
		public Config() {
			bundle=ResourceBundle.getBundle(JFTS_FILE);
		}
		public String getServer(){
			return this.getKey(Config.JFTS_SERVER);
		}
		public int getServerPort(){
			return Integer.valueOf(this.getKey(Config.JFTS_PORT));
		}
		public String getEncoding(){
			return this.getKey(Config.JFTS_ENCODING);
		}
		public String getKey(String key){
			return bundle.getString(key);
		}
	}
}
