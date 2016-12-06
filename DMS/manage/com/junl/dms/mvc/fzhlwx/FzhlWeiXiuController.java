package com.junl.dms.mvc.fzhlwx;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.junl.dms.mvc.fangzhl.FangZHLService;
import com.junl.dms.tools.ToolsFile;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.constant.ConstantWebContext;
import com.platform.mvc.base.BaseController;
import com.platform.mvc.base.BaseService;
import com.platform.tools.StringUtils;
@Controller(controllerKey = "/jf/manage/fzhl")
public class FzhlWeiXiuController extends BaseController {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(FzhlWeiXiuController.class);
	
	
	public void index() {
		String  userIds = getAttr(ConstantWebContext.request_cUserIds);
		//当用户的权限中有超级管理员的权限，则将查询条件中的用户的userIds设置为管理员的ids
		String  roles = getCUser().getGroupids();
		//8a40c0353fa828a6013fa898d4ac0023 		role权限ids
		//03a44ba0aa4e4905bea726d4da976ba5		user管理员的ids
		if (roles.indexOf("8a40c0353fa828a6013fa898d4ac0023")>=0) {
			userIds="03a44ba0aa4e4905bea726d4da976ba5";
		}else{
			//根据用户ID查询该用户所在路段权限
			String sql=BaseService.service.getZHbyUserId(userIds,"startZHK","startZHM");
			splitPage.getQueryParam().put("sql", sql);
		}
		splitPage.getQueryParam().put("createUserId", userIds);
		if((splitPage.getOrderColunm() == null || ("").equals(splitPage.getOrderColunm()))
				&& (splitPage.getOrderMode() == null || ("").equals(splitPage.getOrderMode()))) {
			splitPage.setOrderColunm("createTime");
			splitPage.setOrderMode("desc");
		}
		paging(ConstantInit.db_dataSource_main, splitPage, FzhlWeiXiu.sqlId_splitPageSelect, FzhlWeiXiu.sqlId_splitPageFrom);
		render("/manage/fzhl/list.html");
	}
	
	
	public void save(){
		JSONObject jsonObject=new JSONObject();
		try{
			FzhlWeiXiu fzhl = getModel(FzhlWeiXiu.class);
			String  userIds = getAttr(ConstantWebContext.request_cUserIds);
			int zhs=fzhl.getInt("startZHK")*1000+fzhl.getInt("startZHM");
			int zhe=0;
			if (StringUtils.isEmpty(StringUtils.changNull(fzhl.get("endZHK")))||StringUtils.isEmpty(StringUtils.changNull(fzhl.get("endZHM")))) {
				zhe=zhs;
			}else{
				 zhe=fzhl.getInt("endZHK")*1000+fzhl.getInt("endZHM");
			}
			//判断桩号是否符合范围
			if (BaseService.service.judgeZHScope(BaseService.service.getZHFanWeibyUserId(userIds),zhs,zhe)) {
				//图片数组
				String[] paras = getParas("uploadImgs");
				List<FzhlWeiXiuRelate> fzhlRelateList = getModels(FzhlWeiXiuRelate.class);
				fzhl.set("createUserId",getAttr(ConstantWebContext.request_cUserIds));
				boolean result = FzhlWeiXiuService.service.save(fzhl,fzhlRelateList,paras);			
				String result_desc="新增失败！";
				if(result){
					result_desc="新增成功！";
				}
				jsonObject.put("result", result);
				jsonObject.put("result_desc", result_desc);
			}else{
				jsonObject.put("result", false);
				jsonObject.put("result_desc", "新增失败！输入的桩号不在范围内");
			}
		}catch(Exception e){
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
			String imgs = FzhlWeiXiuService.service.findById(ids).getStr("imgs");
			boolean result = FzhlWeiXiuService.service.delete_DMS_WX_fzhlWeiXiu_item_relate(ids);
			boolean result1 = FzhlWeiXiuService.service.delete_DMS_WX_fzhlWeiXiu(getRequest(),ids);
			String result_desc="删除失败！";
			if(result && result1){
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
	
	public void showUpdate(){
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		FzhlWeiXiu fzhlWeiXiu=FzhlWeiXiuService.service.findById(ids);
		List<Map<String, Object>> fzhlList = FangZHLService.service.findList();
		setAttr("fzhlList", fzhlList);
		List<Record> fzhlRelateList  = FzhlWeiXiuService.service.selectDMS_WX_fzhlWeiXiu_item_relateFindById(ids);
		setAttr("list",fzhlRelateList);
		setAttr("listSize",fzhlRelateList.size());
		setAttr("ids",ids);
		setAttr("fzhlWeiXiu", fzhlWeiXiu);
		setAttr("pageNumber", pageNumber);
		render("/manage/fzhl/update.html");
	}
	
	public void view(){
		String ids = getPara("ids");
		String weiXiuId = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		FzhlWeiXiu fzhlWeiXiu=FzhlWeiXiuService.service.findById(ids);
		
		List<Record> fzhlRelateList  = FzhlWeiXiuService.service.selectDMS_WX_fzhlWeiXiu_item_relateFindById(weiXiuId);
		
		setAttr("list",fzhlRelateList);
		setAttr("listSize",fzhlRelateList.size());
		setAttr("ids",ids);
		setAttr("fzhlWeiXiu", fzhlWeiXiu);
		setAttr("pageNumber", pageNumber);
		render("/manage/fzhl/view.html");
	}
	
	public void update(){
		JSONObject jsonObject=new JSONObject();
		try {
			FzhlWeiXiu fzhl = getModel(FzhlWeiXiu.class);
			String  userIds = getAttr(ConstantWebContext.request_cUserIds);
			int zhs=fzhl.getInt("startZHK")*1000+fzhl.getInt("startZHM");
			int zhe=0;
			if (StringUtils.isEmpty(StringUtils.changNull(fzhl.get("endZHK")))||StringUtils.isEmpty(StringUtils.changNull(fzhl.get("endZHM")))) {
				zhe=zhs;
			}else{
				 zhe=fzhl.getInt("endZHK")*1000+fzhl.getInt("endZHM");
			}
			//判断桩号是否符合范围
			if (BaseService.service.judgeZHScope(BaseService.service.getZHFanWeibyUserId(userIds),zhs,zhe)) {
				List<FzhlWeiXiuRelate> fzhlRelateList = getModels(FzhlWeiXiuRelate.class);
				String[] paras = getParas("uploadImgs");
				boolean result = FzhlWeiXiuService.service.update(fzhl,fzhlRelateList,paras);
				String result_desc="修改失败！";
				if(result){
					result_desc="修改成功！";
					String delId = getPara("delIds");
					String[] delIds = delId.split(",");
					if (delIds.length>0) {
						for (int i = 0; i < delIds.length; i++) {
							if(!"".equals(delIds[i])){
								FzhlWeiXiuService.service.delete("DMS_WX_fzhlWeiXiu_item_relate", delIds[i]);
							}
						}
					}
				}
				jsonObject.put("result", result);
				jsonObject.put("result_desc", result_desc);
			}else{
				jsonObject.put("result", false);
				jsonObject.put("result_desc", "修改失败！输入的桩号不在范围内");
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", false);
			jsonObject.put("result_desc", "操作失败！");
		}
		renderJson(jsonObject.toJSONString());
	}
	public void getFileName(){
		JSONObject jsonObject=new JSONObject();
		try{

			UploadFile upFile = getFile();
			File file = upFile.getFile();

			String serverpath = ToolsFile.getPathUrl(getRequest());

			//上传的绝对路径
			String filePath = serverpath   + file.getName();
			
			File destFile = new File(filePath);
			FileUtils.copyFile(file, destFile);

			String uploadFileName = PropKit.get("tomcat.imgFolderName");
			jsonObject.put("url", uploadFileName+"/file/"+ToolsFile.getPathDate()+"/"+file.getName());
			jsonObject.put("fileName", upFile.getFileName());

		}catch(Exception e){
			e.printStackTrace();
		}
		renderJson(jsonObject.toJSONString());
	}
}