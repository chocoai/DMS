package com.junl.dms.mvc.monthplan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.junl.dms.mvc.baoyan.BaoYan;
import com.platform.annotation.Table;
import com.platform.mvc.base.BaseModel;
import com.platform.mvc.base.BaseModelCache;
import com.platform.mvc.param.Param;
import com.platform.plugin.ParamInitPlugin;
import com.platform.tools.ToolCache;
import com.test.mvc.blog.Blog;


@SuppressWarnings("unused")
@Table(tableName = "DMS_LH_yangHuMonth")
public class MonthPlan extends BaseModel<MonthPlan> {
	
	private static final long serialVersionUID = 5979434062234466436L;

	private static Logger log = Logger.getLogger(MonthPlan.class);
	
	public static final MonthPlan dao = new MonthPlan();
	
	public static final String sqlId_splitPageSelect = "manage.monthplan.splitPageSelect";
	public static final String sqlId_splitPageFrom = "manage.monthplan.splitPageFrom";
	
	
	public MonthPlan findByPlanMonth(String month){
		MonthPlan monthPlan;
		try {
			String sql = getSql("manage.monthplan.findByPlanMonth");
			monthPlan = MonthPlan.dao.find(sql,month).get(0);
			return monthPlan;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			
		}
	}
	
	
	
	
	
	
}
