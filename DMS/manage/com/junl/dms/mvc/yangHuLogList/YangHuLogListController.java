package com.junl.dms.mvc.yangHuLogList;



import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.junl.dms.mvc.yangHuLog.YangHuLog;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;


@Controller(controllerKey = "/jf/manage/yanghuloglist")
public class YangHuLogListController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(YangHuLogListController.class);
	
	
	/**
	 * 首页
	 */
	public void index() {
		paging(ConstantInit.db_dataSource_main, splitPage, YangHuLog.sqlId_splitPageSelect, YangHuLog.sqlId_splitPageFrom);
		String basePath = getRequest().getScheme()
				+ "://" + getRequest().getServerName() + ":" 
				+ getRequest().getServerPort();
		setAttr("basePath",basePath);
		render("/manage/yanghuloglist/list.html");
	}
	
	public void exportword(){
		JSONObject jsonObject = new JSONObject();
		try {
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String ids = getPara("ids");
			YangHuLog yangHuLog = YangHuLog.dao.findById(ids);
			Calendar cal = Calendar.getInstance();
			cal.setTime(yangHuLog.getDate("yangHuTime"));
			dataMap.put("year", cal.get(Calendar.YEAR));
			dataMap.put("month", cal.get(Calendar.MONTH));
			dataMap.put("day", cal.get(Calendar.DAY_OF_MONTH));
			dataMap.put("yangHuLog", yangHuLog);
	//		ExportWord exp = new ExportWord();
	//		String path = getRequest().getServletContext().getRealPath("");
	//		StringBuffer path2 = new StringBuffer(path.substring(0,path.indexOf("webapps")));
	//		exp.createDoc(dataMap, path2+"uploadword/"+ids+".doc", "loglist.ftl");
			
			baseCreateWord(dataMap, "loglist.ftl", ids+".doc");
			jsonObject.put("isSuccess", true);
		} catch (Exception e) {
			jsonObject.put("isSuccess", false);
		}
		renderJson(jsonObject.toString());
	}
	

	

}


