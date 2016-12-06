package com.platform.mvc.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.junl.dms.mvc.xunchainfo.StringUtil;
import com.junl.dms.tools.WordUtil;
import com.platform.constant.ConstantInit;
import com.platform.constant.ConstantRender;
import com.platform.dto.SplitPage;
import com.platform.tools.ToolMail;
import com.platform.tools.ToolSqlXml;
import com.platform.tools.ToolString;

/**
 * 公共方法
 * @author 董华健
 */
public class BaseService {

	private static Logger log = Logger.getLogger(BaseService.class);
	
	public static final BaseService service=new BaseService();

	/**
	 * 封装预处理参数解析并执行查询
	 * @param sqlId
	 * @param param
	 * @return
	 */
	public <T> List<T> query(String sqlId, Map<String, Object> param){
		LinkedList<Object> paramValue = new LinkedList<Object>();
		String sql = getSqlByBeetl(sqlId, param, paramValue);
		return Db.query(sql, paramValue.toArray());
	}

	/**
	 * 封装预处理参数解析并执行查询
	 * @param sqlId
	 * @param param
	 * @return
	 */
	public List<Record> find(String sqlId, Map<String, Object> param){
		LinkedList<Object> paramValue = new LinkedList<Object>();
		String sql = getSqlByBeetl(sqlId, param, paramValue);
		return Db.find(sql, paramValue.toArray());
	}

	/**
	 * 封装预处理参数解析并执行更新
	 * @param sqlId
	 * @param param
	 * @return
	 */
	public int update(String sqlId, Map<String, Object> param){
		LinkedList<Object> paramValue = new LinkedList<Object>();
		String sql = getSqlByBeetl(sqlId, param, paramValue);
		return Db.update(sql, paramValue.toArray());
	}
	
	/**
	 * 分页
	 * @param dataSource	数据源
	 * @param splitPage		分页对象
	 * @param selectSqlId	select之后，from之前
	 * @param fromSqlId		from之后
	 */
	@SuppressWarnings("unchecked")
	public void paging(String dataSource, SplitPage splitPage, String selectSqlId, String fromSqlId){
		String selectSql = getSqlByBeetl(selectSqlId, splitPage.getQueryParam());
		
		Map<String, Object> map = getFromSql(splitPage, fromSqlId);
		String fromSql = (String) map.get("sql");
		LinkedList<Object> paramValue = (LinkedList<Object>) map.get("paramValue");
		
		// 分页封装
		Page<?> page = Db.use(dataSource).paginate(splitPage.getPageNumber(), splitPage.getPageSize(), selectSql, null, fromSql, paramValue.toArray());
		splitPage.setTotalPage(page.getTotalPage());
		splitPage.setTotalRow(page.getTotalRow());
		splitPage.setList(page.getList());
		splitPage.compute();
	}
	
	/**
	 * Distinct分页
	 * @param dataSource		数据源
	 * @param splitPage			分页对象
	 * @param selectSqlId		select之后，from之前
	 * @param distinctSqlId		select distinct之后，from之前
	 * @param fromSqlId			from之后
	 */
	@SuppressWarnings("unchecked")
	public void pagingDistinct(String dataSource, SplitPage splitPage, String selectSqlId, String distinctSqlId, String fromSqlId){
		String selectSql = getSqlByBeetl(selectSqlId, splitPage.getQueryParam());
		
		String distinctSql = getSqlByBeetl(distinctSqlId, splitPage.getQueryParam());
		
		Map<String, Object> map = getFromSql(splitPage, fromSqlId);
		String fromSql = (String) map.get("sql");
		LinkedList<Object> paramValue = (LinkedList<Object>) map.get("paramValue");
		
		// 分页封装
		Page<?> page = Db.use(dataSource).paginate(splitPage.getPageNumber(), splitPage.getPageSize(), selectSql, distinctSql, fromSql, paramValue.toArray());
		splitPage.setTotalPage(page.getTotalPage());
		splitPage.setTotalRow(page.getTotalRow());
		splitPage.setList(page.getList());
		splitPage.compute();
	}
	
	/**
	 * 分页获取form sql和预处理参数
	 * @param splitPage
	 * @param sqlId
	 * @return
	 */
	private Map<String, Object> getFromSql(SplitPage splitPage, String sqlId){
		// 接收返回值对象
		StringBuilder formSqlSb = new StringBuilder();
		LinkedList<Object> paramValue = new LinkedList<Object>();
		
		// 调用生成from sql，并构造paramValue
		String sql = getSqlByBeetl(sqlId, splitPage.getQueryParam(), paramValue);
		formSqlSb.append(sql);
		
		// 行级：过滤，暂未做实现
		rowFilter(formSqlSb);
		
		// 排序
		String orderColunm = splitPage.getOrderColunm();
		String orderMode = splitPage.getOrderMode();
		if(null != orderColunm && !orderColunm.isEmpty() && null != orderMode && !orderMode.isEmpty()){
			formSqlSb.append(" order by ").append(orderColunm).append(" ").append(orderMode);
		}
		
		String formSql = formSqlSb.toString();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", formSql);
		map.put("paramValue", paramValue);
		
		return map;	
	}

	/**
     * 获取SQL，固定SQL
     * @param sqlId
     * @return
     */
	public String getSql(String sqlId){
		return ToolSqlXml.getSql(sqlId);
	}

    /**
     * 获取SQL，动态SQL，使用Beetl解析
     * @param sqlId
     * @param param
     * @return
     */
	public String getSqlByBeetl(String sqlId, Map<String, Object> param){
    	return ToolSqlXml.getSql(sqlId, param, ConstantRender.sql_renderType_beetl);
    }
    
    /**
     * 获取SQL，动态SQL，使用Beetl解析
     * @param sqlId 
     * @param param 查询参数
     * @param list 用于接收预处理的值
     * @return
     */
	public String getSqlByBeetl(String sqlId, Map<String, Object> param, LinkedList<Object> list){
    	return ToolSqlXml.getSql(sqlId, param, ConstantRender.sql_renderType_beetl, list);
    }

	/**
	 * 把11,22,33...转成'11','22','33'...
	 * @param ids
	 * @return
	 */
	public String sqlIn(String ids){
		if(null == ids || ids.trim().isEmpty()){
			return null;
		}
		
		String[] idsArr = ids.split(",");
		
		return sqlIn(idsArr);
	}

	/**
	 * 把数组转成'11','22','33'...
	 * @param ids
	 * @return
	 */
	public String sqlIn(String[] idsArr){
		if(idsArr == null || idsArr.length == 0){
			return null;
		}
		
		int length = idsArr.length;
		
		StringBuilder sqlSb = new StringBuilder();
		
		for (int i = 0, size = length -1; i < size; i++) {
			String id = idsArr[i];
			if(!ToolString.regExpVali(id, ToolString.regExp_letter_5)){ // 匹配字符串，由数字、大小写字母、下划线组成
				log.error("字符安全验证失败：" + id);
				throw new RuntimeException("字符安全验证失败：" + id);
			}
			sqlSb.append(" '").append(id).append("', ");
		}
		
		if(length != 0){
			String id = idsArr[length-1];
			if(!ToolString.regExpVali(id, ToolString.regExp_letter_5)){ // 匹配字符串，由数字、大小写字母、下划线组成
				log.error("字符安全验证失败：" + id);
				throw new RuntimeException("字符安全验证失败：" + id);
			}
			sqlSb.append(" '").append(id).append("' ");
		}
		
		return sqlSb.toString();
	}

	/**
	 * 把11,22,33...转成map，分两部分，sql和值
	 * @param ids
	 * @return
	 * 描述：
	 * 返回map包含两部分数据
	 * 一是 name=? or name=? or name=? or ...
	 * 二是 list = ['11','22','33'...]
	 */
	public Map<String, Object> sqlOr(String column, String ids){
		if(null == ids || ids.trim().isEmpty()){
			return null;
		}
		
		String[] idsArr = ids.split(",");
		int length = idsArr.length;
		
		StringBuilder sql = new StringBuilder();
		List<Object> value = new ArrayList<Object>(length);
		
		for (int i = 0, size = length -1; i < size; i++) {
			String id = idsArr[i];
			if(!ToolString.regExpVali(id, ToolString.regExp_letter_5)){ // 匹配字符串，由数字、大小写字母、下划线组成
				log.error("字符安全验证失败：" + id);
				throw new RuntimeException("字符安全验证失败：" + id);
			}
			
			sql.append(column).append(" = ? or ");
			value.add(id);
		}
		
		if(length != 0){
			String id = idsArr[length-1];
			if(!ToolString.regExpVali(id, ToolString.regExp_letter_5)){ // 匹配字符串，由数字、大小写字母、下划线组成
				log.error("字符安全验证失败：" + id);
				throw new RuntimeException("字符安全验证失败：" + id);
			}
			
			sql.append(column).append(" = ? ");
			value.add(id);
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", sql);
		map.put("value", value);
		
		return map;
	}

	/**
	 * 把11,22,33...转成数组['11','22','33'...]
	 * @param ids
	 * @return
	 * 描述：把字符串分割成数组返回，并验证分割后的数据
	 */
	public String[] splitByComma(String ids){
		if(null == ids || ids.trim().isEmpty()){
			return null;
		}
		
		String[] idsArr = ids.split(",");
		
		for (String id : idsArr) {
			if(!ToolString.regExpVali(id, ToolString.regExp_letter_5)){ // 匹配字符串，由数字、大小写字母、下划线组成
				log.error("字符安全验证失败：" + id);
				throw new RuntimeException("字符安全验证失败：" + id);
			}
		}
		
		return idsArr;
	}
	
	/**
	 * 通用删除
	 * @param table
	 * @param ids 逗号分隔的列值
	 */
	@Before(Tx.class)
	public void delete(String table, String ids){
		String sqlIn = sqlIn(ids);

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("table", table);
		param.put("sqlIn", sqlIn);
		
		String sql = getSqlByBeetl(BaseModel.sqlId_deleteIn, param);
		
		Db.use(ConstantInit.db_dataSource_main).update(sql);
	}

	/**
	 * 行级：过滤
	 * @param formSqlSb
	 */
	public void rowFilter(StringBuilder formSqlSb){
		
	}

	/**
	 * 发送邮件对象
	 * @param sendType 发送邮件的类型：text 、html
	 * @param to	接收者
	 * @param subject 邮件标题
	 * @param content 邮件的文本内容
	 * @param attachFileNames 附件
	 */
	public void sendTextMail(String sendType, List<String> to, String subject, String content, String[] attachFileNames){
		String host = PropKit.get(ConstantInit.config_mail_host);		// 发送邮件的服务器的IP
		String port = PropKit.get(ConstantInit.config_mail_port);	// 发送邮件的服务器的端口

		boolean validate = true;	// 是否需要身份验证
		
		String from = PropKit.get(ConstantInit.config_mail_from);		// 邮件发送者的地址
		String userName = PropKit.get(ConstantInit.config_mail_userName);	// 登陆邮件发送服务器的用户名
		String password = PropKit.get(ConstantInit.config_mail_password);	// 登陆邮件发送服务器的密码
		
		if(sendType != null && sendType.equals(ToolMail.sendType_text)){
			ToolMail.sendTextMail(host, port, validate, userName, password, from, to, subject, content, attachFileNames);
			
		} else if(sendType != null && sendType.equals(ToolMail.sendType_html)){
			ToolMail.sendHtmlMail(host, port, validate, userName, password, from, to, subject, content, attachFileNames);
			
		} else {
			log.error("发送邮件参数sendType错误");
		}
	}

	/**
	 * 根据数据量计算分多少次批处理
	 * @param dataSource
	 * @param sql
	 * @param batchSize 每次数据多少条
	 * @return
	 */
	public static long getBatchCount(String dataSource, String sql, int batchSize){
		long batchCount = 0;
		long count = Db.use(dataSource).queryNumber(" select count(*) " + sql).longValue();
		if(count % batchSize == 0){
			batchCount = count / batchSize;
		}else{
			batchCount = count / batchSize + 1;
		}
		return batchCount;
	}
	
	public void deleteImages(HttpServletRequest request,String imgs){
		if(imgs != null){			
			String path = request.getSession().getServletContext().getRealPath("");
			path = path.substring(0,path.length()-11);
			String[] imgs2 = imgs.split(",");
			for (String str : imgs2) {
				if(str != null && !"".equals(str)){
					File img = new File(path + str );
					if(img.exists()){
						img.delete();
					}
				}
			}
		}
	}
	
	/**
	 * 生成word
	 * @param params
	 * @param templateName
	 * @param fileName
	 * @param path
	 * @throws Exception
	 */
	protected void createWord(Map<String, Object> params, String templateName, String fileName,String path) throws Exception{
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		WordUtil.create(params , templateName, path+fileName);
	}
	
	/**
	 * 根据用户获取养护路段的sql
	 * @param userId
	 * @param startM 
	 * @param startK 
	 * @return
	 */
	public String getZHbyUserId(String userId, String startK, String startM){
		//返回的sql语句
		String val="";
		//养护路段的ids
		String yangHuLuDuanIds="";
		//1.根据用户ids获取养护路段的ids
		Record yangHuLuDuan = Db.find("select yangHuLuDuanId from pt_user u,pt_userinfo ui where u.userinfoids=ui.ids	and u.ids='"+userId+"'").get(0);
		String[] yangHuLuDuanIdArray= yangHuLuDuan.get("yangHuLuDuanId").toString().split(",");
		if (userId.equals("03a44ba0aa4e4905bea726d4da976ba5")) {
			//当用户是管理员的时候则可以负责所有的养护路段
			List<Record> list=Db.find("select * from DMS_PZ_yhLuDuan");
			//清空数组yangHuLuDuanIdArray
			yangHuLuDuanIdArray=new String[list.size()];
			for (int i = 0; i < yangHuLuDuanIdArray.length; i++) {
				Record r=list.get(i);
				yangHuLuDuanIdArray[i]=r.get("ids");
			}
		}
		//拼接sql条件
		for (int i = 0; i < yangHuLuDuanIdArray.length; i++) {
			yangHuLuDuanIds+=yangHuLuDuanIdArray[i]+"','";
		}
		yangHuLuDuanIds=yangHuLuDuanIds.substring(0,yangHuLuDuanIds.length()-3);
		if (StringUtil.isNotEmpty(yangHuLuDuanIds)) {
			//根据养护路段查询桩号
			String sql = "select (startZHK*1000+startZHM) startZH,(endZHK*1000+endZHM) endZH "
						+"from DMS_PZ_yhLuDuan "
						+"where ids in ('"+yangHuLuDuanIds+"')";
			List<Record> list=Db.find(sql);
			if (list.size()>0) {
				for (int i = 0; i < list.size(); i++) {
					Record r = list.get(i);
					int startZH = r.getInt("startZH");
					int endZH = r.getInt("endZH");
					if (list.size()==1) {//当只有一个养护路段的时候
						val=" and ("+startK+"*1000+"+startM+") between "+startZH+" and "+endZH;
						break;
					}else if(i==0){//不止一个养护路段的时候的 开始的
						val=" and ( ("+startK+"*1000+"+startM+") between "+startZH+" and "+endZH;
					}else if(list.size()==i+1){//不止一个养护路段的时候的 最后的收尾的
						val+=" or ("+startK+"*1000+"+startM+") between "+startZH+" and "+endZH+" ) ";
					}else{//不止一个养护路段的时候  中间的
						val+="or ("+startK+"*1000+"+startM+") between "+startZH+" and "+endZH;
					}
				}
				return val;
			}else{
				return val;
			}
		}
		return val;
		
	}
	
	/**
	 * 根据用户的ids获取自身所在桩号范围
	 * @param userId
	 * @param startK
	 * @param startM
	 * @return
	 */
	public List<Map<String, Integer>> getZHFanWeibyUserId(String userId){
		List<Map<String, Integer>> list= null;
		//养护路段的ids
		String yangHuLuDuanIds="";
		//1.根据用户ids获取养护路段的ids
		Record yangHuLuDuan = Db.find("select yangHuLuDuanId from pt_user u,pt_userinfo ui where u.userinfoids=ui.ids	and u.ids='"+userId+"'").get(0);
		String[] yangHuLuDuanIdArray= yangHuLuDuan.get("yangHuLuDuanId").toString().split(",");
		//拼接sql条件
		for (int i = 0; i < yangHuLuDuanIdArray.length; i++) {
			yangHuLuDuanIds+=yangHuLuDuanIdArray[i]+"','";
		}
		yangHuLuDuanIds=yangHuLuDuanIds.substring(0,yangHuLuDuanIds.length()-3);
		if (StringUtil.isNotEmpty(yangHuLuDuanIds)) {
			//根据养护路段查询桩号
			String sql = "select (startZHK*1000+startZHM) startZH,(endZHK*1000+endZHM) endZH "
						+"from DMS_PZ_yhLuDuan "
						+"where ids in ('"+yangHuLuDuanIds+"')";
			List<Record> listr=Db.find(sql);
			if (listr.size()>0) {
				list=new ArrayList<Map<String, Integer>>();
				for (int i = 0; i < listr.size(); i++) {
					Record r = listr.get(i);
					int startZH = r.getInt("startZH");
					int endZH = r.getInt("endZH");
					Map<String, Integer> map=new HashMap<String, Integer>();
					map.put("startZH", startZH);
					map.put("endZH", endZH);
					list.add(map);
				}
				return list;
			}else{
				return list;
			}
		}
		return list;
	}
	/**
	 * 判断输入的桩号是否符合范围
	 * @param list
	 * @param zhk
	 * @param zhm
	 * @return
	 */
	public boolean judgeZHScope(List<Map<String, Integer>> list,int startZh,int endZh){
		boolean flag=false;
		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> map=list.get(i);
			int startZHbz = map.get("startZH");
			int endZHbz = map.get("endZH");
			if ((startZHbz<=startZh&&startZh<=endZHbz)&&(startZHbz<=endZh&&endZh<=endZHbz)) {
				return true;
			}
		}
		return flag;
	}
	
}