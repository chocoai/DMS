package com.junl.dms.mvc.xunchatoulist;

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
@Table(tableName = "DMS_XC_xunChaTou")
public class XunChaTouList extends BaseModel<XunChaTouList> {
	
	private static final long serialVersionUID = -1190698932401852485L;

	private static Logger log = Logger.getLogger(XunChaTouList.class);
	
	public static final XunChaTouList dao = new XunChaTouList();
	public static final String sqlId_splitPageSelect = "manage.xunchatou.splitPageSelect";
	public static final String sqlId_splitPageFrom = "manage.xunchatou.splitPageFrom";
}