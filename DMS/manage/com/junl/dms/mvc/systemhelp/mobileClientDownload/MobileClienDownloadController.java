package com.junl.dms.mvc.systemhelp.mobileClientDownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.DATE;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.junl.dms.mvc.base.BaseMethodService;
import com.junl.dms.mvc.base.QrCodeToImageWriter;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;
import com.platform.mvc.base.BaseService;

@Controller(controllerKey = "/jf/manage/systemhelp/mobileClientDownload")
public class MobileClienDownloadController extends BaseController {

	private static Logger log = Logger.getLogger(MobileClienDownloadController.class);
	public void index()
	{
		render("/manage/systemhelp/mobileClientDownload/index.html");
	}
	
	
	public void getList()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		String sql = " select * from DMS_APP_downloadApp ";
		List<Record> recordList = Db.find(sql);
		if(recordList != null && recordList.size() > 0)
		{
			for (int i = 0; i < recordList.size(); i++) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("appName", recordList.get(i).get("appName")==null?"":recordList.get(i).get("appName").toString());
				map.put("versions", recordList.get(i).get("versions")==null?"":recordList.get(i).get("versions").toString());
				map.put("updateContent", recordList.get(i).get("updateContent")==null?"":recordList.get(i).get("updateContent").toString());
				map.put("createDate", recordList.get(i).get("createDate")==null?"":sdf.format(recordList.get(i).get("createDate")));
				map.put("uploadPath", recordList.get(i).get("uploadPath")==null?"":recordList.get(i).get("uploadPath").toString());
				map.put("ids", recordList.get(i).get("ids")==null?"":recordList.get(i).get("ids").toString());
				map.put("qrCodeHttpPath", recordList.get(i).get("qrCodeHttpPath")==null?"":recordList.get(i).get("qrCodeHttpPath").toString());
				resultList.add(map);
			}
		}
		setAttr("resultList", resultList);
		render("/manage/systemhelp/mobileClientDownload/list.html");
	}
	
	public void update()
	{
		String virtualPath = PropKit.get("tomcat.appPath");;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject json = new JSONObject();
		boolean isSuccess = false;
		try {
			//获取上传的文件
			UploadFile upFile = getFile();
			final File uploadFile = upFile.getFile();
			DownloadApp downloadApp = getModel(DownloadApp.class);
			//tomcat的upload文件夹
			String loadpath = getRequest().getSession().getServletContext().getRealPath("/");
			final String path = new File(loadpath).getParentFile().getParentFile().getAbsolutePath()+File.separator+virtualPath;
			final String fileName = downloadApp.get("appName")+".apk";
			//获取http路径
			String server = getCxt();
			String httUrlSever = server.substring(0,server.lastIndexOf("/")+1);
			final String httpUrl = httUrlSever+virtualPath+"/"+fileName;
			final String imageName = "qrCode.png";
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						BaseMethodService.service.copyInputStreamToFile(path, fileName, new FileInputStream(uploadFile));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			//生成二维码
			QrCodeToImageWriter.generatorQRCodeImage(new File(path+"/"+imageName), httpUrl, 200, 200);
			downloadApp.set("uploadPath", httUrlSever+virtualPath+"/"+fileName);
			downloadApp.set("qrCodeHttpPath", httUrlSever+virtualPath+"/"+imageName);
			Date nowDate = new Date();
			String date = sdf.format(nowDate);
			downloadApp.set("createDate", date);
			isSuccess = downloadApp.update();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("手机客户端下载修改出错");
			isSuccess = false;
		}
		json.put("isSuccess", isSuccess);
		renderJson(json.toJSONString());
	}
	
	
	
	public void getDownloadAppById()
	{
		try {
			String ids = getPara("ids");
			DownloadApp downloadApp = new DownloadApp();
			downloadApp = downloadApp.findById(ids);
			setAttr("downloadApp", downloadApp);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("手机客户端下载根据Id获取信息出错");
		}
		render("/manage/systemhelp/mobileClientDownload/update.html");
	}
	
}
