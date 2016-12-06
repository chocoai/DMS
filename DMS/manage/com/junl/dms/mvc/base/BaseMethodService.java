package com.junl.dms.mvc.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.junl.dms.mvc.jiaotong.JiaoTong;
import com.junl.dms.mvc.jiaotong.JiaoTongRelate;
import com.junl.dms.mvc.jiaotong.JiaoTongService;
import com.junl.dms.mvc.liefeng.LieFeng;
import com.junl.dms.mvc.liefeng.LieFengRelate;
import com.junl.dms.mvc.liefeng.LieFengService;
import com.junl.dms.mvc.roadassessment.roadtechnicstatedetail.LuKuang;
import com.junl.dms.mvc.rwtask.RwTask;
import com.junl.dms.mvc.waydisease.WayDisease;
import com.junl.dms.mvc.waydisease.WayDiseaseCeng;
import com.junl.dms.mvc.waydisease.WayDiseaseService;
import com.junl.dms.mvc.yanghu.YangHu;
import com.junl.dms.mvc.yanghu.YangHuRelate;
import com.junl.dms.mvc.yanghu.YangHuService;
import com.junl.dms.mvc.youwuchuli.YouWuService;
import com.junl.dms.mvc.youwuchuli.Youwu;
import com.platform.mvc.base.BaseService;


public class BaseMethodService extends BaseService {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(BaseMethodService.class);

	public static final BaseMethodService service = new BaseMethodService();

	public boolean batchSave(JSONArray jsonArr,RwTask rwTask,String taskInfoRelateId,String fileName,String userIds) {
		
	    for (int i = 0; i < jsonArr.size(); i++) {
	    	if(fileName.equals("IMPORT.POINTSINFO")){//导入油污处理 已测试
	    		Map<String, Object> objMap = new HashMap<String, Object>();
		    	objMap = jsonArr.getJSONObject(i);
	    		PointsInfo pointsInfo = new PointsInfo();
	    		pointsInfo.put(objMap);
	    		pointsInfo.save();
	    	}
	    	else if(fileName.equals("IMPORT.GONGLUJISHUZHUANGKUANG"))//导入路况处理 已测试
	    	{
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		Map<String, Object> objMap = new HashMap<String, Object>();
		    	objMap = jsonArr.getJSONObject(i);
		    	objMap.put("createDate",sdf.format(new Date()));
		    	LuKuang lukang = new LuKuang();
		    	lukang.put(objMap);
		    	lukang.save();
	    	}
	    	else{
	    		//封装
		    	Map<String, Object> objMap = new HashMap<String, Object>();
		    	objMap = jsonArr.getJSONObject(i);
		    	objMap.put("luXian", rwTask.get("luXian"));
		    	objMap.put("wzType", rwTask.get("wzType"));
		    	objMap.put("wzName", rwTask.get("wzName"));
		    	objMap.put("wzMiaoShu", rwTask.get("wzMiaoShu"));
		    	objMap.put("luBingType", rwTask.get("luBingType"));
		    	objMap.put("startZHK", rwTask.get("xunchastartzhk"));
		    	objMap.put("startZHM",rwTask.get("xunchastartzhm") );
		    	objMap.put("endZHK", rwTask.get("xunchaendzhk"));
		    	objMap.put("endZHM", rwTask.get("xunchaendzhm"));
		    	objMap.put("taskInfoRelate", taskInfoRelateId);
		    	objMap.put("createuserid", userIds);
		    	objMap.put("luBingType", rwTask.get("binghaiOrQueXianType")+" : "+ rwTask.get("yuGuGongChengLiang") + " (" + rwTask.get("yuGuGongChengLiangDw") + ")") ;
		    	//导入
		    	if(fileName.equals("IMPORT.YOUWUCHULI")){//导入油污处理 已测试
		    		Youwu youwu = new Youwu();
			    	youwu.put(objMap);
			    	YouWuService.service.save(youwu,null);
		    	}else if(fileName.equals("IMPORT.JIAOTONGANQUAN")){//导入交通安全数据 已测试
		    		JiaoTong jiaotong = new JiaoTong();
		    		jiaotong.put(objMap);
		    		List<JiaoTongRelate> jiaoTongRelateList = new ArrayList<JiaoTongRelate>();
			    	JiaoTongService.service.save(jiaotong,jiaoTongRelateList,null);// 待加入关联表数据
		    	}else if(fileName.equals("IMPORT.LIEFENG")){//导入裂缝维修数据 已测试
		    		LieFeng liefeng = new LieFeng();
		    		liefeng.put(objMap);
		    		List<LieFengRelate> lieFengRelateList = new ArrayList<LieFengRelate>();
		    		LieFengService.service.save(liefeng, lieFengRelateList, null);//第二个参数为空对象？！
		    	}else if(fileName.equals("IMPORT.LUMIANZAIHAI")){;// 待加入关联表数据
		    		WayDisease wayDisease = new WayDisease();
		    		wayDisease.put(objMap);
		    		WayDiseaseService.service.save(wayDisease, null);
		    		Map<String, Map<String, Object>> retMap = parsingMap(objMap);
		    		WayDiseaseCeng up = new WayDiseaseCeng();//获取上面层
		    		up.put(retMap.get("toMap"));
		    		
					WayDiseaseCeng middle = new WayDiseaseCeng();//获取中面层
					middle.put(retMap.get("moMap"));
					
					WayDiseaseCeng down = new WayDiseaseCeng();//获取下面层
					down.put(retMap.get("doMap"));
					
					WayDiseaseCeng basic = new WayDiseaseCeng();//基层
					basic.put(retMap.get("baMap"));
		    		
		    		WayDiseaseService.service.saveTable(up,wayDisease.getStr("ids"));
					WayDiseaseService.service.saveTable(middle,wayDisease.getStr("ids"));
					WayDiseaseService.service.saveTable(down,wayDisease.getStr("ids"));
					WayDiseaseService.service.saveTable(basic,wayDisease.getStr("ids"));
		    		
		    	}else if(fileName.equals("IMPORT.YANGHUTONGYONG")){//导入养护通用数据 已测试
		    		YangHu yanghu = new YangHu();
		    		yanghu.put(objMap);
		    		List<YangHuRelate> yangHuRelateList = new ArrayList<YangHuRelate>(); 
		    		YangHuService.service.save(yanghu, yangHuRelateList, null);//第二个参数为空对象？！
		    	}
	    	}
		}
		//后期增加异常处理  hank
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<String, Map<String, Object>> parsingMap(Map<String, Object> objMap){
		Map<String, Map<String, Object>> retMap = new HashMap<String, Map<String, Object>>();
		Map<String, Object> toMap = new HashMap<String, Object>();
		toMap.put("cengType", "1");
		Map<String, Object> moMap = new HashMap<String, Object>();
		moMap.put("cengType", "2");
		Map<String, Object> doMap = new HashMap<String, Object>();
		doMap.put("cengType", "3");
		Map<String, Object> baMap = new HashMap<String, Object>();
		baMap.put("cengType", "4");
		Iterator<Entry<String, Object>> it = objMap.entrySet().iterator();
		while(it.hasNext()){
			Entry entry = (java.util.Map.Entry)it.next();
			String key = entry.getKey().toString();
			Object value = entry.getValue();
			System.out.println(entry.getKey() +":" + entry.getValue());
			if(key.indexOf("up_")==0){
				toMap.put(key.replace("up_", ""), value);
			}
			if(key.indexOf("mo_")==0){
				moMap.put(key.replace("mo_", ""), value);
			}
			if(key.indexOf("do_")==0){
				doMap.put(key.replace("do_", ""), value);
			}
			if(key.indexOf("ba_")==0){
				baMap.put(key.replace("ba_", ""), value);
			}
		}
		retMap.put("toMap", toMap);
		retMap.put("moMap", moMap);
		retMap.put("doMap", doMap);
		retMap.put("baMap", baMap);
		return retMap;
	}
	
	
	/**
	 * 上传文件
	 * @param fileServerPath   文件位置
	 * @param fileName		        文件名  带后缀的
	 * @param inputStream	        输入流
	 * @throws IOException
	 */
	public static void copyInputStreamToFile(String fileServerPath,String fileName,InputStream inputStream) throws IOException
	{
		File file = new File(fileServerPath);
		//判断目录是否存在 
		if(!file.isDirectory())
		{
			//不存在则创建该目录  创建文件
			file.mkdirs();
		}
		// 文件读写
		FileOutputStream outputStream = new FileOutputStream(new File(fileServerPath+"/"+fileName));
		byte buffer[]=new byte[1024];
		int tmp = 0;
		while((tmp = inputStream.read(buffer))!=-1){
            for(int i = 0;i < tmp;i++)
            	outputStream.write(buffer[i]);        
        }
		inputStream.close();
		outputStream.close();
	}

}
