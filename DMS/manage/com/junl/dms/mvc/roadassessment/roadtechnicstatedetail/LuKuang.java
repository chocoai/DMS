package com.junl.dms.mvc.roadassessment.roadtechnicstatedetail;

import org.apache.log4j.Logger;

import com.junl.dms.mvc.pingfen.Pingfen;
import com.platform.annotation.Table;
import com.platform.mvc.base.BaseModel;

@SuppressWarnings("unused")
@Table(tableName = "DMS_PZ_lukuang")
public class LuKuang extends BaseModel<LuKuang> {

	private static final long serialVersionUID = 8788890059361456690L;
	
	private static Logger log = Logger.getLogger(Pingfen.class);
	
	public static final Pingfen dao = new Pingfen();

}
