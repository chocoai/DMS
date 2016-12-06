package com.junl.dms.mvc.liefeng;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.junl.dms.tools.ToolsFile;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.constant.ConstantWebContext;
import com.platform.mvc.base.BaseController;
import com.platform.mvc.base.BaseService;
import com.platform.tools.StringUtils;
@Controller(controllerKey = "/jf/manage/liefeng")
/**
 * 
 * @author wlw
 * @date 2016年7月15日 下午2:33:36
 * @description 
 *		TODO
 */
public class LieFengController extends BaseController {
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(LieFengController.class);
	
	/**
	 * 
	 * @author wlw
	 * @date 2016年7月13日 下午4:41:54
	 * @description 首页
	 *		TODO
	 *
	 */
	public void index() {
		String  userIds = getAttr(ConstantWebContext.request_cUserIds);
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
		paging(ConstantInit.db_dataSource_main, splitPage, LieFeng.sqlId_splitPageSelect, LieFeng.sqlId_splitPageFrom);
		render("/manage/liefeng/list.html");
	}
	
	public void add(){
		JSONObject jsonObject=new JSONObject();
		try{			
			LieFeng lieFeng = getModel(LieFeng.class);
			String  userIds = getAttr(ConstantWebContext.request_cUserIds);
			int zhs=lieFeng.getInt("startZHK")*1000+lieFeng.getInt("startZHM");
			int zhe=0;
			if (StringUtils.isEmpty(StringUtils.changNull(lieFeng.get("endZHK")))||StringUtils.isEmpty(StringUtils.changNull(lieFeng.get("endZHM")))) {
				zhe=zhs;
			}else{
				 zhe=lieFeng.getInt("endZHK")*1000+lieFeng.getInt("endZHM");
			}
			//判断桩号是否符合范围
			if (BaseService.service.judgeZHScope(BaseService.service.getZHFanWeibyUserId(userIds),zhs,zhe)) {
				List<LieFengRelate> lieFengRelateList = getModels(LieFengRelate.class);
				String[] paras = getParas("youwuImgs");
				lieFeng.set("createUserId",getAttr(ConstantWebContext.request_cUserIds));
				boolean result = LieFengService.service.save(lieFeng,lieFengRelateList,paras);			
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
			String lieFengDisposeId = getPara("ids");
			String imgs = LieFengService.service.findById(ids).getStr("imgs");
			boolean result2 = LieFengService.service.delete_DMS_WX_lieFengDispose_wxItem_relate(lieFengDisposeId);
			boolean result = LieFengService.service.delete_WX_lieFengDispose(getRequest(),ids);
			String result_desc="删除失败！";
			if(result && result2){
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
	public void view(){
		String ids = getPara("ids");
		String lieFengDisposeId = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		LieFeng lieFeng = LieFengService.service.findById(ids);
		List<Record> list = LieFengService.service.findBylieFengDisposeId(lieFengDisposeId);
		Integer state = lieFeng.get("state");
		String imgs = lieFeng.getStr("imgs");
		String remark = lieFeng.getStr("remark");
		
		setAttr("list",list);
		setAttr("listSize",list.size());
		setAttr("ids",ids);
		setAttr("state", state);
		setAttr("imgs",imgs);
		setAttr("remark",remark);
		setAttr("lieFeng", lieFeng);
		setAttr("pageNumber", pageNumber);
		render("/manage/liefeng/view.html");
	}
	public void showUpdate(){
		String ids = getPara("ids");
		String lieFengDisposeId = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		LieFeng lieFeng = LieFengService.service.findById(ids);
		List<Record> list = LieFengService.service.findBylieFengDisposeId(lieFengDisposeId);
		Integer state = lieFeng.get("state");
		String imgs = lieFeng.getStr("imgs");
		String remark = lieFeng.getStr("remark");
		
		setAttr("list",list);
		setAttr("listSize",list.size());
		setAttr("ids",ids);
		setAttr("state", state);
		setAttr("imgs",imgs);
		setAttr("remark",remark);
		setAttr("lieFeng", lieFeng);
		setAttr("pageNumber", pageNumber);
		render("/manage/liefeng/update.html");
	}
	public void update(){
		JSONObject jsonObject=new JSONObject();
		try {
			LieFeng lieFeng = getModel(LieFeng.class);
			String  userIds = getAttr(ConstantWebContext.request_cUserIds);
			int zhs=lieFeng.getInt("startZHK")*1000+lieFeng.getInt("startZHM");
			int zhe=0;
			if (StringUtils.isEmpty(StringUtils.changNull(lieFeng.get("endZHK")))||StringUtils.isEmpty(StringUtils.changNull(lieFeng.get("endZHM")))) {
				zhe=zhs;
			}else{
				 zhe=lieFeng.getInt("endZHK")*1000+lieFeng.getInt("endZHM");
			}
			//判断桩号是否符合范围
			if (BaseService.service.judgeZHScope(BaseService.service.getZHFanWeibyUserId(userIds),zhs,zhe)) {
				List<LieFengRelate> lieFengRelateList = getModels(LieFengRelate.class);
				String[] paras = getParas("youwuImgs");
				boolean result = LieFengService.service.update(lieFeng,lieFengRelateList,paras);
				String result_desc="修改失败！";
				if(result){
					result_desc="修改成功！";
					/*String delId = getPara("delIds");
					String[] delIds = delId.split(",");
					for (int i = 0; i < delIds.length; i++) {
						if(!"".equals(delIds[i])){
							LieFengService.service.delete("DMS_WX_lieFengDispose_wxItem_relate", delIds[i]);
						}
					}*/
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
	public void saveImgToJson(){
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
