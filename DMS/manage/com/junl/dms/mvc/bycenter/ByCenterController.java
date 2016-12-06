package com.junl.dms.mvc.bycenter;



import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;


@Controller(controllerKey = "/jf/manage/bycenter")
public class ByCenterController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(ByCenterController.class);
	
	
	/**
	 * 首页
	 */
	public void index() {
		paging(ConstantInit.db_dataSource_main, splitPage, ByCenter.sqlId_splitPageSelect, ByCenter.sqlId_splitPageFrom);
		render("/manage/bycenter/list.html");
	}
	
	public void save(){
		JSONObject jsonObject=new JSONObject();
		try {
			ByCenter bycenter = getModel(ByCenter.class);
			bycenter.set("state", "1");
			boolean result = ByCenterService.service.save(bycenter);
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
			ByCenter bycenter = ByCenterService.service.findById(ids);
			bycenter.set("state", "0");
			boolean result = ByCenterService.service.update(bycenter);
//			boolean result = ByCenterService.service.delete(ids);
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
	
	public void edit(){
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		ByCenter bycenter = ByCenterService.service.findById(ids);
		setAttr("byCenter", bycenter);
		setAttr("pageNumber", pageNumber);
		render("/manage/bycenter/update.html");
	}
	
	public void update(){
		JSONObject jsonObject=new JSONObject();
		try {
			ByCenter bycenter = getModel(ByCenter.class);
			boolean result = ByCenterService.service.update(bycenter);
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
		renderJson(jsonObject.toJSONString());
	}

	public void getList(){
		JSONObject jsonObject=new JSONObject();
		try {
			List<Map<String, String>> list=ByCenterService.service.findList();
			jsonObject.put("result_code", 1);
			jsonObject.put("result", list);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result_code", 0);
		}
		renderJson(jsonObject.toJSONString());
	}
}