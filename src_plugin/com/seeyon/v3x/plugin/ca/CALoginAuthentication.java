package com.seeyon.v3x.plugin.ca;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.IP;
import com.seeyon.v3x.common.authenticate.domain.LoginUtil;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.constants.LoginConstants;
import com.seeyon.v3x.common.constants.LoginResult;
import com.seeyon.v3x.common.constants.SystemProperties;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.login.LoginAuthentication;
import com.seeyon.v3x.login.LoginAuthenticationException;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.plugin.ca.caaccount.domain.CAAccount;
import com.seeyon.v3x.plugin.ca.caaccount.manager.CAAccountManager;
import com.seeyon.v3x.util.Strings;

/** 
 * 使用CA验证，根据配置选取对应的CA厂商实现。
 * 
 * @author LeiGuangFa
 * @version 1.0 2011-11-17
 */
public class CALoginAuthentication  implements LoginAuthentication {
	private static final Log log = LogFactory.getLog(CALoginAuthentication.class);

	private IcaManager caManager;
	private CAAccountManager caAccountManager; 
	private PrincipalManager principalManager = null;
	private ConfigManager configManager;

	public static final String ConfigItem_MustCheckCA = "MustCheckCA";

	public static final String ConfigCategory = "IdentificationValidateCA";
	private String caFactory = SystemProperties.getInstance().getProperty("ca.factory");
	
	public CALoginAuthentication(){
		if(caManager == null){
			caManager = (IcaManager)ApplicationContextHolder.getBean("caManager"+caFactory);
			caAccountManager=(CAAccountManager)ApplicationContextHolder.getBean("caAccountManager");
		}
		if(principalManager == null){
			principalManager = (PrincipalManager)ApplicationContextHolder.getBean("principalManager");
		}
		if (configManager == null) {
			configManager = (ConfigManager)ApplicationContextHolder.getBean("configManager");
		}
	}
	
	public String[] authenticate(HttpServletRequest request,
			HttpServletResponse response) throws LoginAuthenticationException {
		if(!SystemEnvironment.hasPlugin("ca")){
			return null; 
		}
		//厂商为天威诚信 
		if("iTrus".equals(caFactory)){
			String username =request.getParameter(LoginConstants.USERNAME);//用户名
			String password = request.getParameter(LoginConstants.PASSWORD);//密码
			String caCertMark = Strings.escapeNULL(request.getParameter("caCertMark"), "noCaCert");
			String userAgentFrom = request.getParameter(Constants.LOGIN_USERAGENT_FROM);
			//登陆方式,验证移动应用登陆
			boolean fromMobile = Constants.login_useragent_from.mobile.name().equals(userAgentFrom)|| LoginUtil.isFromM1(userAgentFrom);
			
			CAAccount account = caAccountManager.findByLoginName(username);
			
			if(account != null && account.isCaState() && fromMobile && account.isMobileEnable() ){//移动不受控制
				return null;
			}
			
			/*
			 * 1. 我的配置必须使用CA登录，这种情况下，忽略大开关和IP范围的控制
			 * 2. 如果前端没有CACert，就失败 
			 */
			if(account != null && account.isCaState() && account.isCaEnable()){
				//客户端必须经过CA校验
				if(caCertMark.equals("noCaCert")){
					throw new LoginAuthenticationException(LoginResult.ERROR_CA_MUSTHASCA_CERTIFICATEORHARDWARE);
				}
				else if(caCertMark.equals("noCaCertMatching")){
					throw new LoginAuthenticationException(LoginResult.ERROR_CA_MUSTUSECALOGIN);
				}
				
				return checkCA(request);
			}
			
			if(caCertMark.indexOf("noCaCert") > -1){//无CA证书
				ConfigItem configItem = configManager.getConfigItem(ConfigCategory, ConfigItem_MustCheckCA);
				if(configItem != null && "true".equals(configItem.getConfigValue())){//CA配置中的开关开启
					String requestIp = configItem.getExtConfigValue();
					boolean IPisIncluding = checkIP(requestIp, Strings.getRemoteAddr(request));
					if(IPisIncluding && !fromMobile){//IP不处于例外范围,并且来自PC端
						return null;
					}
					
					throw new LoginAuthenticationException(LoginResult.ERROR_CA_IP_ISNOTINCLUDING); 
				}
				
				if(account == null){
					return null;
				}
			}
			else{//有CA证书
				return checkCA(request);
			}
		}
		//厂商为格尔
		else if("koal".equals(caFactory)){
			String username =request.getParameter(LoginConstants.USERNAME);//用户名
			
			//当想只通过CA校验后，直接进入协同首页，但协同没有绑定该用户账号，那么会显示协同登录页面，这时候登录就走协同普通登录校验流程了
			//就不再管是否账号匹配了，因为根本还没有绑定关系
			String A8Validate = String.valueOf(request.getSession().getAttribute("A8Validate"));
			if("true".equals(A8Validate)){
				request.getSession().removeAttribute("A8Validate");
				return null;
			}
			
			String[] str = new String[2];
			//需要在login.jsp中传入CA序列号
			String keyNum = request.getParameter("keyNum");
			String loginname = caAccountManager.findLoginName(keyNum);
			
			//是否进行协同账号的二次校验
			String dualvalidate = SystemProperties.getInstance().getProperty("ca.dualvalidate");
			/*如果是二次校验,需要校验CA key里绑定的账号和输入的账号是否一致*/
			if("true".equals(dualvalidate)){
				//如果没有绑定账号，就不看匹配了
				if (loginname == null || loginname.equals("")) {
					return null;
				}
				//绑定了，就要看是否一致
				else{
					if(username.equals(loginname)){
						return null;
					}
					//CA key里绑定的账号和输入的账号不一致
					else{
						//提示[没有这个用户]
						throw new LoginAuthenticationException(LoginResult.ERROR_UNKNOWN_USER);
					}
				}
			}
			
			/*下面是只在CA端校验的情况 (一次校验)*/
			else{
				//这时候用户 没有输入登录名，username为null
				
				//没有绑定CA key与协同账号的用户，不能直接到协同系统首页，需要在登录页面进行输入再提交   (不论是否配置是否需要二次校验)
				if (loginname == null || loginname.equals("")) {
					//在session中存入一个标志，表示用户手动输入登录名和密码时，走协同原来的检验流程 ( 是否自动提交也就可以用这个判断了)
					request.getSession().setAttribute("A8Validate", "true");
					//提示[用户没有绑定账号，与管理员联系]
					throw new LoginAuthenticationException(LoginResult.ERROR_KEY_ACCOUNT_NOTBUNDED);
				}else{
					//用户没有在登录页面输入登录名(也就是直接一次校验的情况)
					str[0] = loginname;  
					return str;
				}
			}
		}
		return null;
	}
	
	private String[] checkCA(HttpServletRequest request) throws LoginAuthenticationException{
		String username = null;
		String[] keyStr = caManager.validateCA(request);
		String caBundtype = SystemProperties.getInstance().getProperty("ca.bundtype");
		
		if(caBundtype.equals("account")){// 根据序列号获取协同账号
			username = caAccountManager.findLoginName(keyStr[0]);
			if (username == null || username.equals("")) {
				throw new LoginAuthenticationException(LoginResult.ERROR_KEY_ACCOUNT_NOTBUNDED);
			}
		}
		else{
			if (keyStr[1] == null || keyStr[1].equals("")) {
				throw new LoginAuthenticationException(LoginResult.ERROR_KEY_ACCOUNT_NOTBUNDED);
			}
			
			username = keyStr[1];
			
			if(!principalManager.isExist(username)){
				throw new LoginAuthenticationException(LoginResult.ERROR_UNKNOWN_USER);
			}
		}
		
		return null;
	}
	
	/**
	 * 如果remoteIP在ipstr的范围内，返回true
	 * @param ipstr
	 * @param remoteIP
	 * @return
	 */
	private static boolean checkIP(String ipstr, String remoteIP) {
		if(Strings.isBlank(ipstr)){
			return false;
		}
		
		String[] ips = ipstr.split(";");
		for (String ip : ips) {
			if(new IP(ip).matching(remoteIP)){
				return true;
			}
		}
		
		return false;
	}
}
