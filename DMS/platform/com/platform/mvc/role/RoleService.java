package com.platform.mvc.role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.platform.mvc.base.BaseService;
import com.platform.mvc.user.User;
import com.platform.mvc.user.UserInfo;
import com.platform.tools.StringUtils;

public class RoleService extends BaseService {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(RoleService.class);

	public static final RoleService service = new RoleService();

	/**
	 * 保存
	 * 
	 * @param role
	 * @return
	 */
	public String save(Role role) {
		// 保存
		role.save();
		return role.getPKValue();
	}

	/**
	 * 更新
	 * 
	 * @param role
	 */
	public void update(Role role) {
		// 更新
		role.update();

		// 缓存
		Role.dao.cacheAdd(role.getPKValue());
	}

	/**
	 * 删除
	 * 
	 * @param role
	 */
	public void delete(String ids) {
		String[] idsArr = splitByComma(ids);
		for (String roleIds : idsArr) {
			// 缓存
			Role.dao.cacheRemove(roleIds);

			// 删除
			Role.dao.deleteById(roleIds);
		}
	}

	/**
	 * 设置角色功能
	 * 
	 * @param roleIds
	 * @param moduleIds
	 * @param operatorIds
	 */
	public void setOperator(String roleIds, String moduleIds, String operatorIds) {
		Role role = Role.dao.findById(roleIds);
		// role.set("moduleids", moduleIds);
		role.set(Role.column_operatorids, operatorIds).update();

		// 缓存
		Role.dao.cacheAdd(roleIds);
	}

	/**
	 * 组角色选择
	 * 
	 * @param ids
	 *            用户ids
	 */
	public Map<String, Object> select(String ids) {
//		List<Role> noCheckedList = new ArrayList<Role>();
//		List<Role> checkedList = new ArrayList<Role>();
//		String roleIds = Group.dao.findById(ids).getStr(Group.column_roleids);
//		if (null != roleIds && !roleIds.equals("")) {
//			String fitler = sqlIn(roleIds);
//
//			Map<String, Object> param = new HashMap<String, Object>();
//			param.put("fitler", fitler);
//
//			noCheckedList = Role.dao.find(getSqlByBeetl(Role.sqlId_noCheckedFilter, param));
//			checkedList = Role.dao.find(getSqlByBeetl(Role.sqlId_checkedFilter, param));
//		} else {
//			noCheckedList = Role.dao.find(getSql(Role.sqlId_noChecked));
//		}
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("noCheckedList", noCheckedList);
//		map.put("checkedList", checkedList);
//		return map;
		
		List<Role> noCheckedList = new ArrayList<Role>();
		List<Role> checkedList = new ArrayList<Role>();
		String roleIds = User.dao.findById(ids).getStr(User.column_groupids);
		if (null != roleIds && !roleIds.equals("")) {
			String fitler = sqlIn(roleIds);

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("fitler", fitler);

			noCheckedList = Role.dao.find(getSqlByBeetl(Role.sqlId_noCheckedFilter, param));
			checkedList = Role.dao.find(getSqlByBeetl(Role.sqlId_checkedFilter, param));
		} else {
			noCheckedList = Role.dao.find(getSql(Role.sqlId_noChecked));
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("noCheckedList", noCheckedList);
		map.put("checkedList", checkedList);
		return map;
		
	}
	/**
	 * 手机权限
	 * @param ids
	 * @return
	 */
	public Map<String, Object> selectPhone(String ids) {
		
		//根据用户信息查询用户的手机权限
		Record r=Db.find("select phoneRole from pt_role where ids='"+ids+"' ").get(0);
		String phoneRole=r.get("phoneRole");
		String[] phoneRole_s=StringUtils.changNull(phoneRole).split(",");
		//到配置文件中查询手机的权限的菜单
		String phoneRole_p=PropKit.get("phone.role");
		//手机权限的菜单
		List<RolePhone> list_p=new ArrayList<RolePhone>();
		String[] phoneRole_array=phoneRole_p.split(",");
		//分隔手机权限的菜单
		for (int i = 0; i < phoneRole_array.length; i++) {
			String[] str=phoneRole_array[i].split("\\:");
			RolePhone rp=new RolePhone();
			rp.setKey(str[0]);
			rp.setValue(str[1]);
			list_p.add(rp);
		}
		//分离已选择和未选择的
		List<RolePhone> checkedList=new ArrayList<RolePhone>();
		List<RolePhone> noCheckedList=new ArrayList<RolePhone>();
		if (phoneRole_s.length==0||phoneRole_s[0]=="") {
			//如果在pt_role中是空的值，就将查询到的所有的数据给未选中的集合
			noCheckedList.addAll(list_p);
		}else{
			for (int i = 0; i < list_p.size(); i++) {
				RolePhone rp=list_p.get(i);
				for (int j = 0; j < phoneRole_s.length; j++) {
					if (rp.getKey().equals(phoneRole_s[j])) {
						checkedList.add(rp);
						break;
					}
				}
			}
			//将选中的数据从配置文件中抽取的数据中减去
			list_p.removeAll(checkedList);
			noCheckedList.addAll(list_p);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("noCheckedList", noCheckedList);
		map.put("checkedList", checkedList);
		return map;
	}

	public void setPhone(String ids, String phoneRoles) {
		int i=Db.update("update pt_role set phoneRole='"+phoneRoles+"' where ids='"+ids+"'");
		System.out.println(i);
	}

}
