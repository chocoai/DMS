package com.junl.dms.mvc.roadassessment.roadstate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.platform.annotation.Controller;
import com.platform.mvc.base.BaseController;

@Controller(controllerKey = "/jf/manage/roadassessment/roadstate")
public class RoadStateController extends BaseController  {
	
	public void index()
	{
		render("/manage/roadassessment/roadstate/list.html");
	}

	/**
	 * 获取数据列表
	 */
	public void getList()
	{
		List<Map<String,Object>> resultData = new ArrayList<Map<String,Object>>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		String luXian = getPara("luXian");
		String fangxiang = getPara("fangXiang");
		if(startTime != null || endTime != null || luXian != null || fangxiang != null)
		{
			try {
				endTime = endTime+" 23:59:59";
				startTime += " 00:00:00";
				startTime = sdf.format(sdf.parse(startTime));
				endTime = sdf.format(sdf.parse(endTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			String sql = "SELECT * FROM  [dbo].[F_LKPG_PCI]('"+startTime+"','"+endTime+"','"+luXian+"','"+fangxiang+"')";
			List<Record> recordList = Db.find(sql);
			if(recordList != null && recordList.size() > 0)
			{
				for (int i = 0; i < recordList.size(); i++) {
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("KSZH", recordList.get(i).get("KSZH"));
					map.put("CD", recordList.get(i).get("CD"));
					map.put("GL1", recordList.get(i).get("龟裂轻"));
					map.put("GL2", recordList.get(i).get("龟裂中"));
					map.put("GL3", recordList.get(i).get("龟裂重"));
					map.put("KZLF1", recordList.get(i).get("块状裂缝轻"));
					map.put("KZLF3", recordList.get(i).get("块状裂缝重"));
					map.put("ZXLF1", recordList.get(i).get("纵向裂缝轻"));
					map.put("ZXLF3", recordList.get(i).get("纵向裂缝重"));
					map.put("HXLF1", recordList.get(i).get("横向裂缝轻"));
					map.put("HXLF3", recordList.get(i).get("横向裂缝重"));
					map.put("KC1", recordList.get(i).get("坑槽轻"));
					map.put("LC3", recordList.get(i).get("坑槽重"));
					map.put("SS1", recordList.get(i).get("松散轻"));
					map.put("SS3", recordList.get(i).get("松散重"));
					map.put("CX1", recordList.get(i).get("沉陷轻"));
					map.put("CX3", recordList.get(i).get("沉陷重"));
					map.put("CC1", recordList.get(i).get("车辙轻"));
					map.put("CC3", recordList.get(i).get("车辙重"));
					map.put("PLYB1", recordList.get(i).get("波浪拥包轻"));
					map.put("PLYB3", recordList.get(i).get("波浪拥包重"));
					map.put("FY", recordList.get(i).get("泛油"));
					map.put("XBBL", recordList.get(i).get("修补不良"));
					map.put("DR", recordList.get(i).get("DR"));
					map.put("PCI", recordList.get(i).get("PCI"));
					resultData.add(map);
				}
			}
			try {
				endTime = sdf1.format(sdf.parse(endTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		setAttr("startTime", startTime);
		setAttr("endTime", endTime);
		setAttr("luXian", luXian);
		setAttr("fangXiang", fangxiang);
		setAttr("resultData", resultData);
		render("/manage/roadassessment/roadstate/table.html");
	}
	
	/**
	 * 获取路线
	 */
	public void getLuxian()
	{
		String sql = " SELECT * from DMS_PZ_luXian ";
		
		List<Record> recordList = Db.find(sql);
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		if(recordList != null && recordList.size() > 0)
		{
			for (int i = 0; i < recordList.size(); i++) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("ids", recordList.get(i).get("ids"));
				map.put("luXianNO", recordList.get(i).get("luXianNO"));
				map.put("luXianName", recordList.get(i).get("luXianName"));
				map.put("startZHK", recordList.get(i).get("startZHK"));
				map.put("startZHM", recordList.get(i).get("startZHM"));
				map.put("endZHK", recordList.get(i).get("endZHK"));
				map.put("endZHM", recordList.get(i).get("endZHM"));
				resultList.add(map);
			}
		}
		JSONObject json = new JSONObject();
		json.put("luxian", resultList);
		renderJson(json.toJSONString());
	}
	
	/**
	 * 根据路线获取方向
	 */
	public void getFangxiangByLuxianId()
	{
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		String luxianId = getPara("luxianId");
		String sql = " SELECT * from DMS_PZ_fangXiang where luXianId = '"+luxianId+"' ";
		List<Record> recordList = Db.find(sql);
		if(recordList != null && recordList.size() > 0)
		{
			for (int i = 0; i < recordList.size(); i++) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("ids", recordList.get(i).get("ids"));
				map.put("luXianId", recordList.get(i).get("luXianId"));
				map.put("name", recordList.get(i).get("name"));
				map.put("pinYinSx", recordList.get(i).get("pinYinSx"));
				resultList.add(map);
			}
		}
		JSONObject json = new JSONObject();
		json.put("fangxiang", resultList);
		renderJson(json.toJSONString());
	}
}
