package com.junl.dms.mvc.shenqingzhifubiaolist;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import com.junl.dms.mvc.dayplan.DayPlan;
import com.junl.dms.mvc.dayplan.DayPlanController;
import com.junl.dms.mvc.luxian.LuXian;
import com.junl.dms.mvc.luxian.LuXianService;
import com.junl.dms.mvc.shenqingzhifubiao.Shenqingzhifubiao;
import com.junl.dms.mvc.shenqingzhifubiao.ShenqingzhifubiaoService;
import com.junl.dms.tools.WordUtil;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantInit;
import com.platform.mvc.base.BaseController;
import com.platform.tools.StringUtils;

@Controller(controllerKey = "/jf/manage/shenqingzhifubiaolist")
public class ShenqingzhifubiaoListController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(DayPlanController.class);

	/**
	 * 首页
	 */
	public void index() {
		paging(ConstantInit.db_dataSource_main, splitPage,
				Shenqingzhifubiao.sqlId_splitPageSelect, Shenqingzhifubiao.sqlId_splitPageFrom);
		String basePath = getRequest().getScheme() + "://" + getRequest().getServerName() + ":"  + getRequest().getServerPort();
		setAttr("basePath",basePath);
		render("/manage/shenqingzhifubiaolist/list.html");
	}

	/**
	 * /jf/manage/dayplan/select
	 */
	public void select() {
		splitPage.setPageSize(50);
		paging(ConstantInit.db_dataSource_main, splitPage,
				DayPlan.sqlId_splitPageSelect_findByDate,
				DayPlan.sqlId_splitPageFrom_findByDate);
		render("/manage/monthplan/chooseCheckBox.html");
	}

	/**
	 * 向新增界面跳转
	 */
	public void add() {
		List<LuXian> luXianList = LuXianService.service.findList();
		setAttr("luXianList", luXianList);
		render("/manage/shenqingzhifubiao/add.html");
	}

	public void save() {
		JSONObject jsonObject = new JSONObject();
		try {
			Shenqingzhifubiao shenqingzhifubiao = getModel(Shenqingzhifubiao.class);
			boolean result = ShenqingzhifubiaoService.service.save(shenqingzhifubiao);
			String result_desc = "新增失败！";
			if (result) {
				result_desc = "新增成功！";
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

	public void delete() {
		JSONObject jsonObject = new JSONObject();
		try {
			String ids = getPara("ids");
			boolean result = ShenqingzhifubiaoService.service.delete(ids);
			String result_desc = "删除失败！";
			if (result) {
				result_desc = "删除成功！";
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

	public void edit() {
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		Shenqingzhifubiao dayPlan = ShenqingzhifubiaoService.service.findById(ids);
		setAttr("shenqingzhifubiao", dayPlan);
		setAttr("pageNumber", pageNumber);
		render("/manage/shenqingzhifubiao/update.html");
	}

	public void view()  {
		String ids = getPara("ids");
		Integer pageNumber = getParaToInt("pageNumber");
		Shenqingzhifubiao biao = ShenqingzhifubiaoService.service.findById(ids);
		SimpleDateFormat df= new SimpleDateFormat("yyy-MM-dd");
		Map<String, Object> m = new HashMap<>();
		m.put("shenqingzhifubiao", biao);
		m.put("starttime", df.format( biao.getDate("starttime") ) );
		m.put("endtime", df.format( biao.getDate("endtime") ) );
		m.put("datetime", new SimpleDateFormat("yyy-MM-dd").format(biao.getDate("datetime" ) ) );
		try {
			WordUtil.create(m, "zhifushenqing.ftl", "E:\\000.doc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		setAttr("shenqingzhifubiao", biao);
		setAttr("pageNumber", pageNumber);
		render("/manage/shenqingzhifubiao/view.html");
	}

	public void daochuWord(){
		JSONObject jsonObject = new JSONObject();
		String ids = getPara("ids");  //jlIds
		try {
			if(ids!=null){
				
				Shenqingzhifubiao biao = ShenqingzhifubiaoService.service.findById(ids);
				SimpleDateFormat df= new SimpleDateFormat("yyy-MM-dd");
				Map<String, Object> m = new HashMap<>();
		        org.json.JSONObject jsonObj = new org.json.JSONObject(biao.toJson() );
		        Iterator<?> it = jsonObj.keys();  
		        while (it.hasNext()) {  
					String key = (String)it.next();  
					Object value = jsonObj.get(key);
					System.out.println( value );
					//生成Word时候，不能为空
					if (StringUtils.changNull(value).equals("null")) {
						biao.set(key, "");
					}else{
						biao.set(key,value);
					}
		        }
				m.put("shenqingzhifubiao", biao);
				m.put("starttime", df.format(df.parse(biao.getStr("starttime"))));
				m.put("endtime", df.format(df.parse(biao.getStr("endtime"))));
				if (StringUtils.isEmpty(StringUtils.changNull(biao.get("datetime")))) {
					m.put("datetime","");
				}else{
					m.put("datetime",df.format(df.parse(biao.getStr("datetime"))));
				}
				
				if(biao.get("tianxiecompany") == null ){
					biao.set("tianxiecompany", "");
				}
			
				baseCreateWord(m, "zhifushenqing.ftl", "zhifushenqing-" + ids + ".doc");
				jsonObject.put("isSuccess", true);
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("isSuccess", false);
		}
		jsonObject.put("downloadPath", PropKit.get("tomcat.WordPath")+"/"+"zhifushenqing-" + ids + ".doc");
		renderJson(jsonObject.toJSONString());
	}

	public void update() {
		JSONObject jsonObject = new JSONObject();
		try {
			Shenqingzhifubiao dayPlan = getModel(Shenqingzhifubiao.class);
			boolean result = ShenqingzhifubiaoService.service.update(dayPlan);
			String result_desc = "修改失败！";
			if (result) {
				result_desc = "修改成功！";
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

}
