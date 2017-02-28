package com.seeyon.v3x.system.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.ldap.config.LDAPConfig;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.event.OrganizationEventComposite;
import com.seeyon.v3x.organization.event.OrganizationEventListener;
import com.seeyon.v3x.plugin.ldap.manager.OrganizationLdapEvent;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.system.util.PwdStrengthValidationUtil;

public class IndividualManagerController extends BaseController {

	private OrgManagerDirect orgManagerDirect;

	private AppLogManager appLogManager;
	private static final Log logger = LogFactory.getLog(IndividualManagerController.class);
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	// private OperationlogManager operationlogManager;
	private OrganizationEventListener eventListener = OrganizationEventComposite
			.getInstance();

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 进入个人管理维护页面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView managerFrame(HttpServletRequest request,
			HttpServletResponse response) {
		// 2017-2-27 诚佰公司 添加密码强制弹出修改
		String defaultView = "sysMgr/individual/individualManager";
		String pwdAlert = request.getParameter("pwdAlert");
		if (!StringUtils.isEmpty(pwdAlert)) {
			defaultView = "sysMgr/individual/pwdEdit";
		}
		ModelAndView result = new ModelAndView(defaultView);
		// 诚佰公司
		
		User user = CurrentUser.get();
		String logerName = user.getLoginName();
		result.addObject("logerName", logerName);
		result.addObject("initPage", true);
		// 读取是否启用密码强度检查
		result.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		return result;
	}
	
	/**
	 * 基础设施 个人管理员维护方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView modifyIndividual(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView(
				"sysMgr/individual/individualManager");
		
		// 2017-2-27 诚佰公司 添加密码强制弹出修改
		String pwdAlert = request.getParameter("pwdAlert");
		
		String password = request.getParameter("nowpassword");
		User user = CurrentUser.get();
		String logerName = user.getLoginName();
		try {
			V3xOrgMember member = orgManagerDirect.getMemberById(user.getId());
			V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
			V3xOrgMember newMember = new V3xOrgMember();
			BeanUtils.copyProperties(memberBeforeUpdate, member);
			member.setPassword(password);
			BeanUtils.copyProperties(newMember, member);
			orgManagerDirect.updateEntity(member);
			/*
			 * operationlogManager.insertOplog(member.getId(),
			 * ApplicationCategoryEnum.modifyPassword,
			 * "org.member_form.modify.password",
			 * "org.member_form.modify.password.self.label", user .getName());
			 */
			appLogManager.insertLog(user,
					AppLogAction.Update_Personal_Password, user.getName());
			if (LDAPConfig.getInstance().getIsEnableLdap()
					&& SystemEnvironment
							.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
									.name())) {
				OrganizationLdapEvent event = (OrganizationLdapEvent) ApplicationContextHolder
						.getBean("organizationLdapEvent");
				event.changePassword(memberBeforeUpdate, newMember);
				appLogManager.insertLog(user, AppLogAction.LDAP_Member_PassWord_Update, member.getName());
			}
			eventListener.changePassword(null,newMember);

			PrintWriter out = response.getWriter();
			
			if (pwdAlert == null || pwdAlert.isEmpty()){
				out.println("<script>");
				out.println("alert('"
						+ Constants.getString4CurrentUser("system.manager.ok")
						+ "')");
				out.println("parent.getA8Top().contentFrame.topFrame.backToPersonalSpace();");
				out.println("</script>");
				out.flush();
			} else {
				Map<String,Object> data = new HashMap<String, Object>();
				data.put("success", true);
				data.put("message", Constants.getString4CurrentUser("system.manager.ok"));
				out.println(new JSONObject(data));
				return null;
			}
		}catch (Exception e) {
			logger.error("",e);
			try {
				PrintWriter out = response.getWriter();
				if (pwdAlert == null || pwdAlert.isEmpty()){
					out.println("<script>");
					out.println("alert('"+Constants.getString4CurrentUser("system.manager.fail")+"')");
					out.println("parent.mainFrame.history.back();");
					out.println("</script>");
				} else {
					Map<String,Object> data = new HashMap<String, Object>();
					data.put("success", false);
					data.put("message", Constants.getString4CurrentUser("system.manager.fail"));
					out.println(new JSONObject(data));
					return null;
				}
			} catch (IOException e1) {
				logger.error("",e);
			}
		}
		/*// 从新获得登录名称
		result.addObject("logerName", logerName);
		// 从新获得是否启用密码强度检查
		result.addObject("pwdStrengthValidation", PwdStrengthValidationUtil
				.getPwdStrengthValidationValue());*/
		
		return null;
	}

	/*
	 * public OperationlogManager getOperationlogManager() { return
	 * operationlogManager; }
	 * 
	 * public void setOperationlogManager(OperationlogManager
	 * operationlogManager) { this.operationlogManager = operationlogManager; }
	 */

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}
}