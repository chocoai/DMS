package com.junl.dms.mvc.jiaotong;

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
@Table(tableName = "DMS_WX_jtaqssWeiXiuItem_relate")
public class JiaoTongRelate extends BaseModel<JiaoTongRelate> {

	private static final long serialVersionUID = 8279220916018574729L;

	private static Logger log = Logger.getLogger(JiaoTongRelate.class);
	
	public static final JiaoTongRelate dao = new JiaoTongRelate();
	
	
}
