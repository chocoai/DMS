package com.junl.dms.mvc.baoyan;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.junl.dms.mvc.bycenter.ByCenterService;
import com.junl.dms.mvc.rwtask.RwTaskService;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;


@Controller(controllerKey = "/jf/manage/baoyan")
public class BaoYanController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(BaoYanController.class);
	
	
	/**
	 * 首页
	 */
	public void index() {
		if((splitPage.getOrderColunm() == null || ("").equals(splitPage.getOrderColunm()))
				&& (splitPage.getOrderMode() == null || ("").equals(splitPage.getOrderMode()))) {
			splitPage.setOrderColunm("createTime");
			splitPage.setOrderMode("desc");
		}
		paging(ConstantInit.db_dataSource_main, splitPage, BaoYan.sqlId_splitPageSelect, BaoYan.sqlId_splitPageFrom);
		render("/manage/baoyan/list.html");
	}
	
	public void add() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		//生成任务单号
		int byNum=BaoYanService.service.getTaskNo();
		String byNum_str=RwTaskService.service.intToStrong(byNum);
		setAttr("byPre", "XZYHWX");
		setAttr("byNum", byNum);
		setAttr("byNo", "XZYHWX"+formatter.format(currentTime)+"-"+byNum_str);
		List<Map<String, String>> byList=ByCenterService.service.findList();
		setAttr("byList", byList);
		Integer pageNumber = getParaToInt("pageNumber");
		setAttr("pageNumber", pageNumber);
		render("/manage/baoyan/add.html");
	}
	
	public void save(){
		JSONObject jsonObject=new JSONObject();
		try {
			//图片数组
			String[] paras = getParas("uploadImgs");
			//获取任务编号
			String choose_checkBox=getPara("taskIds_h");
			BaoYan baoyan = getModel(BaoYan.class);
			boolean result = BaoYanService.service.save(baoyan,paras,choose_checkBox);
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
			String imgs = BaoYanService.service.findById(ids).getStr("imgs");
			boolean result = BaoYanService.service.delete(ids);
			String result_desc="删除失败！";
			if(result){
				result_desc="删除成功！";
				deleteImages(imgs);
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
	
	
	
	
	public void deleteTaskRelateInUpdate(){
		JSONObject jsonObject=new JSONObject();
		try {
			String taskRelateIds = getPara("taskRelateIds");
			String baoYanIds = getPara("baoYanIds");
			boolean result = BaoYanService.service.deleteTaskRelateInUpdate(taskRelateIds, baoYanIds);
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
	
	
	
	public void showBaoYan(){
		String ids = getPara("ids");
		List<Record> byrList=BaoYanService.service.showBaoYan(ids);
		int res=0;
		if (byrList!=null) {
			if (byrList.size()>0) {
				setAttr("byrListSize", byrList.size());
			}
		}else{
			setAttr("byrListSize", res);
		}
		
		setAttr("byrList", byrList);
		render("/manage/baoyan/showBaoYan.html");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void edit(){
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		BaoYan baoyan = BaoYanService.service.findById(ids);
		
//		List<Record> byrList=BaoYanService.service.select_DMS_BY_weiXiuBaoYanF_ByIds(ids);
		List<Record> byrList = BaoYanService.service.getweixiuIDS(ids);
		int res=0;
		if (byrList!=null) {
			if (byrList.size()>0) {
				setAttr("byrListSize", byrList.size());
			}
		}else{
			setAttr("byrListSize", res);
		}
		String taskIds="";
		for (int i = 0; i < byrList.size(); i++) {
			Record r =byrList.get(i);
			taskIds=taskIds+"'"+r.getStr("ids")+"',";
		}
		taskIds=taskIds.substring(0, taskIds.length()-1);
		setAttr("taskIds", taskIds);
		setAttr("byrList", byrList);
		setAttr("baoYan", baoyan);
		setAttr("pageNumber", pageNumber);
		List<Map<String, String>> byList=ByCenterService.service.findList();
		setAttr("byList", byList);
		render("/manage/baoyan/update.html");
	}
	
	public void update(){
		JSONObject jsonObject=new JSONObject();
		try {
			String[] paras = getParas("uploadImgs");
			BaoYan baoyan = getModel(BaoYan.class);
			//获取任务编号
			String choose_checkBox=getPara("taskIds_h");
			boolean result = BaoYanService.service.update(baoyan,paras,choose_checkBox);
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
	public void view(){
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		BaoYan baoyan = BaoYanService.service.findById(ids);
		List<Record> byrList = BaoYanService.service.getweixiuIDS(ids);
		int res=0;
		if (byrList!=null) {
			if (byrList.size()>0) {
				setAttr("byrListSize", byrList.size());
			}
		}else{
			setAttr("byrListSize", res);
		}
		
		setAttr("byrList", byrList);
		setAttr("baoYan", baoyan);
		setAttr("pageNumber", pageNumber);
		List<Map<String, String>> byList=ByCenterService.service.findList();
		setAttr("byList", byList);
		render("/manage/baoyan/view.html");
	}
}