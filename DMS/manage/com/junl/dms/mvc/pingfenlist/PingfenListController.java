package com.junl.dms.mvc.pingfenlist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.junl.dms.tools.WordUtil;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;

@Controller(controllerKey = "/jf/manage/pingfenlist")
public class PingfenListController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(PingfenListController.class);

	/**
	 * 首页
	 */
//	public void index() {
//		paging(ConstantInit.db_dataSource_main, splitPage,
//				Pingfen.sqlId_splitPageSelect, Pingfen.sqlId_splitPageFrom);
//		String basePath = getRequest().getScheme() + "://" + getRequest().getServerName() + ":"  + getRequest().getServerPort();
//		setAttr("basePath",basePath);
//		render("/manage/pingfenlist/list.html");
//	}

	public void index() {
		paging(ConstantInit.db_dataSource_main, splitPage,
				Pingfen.sqlId_splitPageSelect, Pingfen.sqlId_splitPageFrom);
		String basePath = getRequest().getScheme() + "://" + getRequest().getServerName() + ":"  + getRequest().getServerPort();
		setAttr("basePath",basePath);
		render("/manage/pingfenlist/list.html");
	}

	public void view() {
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		Pingfen pingfen = PingfenService.service.findById(ids);
		
		Map<String,Object> m = new HashMap<>();
		
		Object endzhk = pingfen.get("endzhk");
		Object endzhm = pingfen.get("endzhm");
		Object startzhk = pingfen.get("startzhk");
		Object startzhm = pingfen.get("startzhm");
		String duanluo="K"+endzhk+"+"+endzhm;
		Date shijian = pingfen.getDate("tianxietime");
		if(null!=startzhk){
			duanluo += " ~ K"+startzhk+"+"+startzhm;
		}
		m.put("duanluo", duanluo);
		m.put("shijian", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(shijian));
		m.put("o", pingfen);
		try {
			WordUtil.create(m, "pingfen.ftl", "E:/0321000.doc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		setAttr("pingfen", pingfen);
		setAttr("pageNumber", pageNumber);
		render("/manage/pingfen/view.html");
	}
	
	public void daochu(){
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		Pingfen pingfen = PingfenService.service.findById(ids);
		
		Map<String,Object> m = new HashMap<>();
		
		Object endzhk = pingfen.get("endzhk");
		Object endzhm = pingfen.get("endzhm");
		Object startzhk = pingfen.get("startzhk");
		Object startzhm = pingfen.get("startzhm");
		String duanluo="K"+endzhk+"+"+endzhm;
		Date shijian = pingfen.getDate("tianxietime");
		if(null!=startzhk){
			duanluo += " ~ K"+startzhk+"+"+startzhm;
		}
		m.put("duanluo", duanluo);
		m.put("shijian", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(shijian));
		
        org.json.JSONObject jsonObj = new org.json.JSONObject(pingfen.toJson() );
        Iterator<?> it = jsonObj.keys();  
        while (it.hasNext()) {  
			String key = (String)it.next();  
			Object value = jsonObj.get(key);
			//生成Word时候，不能为空
			pingfen.set(key, "null".equals(value)?"":value);
        }
		m.put("o", pingfen);
		
//		String fileName = ids + ".doc";
//		String path = getRequest().getSession().getServletContext().getRealPath("");
//		fileName = path.substring(0,path.length()-11) + "uploadword\\" + "pingfen-" + fileName;
//		WordUtil.create(m , "pingfen.ftl", fileName );
		JSONObject jsonObject=new JSONObject();
		try {
			baseCreateWord(m, "pingfen.ftl", "pingfen-" + ids+".doc");
			jsonObject.put("isSuccess", true);
		} catch (Exception e) {
			jsonObject.put("isSuccess", false);
		}
		renderJson(jsonObject.toJSONString());
//		setAttr("pingfen", pingfen);
//		setAttr("pageNumber", pageNumber);
//		render("/manage/pingfenlist/list.html");
	}

}
