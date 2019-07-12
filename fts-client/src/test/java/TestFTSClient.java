import com.hcq.fts.FTSClient;

public class TestFTSClient {
	public static void main(String[] args) throws Exception {
		FTSClient app=new FTSClient();
		app.uploadFile("f:/笔记/lucene.md","5435jkj2323.md","2012/05/16");
		//app.downLoadFile("c:/tmp/ddddd.exe","mysql-5.5.23-win32.zip","2012/05/07");
		//app.deleteFileFromServer("mysql-5.5.23-win32.zip","2012/05/07");
		//System.out.println(app.fileExist("啊啊啊啊1.exe","2012/05/07"));
		//app.copyFiles("啊啊啊啊1.exe", "区区.exe.", "2012/05/07", "2012/05/06");
		//app.changeFileName("啊啊啊啊1.exe", "AdbeRdr920_zh_CN.exe", "2012/05/07");
		//app.moveFile("AdbeRdr920_zh_CN.exe", "2012/05/07", "2012/05/08");
	}

}
