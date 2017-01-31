package com.seeyon.v3x.plugin.ca;

import javax.servlet.http.HttpServletRequest;

import com.itrus.cvm.CVM;
import com.seeyon.v3x.common.constants.LoginResult;
import com.seeyon.v3x.login.LoginAuthenticationException;

public interface IcaManager {
	/**
	 * CA认证，在此处实现各厂商的认证过程。主要调用个厂商的开发包。
	 * @param request 请求参数都包含在request中，需和前段页面配合使用。
	 * @return  认证接口返回值约定：
	 *		   当验证通过，必给框架返回[用户名(*), 密码(option)]，框架将直接跳转到首页。例如：String[]{username,password};
	 *		   当不验证通过，但要终止本次登录请求，直接throw new LoginAuthenticationException(), 用户将跳转到登录页
	 *		   当不验证通过，返回null，框架将调用下一个认证类认证 
	 * @throws LoginAuthenticationException  异常约定： 
	 * 			当key 过期时，抛出	throw new LoginAuthenticationException(LoginResult.ERROR_KEY_OVERDUE);
	 *			当key 被吊销，抛出	throw new LoginAuthenticationException(LoginResult.ERROR_KEY_FORBIDDEN);
	 *			当key 过期并被吊销，抛出	throw new LoginAuthenticationException(LoginResult.ERROR_KEY_FORBIDDENOVERDUE);
	 *			当其他错误时，抛出	throw new LoginAuthenticationException(LoginResult.ERROR_CA_SERVERERROR);
	 */
	public String[] validateCA(HttpServletRequest request) throws LoginAuthenticationException ;

}
