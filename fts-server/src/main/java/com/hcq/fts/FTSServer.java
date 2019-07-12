package com.hcq.fts;

import com.hcq.fts.conf.Config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
/**
 * @Author: solor
 * @Since: 2012/3/14
 * @Description:
 */
public class FTSServer {
	private PrintStream ps;
	private Config config;
	
	private String host;
	private int port;
	private String encoding;
	private String workDir;
	private boolean showLog;

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
	
	public FTSServer() throws Exception{
		this.config = Config.getInstance();
		host=config.getServer();

		port=config.getServerPort();
		encoding=config.getEncoding();
		workDir=config.getWorkdir();
		showLog=config.showLog();

		ps=new PrintStream(new FileOutputStream("d:/jfts.log"));
	}
	public void setConfig(Map<String,String> config) throws Exception{
		port=Integer.parseInt(config.get("port"));
		encoding=config.get("encoding");
		workDir=config.get("workDir");
		showLog="true".equalsIgnoreCase(config.get("showLog"))?true:false;
		ps=new PrintStream(new FileOutputStream("d:/jfts.log"));
	}
	public void runService() throws UnknownHostException,IOException,ConnectException{		
		ServerSocket server=new ServerSocket();
		SocketAddress endpoint=new InetSocketAddress(port);
		//SocketAddress endpoint=new InetSocketAddress(host,port);
		server.bind(endpoint);
		

		log("\n");
		log("=====================================================================");
		log("                      欢迎使用FTS文件服务器，正在监听连接...");
		log("=====================================================================");
		while(true){
			Socket client=server.accept();//表示客户段过来的请求已经连接上
			String aa = client.getRemoteSocketAddress().toString();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = df.format(new Date());
			log(">>"+aa+"进来请求      "+time);
			this.processThread(client);
		}
	}
	private void log(String msg){
		ps.println(msg);
		ps.flush();
		if(showLog){
			System.out.println(msg);
			}
			
	}
	public void processThread(final Socket client){
		Runnable runnable=new Runnable(){
			public void run() {
				try {
					InputStream ins=client.getInputStream();
					OutputStream os=client.getOutputStream();
					ByteArrayOutputStream buf=new ByteArrayOutputStream();
					List<String> result=parseInfoType(ins, buf);
					String type=result.get(0);
					log(type);
					if(OP_UPLOAD==Integer.parseInt(type)){//上传
						processUploadFile(ins,result.get(1),result.get(2),buf);
					}
					else if(OP_DOWNLOAD==Integer.parseInt(type)){//下载
						processDownUploadFile(os,result.get(1),result.get(2));
					}
					else if(OP_DELETE==Integer.parseInt(type)){//删除
						processDeleteFile(result.get(1),result.get(2));
					}
					else if(OP_CHECK==Integer.parseInt(type)){//判断文件是否存在
						processCheckFileExist(os,result.get(1),result.get(2));
					}
					else if(OP_COPY==Integer.parseInt(type)){//拷贝文件
						processCopyFile(result.get(1),result.get(2),result.get(3),result.get(4));
					}
					else if(OP_RENAME==Integer.parseInt(type)){//重命名文件
						processRenameFile(result.get(1),result.get(2),result.get(3));
					}
					else if(OP_MOVE==Integer.parseInt(type)){//移动文件
						processMoveFile(result.get(1),result.get(2),result.get(3));
					}
					
					ins.close();
					log(">>请求结束");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread thread=new Thread(runnable);
		thread.start();
	}
	
	/**
	 * 解析操作类型
	 * @param ins
	 * 输入流
	 */
	private List<String> parseInfoType(InputStream ins,ByteArrayOutputStream buf) throws Exception{
		List<String> result=new ArrayList<String>();
		byte[] b=new byte[PAKER_SIZE];
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		int len=0;
		while((len=ins.read(b))>0){
			baos.write(b,0,len);
			byte arr[]=baos.toByteArray();
			String tmp=new String(arr,encoding);
			int pos=tmp.indexOf(SPLITER);
			if(pos>0){
				String __t=tmp.substring(0,pos);
				String[] _arr=__t.split(LINE);
				for(String s:_arr)
					result.add(s);
				if(OP_UPLOAD==Integer.parseInt(_arr[0])){
					buf.reset();
					int start=__t.getBytes(encoding).length+SPLITER.length();
					int size=arr.length-start;
					buf.write(arr,start,size);
				}
				break;
			}
		}
		baos.close();
		return result;
	}
	
	/**
	 * 拷贝本地文件
	 * @param oldLocaleFileName
	 * 旧本地文件名
	 * @param newLocaleFileName
	 * 新本地文件名
	 * @param oldLocaleFilePath
	 * 旧本地相对地址
	 * @param newLocaleFilePath
	 * 新本地相对地址
	 * @throws Exception
	 */
	private void processCopyFile(String oldLocaleFileName,String newLocaleFileName,String oldLocaleFilePath,String newLocaleFilePath) throws Exception{
		File oldFile=new File(workDir,oldLocaleFilePath);
		oldFile=new File(oldFile,oldLocaleFileName);
		if(!oldFile.exists()){
			log("文件不存在:"+oldFile.getPath());
			return ;
		}
		log("拷贝文件："+oldFile.getPath());
		File newFile=new File(workDir,newLocaleFilePath);
		if(!newFile.exists())
			newFile.mkdirs();
		FileInputStream fis=new FileInputStream(oldFile);
		newFile=new File(newFile,newLocaleFileName);
		FileOutputStream fos=new FileOutputStream(newFile);
		byte b[]=new byte[PAKER_SIZE];
		int len=0;
		while((len=fis.read(b))>0){
			fos.write(b, 0, len);
		}
		fos.close();
		fis.close();
	}
	
	/**
	 * 处理文件上传操作
	 * @param ins
	 * 文件输入流
	 * @throws Exception
	 * 2.0修改。一个请求对应一个线程
	 */
	private void processUploadFile(InputStream ins,String savePath,String localeFileName,ByteArrayOutputStream buf) throws Exception{
		File saveFile=new File(workDir,savePath);
		if(!saveFile.exists())
			saveFile.mkdirs();
		saveFile=new File(saveFile,localeFileName);
		log(">>上传文件名为"+localeFileName);
		log(">>文件保存路径:"+saveFile.getPath());
		FileOutputStream fos=new FileOutputStream(saveFile);
		fos.write(buf.toByteArray());
		byte b[]=new byte[PAKER_SIZE];
		int len=0;
		while((len=ins.read(b))>0){
			fos.write(b, 0, len);
		}
		fos.flush();
		fos.close();
		buf.close();
	}

	/**
	 * 检测文件是否在服务器上存在
	 * @param os
	 * 输出流对象
	 * @param filePath
	 * 文件保存相对路径
	 * @param localeFileName
	 * 本地文件名
	 * @throws Exception
	 */
	private void processCheckFileExist(OutputStream os,String filePath,String localeFileName) throws Exception{
		File file=new File(workDir,filePath);
		file=new File(file,localeFileName);
		log("检测文件:"+file.getPath());
		if(!file.exists()){//本地文件不存在
			log("文件不存在:"+file.getPath());
			os.write("no".getBytes(encoding));
		}
		else{
			os.write("yes".getBytes(encoding));
		}
		os.flush();
		os.close();
	}
	
	/**
	 * 删除文件操作
	 * @param filePath
	 * 文件相对路径
	 * @param localeFileName
	 * 本地文件名称
	 */
	private void processDeleteFile(String filePath,String localeFileName){
		File file=new File(workDir,filePath);
		file=new File(file,localeFileName);
		if(!file.exists()){//本地文件不存在
			log("文件不存在:"+file.getPath());
			return ;
		}
		log("删除文件:"+file.getPath());
		file.delete();
	}
	
	/**
	 * 下载文件
	 * @param os
	 * 文件输出对象
	 * @param filePath
	 * 文件相对路径
	 * @param localeFileName
	 * 本地文件名
	 * @throws Exception
	 */
	private void processDownUploadFile(OutputStream os,String filePath,String localeFileName) throws Exception{
		File file=new File(workDir,filePath);
		file=new File(file,localeFileName);
		if(!file.exists()){//本地文件不存在
			log("文件不存在:"+file.getPath());
			return ;
		}
		log("下载文件:"+file.getPath());
		FileInputStream fis=new FileInputStream(file); 
		byte[] b=new byte[PAKER_SIZE];
		int len=0;
		while((len=fis.read(b))>0){
			os.write(b,0,len);
		}
		os.flush();
		os.close();
	}
	
	/**
	 * 重命名文件
	 * @param oldLocaleFileName
	 * 旧本地文件名
	 * @param newLocaleFileName
	 * 新本地文件名
	 * @param filePath
	 * 本地文件相对路径
	 * @throws Exception
	 */
	private void processRenameFile(String oldLocaleFileName, String newLocaleFileName, String filePath) throws Exception{
		File file=new File(workDir,filePath);
		file=new File(file,oldLocaleFileName);
		if(!file.exists()){//本地文件不存在
			log("文件不存在:"+file.getPath());
			return ;
		}
		File newFile=new File(file.getParent(),newLocaleFileName);
		file.renameTo(newFile);
		log("重命名文件:"+file.getPath()+"->"+newFile.getPath());
	}
	
	/**
	 * 移动文件
	 * @param LocaleFileName
	 * 本地文件名
	 * @param oldLocaleFilePath
	 * 文件旧相对路径
	 * @param newLocaleFilePath
	 * 文件新相对路径
	 * @throws Exception
	 */
	private void processMoveFile(String LocaleFileName, String oldLocaleFilePath, String newLocaleFilePath) throws Exception{
		File file=new File(workDir,oldLocaleFilePath);
		file=new File(file,LocaleFileName);
		if(!file.exists()){//本地文件不存在
			log("文件不存在:"+file.getPath());
			return ;
		}
		File newFile=new File(workDir,newLocaleFilePath);
		if(!newFile.exists())
			newFile.mkdirs();
		newFile=new File(newFile,LocaleFileName);
		file.renameTo(newFile);
		log("移动文件:"+file.getPath()+"->"+newFile.getPath());
	}
	
	

	
	private Map<String,String> parseParams(String args[]){
		Map<String,String> config=new HashMap<String, String>();
		for(String s:args){
			if(s.trim().length()>1){
				s=s.trim();
				if(s.startsWith("--")){
					s=s.substring(2);
					String p[]=s.split("=");
					if(p.length==2){
						config.put(p[0], p[1]);
					}
				}
			}
		}
		return config;
	}
	
	public static void main(String[] args) throws Exception{
		InetAddress addr = InetAddress.getLocalHost();
		String ip=addr.getHostAddress().toString();//获得本机IP　　
		String address=addr.getHostName().toString();//获得本机名称
		System.out.println(ip+"----"+address);
//		if(args.length>0){
			FTSServer server=new FTSServer();
//			server.setConfig(server.parseParams(args));
			server.runService();
//		}
	}
}
