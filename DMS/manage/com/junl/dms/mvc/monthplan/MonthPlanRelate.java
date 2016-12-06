package com.junl.dms.mvc.monthplan;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.platform.annotation.Table;
import com.platform.mvc.base.BaseModel;
import com.platform.mvc.base.BaseModelCache;
import com.platform.mvc.param.Param;
import com.platform.plugin.ParamInitPlugin;
import com.platform.tools.ToolCache;
import com.test.mvc.blog.Blog;


@SuppressWarnings("unused")
@Table(tableName = "DMS_LH_yangHuMonth_day_relate")
public class MonthPlanRelate extends BaseModel<MonthPlanRelate> {
	
	private static final long serialVersionUID = 5979434062234466436L;

	private static Logger log = Logger.getLogger(MonthPlanRelate.class);
	
	public static final MonthPlanRelate dao = new MonthPlanRelate();
	
	public MonthPlanRelate findPlanMonthRelateByDayIds(String dayIds){
		MonthPlanRelate monthPlan=null;
		try {
			String sql = getSql("manage.monthplan.findPlanMonthRelateByDayIds");
			monthPlan = MonthPlanRelate.dao.find(sql,dayIds).get(0);
			return monthPlan;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			
		}
	}
}