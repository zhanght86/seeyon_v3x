/**
 * 
 */
package com.seeyon.v3x.plugin.identification.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.IdentificationDog;
import com.seeyon.v3x.common.authenticate.domain.IdentificationDogManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.identification.manager.IdentificationManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Strings;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-1-16
 */
@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
public class IdentificationController extends BaseController {

	private String ParameterNameDogId = "dogId";
	
	private OrgManager orgManager;
	
    private IdentificationManager identificationManager;

	public void setParameterNameDogId(String parameterNameDogId) {
		ParameterNameDogId = parameterNameDogId;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

    public void setIdentificationManager(IdentificationManager identificationManager){
        this.identificationManager = identificationManager;
    }
    
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView getSessionId(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/plain; charset=UTF-8");
		
		String dogId = request.getParameter(ParameterNameDogId);
		
		IdentificationDogManager dogManager = IdentificationDogManager.getInstance();
		IdentificationDog dog = dogManager.getDogByEncodeId(dogId);
		
		String result = "";
		
		if(dog == null){ //狗不存在
			result = "";
		}
		else if(!dog.isEnabled()){ //狗不可用
			result = "";
		}
		else if(dog.isGenericDog()){ //通狗
			result = dogManager.newSessionId(dogId, null, null, null);
		}
		else if(dog.getMemberId() != -1){
			long memberId = dog.getMemberId();
			if(memberId == 1L){
				result = dogManager.newSessionId(dogId, 1L, Constants.SYSTEM_LOGIN_NAME, "");
			} else if(memberId == 0L){
				result = dogManager.newSessionId(dogId, 0L, Constants.AUDIT_ADMIN_LOGIN_NAME, "");
			} else {
				V3xOrgMember member = (V3xOrgMember)this.orgManager.getGlobalEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, memberId);
				if(member != null){
					result = dogManager.newSessionId(dogId, member.getId(), member.getLoginName(), member.getPassword());
				}
			}
		}
		
		PrintWriter out = response.getWriter();
		out.print(result);
		out.close();
		
		return null;
	}
	
    /**
     * 身份验证USB-Key制作 首页
     */
    public ModelAndView makeIndex(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("plugin/USBKey/makeIndex");
        return modelAndView;
    }
    
    /**
     * 身份验证USB-Key制作 列表页
     */
    public ModelAndView showUSBKeyList(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("plugin/USBKey/USBKeyList");
        List<IdentificationDog> USBKeyList = identificationManager.getAllDog();
        int USBKeyListSize = 0;
        if(USBKeyList != null && !USBKeyList.isEmpty()){
            USBKeyListSize = USBKeyList.size();
        }
        modelAndView.addObject("USBKeyList", USBKeyList);
        modelAndView.addObject("listCount", USBKeyListSize);
        return modelAndView;
    }
    
    /**
     * 身份验证USB-Key制作 新建
     */
    public ModelAndView makeUSBKey(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("plugin/USBKey/editUSBKey");
        return modelAndView;
    }
    
    /**
     * 身份验证USB-Key制作 编辑
     */
    public ModelAndView editUSBKey(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("plugin/USBKey/editUSBKey");
        String dogId = request.getParameter("dogId");
        if(Strings.isNotBlank(dogId)){
            IdentificationDog USBKey = identificationManager.getDog(dogId);
            modelAndView.addObject("USBKey", USBKey);
        }

        return modelAndView;
    }
    
    /**
     * 身份验证USB-Key制作 保存/更新
     */
    public ModelAndView updateUSBKey(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        String dogId = request.getParameter("dogId");
        String USBKeyName = request.getParameter("USBKeyName");
        boolean isEnabled = "true".equals(request.getParameter("isEnabled"));
        boolean isGeneric = "true".equals(request.getParameter("isGeneric"));
        boolean isMustUseDog = !isGeneric ? "on".equals(request.getParameter("isMustUseDog")) : false;
        boolean isNeedCheckUsername = !isGeneric ? "on".equals(request.getParameter("isNeedCheckUsername")) : false;
        boolean isContinueMake = "on".equals(request.getParameter("isContinueMake"));
        boolean canAccessMoile = isEnabled && isMustUseDog && "true".equals(request.getParameter("canAccessMobile"));
        long memberId = -1L;
        String memberIdStr = request.getParameter("memberId");
        if(!isGeneric && Strings.isNotBlank(memberIdStr)){
            memberId = Long.parseLong(memberIdStr);            
        }
        if("true".equals(request.getParameter("isNew"))){ //新建
            if(Strings.isNotBlank(dogId)){
                identificationManager.makeDog(dogId, USBKeyName, isGeneric, memberId, isNeedCheckUsername, isMustUseDog,isEnabled,canAccessMoile);
            }
        }
        else{//修改
            if(Strings.isNotBlank(dogId)){
                identificationManager.updateDog(dogId, USBKeyName, isGeneric, memberId, isNeedCheckUsername, isMustUseDog, isEnabled,canAccessMoile);
            }
        }
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("parent.refreshList(" + isContinueMake + ")");
        out.println("</script>");
        return null;
    }
    
    /**
     * 启用/停用USB-Key
     */
    public ModelAndView enableUSBKey(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        boolean enabled = "true".equals(request.getParameter("enabled"));
        String[] dogIdsStr = request.getParameterValues("dogIds");
        if(dogIdsStr!=null && dogIdsStr.length>0){
            identificationManager.enabledDog(enabled, dogIdsStr);
        }
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("parent.location.reload(true);");
        out.println("</script>");
        return null;
    }
    
    /**
     * 删除USB-Key
     */
    public ModelAndView deleteUSBKey(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String[] dogIdsStr = request.getParameterValues("dogIds");
        if(dogIdsStr!=null && dogIdsStr.length>0){
            identificationManager.deleteDog(dogIdsStr);
        }
        super.rendJavaScript(response, "parent.location.reload(true);");
        return null;
    }
    
    /**
     * 全局配置首页
     */
    public ModelAndView identificationConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("plugin/USBKey/identificationConfig");
        boolean isMustUseDogLogin = identificationManager.getSystemMustUseDogLogin();
        String noCheckIp = identificationManager.getSystemNoCheckIP();
        modelAndView.addObject("isMustUseDogLogin", isMustUseDogLogin);
        modelAndView.addObject("noCheckIp", noCheckIp);
        return modelAndView;
    }
    
    /**
     * 全局配置 保存设置
     */
    public ModelAndView saveIdentificationConfig(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        boolean isMustUseDogLogin = "on".equals(request.getParameter("isMustUseDogLogin"));
        String noCheckIp = request.getParameter("noCheckIp");
        identificationManager.saveSystemMustUseDogLogin(isMustUseDogLogin, noCheckIp);
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('" + Constants.getString4CurrentUser("system.manager.ok") + "')");
        out.println("</script>");
        
        return super.redirectModelAndView("/identification.do?method=identificationConfig");
    }

}