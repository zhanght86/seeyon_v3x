package com.seeyon.v3x.systemopen.controller;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.system.Constants;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-6
 */

@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
public class SystemOpenController extends BaseController {

	private SystemConfig systemConfig;	
    private OrgManagerDirect orgManagerDirect;
    private AppLogManager appLogManager;
    private MetadataManager metadataManager;
    public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}
    
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
        this.orgManagerDirect = orgManagerDirect;
    }
    /**
	 * 系统开关的首页配置　－　显示
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showSystemOpenSpace(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("v3xmain/systemSwitch/systemswitch");

        //增加是否启用多组织模型结构 ：如果已经存在多集团，那就不允许改为否了
        List<V3xOrgEntity> rootAccounts = orgManagerDirect.getEntityList(V3xOrgAccount.class.getSimpleName(), "superior", -1L, V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
        if(rootAccounts != null && rootAccounts.size() > 1){
            systemConfig.update(IConfigPublicKey.MUCHORG_ENABLE, IConfigPublicKey.ENABLE);
            
            mav.addObject("disableMUCHORGButton", true);
        }
        
        mav.addAllObjects(this.systemConfig.getAll());
        
        Metadata logDeadlineMetadata = metadataManager.getMetadata(MetadataNameEnum.log_deadline);
        mav.addObject("logDeadlineMetadata", logDeadlineMetadata);
       
        return mav;
	}
	/**
	 * 页面提交，更改数据
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView confirm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Set<String> names = this.systemConfig.names();
		for (String name : names) {
			String value = (String)request.getParameter(name);
			systemConfig.update(name, value);
		}
		
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"')");
        out.println("</script>");
        out.flush();
        
        User user = CurrentUser.get();
    	appLogManager.insertLog(user, AppLogAction.SystemOpenModify);
    	
        return super.redirectModelAndView("/systemopen.do?method=showSystemOpenSpace", "parent");
	}
	
	/**
	 * 恢复默认的数据
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView defaultSetting(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Set<String> names = this.systemConfig.names();
		for (String name : names) {
			String value = this.systemConfig.getDefaultValue(name);
			if(name.equals(IConfigPublicKey.MUCHORG_ENABLE)){
				  //增加是否启用多组织模型结构 ：如果已经存在多集团，那就不允许改为否了
		        List<V3xOrgEntity> rootAccounts = orgManagerDirect.getEntityList(V3xOrgAccount.class.getSimpleName(), "superior", -1L, V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		        if(rootAccounts != null && rootAccounts.size() > 1){
		        	value = IConfigPublicKey.ENABLE;
		        }
			}
			
			systemConfig.update(name, value);
		}
		
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"')");
        out.println("</script>");
        out.flush();
        
        User user = CurrentUser.get();
    	appLogManager.insertLog(user, AppLogAction.SystemOpenModify);
        
		return super.redirectModelAndView("/systemopen.do?method=showSystemOpenSpace", "parent");
	}
	
}