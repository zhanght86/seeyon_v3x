package com.seeyon.v3x.system.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.organization.directmanager.SystemAdminManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.system.util.PwdStrengthValidationUtil;

@CheckRoleAccess(roleTypes={RoleType.SystemAdmin, RoleType.AuditAdmin, RoleType.SecretAdmin})
public class ManagerController extends BaseController {
	private final static Log log = LogFactory.getLog(ManagerController.class);
	private SystemAdminManager systemAdminManager;

	private AppLogManager appLogManager;
	private PrincipalManager principalManager;
	public PrincipalManager getPrincipalManager() {
		return principalManager;
	}

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	private ConfigManager configManager;
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setSystemAdminManager(SystemAdminManager systemAdminManager) {
		this.systemAdminManager = systemAdminManager;
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	private String systemName;
	private String adminName;
	private String adminPhone;
	private String adminEmail;
	private String auditName;
	private String secretName;

	/**
	 * 进入系统管理维护页面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView managerFrame(HttpServletRequest request,
			HttpServletResponse response) {
		// 2017-2-27 诚佰公司 添加密码强制弹出修改
		String defaultView = "sysMgr/manager/manager";
		String pwdAlert = request.getParameter("pwdAlert");
		if (!StringUtils.isEmpty(pwdAlert)) {
			defaultView = "sysMgr/manager/pwdEdit";
		}
		ModelAndView result = new ModelAndView(defaultView);
		// 诚佰公司

		User user = CurrentUser.get();
		boolean isShowMore = true;
		if(user.isSystemAdmin()){
			// 初始化登录人员的名称
			systemName = String.valueOf(configManager.getConfigItem("v3xorg_system_admin_definition","system_login_name").getConfigValue());
			adminName = String.valueOf(configManager.getConfigItem("v3xorg_system_admin_definition","system_admin_name").getConfigValue());
			adminPhone = String.valueOf(configManager.getConfigItem("v3xorg_system_admin_definition","system_admin_phone").getConfigValue());
			adminEmail = String.valueOf(configManager.getConfigItem("v3xorg_system_admin_definition","system_admin_email").getConfigValue());
			result.addObject("adminName", adminName);
			result.addObject("adminPhone", adminPhone);
			result.addObject("adminEmail", adminEmail);
			result.addObject("logerName", systemName);			
		}
		else if(user.isAuditAdmin()){ //审计管理员
			isShowMore = false;
			auditName = String.valueOf(configManager.getConfigItem(V3xOrgEntity.CONFIG_AUDIT_ADMIN_CATEGORY, V3xOrgEntity.CONFIG_AUDIT_ADMIN_NAME).getConfigValue());
			result.addObject("logerName", auditName);	
		}
		else if(user.isSecretAdmin()){ //安全管理员
            isShowMore = false;
            secretName = String.valueOf(configManager.getConfigItem(V3xOrgEntity.CONFIG_SECRET_ADMIN_CATEGORY, V3xOrgEntity.CONFIG_SECRET_ADMIN_NAME).getConfigValue());
            result.addObject("logerName", secretName);   
        }
		result.addObject("isShowMore", isShowMore);	
//		读取是否启用密码强度检查
		result.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		return result;
	}

	/**
	 * 基础设施 系统管理员维护方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView modifyManager(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		// 2017-2-27 诚佰公司 添加密码强制弹出修改
		String pwdAlert = request.getParameter("pwdAlert");
				
		String redirectURL = "/manager.do?method=managerFrame";
		User user = CurrentUser.get();
		PrintWriter out = null;

		String name = request.getParameter("name");
		String password = request.getParameter("password");
		String validate = request.getParameter("validatepass");
		String sysname = request.getParameter("system.name");
		String sysphone = request.getParameter("system.phone");
		String syseail = request.getParameter("system.email");
//		JetspeedPrincipal principal = null;
		String loginName;
		if(user.isSystemAdmin()){
			//principal = systemAdminManager.findPrincipalByFullPath(UserPrincipalUtil.getFullPathFromPrincipalName(systemName));
			loginName = systemName;
		}
		else if(user.isAuditAdmin()){
//			principal = systemAdminManager.findPrincipalByFullPath(UserPrincipalUtil.getFullPathFromPrincipalName(auditName));	
			loginName = auditName;
		}
        else if(user.isSecretAdmin()){
            loginName = secretName;			
		}else{
			out = response.getWriter();
			
			if (pwdAlert == null || pwdAlert.isEmpty()) {
				out.println("<script>");
				out.println("alert('登录用户不是系统管理员或审计管理员或安全管理员。')");
				out.println("</script>");
				out.flush();	
			} else {
				Map<String,Object> data = new HashMap<String, Object>();
				data.put("success", false);
				data.put("message", "登录用户不是系统管理员或审计管理员或安全管理员。");
				out.println(new JSONObject(data));
			}
			return null;
		}
// 		JetspeedCredential credential = systemAdminManager.findCredentialByColumnValue(principal.getPrincipalId());
		try {
			if (password == validate || password.equals(validate)) {
//				long memberId = principalManager.getMemberIdByLoginName(loginName);
/*				// 修改 Principal 表中的用户名称
				String fullPath = UserPrincipalUtil.getFullPathFromPrincipalName(name);
				principal.setFullPath(fullPath);
				systemAdminManager.updatePrincipalById(principal);
				// 修改 Credential 表中的密码
				credential.setColumnValue(password);
				credential.setUpdateTime(true);
				// 获取登录名称
				systemAdminManager.updateCredentialColumnValue(name,credential);*/
				
				if(!loginName.equals(name)){
					principalManager.delete(loginName);
					principalManager.add(-1L, -1L, name, password);
				}else{
					principalManager.changePassword(loginName, password);
				}
				// 修改 config 表中的用户名称
				if(user.isSystemAdmin()){
					ConfigItem configItem1 = configManager.getConfigItem("v3xorg_system_admin_definition","system_login_name");
					ConfigItem configItem2 = configManager.getConfigItem("v3xorg_system_admin_definition","system_admin_name");
					ConfigItem configItem3 = configManager.getConfigItem("v3xorg_system_admin_definition","system_admin_phone");
					ConfigItem configItem4 = configManager.getConfigItem("v3xorg_system_admin_definition","system_admin_email");
					configItem1.setConfigValue(name);
					configItem2.setConfigValue(sysname);
					configItem3.setConfigValue(sysphone);
					configItem4.setConfigValue(syseail);
					configManager.updateConfigItem(configItem1);
					configManager.updateConfigItem(configItem2);
					configManager.updateConfigItem(configItem3);
					configManager.updateConfigItem(configItem4);
					
				}
				else if(user.isAuditAdmin()){
					ConfigItem nameItem = configManager.getConfigItem(V3xOrgEntity.CONFIG_AUDIT_ADMIN_CATEGORY, V3xOrgEntity.CONFIG_AUDIT_ADMIN_NAME);
					nameItem.setConfigValue(name);
					configManager.updateConfigItem(nameItem);
					redirectURL += "&from=audit";
					auditName = name;
				}
                else if(user.isSecretAdmin()){
                    ConfigItem nameItem = configManager.getConfigItem(V3xOrgEntity.CONFIG_SECRET_ADMIN_CATEGORY, V3xOrgEntity.CONFIG_SECRET_ADMIN_NAME);
                    nameItem.setConfigValue(name);
                    configManager.updateConfigItem(nameItem);
                    redirectURL += "&from=secret";
                    secretName = name;
                }
				appLogManager.insertLog(user, AppLogAction.Systemmanager_UpdateAdminPassWord, user.getName(), user.getName());

				out = response.getWriter();
				
				if (pwdAlert == null || pwdAlert.isEmpty()) {
					out.println("<script>");
					out.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"')");
					out.println("</script>");
					out.flush();
					
					//日志
					return super.redirectModelAndView(redirectURL);
				} else {
					Map<String,Object> data = new HashMap<String, Object>();
					data.put("success", true);
					data.put("message", Constants.getString4CurrentUser("system.manager.ok"));
					out.println(new JSONObject(data));
					
					return null;
				}
			}
			else {
				out = response.getWriter();
				
				if (pwdAlert == null || pwdAlert.isEmpty()) {
					out.println("<script>");
					out.println("alert('密码和验证码不一致！！！')");
					out.println("</script>");
					out.flush();
				} else {
					Map<String,Object> data = new HashMap<String, Object>();
					data.put("success", false);
					data.put("message", "密码和验证码不一致！！！");
					out.println(new JSONObject(data));
				}
				
				return null;
			}
		}
		catch (Exception e) {
			log.error("", e);
			
			if (pwdAlert == null || pwdAlert.isEmpty()) {
				out.println("<script>");
				out.println("alert('源码错误！！！')");
				out.println("</script>");
			} else {
				Map<String,Object> data = new HashMap<String, Object>();
				data.put("success", false);
				data.put("message", "源码错误！！！");
				out.println(new JSONObject(data));
			}
			
			return null;
		}
	}
}
