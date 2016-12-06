package com.junl.dms.mvc.luxian;



import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;


@Controller(controllerKey = "/jf/manage/luxian")
public class LuXianController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(LuXianController.class);
	
	
	/**
	 * 首页
	 */
	public void index() {
		paging(ConstantInit.db_dataSource_main, splitPage, LuXian.sqlId_splitPageSelect, LuXian.sqlId_splitPageFrom);
		render("/manage/luxian/list.html");
	}
	
	public void save(){
		JSONObject jsonObject=new JSONObject();
		try {
			LuXian luXian = getModel(LuXian.class);
			boolean result = LuXianService.service.save(luXian);
			String result_desc="新增失败！";
			if(result){
				result_desc="新增成功！";
			}
			jsonObject.put("result", result);
			jsonObject.put("result_desc", result_desc);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", false);
			jsonObject.put("result_desc", "操作失败！");
		}
		renderJson(jsonObject.toJSONString());
	}
	
	public void delete(){
		JSONObject jsonObject=new JSONObject();
		try {
			String ids = getPara("ids");
			boolean result = LuXianService.service.delete(ids);
			String result_desc="删除失败！";
			if(result){
				result_desc="删除成功！";
			}
			jsonObject.put("result", result);
			jsonObject.put("result_desc", result_desc);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", false);
			jsonObject.put("result_desc", "操作失败！");
		}
		renderJson(jsonObject.toJSONString());
	}
	
	public void showUpdate(){
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		LuXian luXian = LuXianService.service.findById(ids);
		setAttr("luXian", luXian);
		setAttr("pageNumber", pageNumber);
		render("/manage/luxian/update.html");
	}
	
	public void update(){
		JSONObject jsonObject=new JSONObject();
		try {
			LuXian luXian = getModel(LuXian.class);
			boolean result = LuXianService.service.update(luXian);
			String result_desc="修改失败！";
			if(result){
				result_desc="修改成功！";
			}
			jsonObject.put("result", result);
			jsonObject.put("result_desc", result_desc);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", false);
			jsonObject.put("result_desc", "操作失败！");
		}
		System.out.println("luxian!!");
		renderJson(jsonObject.toJSONString());
	}

	

}


