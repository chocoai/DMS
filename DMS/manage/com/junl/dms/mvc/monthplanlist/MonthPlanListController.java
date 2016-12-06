package com.junl.dms.mvc.monthplanlist;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.junl.dms.mvc.monthplan.MonthPlan;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;


@Controller(controllerKey = "/jf/manage/monthplanlist")
public class MonthPlanListController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(MonthPlanListController.class);
	
	
	/**
	 * 首页
	 */
	public void index() {
		paging(ConstantInit.db_dataSource_main, splitPage, MonthPlan.sqlId_splitPageSelect, MonthPlan.sqlId_splitPageFrom);
		String basePath = getRequest().getScheme()
				+ "://" + getRequest().getServerName() + ":" 
				+ getRequest().getServerPort();
		setAttr("basePath",basePath);
		render("/manage/monthplanlist/list.html");
	}
	
	public void exportword(){
		JSONObject json = new JSONObject();
		try {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String ids = getPara("ids");
			String riqi = getPara("riqi");
			String sql = getSql("manage.monthplan.exportinfo");
			List<Record> list1 = Db.find(sql,ids);
			dataMap.put("list", list1);
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sim=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date=null;
			date=sim.parse(riqi);
			cal.setTime(date);
			if(list1.size()<5){
				List list2 = new ArrayList();
				for(int i = 0;i<5-list1.size();i++){
					list2.add(0);
				}
				dataMap.put("size", list1.size());
				dataMap.put("nullobject", list2);
			}
			dataMap.put("year", cal.get(Calendar.YEAR));
			dataMap.put("month", cal.get(Calendar.MONTH)+1);
	//		String path = getRequest().getServletContext().getRealPath("");
	//		StringBuffer path2 = new StringBuffer(path.substring(0,path.indexOf("webapps")));
	//		ExportWord epw = new ExportWord();
	//		epw.createDoc(dataMap,  path2+"uploadword/"+ids+".doc", "yuejihualist.ftl");
		
			baseCreateWord(dataMap, "yuejihualist.ftl", "yuejihua-" + ids+".doc");
			json.put("isSuccess", true);
		} catch (Exception e) {
			json.put("isSuccess", false);
		}
		renderJson(json.toJSONString());
	}
	
	

	

}


