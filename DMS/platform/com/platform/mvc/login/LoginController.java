package com.platform.mvc.login;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.platform.annotation.Controller;
import com.platform.constant.ConstantLogin;
import com.platform.constant.ConstantWebContext;
import com.platform.interceptor.AuthInterceptor;
import com.platform.mvc.base.BaseController;
import com.platform.mvc.user.User;
import com.platform.tools.ToolWeb;
import com.platform.tools.security.ToolIDEA;

/**
 * 登陆处理
 */
@Controller(controllerKey = "/jf/platform/login")
public class LoginController extends BaseController {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(LoginController.class);
	
	/**
	 * 准备登陆
	 */
	public void index() {
		User user = getCUser(); // cookie认证自动登陆处理
		String error=getPara("type");
		if(error == null) {
			ToolWeb.addCookie(getResponse(), "", "/", true, ConstantWebContext.cookie_authmark, null, 0);
			render("/platform/login/login.html");
		} else {
			if(null != user){//后台
				if (error.equals("error")) {//是否登陆失败
					ToolWeb.addCookie(getResponse(), "", "/", true, ConstantWebContext.cookie_authmark, null, 0);
					render("/platform/login/login.html");
				}
				redirect("/jf/platform/");
			}else{
				render("/platform/login/login.html");
			}
		}
	}

	/**
	 * 第三方系统P3P登陆
	 * 最后面URL的参数可以是UIB中加密过的认证字符串，也可以是其他协定好的加密串，加密串里面主要存放的是用户的id或者账号
	 * <script type="text/javascript" src="http://www.uib.com/jf/platform/login/p3p/RUdtNVpET1E5ZWF6bFNFTGJDa0dzK2E1NURXYTF5TXpBay8zZ0p
	 * pN040SDd1bWI5OVFtTlJkdTh1ZVRnbU1Cem42MGxBVEx1U2lOUVBKYTNDdmhiVGpNL1VKQkVKdHJ5U0xFZXJ3aFpCd0pobUJRTWQvbWNCRFYzMFZ3aXM0dU1oWjFMVWZPWVd
	 * 1N2hxWjBnNjk2Y29sMmVtSDdlR3A5alZ4aGdvNnZWNGRhMlhFUkhDU0ZIOVZvVExRL2hiekpS"></script>
	 */
	public void p3p() {
		String act = getPara();
		if(null != act){
			// 1.解密认证数据
			String data = ToolIDEA.decrypt(act);
			String[] datas = data.split(".#.");	//arr[0]：时间戳，arr[1]：USERID，arr[2]：USER_IP， arr[3]：USER_AGENT
			
			// 2. 分解认证数据
			String userIds = datas[1]; // 用户id
			User user = User.dao.cacheGet(userIds);
			if(user != null){
				getResponse().setHeader("P3P", "CP=\"NON DSP COR CURa ADMa DEVa TAIa PSAa PSDa IVAa IVDa CONa HISa TELa OTPa OUR UNRa IND UNI COM NAV INT DEM CNT PRE LOC\""); 
				AuthInterceptor.setCurrentUser(getRequest(), getResponse(), user, false);
				renderText("success");
				return;
			}
		}
		renderText("error");
	}

	/**
	 * 验证账号是否可用
	 */
	public void valiUserName(){
		String userIds = getPara("userIds");
		String userName = getPara("userName");
		
		boolean bool = LoginService.service.valiUserName(userIds, userName);
		renderText(String.valueOf(bool));
	}
	
	/**
	 * 验证邮箱是否可用
	 */
	public void valiMailBox(){
		String userInfoIds = getPara("userInfoIds");
		String mailBox = getPara("mailBox");
		boolean bool = LoginService.service.valiMailBox(userInfoIds, mailBox);
		renderText(String.valueOf(bool));
	}

	/**
	 * 验证身份证是否可用
	 */
	public void valiIdcard(){
		String userInfoIds = getPara("userInfoIds");
		String idcard = getPara("idcard");
		boolean bool = LoginService.service.valiIdcard(userInfoIds, idcard);
		renderText(String.valueOf(bool));
	}

	/**
	 * 验证手机号是否可用
	 */
	public void valiMobile(){
		String userInfoIds = getPara("userInfoIds");
		String mobile = getPara("mobile");
		boolean bool = LoginService.service.valiMobile(userInfoIds, mobile);
		renderText(String.valueOf(bool));
	}

	/**
	 * 登陆验证
	 */
	//@Before(LoginValidator.class)
	public void vali() {
		// 获取表单信息
//		String username = getPara("username");
//		String password = getPara("password");
//		String remember = getPara("remember");
//		String returnJson = getPara("returnText");

		// 如果是httpclient登陆就不处理验证码，不用担心密码暴力破解，因为init文件有密码错误次数限制
//		if(null != returnJson && !returnJson.isEmpty()){
//			int result = LoginService.service.login(getRequest(), getResponse(), username, password, false);
//			if(result == ConstantLogin.login_info_3){ // 登陆验证成功
//				renderText("success");
//				return;
//			}
//		}else{
//			//boolean authCode = authCode(); // 验证验证码
//			//if(authCode){
//				boolean autoLogin = false;
//				if(null != remember && remember.equals("1")){ // 是否选中记住密码自动登陆
//					autoLogin = true;
//				}
//				
//				int result = LoginService.service.login(getRequest(), getResponse(), username, password, autoLogin);
//				if(result == ConstantLogin.login_info_3){ // 登陆验证成功
//					redirect("/jf/platform/index");
//					return;
//				}
//			//}
//		}
//		redirect("/jf/platform/login");
//		
		JSONObject jsonObject=new JSONObject();
		try {
			String username = getPara("username");
			String password = getPara("password");
			String remember = getPara("remember");
			boolean autoLogin = false;
			if(null != remember && remember.equals("1")){ // 是否选中记住密码自动登陆
				autoLogin = true;
			}
			int result = LoginService.service.login(getRequest(), getResponse(), username, password, autoLogin);
			String result_desc="";
			switch(result){
				case 0:
					result_desc="账号不存在！";
					break;
				case 1:
					result_desc="账号已停用！";
					break;
				case 2:
					result_desc="密码错误次数超限！";
					break;
				case 4:
					result_desc="密码错误！";
					break;
			}
			
			jsonObject.put("result", result);
			jsonObject.put("result_desc", result_desc);
		} catch (Exception e) {
			e.printStackTrace();
			jsonObject.put("result", false);
			jsonObject.put("result_desc", "操作失败！");
		}
		renderJson(jsonObject.toJSONString());
	}

	/**
	 * 锁屏验证密码
	 */
	@Before(LoginValidator.class)
	public void pass() {
		User user = getCUser(); // 获取当前用户
		String password = getPara("password"); // 获取输入的密码
		int result = LoginService.service.pass(getRequest(), getResponse(), user.getStr("username"), password);
		if(result == ConstantLogin.login_info_3){ // 密码验证成功
			redirect("/jf/platform/index");
			return;
		}
		
		redirect("/jf/platform/login");
	}

	/**
	 * 注销
	 */
	public void logout() {
		ToolWeb.addCookie(getResponse(), "", "/", true, ConstantWebContext.cookie_authmark, null, 0);
		redirect("/jf/platform/login");
	}

}
