package com.junl.dms.mvc.xunchatoulist;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.junl.dms.mvc.company.CompanyService;
import com.junl.dms.mvc.xunchatou.XunChaTou;
import com.junl.dms.mvc.xunchatou.XunChaTouService;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.constant.ConstantWebContext;
import com.platform.mvc.base.BaseController;



@Controller(controllerKey = "/jf/manage/xunChaTouList")
public class XunChaTouListController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(XunChaTouListController.class);
	
	
	/**
	 * 首页
	 */
	public void index() {
		String path = getRequest().getContextPath();
		String basePath = getRequest().getScheme()
					+ "://" + getRequest().getServerName() + ":" 
					+ getRequest().getServerPort();
		setAttr("basePath",basePath);
		String  userIds = getAttr(ConstantWebContext.request_cUserIds);
		splitPage.getQueryParam().put("createUserId", userIds);
		if((splitPage.getOrderColunm() == null || ("").equals(splitPage.getOrderColunm()))
				&& (splitPage.getOrderMode() == null || ("").equals(splitPage.getOrderMode()))) {
			splitPage.setOrderColunm("createTime");
			splitPage.setOrderMode("desc");
		}
		paging(ConstantInit.db_dataSource_main, splitPage, XunChaTouList.sqlId_splitPageSelect, XunChaTouList.sqlId_splitPageFrom);
		
		render("/manage/xunchatoulist/list.html");
	}
	
	
	
	
	
	
	/**
	 * 查看
	 */
	public void view() {
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		XunChaTou xunChaTou = XunChaTou.dao.findById(ids);
		List<Map<String, String>> list=CompanyService.service.findList();
		setAttr("xunChaTou", xunChaTou);
		setAttr("luXianId", xunChaTou.getStr("luXianId"));
		setAttr("cList", list);
		setAttr("pageNumber", pageNumber);
		render("/manage/xunchatoulist/view.html");
	}
	
	
	public void searchbinhaiinfo(){
		String ids = getPara("ids");
		List<Record> list  = XunChaTouService.service.getbinghaiinfo(ids);
		
		setAttr("list",list);
		
		
		
		render("/manage/xunchatoulist/show.html");
	}
	
	public void serachnum(){
		JSONObject jsonObject = new JSONObject();
		try{
			String ids = getPara("ids");
			List<Record> list  = XunChaTouService.service.getbinghaiinfo(ids);
			jsonObject.put("list", list);
			jsonObject.put("num", list.size());
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		renderJson(jsonObject.toJSONString());
	}  
	/**
	 * 
	 * @author wlw
	 * @date 2016年8月3日 上午11:48:02
	 * @description 导出巡查信息登记表word
	 *		TODO
	 * @throws Exception
	 *
	 */
	public void exportword(){
		JSONObject jsonObject = new JSONObject();
		try {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			XunChaTou xunChaTou = XunChaTouService.service.serachxunchatoudata(ids);
			dataMap.put("xunChaTou", xunChaTou);
			String fangxiang1ids = xunChaTou.get("xunChaFangXiang1");
			String fangxiang2ids = xunChaTou.get("xunChaFangXiang2");
			String id = xunChaTou.get("ids");
			List<Record> list1 = XunChaTouService.service.serachfangxiang1(fangxiang1ids,id);
			List<Record> list3 =  XunChaTouService.service.serachfangxiang2(fangxiang2ids,id);
			dataMap.put("xunChaFangXiang1", list1);
			dataMap.put("xunChaFangXiang2", list3);
			Calendar cal = Calendar.getInstance();
			cal.setTime(xunChaTou.getDate("xunChaStartTime"));
			dataMap.put("xunChaStartTimeyear", cal.get(Calendar.YEAR));
			dataMap.put("xunChaStartTimemonth", cal.get(Calendar.MONTH)+1);
			dataMap.put("xunChaStartTimeday", cal.get(Calendar.DAY_OF_MONTH));
			dataMap.put("xunChaStartTimehour", cal.get(Calendar.HOUR_OF_DAY));
			dataMap.put("xunChaStartTimeminute", cal.get(Calendar.MINUTE));
			Calendar c = Calendar.getInstance();
			c.setTime(xunChaTou.getDate("xunChaEndTime"));
			dataMap.put("xunChaEndTimeday", c.get(Calendar.HOUR_OF_DAY));
			dataMap.put("xunChaEndTimeminute", c.get(Calendar.MINUTE));
			dataMap.put("usehour", c.get(Calendar.HOUR_OF_DAY)-cal.get(Calendar.HOUR_OF_DAY));
			dataMap.put("useminute", c.get(Calendar.MINUTE)-cal.get(Calendar.MINUTE));
			List<Record> list = XunChaTouService.service.serachxunchainfo(ids);
			List list2 = new ArrayList();
			if(list.size()<16){
				for(int i = 0;i<16-list.size();i++){
					list2.add(0);
				}
				dataMap.put("size", list.size());
				dataMap.put("nullobject", list2);
			}
	
			dataMap.put("xunchalist", list);
		
		/*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dataMap.put("xunChaStartTime",dateFormat.format(xunChaTou.get("xunChaStartTime")));*/
//		DocumentHandler mdoc = new DocumentHandler();  
//		
//		String path = getRequest().getServletContext().getRealPath("");
//		StringBuffer path2 = new StringBuffer(path.substring(0,path.indexOf("webapps")));
//        mdoc.createDoc(dataMap, path2+"uploadword/"+xunChaTou.get("ids")+".doc");
		
			baseCreateWord(dataMap, "xunchainfo.ftl", xunChaTou.get("ids")+".doc");
			jsonObject.put("isSuccess", true);
		} catch (Exception e) {
			jsonObject.put("isSuccess", false);
		}
		renderJson(jsonObject.toString());
        
	}

}


