package com.junl.dms.mvc.roadassessment.roadtechnicstatedetail;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.platform.annotation.Controller;
import com.platform.mvc.base.BaseController;

@Controller(controllerKey = "/jf/manage/roadassessment/roadTechnicStateDetail")
public class RoadTechnicStateDetailController extends BaseController {
	
	public static final String EXCEL03_EXTENSION = ".xls"; //excel2003扩展名
	public static final String EXCEL07_EXTENSION = ".xlsx";//excel2007扩展名
	public void index()
	{
		render("/manage/roadassessment/roadtechnicstatedetail/list.html");
	}

	/**
	 * 获取数据列表
	 */
	public void getList()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> dateList = new ArrayList<String>();
		String sql = " select createDate,count(*) from DMS_PZ_lukuang GROUP BY createDate ORDER BY createDate DESC ";
		List<Record> recordList = Db.find(sql);
		if(recordList != null && recordList.size() > 0)
		{
			for (int i = 0; i < recordList.size(); i++) {
				if(recordList.get(i).get("createDate") != null)
				{
					dateList.add(sdf.format(recordList.get(i).get("createDate")).toString());
				}
			}
		}
		setAttr("dateList", dateList);
		render("/manage/roadassessment/roadtechnicstatedetail/table.html");
	}
	
	public void getListByRoadNameAndDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		String roadName = getPara("roadName");
		String date = getPara("date");
		String tmpDate = getPara("date");
		if(date != null)
		{
			date += " 00:00:00";
			tmpDate += " 23:59:59";
			try {
				date = sdf.format(sdf.parse(date));
				tmpDate = sdf.format(sdf.parse(tmpDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String sql = " select * from DMS_PZ_lukuang where createDate >= '"+date+"' and createDate <= '"+tmpDate+"'  and luxian = '"+roadName+"' ";
		List<Record> recordList = Db.find(sql);
		if(recordList != null && recordList.size() > 0)
		{
			for (int i = 0; i < recordList.size(); i++) {
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("ids", recordList.get(i).get("ids"));
				map.put("luxian", recordList.get(i).get("luxian"));
				map.put("fangxiang", recordList.get(i).get("fangxiang"));
				map.put("zhuanghaoNum", recordList.get(i).get("zhuanghaoNum"));
				map.put("zhuanghaoText", recordList.get(i).get("zhuanghaoText"));
				map.put("lumianRQI", recordList.get(i).get("lumianRQI"));
				map.put("lumianRDI", recordList.get(i).get("lumianRDI"));
				map.put("lumianSRI", recordList.get(i).get("lumianSRI"));
				map.put("lumianPSST", recordList.get(i).get("lumianPSST"));
				map.put("BCI", recordList.get(i).get("BCI"));
				map.put("createDate", recordList.get(i).get("createDate"));
				resultList.add(map);
			}
		}
		JSONObject json = new JSONObject();
		json.put("list", resultList);
		renderJson(json.toJSONString());
	}
	
	public void uploadFile()
	{
		JSONObject json = new JSONObject();
		boolean isExcel = false;
		UploadFile upFile = getFile("file");
		File file = null;
		String suffix = "";
		if(upFile != null)
		{
			file = upFile.getFile();
			if(file != null)
			{
				String name = file.getName();
				suffix = name.substring(name.lastIndexOf('.'));
				if(suffix.equals(EXCEL03_EXTENSION) || suffix.equals(EXCEL07_EXTENSION))
				{
					//解析Excel
					isExcel = true;
					
				}
			}
		}
		json.put("isExcel", isExcel);
		renderJson(json.toJSONString());
	}
	
	public void deleteLuKuang()
	{
		String id = getPara("id");
		String sql = " delete from DMS_PZ_lukuang where ids = '"+id+"' ";
		LuKuang lukang = new LuKuang();
		lukang.put("ids", id);
		boolean isDelete = lukang.delete();
		JSONObject json = new JSONObject();
		json.put("isDelete", isDelete);
		renderJson(json.toJSONString());
	}
	
	public void refashDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> dateList = new ArrayList<String>();
		String sql = " select createDate,count(*) from DMS_PZ_lukuang GROUP BY createDate ORDER BY createDate DESC ";
		List<Record> recordList = Db.find(sql);
		if(recordList != null && recordList.size() > 0)
		{
			for (int i = 0; i < recordList.size(); i++) {
				if(recordList.get(i).get("createDate") != null)
				{
					dateList.add(sdf.format(recordList.get(i).get("createDate")).toString());
				}
			}
		}
		JSONObject json = new JSONObject();
		json.put("dateList", dateList);
		renderJson(json.toJSONString());
	}
	
}
