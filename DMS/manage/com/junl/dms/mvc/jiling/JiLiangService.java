package com.junl.dms.mvc.jiling;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.junl.dms.mvc.baoyan.BaoYan;
import com.junl.dms.mvc.baoyan.BaoYanService;
import com.junl.dms.mvc.choujian.ChouJian;
import com.junl.dms.mvc.choujian.ChouJianService;
import com.junl.dms.mvc.xunchainfo.StringUtil;
import com.platform.mvc.base.BaseService;

public class JiLiangService extends BaseService {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(JiLiangService.class);

	public static final JiLiangService service = new JiLiangService();
			//获取油污的维修记录			DMS_WX_youWuDispose
			//获取养护通用的维修记录		DMS_WX_yangHuTongYongWeiXiu
			//获取路面病害的维修记录		DMS_WX_luMianBingHaiWeiXiu
			//获取裂缝处理的维修记录		DMS_WX_lieFengDispose
			//获取交通安全设施维修记录	DMS_WX_jtaqssWeiXiu
			//防撞护栏的维修记录			DMS_WX_fzhlWeiXiu
	public static String[] weiXiuJiLu_Array={"DMS_WX_youWuDispose","DMS_WX_yangHuTongYongWeiXiu",
			"DMS_WX_luMianBingHaiWeiXiu","DMS_WX_lieFengDispose",
			"DMS_WX_jtaqssWeiXiu","DMS_WX_fzhlWeiXiu"};
	
	
	
	
	public List<Map<String, Object>> getYanShouXiMu(String yanShouXiMu) {
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		//数据不为空的情况下
		if (StringUtil.isNotEmpty(yanShouXiMu)) {
			List<Record> list1=Db.find("select name,dw,dj,levelNum,ids from DMS_PZ_checkInfo where isshow=1 and levelNum like '"+yanShouXiMu+"%' order by sort");
			 for (int i = 0; i < list1.size(); i++) {
					Record r=list1.get(i);
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("name", r.get("name"));
					map.put("dw", r.get("dw"));
					map.put("dj", r.get("dj"));
					map.put("id", r.get("ids"));
					map.put("level", r.get("levelNum"));
					list.add(map);
				}
			}
		return list;
	}
	
	/**
	 * 获取指定字符串出现的次数
	 * 
	 * @param srcText 源字符串
	 * @param findText 要查找的字符串
	 * @return
	 */
	public static int appearNumber(String srcText, String findText) {
		
	    int count = 0;
	    Pattern p = Pattern.compile(findText);
	    Matcher m = p.matcher(srcText);
	    while (m.find()) {
	        count++;
	    }
	    return count;
	}
	
	
	
	
	
	
	
	/**
	 * 修改计量表的状态和确认时间
	 * @param ids
	 * @return
	 */
	public boolean changeJiLiangState(String ids){
		boolean flag=false;
		JiLiang jiLiang=JiLiang.dao.findById(ids);
		jiLiang.set("state", "1");
		jiLiang.set("confirmTime", new Timestamp(System.currentTimeMillis()));
		flag=jiLiang.update();
		if (flag) {
			//生成抽检
			ChouJian choujian = new ChouJian();
			choujian.set("jiLingIds", ids);
			choujian.set("byIds", jiLiang.getStr("byIds"));
			choujian.set("state", 0);
			choujian.set("createTime", new Timestamp(System.currentTimeMillis()));
			choujian.save();
			//报验的状态需要设置为1
			BaoYan by=BaoYanService.service.findById(jiLiang.getStr("byIds"));
			by.set("state", "1");
			by.update();
			//如果配置的抽检率为0，就自动执行下面的抽检的操作
			String hegelv = PropKit.get("hegelv");
			if (hegelv.equals("0")||hegelv.equals("")||hegelv==null) {
				ChouJianService.service.autoSampling(choujian.get("ids").toString());
			}
		}
		return flag;
	}
	
	
	/**
	 * 计量细目的新增
	 * @param jiLiangXiMu
	 * @param paras
	 * @return
	 */
	public boolean saveItems(JiLiangXiMu jiLiangXiMu,String[] paras){
		//报验的状态需要设置为1
		JiLiang jiLiang=JiLiang.dao.findById(jiLiangXiMu.getStr("jlIds"));
		BaoYan by=BaoYanService.service.findById(jiLiang.getStr("byIds"));
		by.set("state", "1");
		by.update();
		boolean flag=false;
		//保存图片
		StringBuffer sb = new StringBuffer();		
		if(paras != null) {
			for(int i=0;i<paras.length;i++){
				sb.append(paras[i]+",");
				String newStr = sb.toString();
				//去掉最后一个逗号
				String newStr1 = newStr.substring(0, newStr.length()-1);
				jiLiangXiMu.set("imgs",newStr1);
			}
		}else{
			jiLiangXiMu.set("imgs","");
		}
		//获取维修记录表的ids
		String ids=jiLiangXiMu.getStr("weiXiuIds");
		//维修记录的状态需要改成已计量，计量数亦需要更新
		flag=jiLiangXiMu.save();
		if (flag) {
			//更新计量表的计量数量合计、计量金额合计total(price*yanShouGongChengLiang)
			double price=jiLiangXiMu.getFloat("price");
			double yanShouGongChengLiang=jiLiangXiMu.getFloat("yanShouGongChengLiang");
			double total=price*yanShouGongChengLiang;
			for (int i = 0; i < weiXiuJiLu_Array.length; i++) {
				Db.update("update "+weiXiuJiLu_Array[i]+" set state=1 , meteringNum=isnull(meteringNum,0)+1,meteringMoney=isnull(meteringMoney,0)+"+total+" where ids='"+ids+"'");
			}
			String jlIds=jiLiangXiMu.getStr("jlIds");
			Db.update("update DMS_JL_jiLing set meteringNum=isnull(meteringNum,0)+1 , meteringMoney=isnull(meteringMoney,0)+"+total+" where ids='"+jlIds+"'");
		}
		return flag;
	}
	
	
	/**
	 * 退回的新增
	 * @param jiLingTuiHui
	 * @param paras
	 * @return
	 */
	public boolean saveReturn(jiLingTuiHui jiLingTuiHui,String[] paras){
		//报验的状态需要设置为1
		JiLiang jiLiang=JiLiang.dao.findById(jiLingTuiHui.getStr("jlIds"));
		BaoYan by=BaoYanService.service.findById(jiLiang.getStr("byIds"));
		by.set("state", "1");
		by.update();
		boolean flag=false;
		//保存图片
		StringBuffer sb = new StringBuffer();		
		if(paras != null) {
			for(int i=0;i<paras.length;i++){
				sb.append(paras[i]+",");
				String newStr = sb.toString();
				//去掉最后一个逗号
				String newStr1 = newStr.substring(0, newStr.length()-1);
				jiLingTuiHui.set("imgs",newStr1);
			}
		}else{
			jiLingTuiHui.set("imgs","");
		}
		flag=jiLingTuiHui.save();
		if (flag) {
			//获取维修记录表的ids
			String ids=jiLingTuiHui.getStr("weixiuids");
			//维修记录表的状态需要改成退回			退回时返修次数需要+1	将之前计量的东西删除
			for (int i = 0; i < weiXiuJiLu_Array.length; i++) {
				//查询维修记录
				Record record =Db.find("select * from xz_wx where ids='"+ids+"'").get(0);
				//删除所有计量的细目
				Db.update("delete from  DMS_JL_jiLingXiMu  where weiXiuIds='"+ids+"'");
				//修改计量主表的计量数和计量金额
				Db.update("update  DMS_JL_jiLing set meteringNum=meteringNum-"+record.get("meteringNum")
													+",meteringMoney=meteringMoney-"+record.get("meteringMoney")+"  where ids='"+ids+"'");
				//返修+1			清空计量数  计量金额  
				Db.update("update "+weiXiuJiLu_Array[i]+" set state=2,repairNum=repairNum+1,meteringNum=0,meteringMoney=0 where ids='"+ids+"'");
				
			}
			//更新计量表的退回总数
			String jlIds=jiLingTuiHui.getStr("jlIds");
			Db.update("update DMS_JL_jiLing set returnNum=returnNum+1 where ids='"+jlIds+"'");
		}
	
		return flag;
	}
	
	
	/**
	 * 获取维修记录
	 * @param byanIds
	 * @return
	 */
	public List<Map<String, Object>> getWeiXiuRecord(String byanIds){
		//将循环查询换成视图查询
		List<Map<String, Object>> _list=selectForEach("xz_wx",byanIds);
		
		/*for (int i = 0; i < weiXiuJiLu_Array.length; i++) {
			List<Map<String, Object>> list=selectForEach(weiXiuJiLu_Array[i],byanIds);
			if (list.size()>0) {
				_list.addAll(list);
			}
		}*/
		return _list;
	}
	
	/**
	 * 循环查询各个维修记录表
	 */
	private List<Map<String, Object>> selectForEach(String tableName, String byanIds) {
		//首先根据报验的主键ids到报验外键表获取任务（任务表的外键表）的ids
		//根据获取到的任务（任务表的外键表）的ids获取维修表的记录数
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		List<Record> list1=Db.find("select wx.shiGongJiXie shiGongJiXie,wx.weiXiuRenYuan weiXiuRenYuan,wx.anQuanYuan anQuanYuan,wx.fuZeRen fuZeRen,wx.pingJia pingJia,wx.sunHuaiCauseType sunHuaiCauseType,wx.qiWen qiWen, wx.diseaseType diseaseType,wx.state wxState,taskrelate.ids taskrelateIds, wx.meteringMoney meteringMoney,task.taskNo taskNo,wx.ids ids, wx.startZHK startZHK,wx.startZHM startZHM,wx.endZHK endZHK,wx.endZHM endZHM,wx.luXian luXian,wx.wzType wzType,wx.wzName wzName,wx.meteringNum meteringNum,xc.binghaiOrQueXianType binghaiOrQueXianType "+
					  "from "+tableName+" wx,DMS_RW_task_info_relate taskrelate,DMS_XC_xunChaInfo xc,DMS_RW_task task " + 
					  "where xc.ids=taskrelate.xunChaInfoId and wx.taskInfoRelate=taskrelate.ids and taskrelate.taskId=task.ids" + 
					  		" and isnull(wx.state,0)!=2 "+
					  		/*"	and wx.taskInfoRelate IN (select taskId from DMS_BY_weiXiuBaoYanF f " + 
					  									" where f.byIds='"+byanIds+"')");*/
					  		" and wx.baoyanId='"+byanIds+"'");
					  									
		for (int i = 0; i < list1.size(); i++) {
			Record r=list1.get(i);
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("taskrelateIds", r.get("taskrelateIds"));
			map.put("taskNo", r.get("taskNo"));
			map.put("ids", r.get("ids"));
			map.put("startZHK", r.get("startZHK"));
			map.put("startZHM", r.get("startZHM"));
			map.put("endZHK", r.get("endZHK"));
			map.put("endZHM", r.get("endZHM"));
			map.put("luXian", r.get("luXian"));
			map.put("wzType", r.get("wzType"));
			map.put("wzName", r.get("wzName"));
			map.put("wxState", r.get("wxState"));
			map.put("meteringNum", r.get("meteringNum"));
			map.put("meteringMoney", r.get("meteringMoney"));
			map.put("diseaseType", r.get("diseaseType"));
			map.put("qiWen", r.get("qiWen"));
			map.put("sunHuaiCauseType", r.get("sunHuaiCauseType"));
			map.put("pingJia", r.get("pingJia"));
			map.put("fuZeRen", r.get("fuZeRen"));
			map.put("anQuanYuan", r.get("anQuanYuan"));
			map.put("weiXiuRenYuan", r.get("weiXiuRenYuan"));
			map.put("shiGongJiXie", r.get("shiGongJiXie"));
			list.add(map);
		}
		return list;
	}

	/**
	 * 根据计量列表获取计量的单挑数据
	 * @param ids
	 * @return
	 */
	public JiLiang findByIds(String ids) {
		JiLiang jiliang = JiLiang.dao.findByIds(ids);
		return jiliang;
	}
	
	
	/**
	 * 获取维修数量
	 * @param ids
	 * @return
	 */
	public int getWeiXiuNum(String ids){
		int num=0; 
		try {
			/*num= Db.find("	select sum(taskWeiXiuNum) taskWeiXiuNum from DMS_BY_weiXiuBaoYanF f ,DMS_RW_task_info_relate r " + 
										"	where f.taskId=r.ids and f.byIds ='"+ids+"'").get(0).getInt("taskWeiXiuNum");*/
			//从函数中选择加到报验中的维修记录（20160905 改）
			num= Db.find("select sum(1) taskWeiXiuNum from xz_wx where baoyanId='"+ids+"'").get(0).getInt("taskWeiXiuNum");
			
		} catch (Exception e) {
			e.printStackTrace();
			return num;
		}
		return num;
	}


	
	public Map<String,Object> getWord1(String ids) {
		Map<String,Object> map = new HashMap<>();
		List list=new ArrayList();
		//数据不为空的情况下
		if (StringUtil.isNotEmpty(ids)) {
			Object o = Db.findFirst("SELECT byan.ids byanIds,byan.byNo byNo,jl.* FROM DMS_JL_jiLing jl,DMS_BY_weiXiuBaoYanP byan WHERE jl.byIds = byan.ids  AND jl.ids = '"+ids+"'");
			list =Db.find("select xm.*, xx.*  from  DMS_JL_jiLingXiMu xm  , DMS_PZ_checkInfo xx where xm.yanShouXiMuId = xx.ids and xm.jlIds =  '"+ids+"' ");
			map.put("one", o);
			map.put("list", list);
		}
		return map;
	}
	
	
	
	
	
	
	
	/**
	 * 获取维修记录		退回
	 * @param byanIds
	 * @return
	 */
	public List<Map<String, Object>> getWeiXiuRecord_return(String byanIds){
		//将循环查询换成视图查询
		List<Map<String, Object>> _list=selectForEach_return("xz_wx",byanIds);
	/*	for (int i = 0; i < weiXiuJiLu_Array.length; i++) {
			List<Map<String, Object>> list=selectForEach_return(weiXiuJiLu_Array[i],byanIds);
			if (list.size()>0) {
				_list.addAll(list);
			}
		}*/
		return _list;
	}
	
	/**
	 * 循环查询各个维修记录表	退回
	 */
	private List<Map<String, Object>> selectForEach_return(String tableName, String byanIds) {
		//首先根据报验的主键ids到报验外键表获取任务（任务表的外键表）的ids
		//根据获取到的任务（任务表的外键表）的ids获取维修表的记录数
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		List<Record> list1=Db.find("select wx.state wxState,taskrelate.ids taskrelateIds, wx.meteringMoney meteringMoney,task.taskNo taskNo,wx.ids ids, wx.startZHK startZHK,wx.startZHM startZHM,wx.endZHK endZHK,wx.endZHM endZHM,wx.luXian luXian,wx.wzType wzType,wx.wzName wzName,wx.meteringNum meteringNum,xc.binghaiOrQueXianType binghaiOrQueXianType "+
					  "from "+tableName+" wx,DMS_RW_task_info_relate taskrelate,DMS_XC_xunChaInfo xc,DMS_RW_task task " + 
					  "where xc.ids=taskrelate.xunChaInfoId and wx.taskInfoRelate=taskrelate.ids and taskrelate.taskId=task.ids" + 
					  		" and isnull(wx.state,0)=2 "+
					  		" and wx.baoyanId='"+byanIds+"'");
					  		/*"	and wx.taskInfoRelate IN (select taskId from DMS_BY_weiXiuBaoYanF f " + 
					  									" where f.byIds='"+byanIds+"')");*/
		for (int i = 0; i < list1.size(); i++) {
			Record r=list1.get(i);
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("taskrelateIds", r.get("taskrelateIds"));
			map.put("taskNo", r.get("taskNo"));
			map.put("ids", r.get("ids"));
			map.put("startZHK", r.get("startZHK"));
			map.put("startZHM", r.get("startZHM"));
			map.put("endZHK", r.get("endZHK"));
			map.put("endZHM", r.get("endZHM"));
			map.put("luXian", r.get("luXian"));
			map.put("wzType", r.get("wzType"));
			map.put("wzName", r.get("wzName"));
			map.put("wxState", r.get("wxState"));
			map.put("meteringNum", r.get("meteringNum"));
			map.put("meteringMoney", r.get("meteringMoney"));
			map.put("binghaiOrQueXianType", r.get("binghaiOrQueXianType"));
			list.add(map);
		}
		return list;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 获取维修记录				验收
	 * @param byanIds
	 * @return
	 */
	public List<Map<String, Object>> getWeiXiuRecord_accept(String byanIds){
		//将循环查询换成视图查询
		List<Map<String, Object>> _list=selectForEach_accept("xz_wx",byanIds);
		/*for (int i = 0; i < weiXiuJiLu_Array.length; i++) {
			List<Map<String, Object>> list=selectForEach_accept(weiXiuJiLu_Array[i],byanIds);
			if (list.size()>0) {
				_list.addAll(list);
			}
		}*/
		return _list;
	}
	
	/**
	 * 循环查询各个维修记录表				验收				state=3
	 */
	private List<Map<String, Object>> selectForEach_accept(String tableName, String byanIds) {
		//首先根据报验的主键ids到报验外键表获取任务（任务表的外键表）的ids
		//根据获取到的任务（任务表的外键表）的ids获取维修表的记录数
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		List<Record> list1=Db.find("select DISTINCT wx.shiGongJiXie shiGongJiXie,wx.weiXiuRenYuan weiXiuRenYuan,wx.anQuanYuan anQuanYuan,wx.fuZeRen fuZeRen,wx.pingJia pingJia,wx.sunHuaiCauseType sunHuaiCauseType,wx.qiWen qiWen, wx.diseaseType diseaseType,wx.state wxState,taskrelate.ids taskrelateIds, wx.meteringMoney meteringMoney,task.taskNo taskNo,wx.ids ids, wx.startZHK startZHK,wx.startZHM startZHM,wx.endZHK endZHK,wx.endZHM endZHM,wx.luXian luXian,wx.wzType wzType,wx.wzName wzName,wx.meteringNum meteringNum,xc.binghaiOrQueXianType binghaiOrQueXianType "+
					  "from DMS_CJ_chouJian_relate cjr,"+tableName+" wx,DMS_RW_task_info_relate taskrelate,DMS_XC_xunChaInfo xc,DMS_RW_task task " + 
					  "where cjr.weiXiuIds=wx.ids and xc.ids=taskrelate.xunChaInfoId and wx.taskInfoRelate=taskrelate.ids and taskrelate.taskId=task.ids" + 
					  		//" and isnull(wx.state,0)=3 "+
					  		" and wx.baoyanId='"+byanIds+"'");
		
					  	/*	"	and wx.taskInfoRelate IN (select taskId from DMS_BY_weiXiuBaoYanF f " + 
					  									" where f.byIds='"+byanIds+"')");*/
		for (int i = 0; i < list1.size(); i++) {
			Record r=list1.get(i);
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("taskrelateIds", r.get("taskrelateIds"));
			map.put("taskNo", r.get("taskNo"));
			map.put("ids", r.get("ids"));
			map.put("startZHK", r.get("startZHK"));
			map.put("startZHM", r.get("startZHM"));
			map.put("endZHK", r.get("endZHK"));
			map.put("endZHM", r.get("endZHM"));
			map.put("luXian", r.get("luXian"));
			map.put("wzType", r.get("wzType"));
			map.put("wzName", r.get("wzName"));
			map.put("wxState", r.get("wxState"));
			map.put("meteringNum", r.get("meteringNum"));
			map.put("meteringMoney", r.get("meteringMoney"));
			map.put("binghaiOrQueXianType", r.get("binghaiOrQueXianType"));
			map.put("diseaseType", r.get("diseaseType"));
			map.put("qiWen", r.get("qiWen"));
			map.put("sunHuaiCauseType", r.get("sunHuaiCauseType"));
			map.put("pingJia", r.get("pingJia"));
			map.put("fuZeRen", r.get("fuZeRen"));
			map.put("anQuanYuan", r.get("anQuanYuan"));
			map.put("weiXiuRenYuan", r.get("weiXiuRenYuan"));
			map.put("shiGongJiXie", r.get("shiGongJiXie"));
			list.add(map);
		}
		return list;
	}
	/**
	 * 根据计量ids和维修ids获取细目的数据
	 * @param weiXiuIds
	 * @param jlIds
	 * @return
	 */
	public List<Map<String, Object>> getXiMuByJlIdsAndWeiXiuIds(String weiXiuIds, String jlIds) {
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		List<Record> list1=Db.find("select ids,yanShouXiMu,yanShouTime,price,dw,yanShouGongChengLiang,imgs,remark,weiXiuIds,jlIds,yanShouXiMuId,yanShouXiMuLevel from DMS_JL_jiLingXiMu"
				+ " where jlIds='"+jlIds+"' and weiXiuIds='"+weiXiuIds+"'");
		for (int i = 0; i < list1.size(); i++) {
			Record r=list1.get(i);
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("ids", r.get("ids"));
			map.put("yanShouXiMu", r.get("yanShouXiMu"));
			map.put("yanShouTime", r.get("yanShouTime"));
			map.put("price", r.get("price"));
			map.put("dw", r.get("dw"));
			map.put("yanShouGongChengLiang", r.get("yanShouGongChengLiang"));
			map.put("imgs", r.get("imgs"));
			map.put("taskrelateIds", r.get("taskrelateIds"));
			map.put("remark", r.get("remark"));
			map.put("yanShouXiMuLevel", r.get("yanShouXiMuLevel"));
			list.add(map);
		}
		return list;
	}
	/**
	 * 已计量和未计量
	 * @param byanIds
	 * @param state
	 * @return
	 */
	public List<Record> showCounts(String byanIds, String state) {
		List<Record> list = Db.find("SELECT "+
										"t.ids,"+
										"t.startZHK,"+
										"t.startZHM,"+
										"t.endZHK,"+
										"t.endZHM,"+
										"t.luXian,"+
										"t.wzType,"+
										"t.wzName,"+
										"t.wzMiaoShu,"+
										"t.weatherState,"+
										"t.qiWen,"+
										"t.shiGongJiXie,"+
										"t.weiXiuRenYuan,"+
										"t.anQuanYuan,"+
										"t.fuZeRen,"+
										"t.pingJia,"+
										"t.imgs,"+
										"t.diseaseType,"+
										"t.taskInfoRelate,"+
										"t.state,"+
										"t.meteringMoney,"+
										"t.meteringNum,"+
										"t.weiXiuTime,"+
										"t.baoyanId"+
									" FROM xz_wx t where  t.baoyanId='"+byanIds+"' and t.state='"+state+"'");
		return list;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
