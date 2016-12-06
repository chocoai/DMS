package com.junl.dms.mvc.roadassessment.roadtechnicstate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.dev.RecordLister;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.platform.annotation.Controller;
import com.platform.mvc.base.BaseController;

@Controller(controllerKey = "/jf/manage/roadassessment/roadTechnicState")
public class RoadTechnicStateController extends BaseController {
	
	public void index()
	{
		render("/manage/roadassessment/roadtechnicstate/list.html");
	}

	/**
	 * 获取数据列表
	 */
	public void getList()
	{
		List<Map<String,Object>> resultHZData = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> resultMXData = new ArrayList<Map<String,Object>>();
		Map<String,Object> resultHZBaseData = new HashMap<String,Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		//保留小数
		DecimalFormat dfToThree = new DecimalFormat("#.000");
		DecimalFormat dfToOne = new DecimalFormat("#.0");
		String startTime = getPara("startTime");
		String endTime = getPara("endTime");
		String luXian = getPara("luXian");
		String luXianId = getPara("luXianId");
		String fangXiang = getPara("fangXiang");
		String direction1 = "";
		String direction2 = "";
		if(startTime != null || endTime != null || luXian != null || fangXiang != null)
		{
			try {
				endTime = endTime+" 23:59:59";
				startTime += " 00:00:00";
				startTime = sdf.format(sdf.parse(startTime));
				endTime = sdf.format(sdf.parse(endTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
//--------------------------------------汇总---------------------------------
			//根据路线Id 查方向
			List<Map<String,Object>> FXList = getFangxiangByLuxianId(luXianId);
//			direction1 = "锡张";
//			direction2 = "张锡";
			if(FXList != null)
			{
				if(FXList.size() > 0)
				{
					direction1 = FXList.get(0).get("name").toString();
					direction2 = FXList.get(1).get("name").toString();
				}
			}
			RoadTechnicStatePro roadTechnicState = new RoadTechnicStatePro(startTime, endTime, luXian,direction1, direction2, "P_LKPG_Summary");
			//-----------------
			Db.execute(roadTechnicState);
			resultHZData = roadTechnicState.getResult();
			//根据路线名称查询基础信息
			String sqlBaseInfo = " select * from DMS_PZ_roadBaseInfo where roadName = '"+luXian+"' ";
			List<Record> recordListBaseInfo = Db.find(sqlBaseInfo);
			if(recordListBaseInfo != null && recordListBaseInfo.size() > 0 )
			{
				for (int i = 0; i < recordListBaseInfo.size(); i++) {
					resultHZBaseData.put("competentUnit", recordListBaseInfo.get(i).get("ZG"));
					resultHZBaseData.put("roadName", recordListBaseInfo.get(i).get("roadName"));
					resultHZBaseData.put("belongCity", recordListBaseInfo.get(i).get("belongCity"));
					resultHZBaseData.put("technicLevel", recordListBaseInfo.get(i).get("technicLevel"));
					resultHZBaseData.put("roadType", recordListBaseInfo.get(i).get("roadType"));
					resultHZBaseData.put("feedUnit", recordListBaseInfo.get(i).get("feedUnit"));
				}
			}
			//平均MQI  和 长度
			String sqlMQIAndLength1 = " SELECT SUM(cd/1000) AS length,AVG(MQI) AS avgMQI FROM  [dbo].[F_LKPG_BreakDown]('"+startTime+"','"+endTime+"','"+luXian+"','"+direction1+"') ";
			String sqlMQIAndLength2 = " SELECT SUM(cd/1000) AS length,AVG(MQI) AS avgMQI FROM  [dbo].[F_LKPG_BreakDown]('"+startTime+"','"+endTime+"','"+luXian+"','"+direction2+"') ";
			double d1Length	= 0;
			double d2Length	= 0;
			double d1AvgMQI	= 0;
			double d2AvgMQI	= 0;
			double dAllAvgLength = 0;
			double dAllAvgMQI = 0;
			List<Record> recordListBaseInfo1 = Db.find(sqlMQIAndLength1);
			if(recordListBaseInfo1 != null && recordListBaseInfo1.size() > 0 )
			{
				for (int i = 0; i < recordListBaseInfo1.size(); i++) {
					if(recordListBaseInfo1.get(i).get("length") != null)
					{
						d1Length = Double.valueOf(recordListBaseInfo1.get(i).get("length").toString());
						String d1LengthStr = dfToThree.format(d1Length);
						resultHZBaseData.put("d1Length",d1LengthStr);
					}
					if(recordListBaseInfo1.get(i).get("avgMQI") != null)
					{
						d1AvgMQI = Double.valueOf(recordListBaseInfo1.get(i).get("avgMQI").toString());
						String d1AvgMQIStr = dfToOne.format(d1AvgMQI);
						resultHZBaseData.put("d1AvgMQI", d1AvgMQIStr);
					}
					
				}
			}
			List<Record> recordListBaseInfo2 = Db.find(sqlMQIAndLength2);
			if(recordListBaseInfo2 != null && recordListBaseInfo2.size() > 0 )
			{
				for (int i = 0; i < recordListBaseInfo2.size(); i++) {
					if(recordListBaseInfo2.get(i).get("length") != null)
					{
						d2Length = Double.valueOf(recordListBaseInfo2.get(i).get("length").toString());
						String d2LengthStr = dfToThree.format(d2Length);
						resultHZBaseData.put("d2Length",d2LengthStr);
					}
					if(recordListBaseInfo2.get(i).get("avgMQI") != null)
					{
						d2AvgMQI = Double.valueOf(recordListBaseInfo2.get(i).get("avgMQI").toString());
						String d2AvgMQIStr = dfToOne.format(d2AvgMQI);
						resultHZBaseData.put("d2AvgMQI", d2AvgMQIStr);
					}
				}
			}
			//去平均  等级
			resultHZBaseData.put("d1level", getLevelByMQI(d1AvgMQI));
			resultHZBaseData.put("d2level", getLevelByMQI(d2AvgMQI));
			dAllAvgLength = (d1Length+d2Length)/2;
			dAllAvgMQI = (d1AvgMQI+d2AvgMQI)/2;
			String dAllAvgLengthStr = dfToThree.format(dAllAvgLength);
			String dAllAvgMQIStr = dfToOne.format(dAllAvgMQI);
			resultHZBaseData.put("dAllAvgLength", dAllAvgLengthStr);
			resultHZBaseData.put("dAllAvgMQI", dAllAvgMQIStr);
			resultHZBaseData.put("dAllLevel", getLevelByMQI(dAllAvgMQI));
//--------------------------------------明细---------------------------------		
			String sql = "SELECT * FROM  [dbo].[F_LKPG_BreakDown]('"+startTime+"','"+endTime+"','"+luXian+"','"+fangXiang+"') ";
			List<Record> recordList = Db.find(sql);
			if(recordList != null && recordList.size() > 0)
			{
				
				for (int i = 0; i < recordList.size(); i++) {
					Map<String,Object> map = new HashMap<String,Object>();
					String zh = recordList.get(i).get("startZhText").toString()+"-"+recordList.get(i).get("endZhText").toString();
					map.put("ZH", zh);
					map.put("CD",recordList.get(i).get("cd"));
					map.put("MQI", recordList.get(i).get("MQI"));
					map.put("PQI", recordList.get(i).get("PQI"));
					map.put("PCI", recordList.get(i).get("PCI"));
					map.put("RQI", recordList.get(i).get("RQI"));
					map.put("RDI", recordList.get(i).get("RDI"));
					map.put("SRI", recordList.get(i).get("SRI"));
					map.put("PSSI", recordList.get(i).get("PSSI"));
					map.put("SCI", recordList.get(i).get("SCI"));
					map.put("BCI", recordList.get(i).get("BCI"));
					map.put("TCI", recordList.get(i).get("TCI"));
					map.put("luxian", recordList.get(i).get("luxian"));
					resultMXData.add(map);
				}
			}
			try {
				endTime = sdf1.format(sdf.parse(endTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		setAttr("endTime", endTime);
		setAttr("startTime", startTime);
		setAttr("luXian", luXian);
		setAttr("fangXiang", fangXiang);
		setAttr("direction1", direction1);
		setAttr("direction2", direction2);
		setAttr("resultHZData", resultHZData);
		setAttr("resultMXData", resultMXData);
		setAttr("resultHZBaseData", resultHZBaseData);
		render("/manage/roadassessment/roadtechnicstate/table.html");
	}
	
	
	/**
	 * 根据路线获取方向
	 */
	public List<Map<String,Object>> getFangxiangByLuxianId(String luxianId)
	{
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
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
		return resultList;
	}
	
	//根据MQI 去等级  优良中次差
	public String getLevelByMQI(double mqi)
	{
		if(mqi > 90)
		{
			return "优";
		}
		else if(mqi > 80)
		{
			return "良";
		}
		else if(mqi > 70)
		{
			return "中";
		}
		else if(mqi > 60)
		{
			return "次";
		}
		else
		{
			return "差";
		}
	}
}
