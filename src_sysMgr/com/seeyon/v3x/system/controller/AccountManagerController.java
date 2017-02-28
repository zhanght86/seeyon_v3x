package com.seeyon.v3x.system.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.main.AccountSymbol;
import com.seeyon.v3x.main.MainDataLoader;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.system.util.PwdStrengthValidationUtil;
import com.seeyon.v3x.util.Base64;
import com.seeyon.v3x.util.Datetimes;

public class AccountManagerController extends BaseController {
    private static final Log log = LogFactory.getLog(AccountManagerController.class);
    private OrgManagerDirect orgManagerDirect;
	private ConfigManager configManager;
    private OrgManager orgManager;
	private AppLogManager appLogManager;
	private FileManager fileManager;

	public AppLogManager getAppLogManager() {
		return appLogManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
    @Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 进入单位管理维护页面方法
	 */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.SystemAdmin})
	public ModelAndView managerFrame(HttpServletRequest request,
			HttpServletResponse response) {
    	// 2017-2-27 诚佰公司 添加密码强制弹出修改
		String defaultView = "sysMgr/account/accountManager";
		String pwdAlert = request.getParameter("pwdAlert");
		if (!StringUtils.isEmpty(pwdAlert)) {
			defaultView = "sysMgr/account/pwdEdit";
		}
		ModelAndView result = new ModelAndView(defaultView);
		// 诚佰公司
    			
		String logerName = null;
		try {
			logerName = orgManagerDirect.getAccountById(CurrentUser.get().getLoginAccount()).getAdminName();
		} catch (BusinessException e) {
			logger.error("", e);
		}
		result.addObject("logerName", logerName);
//		读取是否启用密码强度检查
		result.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		return result;
	}
    
	@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
	public ModelAndView groupManagerFrame(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/account/groupManager");
		User user = CurrentUser.get();
		try {	
			V3xOrgRole role = orgManagerDirect.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_GROUPADMIN);
			List<V3xOrgMember> memberList = orgManagerDirect.getMemberByRole(V3xOrgEntity.ROLE_BOND_ACCOUNT, user.getLoginAccount(), role.getId());
		    String groupAdminIds = "";
			String groupAdminNames = "";
			if(memberList!=null&&(!ListUtils.EMPTY_LIST.equals(memberList))){
				for(V3xOrgMember member:memberList){
					groupAdminIds += member.getId()+",";
					groupAdminNames += member.getName()+",";
				}
			}
			if(StringUtils.isNotBlank(groupAdminIds)&&StringUtils.isNotBlank(groupAdminNames)){
				result.addObject("groupAdminIds", groupAdminIds.substring(0, groupAdminIds.length()-1));
				result.addObject("groupAdminNames", groupAdminNames.substring(0, groupAdminNames.length()-1));				
			}
		} catch (BusinessException e) {
			logger.error("", e);
		}
		result.addObject("logerName", user.getLoginName());
//		读取是否启用密码强度检查
		result.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		return result;
	}

	/**
	 * 集团管理员关联到人
	 */	
	@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
	public ModelAndView groupToUserManager(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/account/groupToUserManager");
		User user = CurrentUser.get();
		try {	
			V3xOrgRole role = orgManagerDirect.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_GROUPADMIN);
			List<V3xOrgMember> memberList = orgManagerDirect.getMemberByRole(V3xOrgEntity.ROLE_BOND_ACCOUNT, user.getLoginAccount(), role.getId());
		    String groupAdminIds = "";
			String groupAdminNames = "";
			if(memberList!=null&&(!ListUtils.EMPTY_LIST.equals(memberList))){
				for(V3xOrgMember member:memberList){
					groupAdminIds += member.getId()+",";
					groupAdminNames += member.getName()+",";
				}
			}
			if(StringUtils.isNotBlank(groupAdminIds)&&StringUtils.isNotBlank(groupAdminNames)){
				result.addObject("groupAdminIds", groupAdminIds.substring(0, groupAdminIds.length()-1));
				result.addObject("groupAdminNames", groupAdminNames.substring(0, groupAdminNames.length()-1));				
			}
		} catch (BusinessException e) {
			logger.error("", e);
		}
		result.addObject("logerName", user.getLoginName());
		return result;
	}


	/**
	 * 基础设施 单位管理员维护方法
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.SystemAdmin})
	public ModelAndView modifyManager(HttpServletRequest request,
			HttpServletResponse response) {
		// 2017-2-27 诚佰公司 添加密码强制弹出修改
		String pwdAlert = request.getParameter("pwdAlert");
				
		ModelAndView result = new ModelAndView("sysMgr/account/accountManager");
		PrintWriter out = null;
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		V3xOrgMember member = new V3xOrgMember();
		User user = CurrentUser.get();
		try {
			V3xOrgAccount account = orgManagerDirect.getAccountById(user.getLoginAccount());
			member = orgManagerDirect.getMemberByLoginName(account.getAdminName());
			
			if (member == null) {
				//按登录名找不到管理员，按人员的信息找管理员（isAdmin、orgAccountId、name）
				List<V3xOrgEntity> adminList = orgManager.getEntity(V3xOrgMember.class.getSimpleName(), "isAdmin", true, account.getId());
				String adminName = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.account_form.adminName.value", "");
				for (V3xOrgEntity admin : adminList) {
					if (adminName.equals(admin.getName())) {
						member = (V3xOrgMember) admin;
						break;
					}
				}
			}
			
			member.setLoginName(name);
			member.setPassword(password);
			orgManagerDirect.updateEntity(member);
			account.setAdminName(name);
			orgManagerDirect.updateEntity(account);
			//切换新的帐号
			String oldLoginName = user.getLoginName();
			if(!oldLoginName.equalsIgnoreCase(name)){
				 user.setLoginName(name);
				 CurrentUser.set(user);
				 OnlineUser ou = OnlineRecorder.getOnlineAdmin(oldLoginName);
				 ou.setLoginName(name);
				 OnlineRecorder.loginAdmin(user, ou);
				 OnlineRecorder.removeAdmin(oldLoginName);
			}
			//日志
			appLogManager.insertLog(user, AppLogAction.Systemmanager_UpdateAdminPassWord, user.getName(),member.getName());

		} catch (BusinessException e1) {
			logger.error("", e1);
		}
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			logger.error("", e1);
		}
		
		if (pwdAlert == null || pwdAlert.isEmpty()) {
			out.println("<script>");
			out.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"')");
			out.println("parent.location='accountManager.do?method=managerFrame';");
			out.println("</script>");
			out.flush();
		} else {
			Map<String,Object> data = new HashMap<String, Object>();
			data.put("success", true);
			data.put("message", Constants.getString4CurrentUser("system.manager.ok"));
			out.println(new JSONObject(data));
			return null;
		}
		
		// 从新获得登录名称
		result.addObject("logerName", name);
//		读取是否启用密码强度检查
		result.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		
//		return result;
		return null;
	}

	@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
	public ModelAndView modifyGroupManager(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/account/groupManager");
		PrintWriter out = null;
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		V3xOrgMember member = new V3xOrgMember();
		User user = CurrentUser.get();
		String logerName = request.getParameter("name");
		try {
			V3xOrgAccount account = orgManagerDirect.getAccountById(user.getLoginAccount());
			boolean isLoginNameChanged = !name.equals(account.getAdminName());
			//用户修改了密码
			account.setAdminName(name);
			orgManagerDirect.updateEntity(account);
		    member = orgManagerDirect.getMemberByLoginName(user.getLoginName());		   
			member.setLoginName(name);
			member.setPassword(password);
			orgManagerDirect.updateEntity(member);	
			//日志
			appLogManager.insertLog(user, AppLogAction.Systemmanager_UpdateAdminPassWord, user.getName(), user.getName());
			logerName = account.getAdminName();
            if(isLoginNameChanged){
            	// 管理员登录名改变，强制下线
            	OnlineRecorder.moveToOffline(user.getLoginName(), LoginOfflineOperation.adminKickoff);
            }
			//设置集团管理员角色匹配
			/*
			V3xOrgRole role = orgManagerDirect.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_GROUPADMIN);
			String groupAdminIds = request.getParameter("groupAdminIds");
			String groupAdminNames = request.getParameter("groupAdminNames");
			if(StringUtils.isNotBlank(groupAdminIds)){
				String[] userIds = groupAdminIds.split(",");
				Long[] groupIds = new Long[userIds.length];
				if (userIds != null && userIds.length > 0) {
					for (int i = 0; i < userIds.length; i++) {
						if (!userIds[i].equals("")) {
	                      groupIds[i]=new Long(userIds[i]);
						}
					}				
					orgManagerDirect.addRole2Member(V3xOrgEntity.ROLE_BOND_ACCOUNT, user.getLoginAccount(), role.getId(), groupIds);
				}
			}else{
				orgManagerDirect.addRole2Member(V3xOrgEntity.ROLE_BOND_ACCOUNT, user.getLoginAccount(), role.getId(), new Long[0]);
			}
			orgManagerDirect.updateEntity(account);
			result.addObject("groupAdminIds", groupAdminIds);
			result.addObject("groupAdminNames", groupAdminNames);*/
		} catch (BusinessException e1) {
			log.error("管理员修改密码错误", e1);
		}
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			log.error("管理员修改密码错误", e1);
		}
		out.println("<script>");
		out.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"')");
		out.println("parent.location.reload(true);");
		out.println("</script>");
		out.flush();
		// 从新获得登录名称
		result.addObject("logerName", logerName);	
//		读取是否启用密码强度检查
		result.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		
		return result;
	}

	@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
	public ModelAndView modifyGroupToUser(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView result = new ModelAndView("sysMgr/account/groupToUserManager");
		PrintWriter out = null;
		User user = CurrentUser.get();
		try {
			V3xOrgAccount account = orgManagerDirect.getAccountById(user.getLoginAccount());
			//设置集团管理员角色匹配
			V3xOrgRole role = orgManagerDirect.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_GROUPADMIN);
			String groupAdminIds = request.getParameter("groupAdminIds");
			String groupAdminNames = request.getParameter("groupAdminNames");
			if(StringUtils.isNotBlank(groupAdminIds)){
				String[] userIds = groupAdminIds.split(",");
				Long[] groupIds = new Long[userIds.length];
				if (userIds != null && userIds.length > 0) {
					for (int i = 0; i < userIds.length; i++) {
						if (!userIds[i].equals("")) {
	                      groupIds[i]=new Long(userIds[i]);
						}
					}				
					orgManagerDirect.addRole2Member(V3xOrgEntity.ROLE_BOND_ACCOUNT, user.getLoginAccount(), role.getId(), groupIds);
				}
			}else{
				orgManagerDirect.addRole2Member(V3xOrgEntity.ROLE_BOND_ACCOUNT, user.getLoginAccount(), role.getId(), new Long[0]);
			}
			orgManagerDirect.updateEntity(account);
			result.addObject("groupAdminIds", groupAdminIds);
			result.addObject("groupAdminNames", groupAdminNames);
		} catch (BusinessException e1) {
			logger.error("", e1);
		}
		try {
			out = response.getWriter();
		} catch (IOException e1) {
			logger.error("", e1);
		}
		out.println("<script>");
		out.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"')");
		out.println("</script>");
		out.flush();
	
		return result;
	}

    /**
     * 显示天气预报配置页
     * @param request
     * @param response
     * @return
     */
    public ModelAndView showWeatherConfig(HttpServletRequest request,
            HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("sysMgr/weatherConfig");
        Long configId = null;
        String weatherConfig = "";
        String weatherServiceState = "on";
        ConfigItem configItem_weather = configManager.getConfigItem(Constants.CONFIG_CATRGORY_COMMON_TOOLS, Constants.CONFIG_ITEM_WEATHER);
        if(configItem_weather!=null){
            configId = configItem_weather.getId();
            weatherConfig = configItem_weather.getConfigValue();
            weatherServiceState = configItem_weather.getExtConfigValue();
        }
        
        modelAndView.addObject("configId", configId);
        modelAndView.addObject("weatherConfig", weatherConfig);
        modelAndView.addObject("weatherServiceState", weatherServiceState);
        
        return modelAndView;
    }

    /**
     * 保存天气预报设置
     * @param request
     * @param response
     * @return
     */
    public ModelAndView updateWeatherConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String configId = request.getParameter("configId");
        String weatherConfig = request.getParameter("weatherConfig");
        String weatherServiceState = request.getParameter("weatherServiceState");
        
        if(configId!=null && configId.length()>0){
            ConfigItem configItem_weather = configManager.getConfigItem(Long.parseLong(configId));
            configItem_weather.setConfigValue(weatherConfig);
            configItem_weather.setExtConfigValue(weatherServiceState);
            configManager.updateConfigItem(configItem_weather);
        }else{
            ConfigItem configItem_weather = new ConfigItem();
            configItem_weather.setIdIfNew();
            configItem_weather.setConfigCategory(Constants.CONFIG_CATRGORY_COMMON_TOOLS);
            configItem_weather.setConfigItem(Constants.CONFIG_ITEM_WEATHER);
            configItem_weather.setConfigValue(weatherConfig);
            configItem_weather.setExtConfigValue(weatherServiceState);
            configManager.addConfigItem(configItem_weather);
        }

        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("weather.update.ok") + "')");
        out.println("</script>");
        out.flush();

        return super.redirectModelAndView("/accountManager.do?method=showWeatherConfig");
    }

    /**
     * 登录背景图片设置 - 显示
     */
    @CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView showLoginImage(HttpServletRequest request,
            HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("sysMgr/loginImgConfig");
        ConfigItem configItem_login = configManager.getConfigItem("System_Login_Title", "loginTitleName");
        String title=null;
        if(configItem_login!=null){
        	title=configItem_login.getConfigValue();
        }
        modelAndView.addObject("loginImgFilePath", MainDataLoader.getInstance().getLoginImagePath());
        modelAndView.addObject("defaultLoginImgFilePath", "/common/skin/default" + com.seeyon.v3x.skin.Constants.getSkinSuffix() + "/images/login.gif");
        modelAndView.addObject("loginTitleName", title);
        return  modelAndView;
    }

    /**
     * 登录背景图片设置 - Form处理
     */
    @CheckRoleAccess(roleTypes={RoleType.SystemAdmin})
    public ModelAndView updateLoginImage(HttpServletRequest request,
            HttpServletResponse response) throws Exception  {

    	String imageModify= request.getParameter("imageModify");
        String titleModify= request.getParameter("titleModify");
        String loginTitle=null;
        if(imageModify!=null&&imageModify.equals("true")){
        	 //如果是恢复默认设置
        	String imgPath = "";
            ConfigItem configItem_login = configManager.getConfigItem(Constants.CONFIG_CATRGORY_LOGIN_IMAGE, Constants.CONFIG_ITEM_LOGIN_IMAGE);
            if("true".equals(request.getParameter("toImageDefault"))){
                if(configItem_login != null){
                    configManager.deleteConfigItem(Constants.CONFIG_CATRGORY_LOGIN_IMAGE, Constants.CONFIG_ITEM_LOGIN_IMAGE);
                }
				imgPath = "/common/skin/default" + com.seeyon.v3x.skin.Constants.getSkinSuffix() + "/images/login.gif";
            }
            else{
            	String srcImg = null;
            	try{
	            	Long fileId = Long.parseLong(request.getParameter("fileId"));
	            	Date fileCreateDate = Datetimes.parseDate(request.getParameter("fileCreateDate"));
	            	srcImg = this.fileManager.getFile(fileId, fileCreateDate).getAbsolutePath();
            	}
            	catch(Exception e){
            		PrintWriter out = response.getWriter();
            		out.println("<script>");
            		out.println("alert('" + Constants.getString4CurrentUser("system.loginbg.update.error") +"')");
            		out.println("</script>");
            		out.flush();

            		return super.redirectModelAndView("/accountManager.do?method=showLoginImage");
            	}

                String realPath = SystemEnvironment.getA8ApplicationFolder();
                imgPath = Constants.USER_IMAGE_PATH + "LOGIN" + Constants.PATH_SEPARATOR + "login.gif";
                reNameFile(srcImg, realPath + imgPath);
                if(configItem_login != null){
//                    configItem_login.setConfigValue(imgPath);
//                    configManager.updateConfigItem(configItem_login);
                	updateImageConfig(configItem_login, imgPath);
                }
                else{
//                    configManager.addConfigItem(Constants.CONFIG_CATRGORY_LOGIN_IMAGE, Constants.CONFIG_ITEM_LOGIN_IMAGE, imgPath);
                    addImageConfig(Constants.CONFIG_CATRGORY_LOGIN_IMAGE, Constants.CONFIG_ITEM_LOGIN_IMAGE, imgPath,1L);
                }
            }
            MainDataLoader.getInstance().setLoginImagePath(imgPath);
            //CLUSTER 通知备机更新
            NotificationManager.getInstance().send(NotificationType.AccountSymbol_UpdateLoginImg, imgPath);
        }
       if(titleModify!=null&&titleModify.equals("true")){
    	   String configValue = request.getParameter("title");
		   loginTitle=configValue;
    	   ConfigItem configItem_login = configManager.getConfigItem("System_Login_Title", "loginTitleName");
    	   if("true".equals(request.getParameter("toTitleDefault"))){
    		   if(configItem_login != null){
                   configManager.deleteConfigItem("System_Login_Title","loginTitleName");
               }
    	   }
    	   else{
    		   if(configItem_login != null){
                   configItem_login.setConfigValue(configValue);
                   configManager.updateConfigItem(configItem_login);
               }
               else{
                   configManager.addConfigItem("System_Login_Title", "loginTitleName", configValue);
               }
    	   }
        }
       PrintWriter out = response.getWriter();
   	   out.println("<script>");
       out.println("alert('" + Constants.getString4CurrentUser("system.loginbg.update.ok") +"')");
    	  if(loginTitle!=null){
    		  out.println("var oldTitle=parent.parent.document.title;");
    		  out.println("var index=oldTitle.indexOf('V');");
    		  out.println("parent.parent.document.title='"+loginTitle+"'+oldTitle.substring(index-1);");
              }
       out.println("</script>");
       out.flush();

        return  super.redirectModelAndView("/accountManager.do?method=showLoginImage");
    }


    /**
     * 集团标示信息配置 - 显示
     */
    @CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
    public ModelAndView showGroupSymbolConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception  {
        ModelAndView modelAndView = new ModelAndView("sysMgr/account/groupSymbolConfig");
        String logoFileName = Constants.getDefaultLogoName();
        boolean isHiddenLogo = false;
        String bannerFileName = Constants.DEFAULT_BANNER_NAME;
        boolean isTileBanner = false;
        boolean isHiddenName = false;
        //取得集团单位的ID
        Long groupAccountId = null;
        try{
            V3xOrgAccount groupAccount = orgManager.getRootAccount();
            if(groupAccount != null){
                groupAccountId = groupAccount.getId();
            }
        }
        catch (BusinessException e){
            log.error("集团标识配置保存时取得根单位报错:", e);
        }
        
        AccountSymbol accountSymbol = MainDataLoader.getInstance().getAccountSymbol(groupAccountId);
        if(accountSymbol != null){
            if(accountSymbol.getLogoImagePath() != null){
                logoFileName = accountSymbol.getLogoImagePath();
            }
            if(accountSymbol.getBannerImagePath() != null){
                bannerFileName = accountSymbol.getBannerImagePath();
            }
            isHiddenLogo = accountSymbol.isHiddenLogo();
            isTileBanner = accountSymbol.isTileBanner();
            isHiddenName = accountSymbol.isHiddenGroupName();
        }

        modelAndView.addObject("logoFileName", logoFileName);
        modelAndView.addObject("isHiddenLogo", isHiddenLogo);
        modelAndView.addObject("bannerFileName", bannerFileName);
        modelAndView.addObject("isTileBanner", isTileBanner);
        modelAndView.addObject("isHiddenName", isHiddenName);
        modelAndView.addObject("groupAccountId", groupAccountId);
     
        return modelAndView;
    }

    /**
     * 集团标示信息配置 - FORM处理
     */
    @CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
    public ModelAndView updateGroupSymbolConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception  {

        String groupAccountIdItem = request.getParameter("groupAccountId");  
        Long groupAccountId = null;
        if(groupAccountIdItem!=null && groupAccountIdItem.length()>0){
            groupAccountId = Long.parseLong(groupAccountIdItem);
        }

        AccountSymbol accountSymbol = MainDataLoader.getInstance().getAccountSymbolFromMap(groupAccountId);
        //如果是恢复默认设置
        if("true".equals(request.getParameter("toDefault"))){
            if(accountSymbol != null){
                configManager.deleteByConfigCategory(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, groupAccountId);
                //更新内存
                MainDataLoader.getInstance().deleteAccountSymbol(groupAccountId);
                //CLUSTER 通知备机更新
//                NotificationManager.getInstance().send(NotificationType.AccountSymbol_Delete, groupAccountId);
            }
        }
        else{
            if(accountSymbol == null){
                accountSymbol = new AccountSymbol();
            }

            String realPath = SystemEnvironment.getA8ApplicationFolder();
            if("true".equals(request.getParameter("isLogoReplaced"))){
            	String srcImg = null;
            	try{
	            	Long fileId = Long.parseLong(request.getParameter("fileId1"));
	            	Date fileCreateDate = Datetimes.parseDate(request.getParameter("fileCreateDate1"));
	            	srcImg = this.fileManager.getFile(fileId, fileCreateDate).getAbsolutePath();
            	}
            	catch(Exception e){
            		PrintWriter out = response.getWriter();
            		out.println("<script>");
            		out.println("alert('" + Constants.getString4CurrentUser("system.loginbg.update.error") +"')");
            		out.println("</script>");
            		out.flush();

            		return super.redirectModelAndView("/accountManager.do?method=showGroupSymbolConfig");
            	}

                String configValue = Constants.USER_IMAGE_PATH + "LOGO" + Constants.PATH_SEPARATOR + groupAccountId +".gif";
                reNameFile(srcImg, realPath + configValue);

                ConfigItem configItem_logo = configManager.getConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_LOGO, groupAccountId);
                if(configItem_logo != null){
//                    configItem_logo.setConfigValue(configValue);
//                    configManager.updateConfigItem(configItem_logo);
                	updateImageConfig(configItem_logo, configValue);
                }
                else{
//                    configManager.addConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_LOGO, configValue, groupAccountId);
                    addImageConfig(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_LOGO, configValue, groupAccountId);                    
                }
                accountSymbol.setLogoImagePath(configValue);
            }
            if("true".equals(request.getParameter("isBannerReplaced"))){
            	String srcImg = null;
            	try{
	            	Long fileId = Long.parseLong(request.getParameter("fileId2"));
	            	Date fileCreateDate = Datetimes.parseDate(request.getParameter("fileCreateDate2"));
	            	srcImg = this.fileManager.getFile(fileId, fileCreateDate).getAbsolutePath();
            	}
            	catch(Exception e){
            		PrintWriter out = response.getWriter();
            		out.println("<script>");
            		out.println("alert('" + Constants.getString4CurrentUser("system.loginbg.update.error") +"')");
            		out.println("</script>");
            		out.flush();

            		return super.redirectModelAndView("/accountManager.do?method=showGroupSymbolConfig");
            	}

                String configValue = Constants.USER_IMAGE_PATH + "BANNER" + Constants.PATH_SEPARATOR + groupAccountId +".gif";
                reNameFile(srcImg, realPath + configValue);

                ConfigItem configItem_banner = configManager.getConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_BANNER, groupAccountId);
                if(configItem_banner != null){
//                    configItem_banner.setConfigValue(configValue);
//                    configManager.updateConfigItem(configItem_banner);
                	updateImageConfig(configItem_banner, configValue);                	
                }
                else{
//                    configManager.addConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_BANNER, configValue, groupAccountId);
                    addImageConfig(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_BANNER, configValue, groupAccountId);                    
                }
                accountSymbol.setBannerImagePath(configValue);
            }

            //是否隐藏集团简称
            boolean isHiddenGroupName = "on".equals(request.getParameter("isHiddenAccountName"));
            ConfigItem configItem_hiddenName = configManager.getConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_GROUPNAME_IsHidden, groupAccountId);
            String hiddenAccountName = String.valueOf(isHiddenGroupName);
            if(configItem_hiddenName != null){
                if(!hiddenAccountName.equals(configItem_hiddenName.getConfigValue())){
                    configItem_hiddenName.setConfigValue(hiddenAccountName);
                    configManager.updateConfigItem(configItem_hiddenName);
                }
            }
            else{
                if(isHiddenGroupName){
                    configManager.addConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_GROUPNAME_IsHidden, hiddenAccountName, groupAccountId);
                }
            }
            accountSymbol.setHiddenGroupName(isHiddenGroupName);


            //设置是否隐藏LOGO
            boolean isHiddenLogo = "on".equals(request.getParameter("isHiddenLogo"));
            String isHiddenLogoStr = String.valueOf(isHiddenLogo);
            ConfigItem configItem_logo_hidden = configManager.getConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_LOGO_IsHidden, groupAccountId);
            if(configItem_logo_hidden != null){
                if(!isHiddenLogoStr.equals(configItem_logo_hidden.getConfigValue())){
                    configItem_logo_hidden.setConfigValue(isHiddenLogoStr);
                    configManager.updateConfigItem(configItem_logo_hidden);
                }
            }
            else{
                if(isHiddenLogo){
                    configManager.addConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_LOGO_IsHidden, isHiddenLogoStr, groupAccountId);
                }
            }
            accountSymbol.setHiddenLogo(isHiddenLogo);

            //设置是否平铺背景
            ConfigItem configItem_banner_tile = configManager.getConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_BANNER_IsTile, groupAccountId);
            boolean isTileBanner = "on".equals(request.getParameter("isTileBanner"));
            String isTileBannerStr = String.valueOf(isTileBanner);
            if(configItem_banner_tile != null){
                if(!isTileBannerStr.equals(configItem_banner_tile.getConfigValue())){
                    configItem_banner_tile.setConfigValue(isTileBannerStr);
                    configManager.updateConfigItem(configItem_banner_tile);
                }
            }
            else{
                if(isTileBanner){
                    configManager.addConfigItem(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, Constants.CONFIG_ITEM_BANNER_IsTile, isTileBannerStr, groupAccountId);
                }
            }
            accountSymbol.setTileBanner(isTileBanner);

            //更新内存
            accountSymbol.setAccountId(groupAccountId);
            MainDataLoader.getInstance().updateAccountSymbol(groupAccountId, accountSymbol);
            //CLUSTER 通知备机更新
            NotificationManager.getInstance().send(NotificationType.AccountSymbol_Update, accountSymbol);
        }
        
        //branches_a8_v350_r_gov GOV-2567 常屹 修改------- start
        //功能描述：组织标识设置页面点恢复默认的话，就提示"集团标识配置成功"。政务版需要去掉 '集团'
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();  //政务版标识
        String alertStr = "";
        if(isGovVersion){
        	alertStr = Constants.getString4CurrentUser("space.corporation.group.update.ok.GOV");
        }else{
        	alertStr = Constants.getString4CurrentUser("space.corporation.group.update.ok");
        }
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + alertStr + "')");
        out.println("top.contentFrame.topFrame.location.reload();");
        out.println("</script>");
        out.flush();
        //branches_a8_v350_r_gov GOV-2567 常屹 修改------- end
        return super.redirectModelAndView("/accountManager.do?method=showGroupSymbolConfig");
    }


    /**
     * 单位标示信息配置 - 显示
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView showAccountSymbolConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception  {
        ModelAndView modelAndView = new ModelAndView("sysMgr/account/accountSymbolConfig");
        
        Long accountId = CurrentUser.get().getLoginAccount();
        Long groupAccountId = null;
        if((Boolean)(SysFlag.frontPage_isNeedGetSymbolFromGroup.getFlag())){
            try{
                V3xOrgAccount groupAccount = orgManager.getRootAccount();
                if(groupAccount != null){
                    groupAccountId = groupAccount.getId();
                }
            }
            catch (BusinessException e){
                log.error("单位标识配置保存时取得根单位报错", e);
            }
        }
        //导入单位标示设置数据
        AccountSymbol accountSymbol = MainDataLoader.getInstance().getAccountSymbol(accountId);
        // MainHelper.getAccountSymbol(orgManager, accountId, groupAccountId, false);
        modelAndView.addObject("logoFileName", accountSymbol.getLogoImagePath());
        modelAndView.addObject("isHiddenLogo", accountSymbol.isHiddenLogo());
        modelAndView.addObject("bannerFileName", accountSymbol.getBannerImagePath());
        modelAndView.addObject("isTileBanner", accountSymbol.isTileBanner());
        modelAndView.addObject("isHiddenAccountName", accountSymbol.isHiddenAccountName());
        modelAndView.addObject("isHiddenGroupName", accountSymbol.isHiddenGroupName());
        modelAndView.addObject("accountId", accountId);
        modelAndView.addObject("groupAccountId", groupAccountId);
        return modelAndView;
    }

    /**
     * 单位标示信息配置 - Form处理
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator})
    public ModelAndView updateAccountSymbolConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception  {

        String accountIdStr = request.getParameter("accountId");
        Long accountId = null;
        if(accountIdStr != null && accountIdStr.length()>0){
            accountId = Long.parseLong(accountIdStr);
        }
        AccountSymbol accountSymbol = MainDataLoader.getInstance().getAccountSymbolFromMap(accountId);
        //如果是恢复默认设置
        final String configCategory = Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL;
		if("true".equals(request.getParameter("toDefault"))){
            if(accountSymbol != null){
                configManager.deleteByConfigCategory(configCategory, accountId);
                //更新内存
                MainDataLoader.getInstance().deleteAccountSymbol(accountId);
              //CLUSTER 通知备机更新
//                NotificationManager.getInstance().send(NotificationType.AccountSymbol_Delete, accountId);
            }
        }
        else{
            if(accountSymbol == null){
                accountSymbol = new AccountSymbol();
            }
            //boolean isCopyFile = false; //是否需要拷贝标识数据到另外的服务器
            //保存LOGO设置
            if("true".equals(request.getParameter("isLogoReplaced"))){

            	String srcImg = null;
            	try{
	            	Long fileId = Long.parseLong(request.getParameter("fileId1"));
	            	Date fileCreateDate = Datetimes.parseDate(request.getParameter("fileCreateDate1"));
	            	srcImg = this.fileManager.getFile(fileId, fileCreateDate).getAbsolutePath();
            	}
            	catch(Exception e){
            		PrintWriter out = response.getWriter();
            		out.println("<script>");
            		out.println("alert('" + Constants.getString4CurrentUser("system.loginbg.update.error") +"')");
            		out.println("</script>");
            		out.flush();

            		return super.redirectModelAndView("/accountManager.do?method=showLoginImage");
            	}

                String realPath = SystemEnvironment.getA8ApplicationFolder();
                String imgPath = Constants.USER_IMAGE_PATH+"LOGO"+Constants.PATH_SEPARATOR + accountId + ".gif";
                reNameFile(srcImg, realPath + imgPath);

                ConfigItem configItem_logo = configManager.getConfigItem(configCategory, Constants.CONFIG_ITEM_LOGO, accountId);
                if(configItem_logo != null){
//                    configItem_logo.setConfigValue(imgPath);
//                    configManager.updateConfigItem(configItem_logo);
                	updateImageConfig(configItem_logo, imgPath);
                }
                else{
//                    configManager.addConfigItem(configCategory, Constants.CONFIG_ITEM_LOGO, imgPath, accountId);
                    addImageConfig(configCategory, Constants.CONFIG_ITEM_LOGO, imgPath, accountId);
                }
                accountSymbol.setLogoImagePath(imgPath);
            }

            //保存Banner设置
            if("true".equals(request.getParameter("isBannerReplaced"))){
            	String srcImg = null;
            	try{
	            	Long fileId = Long.parseLong(request.getParameter("fileId2"));
	            	Date fileCreateDate = Datetimes.parseDate(request.getParameter("fileCreateDate2"));
	            	srcImg = this.fileManager.getFile(fileId, fileCreateDate).getAbsolutePath();
            	}
            	catch(Exception e){
            		PrintWriter out = response.getWriter();
            		out.println("<script>");
            		out.println("alert('" + Constants.getString4CurrentUser("system.loginbg.update.error") +"')");
            		out.println("</script>");
            		out.flush();

            		return super.redirectModelAndView("/accountManager.do?method=showLoginImage");
            	}

                String realPath = SystemEnvironment.getA8ApplicationFolder();
                String imgPath = Constants.USER_IMAGE_PATH+"BANNER"+Constants.PATH_SEPARATOR + accountId + ".gif";
                reNameFile(srcImg, realPath + imgPath);

                ConfigItem configItem_banner = configManager.getConfigItem(configCategory, Constants.CONFIG_ITEM_BANNER, accountId);
                if(configItem_banner != null){
//                    configItem_banner.setConfigValue(imgPath);
//                    configManager.updateConfigItem(configItem_banner);
                	updateImageConfig(configItem_banner, imgPath);
                }
                else{
//                    configManager.addConfigItem(configCategory, Constants.CONFIG_ITEM_BANNER, imgPath, accountId);
                    addImageConfig(configCategory, Constants.CONFIG_ITEM_BANNER, imgPath, accountId);
                }
                accountSymbol.setBannerImagePath(imgPath);
            }

            //设置是否隐藏单位名称
            boolean isHiddenAccountName = "on".equals(request.getParameter("isHiddenAccountName"));
            ConfigItem configItem_hiddenName = configManager.getConfigItem(configCategory, Constants.CONFIG_ITEM_ACCOUNTNAME_IsHidden, accountId);
            String hiddenAccountName = String.valueOf(isHiddenAccountName);
            if(configItem_hiddenName != null){
                if(!hiddenAccountName.equals(configItem_hiddenName.getConfigValue())){
                    configItem_hiddenName.setConfigValue(hiddenAccountName);
                    configManager.updateConfigItem(configItem_hiddenName);
                }
            }
            else{
                configManager.addConfigItem(configCategory, Constants.CONFIG_ITEM_ACCOUNTNAME_IsHidden, hiddenAccountName, accountId);
            }
            accountSymbol.setHiddenAccountName(isHiddenAccountName);
            
            //设置是否隐藏集团名称
            boolean isHiddenGroupName = "on".equals(request.getParameter("isHiddenGroupName"));
            ConfigItem configItem_hiddenGroupName = configManager.getConfigItem(configCategory, Constants.CONFIG_ITEM_GROUPNAME_IsHidden, accountId);
            String hiddenGroupName = String.valueOf(isHiddenGroupName);
            if(configItem_hiddenGroupName != null){
                if(!hiddenGroupName.equals(configItem_hiddenGroupName.getConfigValue())){
                	configItem_hiddenGroupName.setConfigValue(hiddenGroupName);
                    configManager.updateConfigItem(configItem_hiddenGroupName);
                }
            }
            else{
                configManager.addConfigItem(configCategory, Constants.CONFIG_ITEM_GROUPNAME_IsHidden, hiddenGroupName, accountId);
            }
            accountSymbol.setHiddenGroupName(isHiddenGroupName);
            
            //设置是否隐藏LOGO
            boolean isHiddenLogo = "on".equals(request.getParameter("isHiddenLogo"));
            ConfigItem configItem_logo_hidden = configManager.getConfigItem(configCategory, Constants.CONFIG_ITEM_LOGO_IsHidden, accountId);
            String isHiddenLogoStr = String.valueOf(isHiddenLogo);
            if(configItem_logo_hidden != null){
                if(!isHiddenLogoStr.equals(configItem_logo_hidden.getConfigValue())){
                    configItem_logo_hidden.setConfigValue(isHiddenLogoStr);
                    configManager.updateConfigItem(configItem_logo_hidden);
                }
            }
            else{
                configManager.addConfigItem(configCategory, Constants.CONFIG_ITEM_LOGO_IsHidden, isHiddenLogoStr, accountId);
            }
            accountSymbol.setHiddenLogo(isHiddenLogo);
            
            //设置是否平铺背景
            boolean isTileBanner = "on".equals(request.getParameter("isTileBanner"));
            ConfigItem configItem_banner_tile = configManager.getConfigItem(configCategory, Constants.CONFIG_ITEM_BANNER_IsTile, accountId);
            String isTileBannerStr = String.valueOf(isTileBanner);
            if(configItem_banner_tile != null){
                if(!isTileBannerStr.equals(configItem_banner_tile.getConfigValue())){
                    configItem_banner_tile.setConfigValue(isTileBannerStr);
                    configManager.updateConfigItem(configItem_banner_tile);
                }
            }
            else{
                configManager.addConfigItem(configCategory, Constants.CONFIG_ITEM_BANNER_IsTile, isTileBannerStr, accountId);
            }
            accountSymbol.setTileBanner(isTileBanner);
            //更新内存
            accountSymbol.setAccountId(accountId);
            MainDataLoader.getInstance().updateAccountSymbol(accountId, accountSymbol);
            //CLUSTER 通知备机更新
            NotificationManager.getInstance().send(NotificationType.AccountSymbol_Update, accountSymbol);
        }
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("space.corporation.account.update.ok") + "')");
        out.println("top.contentFrame.topFrame.location.reload();");
        out.println("</script>");
        out.flush();

        return super.redirectModelAndView("/accountManager.do?method=showAccountSymbolConfig");
    }

    /**
     *
     * @param srcFilepath
     * @param destFilepath
     */
    private static void reNameFile(String srcFilepath, String destFilepath){
    	try {
			File srcFile = new File(srcFilepath);
			if (srcFile.exists() && srcFile.isFile()) {
				File destFile = new File(destFilepath);
				if (destFile.exists()) {
					destFile.delete();
				}
				FileUtils.copyFile(srcFile, destFile);
				srcFile.delete();
				// srcFile.renameTo(destFile);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
    }
    // 更新含图片的配置项目（登录背景、单位Logo、单位Banner、集团Logo、集团Banner），将图片以Base64形式保存到ExtConfigValue
    private void updateImageConfig(ConfigItem item,String imgPath) throws IOException    {
        item.setConfigValue(imgPath);
        item.setExtConfigValue(loadFile2Base64((SystemEnvironment.getA8ApplicationFolder()+imgPath)));
        configManager.updateConfigItem(item);
    }
    // 增加含图片的配置项目（登录背景、单位Logo、单位Banner、集团Logo、集团Banner），将图片以Base64形式保存到ExtConfigValue
	public ConfigItem addImageConfig(String configCategory, String item,
			String configValue,Long accountId) throws IOException {
		ConfigItem configItem=new ConfigItem();
		configItem.setConfigCategory(configCategory);
		configItem.setConfigItem(item);
		configItem.setConfigValue(configValue);
		configItem.setExtConfigValue(loadFile2Base64((SystemEnvironment.getA8ApplicationFolder()+configValue)));
		
		Date date=new Date();
		Timestamp stamp=new Timestamp(date.getTime());
		configItem.setCreateDate(stamp);
		configItem.setIdIfNew();
		configItem.setOrgAccountId(accountId);
		configManager.addConfigItem(configItem);
		return configItem;
	}    
    // 加载文件为Base64 String。
    private String loadFile2Base64(String filePath)
			throws IOException {
		InputStream in = new FileInputStream(filePath);
		byte[] data = new byte[in.available()];
		in.read(data);
		in.close();
		String base64String = new String(new Base64().encode(data));
		return base64String;
	}

}