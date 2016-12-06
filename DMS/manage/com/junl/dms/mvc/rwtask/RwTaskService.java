package com.junl.dms.mvc.rwtask;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.junl.dms.mvc.taskinforelate.TaskInfoRelate;
import com.junl.dms.mvc.xunchainfo.StringUtil;
import com.junl.dms.mvc.xunchainfo.XunChaInfo;
import com.platform.mvc.base.BaseService;

public class RwTaskService extends BaseService {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(RwTaskService.class);

	public static final RwTaskService service = new RwTaskService();
		//获取油污的维修记录			DMS_WX_youWuDispose
		//获取养护通用的维修记录		DMS_WX_yangHuTongYongWeiXiu
		//获取路面病害的维修记录		DMS_WX_luMianBingHaiWeiXiu
		//获取裂缝处理的维修记录		DMS_WX_lieFengDispose
		//获取交通安全设施维修记录	DMS_WX_jtaqssWeiXiu
		//防撞护栏的维修记录			DMS_WX_fzhlWeiXiu
	public static String[] weiXiuJiLu_Array={"DMS_WX_youWuDispose","DMS_WX_yangHuTongYongWeiXiu",
		"DMS_WX_luMianBingHaiWeiXiu","DMS_WX_lieFengDispose",
		"DMS_WX_jtaqssWeiXiu","DMS_WX_fzhlWeiXiu"};
	
	
	
	
	/**
	 * 获取维修记录
	 * @param byanIds
	 * @return
	 */
	public List<Map<String, Object>> getWeiXiuJiLuByTaskIdFromTables(String tirIds){
		List<Map<String, Object>> _list=new ArrayList<Map<String, Object>>();
		for (int i = 0; i < weiXiuJiLu_Array.length; i++) {
			List<Map<String, Object>> list=selectForEach(weiXiuJiLu_Array[i],tirIds);
			if (list.size()>0) {
				_list.addAll(list);
			}
		}
		return _list;
	}
	
	/**
	 * 循环查询各个维修记录表
	 */
	private List<Map<String, Object>> selectForEach(String table, String tirIds) {
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		String weiXiuRenYuan="weiXiuRenYuan";
		if (table.equals("DMS_WX_youWuDispose")) {
			weiXiuRenYuan="shiGongRenYuan";
		}
		List<Record> records=Db.find("select ids, startZHK,startZHM,endZHK,endZHM," + 
										"	 luXian,wzType,wzName,wzMiaoShu,weatherState,qiWen," + 
										"	 shiGongJiXie,"+weiXiuRenYuan+" weiXiuRenYuan,anQuanYuan,fuZeRen,pingJia,imgs" + 
										" from "+table +" where taskInfoRelate='"+tirIds+"'");
		if (records.size()>0) {
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> map=new HashMap<String, Object>();
				Record record=records.get(i);
				map.put("ids", record.get("ids"));
				map.put("startZHK", record.get("startZHK"));
				map.put("startZHM", record.get("startZHM"));
				map.put("endZHK", record.get("endZHK"));
				map.put("endZHM", record.get("endZHM"));
				map.put("luXian", record.get("luXian"));
				map.put("wzType", record.get("wzType"));
				map.put("wzName", record.get("wzName"));
				map.put("wzMiaoShu", record.get("wzMiaoShu"));
				map.put("weatherState", record.get("weatherState"));
				map.put("qiWen", record.get("qiWen"));
				map.put("shiGongJiXie", record.get("shiGongJiXie"));
				map.put("weiXiuRenYuan", record.get("weiXiuRenYuan"));
				map.put("anQuanYuan", record.get("anQuanYuan"));
				map.put("fuZeRen", record.get("fuZeRen"));
				map.put("pingJia", record.get("pingJia"));
				map.put("imgs", record.get("imgs"));
				list.add(map);
			}
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 根据弹出框传过来的table名称（维修记录的类型）和子任务的编号查询，此任务中的维修记录
	 */
	public List<Map<String, Object>> getWeiXiuJiLuByTaskId(String table, String tirIds) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		//因为油污的没有'weiXiuRenYuan' ，只有shiGongRenYuan
		String weiXiuRenYuan="weiXiuRenYuan";
		if (table.equals("DMS_WX_youWuDispose")) {
			weiXiuRenYuan="shiGongRenYuan";
		}
		List<Record> records=Db.find("select ids, startZHK,startZHM,endZHK,endZHM," + 
										"	 luXian,wzType,wzName,wzMiaoShu,weatherState,qiWen," + 
										"	 shiGongJiXie,"+weiXiuRenYuan+" weiXiuRenYuan,anQuanYuan,fuZeRen,pingJia,imgs" + 
										" from "+table +" where taskInfoRelate='"+tirIds+"'");
		if (records.size()>0) {
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> map=new HashMap<String, Object>();
				Record record=records.get(i);
				map.put("startZHK", record.get("startZHK"));
				map.put("startZHM", record.get("startZHM"));
				map.put("endZHK", record.get("endZHK"));
				map.put("endZHM", record.get("endZHM"));
				map.put("luXian", record.get("luXian"));
				map.put("wzType", record.get("wzType"));
				map.put("wzName", record.get("wzName"));
				map.put("wzMiaoShu", record.get("wzMiaoShu"));
				map.put("weatherState", record.get("weatherState"));
				map.put("qiWen", record.get("qiWen"));
				map.put("shiGongJiXie", record.get("shiGongJiXie"));
				map.put("weiXiuRenYuan", record.get("weiXiuRenYuan"));
				map.put("anQuanYuan", record.get("anQuanYuan"));
				map.put("fuZeRen", record.get("fuZeRen"));
				map.put("pingJia", record.get("pingJia"));
				map.put("imgs", record.get("imgs"));
				list.add(map);
			}
		}else{
			return null;
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public boolean save(RwTask rwtask, String userIds, String xunChaInfoIds) {
		Boolean flag=false;
		rwtask.set("createUserId", userIds);
		if (StringUtil.isNotEmpty(xunChaInfoIds)) {
			//如果巡查信息的id集合不为空，先分割，就执行更新操作
			String[] spilt_str=xunChaInfoIds.split(",");
			//设置病害数并保存
			rwtask.set("DiseaseNumber", spilt_str.length);
			flag=rwtask.save();
			if (spilt_str.length>0) {
				for (int i = 0; i < spilt_str.length; i++) {
					String XunInfoId=spilt_str[i];
					if (StringUtil.isNotEmpty(XunInfoId)) {
						//巡查信息表的state=1，表示已经有关联的任务
						XunChaInfo info=XunChaInfo.dao.findById(XunInfoId);
						//设置关联状态
						info.set("state", "1");
						info.update();
						//并且将巡查信息表的ids和任务表的ids保存的关联表中
						TaskInfoRelate taskInfoRelate=new TaskInfoRelate();
						taskInfoRelate.set("xunChaInfoId", XunInfoId);
						taskInfoRelate.set("taskId", rwtask.get("ids"));
						taskInfoRelate.save();
					}
				}
			}
		}else{
			rwtask.set("DiseaseNumber",0);
			flag=rwtask.save();
		}

		return flag;
	}

	
	/**
	 * 删除任务之前首先查看是否有维修记录
	 * @param ids
	 * @return
	 */
	public boolean deleteTaskBefore(String ids) {
		List<Record> list=Db.find("select * from xz_wx wx where wx.taskInfoRelate in (select ids from DMS_RW_task_info_relate tir where tir.taskId='"+ids+"' )");
		if (list==null||list.size()==0) {
			return true;
		}else{
			return false;
		}
	}
	
	
	
	
	
	
	
	
	public boolean delete(String ids) {
		boolean flag=false;
		//首先删除管理任务表和巡查信息表的中间表
			//删除的第一步首先查询出中间表中的外键列等于任务表主键的数据
		List<TaskInfoRelate> list=TaskInfoRelate.dao.findListByTaskId(ids);
		if (list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				TaskInfoRelate tir=list.get(i);
				//恢复巡查表的state=0
				XunChaInfo info=XunChaInfo.dao.findById(tir.get("xunChaInfoId"));
				info.set("state", "0");
				info.update();
				//删除中间表
				tir.delete();
			}
		}
		flag=RwTask.dao.deleteById(ids);
			
		return flag;
	}

	public RwTask findById(String ids) {
		RwTask rwtask = RwTask.dao.findById(ids);
		return rwtask;
	}
	
	public boolean update(RwTask rwtask, String xunChaInfoIds){
		boolean flag=false;
		//首先将之前的所有的任务关联的巡查信息清空
			//首先删除管理任务表和巡查信息表的中间表
			//删除的第一步首先查询出中间表中的外键列等于任务表主键的数据
			List<TaskInfoRelate> list=TaskInfoRelate.dao.findListByTaskId(rwtask.getStr("ids"));
			if (list.size()>0) {
				for (int i = 0; i < list.size(); i++) {
					TaskInfoRelate tir=list.get(i);
					//恢复巡查表的state=0
					XunChaInfo info=XunChaInfo.dao.findById(tir.get("xunChaInfoId"));
					info.set("state", 0);
					info.update();
					//删除中间表
					tir.delete();
				}
			}
			String[] spilt_str=xunChaInfoIds.split(",");
			//设置病害数并保存
			rwtask.set("DiseaseNumber", spilt_str.length);
			flag=rwtask.update();
			if (spilt_str.length>0) {
				for (int i = 0; i < spilt_str.length; i++) {
					String XunInfoId=spilt_str[i];
					if (StringUtil.isNotEmpty(XunInfoId)) {
						//巡查信息表的state=1，表示已经有关联的任务
						XunChaInfo info=XunChaInfo.dao.findById(XunInfoId);
						//设置关联状态
						info.set("state", "1");
						info.update();
						//并且将巡查信息表的ids和任务表的ids保存的关联表中
						TaskInfoRelate taskInfoRelate=new TaskInfoRelate();
						taskInfoRelate.set("xunChaInfoId", XunInfoId);
						taskInfoRelate.set("taskId", rwtask.get("ids"));
						taskInfoRelate.save();
					}
				}
			}
		
		
		
		return flag;
	}
	
	public List<RwTask> findByTaskId(String taskId){
		String sql = getSql("manage.rwtask.findByTaskId");
		List<RwTask> list = RwTask.dao.find(sql, taskId);
		return list;
	}
	
	
	
	
	
	/**
	 * 获取本月的任务数
	 * @return
	 */
	public int getTaskNo(){
		int res=1;
		List<RwTask> list = RwTask.dao.getTaskNo();
		if (list.size()>0) {
			RwTask rt=list.get(0);
			res=rt.getInt("taskNum")+1;
		}
		return res;
	}
	/**
	 * 转换任务数
	 * @param num
	 * @return
	 */
	public String intToStrong(int num){
		String str="";
		int length=String.valueOf(num).length();
		if (length==1) {
			str="00"+num;
		}else if (length==2) {
			str="0"+num;
		}else if (length==3) {
			str=""+num;
		}
		return str;
	}
	public List<Record> getXunChaInfoByIds(String ids){
		List<Record> list = Db.find("select x.xunChaTime xunChaTime,x.xiuBuBiaoZhi xiuBuBiaoZhi,x.wxmkId,ci.name wxmkName, " + 
									"			x.binghaiOrQueXianType binghaiOrQueXianType,x.binghaiOrQueXianMiaoShu binghaiOrQueXianMiaoShu,x.yuGuGongChengLiang,x.yuGuGongChengLiangDw, " + 
									"			x.level level ,tir.state,x.ids,dbo.ConcatBuJian(x.ids,',') as FZHLBHIF " + 
									" from DMS_RW_task t " + 
									"	left join DMS_RW_task_info_relate tir  on  t.ids=tir.taskId " +   
									"	left join DMS_XC_xunChaInfo x  on x.ids=tir.xunChaInfoId " +  
									"	left join DMS_PZ_chooseInfo ci on ci.ids=x.wxmkId " +  
									" where t.ids='"+ids+"' order by xunChaTime");
		return list;
	}
	
	public boolean deleteTaskRelateInUpdate(String taskRelateIds, String rwTaskIds){
		boolean flag=false;
		//通过任务的ids和巡查信息的ids到任务关系表获取对象
		//修改巡查信息的状态
		//删除任务关系表的对象
		List<Record> list=Db.find("select * from DMS_RW_task_info_relate where taskId='"+rwTaskIds+"'and xunChaInfoId='"+taskRelateIds+"'");
		if (list.size()>0) {
			for (int i = 0; i < list.size(); i++) {
				Record tir=list.get(i);
				//恢复巡查表的state=0
				XunChaInfo info=XunChaInfo.dao.findById(tir.get("xunChaInfoId"));
				info.set("state", "0");
				info.update();
				//删除中间表
				Db.update("delete from DMS_RW_task_info_relate where taskId='"+rwTaskIds+"'and xunChaInfoId='"+taskRelateIds+"'");
				//更新任务表的病害数
				Db.update("update DMS_RW_task set DiseaseNumber=DiseaseNumber-1 where ids='"+rwTaskIds+"'");
			}
		}
		return flag;
	}
	
	public RwTask getTaskInfoById(String ids){
		return RwTask.dao.getTaskInfoById(ids);
	}
	
}