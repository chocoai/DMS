package com.junl.dms.mvc.yanghuyhuizong;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.platform.mvc.base.BaseService;

public class YangHuLogService extends BaseService {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(YangHuLogService.class);

	public static final YangHuLogService service = new YangHuLogService();

	public boolean save(YangHuHuiZong yangHuLog) {
		return yangHuLog.save();
	}

	public boolean delete(String ids) {
		return YangHuHuiZong.dao.deleteById(ids);
	}

	public YangHuHuiZong findById(String ids) {
		YangHuHuiZong yangHuLog = YangHuHuiZong.dao.findById(ids);
		return yangHuLog;
	}
	
	public boolean update(YangHuHuiZong yangHuLog){
		return yangHuLog.update();
	}
	
	/**
	 *  计量汇总表
	 * @param year
	 * @return
	 */
	public Map<String,Object> getHuiZong(String parentid, String year, String serarchTime,boolean check){
		String sql = "SELECT ids, levelNum, name,dw, remark FROM DMS_PZ_checkInfo WHERE parentid = '#parentid' AND isdel = 0 and year ='"+year+"' order by sort";
		
		String tempSql = "SELECT ids, cinfo.levelNum levelNum, name, dw, remark, price, ysgcl FROM DMS_PZ_checkInfo cinfo "
				+ "LEFT JOIN( SELECT levelNum, SUM(price) price, SUM(yanshougongchengLiang) ysgcl FROM DMS_PZ_checkInfo c "
				+ "LEFT JOIN( SELECT yanShouXiMuLevel ysxml, price, yanshougongchengLiang FROM DMS_JL_jiLingXiMu "
				+ "WHERE CONVERT(VARCHAR(7), yanShouTime, 120) = '"+serarchTime+"') jlxm ON jlxm.ysxml LIKE c.levelNum + '.%' "
				+ "WHERE parentid = '#parentid' AND isdel = 0 AND YEAR = '"+year+"' GROUP BY levelNum) ckhzinfo ON ckhzinfo.levelNum = cinfo.levelNum "
				+ "WHERE parentid = '#parentid' AND isdel = 0 AND YEAR = '"+year+"' ORDER BY sort";
		List<Record> retList = Db.find(sql.replace("#parentid",parentid));
		List<Map<String,Object>> tabList = new ArrayList<Map<String, Object>>();
		double price = 0;
		for (int i = 0; i < retList.size(); i++) {
			Record record = retList.get(i);
			Map<String,Object> mapL = new HashMap<String, Object>();
			mapL.put("ids", record.get("ids"));
			mapL.put("levelNum", record.get("levelNum"));
			mapL.put("name", record.get("name")); //二级 
			mapL.put("dw", record.get("dw"));
			mapL.put("remark", record.get("remark"));
			List<Record> child = Db.find(tempSql.replace("#parentid", record.getStr("ids")));
			List<Map<String,Object>> childList = new ArrayList<Map<String, Object>>();
			int childSize = child.size();
			for (int j = 0; j < child.size(); j++) { 
				Record re = child.get(j);
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("iscolspan", "false");
				map.put("price", re.get("price"));
				if(re.get("price")!= null){
					price +=re.getDouble("price");
				}
				map.put("ysgcl", re.get("ysgcl"));
				if(check&&record.get("levelNum").equals("2") && PropKit.get("YANSHOU.FILTER").toString().indexOf(re.getStr("name")) == -1){
					Map<String,Object> tempMap = new HashMap<String, Object>();
					tempMap.put("tempName", re.get("name"));
					tempMap.put("levelNum", re.get("levelNum"));
					tempMap.put("name", re.get("name")+"(事故维修)");
					tempMap.put("rowspan", "2");
					tempMap.put("dw", re.get("dw"));
					tempMap.put("remark", re.get("remark"));
					tempMap.put("price", re.get("price"));
					tempMap.put("ysgcl", re.get("ysgcl"));
					map.put("iscolspan", "true");
					childList.add(tempMap);
					childSize++;
				}
				
				map.put("levelNum", re.get("levelNum"));
				map.put("name", "");
				map.put("rowspan", "1");
				map.put("tempName", re.get("name"));
				mapL.put("dw", re.get("dw"));
				mapL.put("remark", re.get("remark"));
				childList.add(map);
			}
			mapL.put("child", childList);
			mapL.put("size", childSize);
			tabList.add(mapL);
		}
		Map<String, Object> rmap = new HashMap<String, Object>();
		rmap.put("tabList", tabList);
		rmap.put("price", price);
		return rmap;
	}
	
	/**
	 * 5c254454ef804c9b9328dc8d148681ac
	 * @param parentId
	 * @param year
	 * @param month
	 * @param serarchTime
	 * @return
	 */
	public List<Map<String,Object>> getTabInfo(String parentId,String year,String month,String serarchTime){
		String sql = "SELECT ids, levelNum, name FROM DMS_PZ_checkInfo WHERE parentid = '#parentid' AND isdel = 0 and year = '2016' order by sort";
		List<Record> retList = Db.find(sql.replace("#parentid", parentId));
		List<Map<String,Object>> tabList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < retList.size(); i++) {
			Record record = retList.get(i);
			Map<String,Object> mapL = new HashMap<String, Object>();
			mapL.put("firstLevelNumberDetail", record.get("name"));
			mapL.put("levelNum", record.get("levelNum"));
			mapL.put("index", i+1);
			mapL.put("titleName", year+"年日常养护"+month+"月清单明细表");
			mapL.put("secondName", "二单价承包项目");
			if(record.getStr("name").equals("安全设施")){
				mapL.put("isparent", "true");
				List<Record> anqList = Db.find(sql.replace("#parentid", record.getStr("ids")));
				List<Map<String,Object>> anqTabList = new ArrayList<Map<String, Object>>();
				for (int j = 0; j < anqList.size(); j++) {
					Record anqRe = anqList.get(j);
					Map<String,Object> anqMap = new HashMap<String, Object>();
					anqMap.put("firstLevelNumberDetail", anqRe.get("name"));
					anqMap.put("levelNum", anqRe.get("levelNum"));
					anqMap.put("index", j+1);
					
					Map<String,Object> gcInfoMap = getchengbaoInfo(anqRe.getStr("ids"), year,month,serarchTime);
					anqMap.put("gcl", gcInfoMap.get("gcl"));
					anqMap.put("price", gcInfoMap.get("price"));
					anqMap.put("sumprice", gcInfoMap.get("sumprice"));
					anqMap.put("sumgcl", gcInfoMap.get("sumgcl"));
					anqMap.put("gcInfo", gcInfoMap.get("tabList"));
					anqTabList.add(anqMap);
				}
				mapL.put("anqTabList", anqTabList);
			}else{
				mapL.put("isparent", "false");
				Map<String,Object> gcInfoMap = getchengbaoInfo(record.getStr("ids"), year,month,serarchTime);
				mapL.put("gcl", gcInfoMap.get("gcl"));
				mapL.put("price", gcInfoMap.get("price"));
				mapL.put("sumprice", gcInfoMap.get("sumprice"));
				mapL.put("sumgcl", gcInfoMap.get("sumgcl"));
				mapL.put("gcInfo", gcInfoMap.get("tabList"));
			}
			
			tabList.add(mapL);
		}
		return tabList;
		
//		String sql = "SELECT firstLevelNumberDetail FROM DMS_PZ_yanShouXinXi GROUP BY firstLevelNumberDetail";
//		List<Record> retList = Db.find(sql);
//		List<Map<String,Object>> tabList = new ArrayList<Map<String, Object>>();
//		for (int i = 0; i < retList.size(); i++) {
//			Record record = retList.get(i);
//			Map<String,Object> mapL = new HashMap<String, Object>();
//			mapL.put("firstLevelNumberDetail", record.get("firstLevelNumberDetail"));
//			mapL.put("index", i+1);
//			mapL.put("titleName", year+"年日常养护"+month+"月清单明细表");
//			mapL.put("secondName", "二单价承包项目");
//			mapL.put("gcInfo", getchengbaoInfo(record.getStr("firstLevelNumberDetail"), year,serarchTime));
//			tabList.add(mapL);
//		}
//		return tabList;
	}
	public Map<String,Object> getchengbaoInfo(String parentId,String year,String month,String serarchTime){
		String sql = "";
		List<Map<String,Object>> tabList = new ArrayList<Map<String, Object>>();
		String searchYear = year;
		String searchMonth = month;
		
		double price = 0;
		double gcl = 0;
		double sumprice = 0;
		double sumgcl = 0;
		
		if(!month.equals("12")){
			searchYear = String.valueOf(Integer.valueOf(searchYear)+1);
			searchMonth = "01";
		}else{
			searchMonth = "31";
		}
		sql = "SELECT * FROM DMS_PZ_checkInfo cinfo LEFT JOIN( "
				+ "SELECT yanShouXiMuLevel ysxml, SUM(price) price,SUM(yanshougongchengLiang) gcl FROM DMS_JL_jiLingXiMu where convert(varchar(7),yanShouTime,120) ='"+serarchTime +"' GROUP BY yanShouXiMuLevel ) "
				+ "jlxm ON jlxm.ysxml = cinfo.levelNum "
				+"LEFT JOIN( SELECT yanShouXiMuLevel ysxml2, SUM(price) sumprice, SUM(yanshougongchengLiang) sumgcl FROM DMS_JL_jiLingXiMu where yanShouTime >= '"+year+"-01-01 00:00:00' AND yanShouTime <= '"+searchYear+"-"+searchMonth+"-01 00:00:00' GROUP BY yanShouXiMuLevel) jlxm2 ON jlxm2.ysxml2 = cinfo.levelNum "
				+ "WHERE  parentid = '"+parentId+"' AND YEAR = '"+year+"' AND isdel = 0 order by sort";
		List<Record> retList = Db.find(sql);
		for (int i = 0; i < retList.size(); i++) {
			Record record = retList.get(i);
			Map<String,Object> mapL = new HashMap<String, Object>();
			mapL.put("secondLevelNumber",record.get("levelNum"));
			mapL.put("secondLevelNumberDetail",record.get("name"));
			mapL.put("dw",record.get("dw"));
			mapL.put("dj",record.get("dj"));
			mapL.put("gcl",record.get("gcl") == null? "0":Double.toString(record.getDouble("gcl")).substring(0,Double.toString(record.getDouble("gcl")).indexOf(".")));
			mapL.put("price",record.get("price"));
			mapL.put("sumprice",record.get("sumprice"));
			mapL.put("sumgcl",record.get("sumgcl") == null? "0":Double.toString(record.getDouble("sumgcl")).substring(0,Double.toString(record.getDouble("sumgcl")).indexOf(".")));
			if(record.get("gcl")!=null){
				gcl +=record.getDouble("gcl");
			}
			if(record.get("price")!=null){
				price +=record.getDouble("price");
			}
			if(record.get("sumprice")!=null){
				sumprice +=record.getDouble("sumprice");
			}
			if(record.get("sumgcl")!=null){
				sumgcl +=record.getDouble("sumgcl");
			}
			
			tabList.add(mapL);
		}
		Map<String,Object> retmap = new HashMap<String, Object>();
		retmap.put("tabList", tabList);
		retmap.put("gcl", gcl);
		retmap.put("price", price);
		retmap.put("sumprice", sumprice);
		retmap.put("sumgcl", sumgcl);
		
		return retmap;
	}
	
	
}
