package com.seeyon.v3x.space.controller;

import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.OperBaseManager;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.IPagePublicParam;
import www.seeyon.com.v3x.form.controller.report.ReportChartInfo;
import www.seeyon.com.v3x.form.domain.FormAppMain;
import www.seeyon.com.v3x.form.domain.FormQueryPlan;
import www.seeyon.com.v3x.form.manager.SeeyonForm_ApplicationImpl;
import www.seeyon.com.v3x.form.manager.define.report.SeeyonReportImpl;
import www.seeyon.com.v3x.form.manager.form.FormDaoManager;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BaseBulletinManager;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.LayoutConstants;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.thirdparty.ThirdpartySpace;
import com.seeyon.v3x.common.thirdparty.ThirdpartySpaceManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfig;
import com.seeyon.v3x.formbizconfig.manager.FormBizConfigManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.domain.LinkSpace;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.main.Constant;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.main.section.BaseSection;
import com.seeyon.v3x.main.section.SectionRegisterManager;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.portal.decorations.PortalDecorationManager;
import com.seeyon.v3x.portal.util.PortalConstants;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.Constants.SectionType;
import com.seeyon.v3x.space.Constants.SpaceState;
import com.seeyon.v3x.space.Constants.SpaceType;
import com.seeyon.v3x.space.Constants.SpaceTypeClass;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.domain.Banner;
import com.seeyon.v3x.space.domain.FormSectionWebModel;
import com.seeyon.v3x.space.domain.Fragment;
import com.seeyon.v3x.space.domain.PortletEntityProperty;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.domain.SpaceSecurity;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.space.manager.UserFixManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.EnumUtil;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-6
 */
public class SpaceController extends BaseController {
	private static final Log log = LogFactory.getLog(SpaceController.class);
	
	private SpaceManager spaceManager;

    private OrgManager orgManager;
    
	private BulDataManager bulDataManager;

	private BulTypeManager bulTypeManager;

	private BbsBoardManager bbsBoardManager;
    
    private SectionRegisterManager sectionRegisterManager;
    
    private OuterlinkManager outerlinkManager;
    
    private ProjectManager projectManager;
    
    private FormBizConfigManager formBizConfigManager;
    
    private AppLogManager appLogManager;
    
    private PortletEntityPropertyManager portletEntityPropertyManager;
    
    private MenuManager menuManager;
    
    private InquiryManager inquiryManager;
    
    private NewsTypeManager newsTypeManager;
    
    private UserFixManager userFixManager;
    
    public UserFixManager getUserFixManager() {
		return userFixManager;
	}
	public void setUserFixManager(UserFixManager userFixManager) {
		this.userFixManager = userFixManager;
	}
	public void setInquiryManager(InquiryManager inquiryManager) {
    	this.inquiryManager = inquiryManager;
    }
    public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public void setPortletEntityPropertyManager(PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	
	public void setSectionRegisterManager(SectionRegisterManager sectionRegisterManager) {
		this.sectionRegisterManager = sectionRegisterManager;
	}
	
	public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
		this.outerlinkManager = outerlinkManager;
	}

	public ModelAndView sortPopup(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/space/editPersonalSpacePopup");
		User user = CurrentUser.get();
		long accountId = user.getLoginAccount();
		boolean is2Default = "true".equals(request.getParameter("toDefault"));
        List<String[]> spaceSortList = spaceManager.getSpaceSort(user.getId(), accountId, user.getLocale(), is2Default, null);
        modelAndView.addObject("spaceSortList", spaceSortList);
		return modelAndView;
	}
	
	/**
	 * 业务配置：用户点击"发布到首页"按钮，进入栏目发布配置页面
	 */
	public ModelAndView  toPublishColumn (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("formbizconfig/write/bizconfig_publish_config");
		this.renderSpaceInfo(modelAndView, request);
		return modelAndView;
	}
	
	/**
	 * 业务配置：用户完成若干表单栏目发布或取消等操作点击"确定"按钮后，个人空间也随之更新
	 */
	public ModelAndView  configPublishColumn (HttpServletRequest request, HttpServletResponse response) throws Exception {
		String editKeyId = request.getParameter("editKeyId");
    	String spaceId = request.getParameter("spaceId");
    	String decoration = request.getParameter("decorationId");
    	String toDefalut = "no";
    	User user = CurrentUser.get();
    	boolean isPublished = false;
    	String singleBoardId = request.getParameter("id");
    	String path = spaceManager.getSpace(Long.valueOf(spaceId)).getPagePath();
    	if(Strings.isNotBlank(editKeyId)){
    		String pagePath = spaceManager.updateSpaceByCache(Long.valueOf(editKeyId),user.getId(),Long.valueOf(spaceId),decoration,toDefalut);
    		if(Strings.isNotBlank(singleBoardId)){
    			isPublished = spaceManager.IsPublishedFormBizSection(singleBoardId,pagePath);
    		}
    		// 判断用户在操作时是否将业务配置对应栏目发布到首页个人空间了
    		super.rendJavaScript(response, "alert('" + FormBizConfigUtils.getI18NValue("bizconfig.publish_oper_success.label") + "');" +
    				"parent.window.returnValue = ['" + isPublished + "','" + path + "','" + pagePath + "'];" +
    		"parent.close();");
    	}else{
    		super.rendJavaScript(response,"parent.close();");
    	}
    	return null;
	}	
	
	/**
	 * 个人空间的首页配置　－　显示
	 */
	public ModelAndView showPersonalSpace(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/space/editPersonalSpace");
		renderSpaceInfo(modelAndView, request);
		return modelAndView;
	}
	
	private void renderSpaceInfo(ModelAndView mav, HttpServletRequest request) throws SpaceException, BusinessException {
		User user = CurrentUser.get();
		long accountId = user.getLoginAccount();
		SpaceFix space = spaceManager.getPersonSpace(user.getId(), accountId);
		if(space!=null){
			String pagePath = space.getPagePath();
			
			boolean isAllowedUserDefined = space.isAllowdefined();
			
			String[] layout = spaceManager.getLayoutType(pagePath);
			
			mav.addObject("layoutType", layout[0]);
	        mav.addObject("decoration", layout[1]);
	        mav.addObject("pagePath", pagePath);
	        mav.addObject("spaceId", space.getId());
	        mav.addObject("isAllowedUserDefined", isAllowedUserDefined);
		}
				
	}
	
	/**
	 * 个人空间首页配置- Form处理
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updatePersonalSpace(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    User user = CurrentUser.get();
		String pagePath = null;
		String toDefault = request.getParameter("toDefault");
        String spaceSortStr = request.getParameter("sortResult");
        String disSortStr = request.getParameter("disResult");
        Long accountId = user.getLoginAccount();
        boolean isRealignSpaceMenu = false;
        //恢复默认
        if("true".equals(toDefault)){
    		spaceManager.deletePersonalSpace(user.getId());
    		spaceManager.deleteSpaceSort(user.getId(), accountId);
    		
    		SpaceFix fix = spaceManager.getPersonSpace(user.getId(), accountId);
    		pagePath = fix.getPagePath();
            isRealignSpaceMenu = true;
        }
        else{
        	SpaceFix personalFix = spaceManager.createPersonalSpace(user.getId(), accountId);
        	pagePath = personalFix.getPagePath();
        	
        	chanageLayoutType(request, pagePath);
        	
        	//更新排序设置
            String spaceAlginStr = request.getParameter("spaceAlgin");
            String[] spaceAlginArr = spaceAlginStr.split(",");
            int i = 0;
            if(Strings.isNotBlank(spaceSortStr)){
                StringTokenizer s = new StringTokenizer(spaceSortStr, "|");
                List<String[]> spaceSortList = new ArrayList<String[]>();
                int j = 0;
                while(s.hasMoreTokens()){
                    String[] temp = s.nextToken().split(",");
                    spaceSortList.add(temp);
                    //排序不一样
                    if(spaceAlginArr.length > i && !temp[1].equals(spaceAlginArr[i++])){
                        isRealignSpaceMenu = true;
                    }
                    j++;
                }
                //有删除不显示的。
                if(j != spaceAlginArr.length){
                	isRealignSpaceMenu = true;
                }
                StringTokenizer dis = new StringTokenizer(disSortStr, "|");
                List<String[]> disSort = new ArrayList<String[]>();
                while(dis.hasMoreTokens()){
                	 String[] temp = dis.nextToken().split(",");
                	 disSort.add(temp);
                }
                spaceManager.updateSpaceSort(spaceSortList,disSort, user.getId(), accountId);                    
            }
		}
        
        PrintWriter out = response.getWriter();
        super.printV3XJS(out);
		out.println("<script>");
		//out.println("alert('"+Constants.getValueOfKey("space.personal.update.ok")+"');");
		//这里判断一下是否更改了顺序，不做没必要的AJAX调用
        if(isRealignSpaceMenu){
            //out.println("getA8Top().contentFrame.topFrame.realignSpaceMenu('" + accountId + "')");
        }
        out.println("getA8Top().contentFrame.topFrame.updatePersonalSpaceURL('" + pagePath + "')");
		//out.println("getA8Top().contentFrame.topFrame.backToPersonalSpace();");
		out.println("alert('"+Constants.getValueOfKey("add.partition.createok")+"')");
		out.println("</script>");
        out.flush();
		return super.redirectModelAndView("/space.do?method=showPersonalSpace");
		
	}
	

    /**
     * 部门空间首页配置- 显示
     */
	//@CheckRoleAccess(roleTypes={RoleType.DepartmentManager})
    public ModelAndView showDepartmentSpace(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView("sysMgr/space/editDepartmentSpace");
		User user = CurrentUser.get();
		long accountId = user.getLoginAccount();
		Long departmentId = null;
		String departmentName = null;
		String selectOwnerId = request.getParameter("departmentId");

		if (Strings.isNotBlank(selectOwnerId)) {
			departmentId = Long.parseLong(selectOwnerId);
			//departmentName = this.orgManager.getDepartmentById(departmentId).getName();
		}
        
        boolean isEnabledDepartmentSpace = spaceManager.isCreateDepartmentSpace(departmentId);
        if(!isEnabledDepartmentSpace){
            PrintWriter out = response.getWriter();
            super.printV3XJS(out);
            out.println("<script>");
            out.println("alert('"+Constants.getValueOfKey("space.notExistThisSpace.label")+"');");
            //out.println("getA8Top().contentFrame.topFrame.realignSpaceMenu('" + accountId + "');");
            out.println("getA8Top().contentFrame.topFrame.backToPersonalSpace();");
            out.println("</script>");
            out.flush();
            return null;
        }
        
		SpaceFix spaceFix = spaceManager.createDepartmentSpace(departmentId, departmentName, accountId);

		String pagePath = spaceFix.getPagePath();
		boolean isAllowedUserDefined = spaceFix.isAllowdefined();
		
		boolean toDefault = "true".equals(request.getParameter("toDefault"));
		if(isAllowedUserDefined || toDefault){ //允许修改
			// 取得部门空间banner和口号
			if (toDefault || (spaceFix.getBanner() == null && spaceFix.getSlogan() == null)) {
				SpaceFix spaceFix1 = spaceManager.getSpaceFix(Constants.SpaceType.Default_department, accountId, null); //取默认空间的
                if(toDefault){
                    pagePath = spaceFix1.getPagePath();
                }
			}
			List<SpaceSecurity> spaceSecurities = new ArrayList<SpaceSecurity>();
			java.util.List<SpaceSecurity> securities = spaceFix.getSpaceUsers();
			if(securities != null){
				for (SpaceSecurity security : securities) {
					if("Department".equals(security.getEntityType()) && security.getEntityId().equals(departmentId)){
						//当前部门不授权
						continue;
					}
					spaceSecurities.add(security);
				}
			}
			modelAndView.addObject("managements", spaceFix.getSpaceManagements());
			modelAndView.addObject("securities", spaceSecurities);
			List<SpaceSecurity> vistors = spaceFix.getSpaceSecurity(SpaceSecurity.SecurityType.vistor);
	        modelAndView.addObject("vistors", vistors);
	            
			String[] layout = spaceManager.getLayoutType(pagePath);
			Map<String,Map<String,Fragment>> fragments = spaceManager.getFragments(pagePath);
            modelAndView.addObject("layoutType", layout[0]);
            modelAndView.addObject("decoration", layout[1]);
            modelAndView.addObject("fragments", fragments);            
			modelAndView.addObject("allLayout", PortalDecorationManager.getAllLayoutType());
			modelAndView.addObject("layoutTypes", LayoutConstants.lagoutToDecorations);
			
		}

		modelAndView.addObject("isAllowedUserDefined", isAllowedUserDefined);
		modelAndView.addObject("pagePath", spaceFix.getPagePath());
		modelAndView.addObject("isEnabled", true);
		
		modelAndView.addObject("spaceType", Constants.SpaceType.department.name());
		modelAndView.addObject("spaceType", Constants.SpaceType.department.name());
		
		modelAndView.addObject("spaceId", spaceFix.getId());
		modelAndView.addObject("spaceName", Constants.getSpaceName(spaceFix));
        modelAndView.addObject("currentDepartmentId", departmentId);

        modelAndView.addObject("isDepartmentManager", MainHelper.isDepartmentManager(user.getId(), departmentId, orgManager));

		return modelAndView;
	}
    
    /**
	 * 部门空间首页配置- 更新
	 * 
	 * 不在使用这个了。使用公共的
	 */
	//@CheckRoleAccess(roleTypes={RoleType.DepartmentManager})
    @Deprecated
    public ModelAndView updateDepartmentSpace(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Long departmentId = null;
        String selectOwnerId = request.getParameter("departmentId");
        if(selectOwnerId != null && selectOwnerId != ""){
            departmentId = Long.parseLong(selectOwnerId);
        }
        
        String pagePath = request.getParameter("pagePath");
        String slogan = request.getParameter("slogan");
        String motto = request.getParameter("motto");
        //String isReplaceBanner = request.getParameter("isReplaceBanner"); 
        String bannerFileName = request.getParameter("bannerFileName");
        String newPagePath = pagePath;

        String[][] _securities = Strings.getSelectPeopleElements(request.getParameter("security"));
        String[][] managers = Strings.getSelectPeopleElements(request.getParameter("manager"));
        String[][] vistors =  Strings.getSelectPeopleElements(request.getParameter("vistor"));
        
        Banner banner = null;
        if(slogan != null){
        	banner = new Banner();
        	banner.setSlogan(slogan);
        	banner.setBanner(bannerFileName);
        }
        
        int securitiesLength = 0;
        if(_securities != null){
            securitiesLength = _securities.length;
        }
        String[][] securities = new String[securitiesLength + 1][2];
        securities[0] = new String[]{ORGENT_TYPE_DEPARTMENT, String.valueOf(departmentId)};
        if(_securities != null){
            System.arraycopy(_securities, 0, securities, 1, securitiesLength);
        }
        if("true".equals(request.getParameter("toDefault"))){
            spaceManager.deleteDepartmentSpace(departmentId);
            long accountId = CurrentUser.get().getLoginAccount();
            String departmentName = this.orgManager.getDepartmentById(departmentId).getName();
            SpaceFix newSpaceFix = spaceManager.createDepartmentSpace(departmentId, departmentName, accountId);
            newPagePath = newSpaceFix.getPagePath();
            //SpaceFix spaceFix = spaceManager.getSpaceFix(Constants.SpaceType.Default_department, accountId, null);
            
            spaceManager.updateFixDCGSpaceInfo(newPagePath, managers, securities, vistors,banner, motto, newSpaceFix.isAllowdefined(), null, Constants.SpaceState.normal.ordinal());
        }
        else{
            chanageLayoutType(request, pagePath);
            /*空间Banner已去掉，
	        if("true".equals(isReplaceBanner)){
	            bannerFileName = "space_" + UUIDLong.longUUID() + ".gif"; 
	            String realPath = request.getParameter("realPath"); 
	            this.renameSpaceBanner(realPath, bannerFileName);
	        }*/
	        spaceManager.updateFixDCGSpaceInfo(pagePath,managers, securities, vistors,banner, motto, true, null, Constants.SpaceState.normal.ordinal());
	        
	        //部门空间管理员可以管理部门讨论版块 added by Meng Yang 20090521
	        bbsBoardManager.updateDeptBBSBoardManager(departmentId, request.getParameter("manager"));
	        
	        // lihf 部门空间管理员可以管理部门公告
	        ((BaseBulletinManager)bulDataManager).getBulletinUtils().updateDeptBulTypeManagers(departmentId, request.getParameter("manager"));
        }
        
        PrintWriter out = response.getWriter();
        super.printV3XJS(out);
        out.println("<script>");
        out.println("alert('"+Constants.getValueOfKey("space.update.ok")+"')");
        out.println("getA8Top().contentFrame.topFrame.updateDepartmentSpaceURL('" + pagePath + "','" + newPagePath + "','')");
        out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/space.do?method=showDepartmentSpace&type=department&departmentId="+departmentId);
    }
    
    /**
     * 空间导航配置 - 显示
     */
    public ModelAndView showSpaceNavigation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("sysMgr/space/spaceNavigationSetting");
    	User user = CurrentUser.get();
    	Long userId = user.getId();
    	Long loginAccountId = user.getLoginAccount();
    	
    	//备选空间：当前用户有权访问的所有空间（个人、部门、单位及自定义、集团及自定义）
    	List<String[]> spaceSortList = spaceManager.getCanAccessSpace(userId, loginAccountId, user.getLocale(), false, null);
    	//已选空间：当前用户设置显示的访问空间
    	List<String[]> spaceList = spaceManager.getSpaceSort(userId, loginAccountId, user.getLocale(), false, null);
    	
    	//备选关联系统：当前用户可访问的所有允许配置的内部和外部系统以及集成的第三方系统
    	List<LinkSpace> linkSpaces = outerlinkManager.findLinkSpacesCanAccess(userId);
    	List<ThirdpartySpace> thirdpartySpaces = ThirdpartySpaceManager.getInstance().getAccessSpaces(orgManager, userId);
    	
    	//备选关联项目：当前用户可访问的所有已启用、进行中的项目
    	List<ProjectSummary> relatedProjects = projectManager.getIndexProjectList(userId, -1);
    	
    	return mav.addObject("spaceSortList", spaceSortList).addObject("spaceList", spaceList)
    	          .addObject("linkSpaces", linkSpaces)
    			  .addObject("thirdpartySpaces", thirdpartySpaces)
    			  .addObject("relatedProjects", relatedProjects);
    }
    
    public String closeSpaceTop(String link, String type) throws SpaceException {
    	
    	if("".equals(link) || link == null  || "".equals(type) || type == null){
    		return "false";
    	}
    	
    	User user = CurrentUser.get();
    	Long userId = user.getId();
    	Long loginAccountId = user.getLoginAccount();
    	List<String[]> spaceList = null;
		try {
			spaceList = spaceManager.getSpaceSort(userId, loginAccountId, user.getLocale(), false, null);
		} catch (SpaceException e) {
			e.printStackTrace();
		}
		
		if(spaceList == null || spaceList.size() == 0){return "false";}
		
		String spaceId = null;
		if("thirdparty".equals(type) || "related_system".equals(type) || "related_project".equals(type) ){
			spaceId = link;
					
		}else if("department".equals(type)){
			SpaceFix spaceFix = spaceManager.getSpaceFix(link);
	    	spaceId = spaceFix.getEntityId().toString();
		}else if("corporation".equals(type)){
			spaceId = "2";
		}else if("group".equals(type)){
			spaceId = "3";
		}else{
	    	SpaceFix spaceFix = spaceManager.getSpaceFix(link);
	    	spaceId = spaceFix.getId().toString();
		}
    	String[] currentSpace  = null;
    	List<String[]> sortList = new ArrayList<String[]>();
    	List<String[]> disSort = new ArrayList<String[]>();
    	if (spaceList != null) {
    		for (int i = 0; i < spaceList.size(); i++) {
    			String[] temp  =spaceList.get(i);
    			
    			if(temp!=null && temp[1].equals(spaceId.toString())){
    				currentSpace = temp;
    			}else{
    				if(temp[3].equals("false")){
    					sortList.add(temp);
    				}else{
    					disSort.add(temp);
    				}
    			}
    		}
    	}
    	sortList.add(currentSpace);
    	this.spaceManager.updateSpaceSort(sortList, disSort, userId, loginAccountId);
    	return  loginAccountId.toString();
    }
    
    
    
    
    /**
     * 空间导航配置 - 更新
     */
    public ModelAndView updateSpaceNavigation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
    	Long userId = user.getId();
    	Long loginAccountId = user.getLoginAccount();
    	//获取空间导航配置修改前后的信息
    	String[] sortArr = request.getParameterValues("spaceSort");
    	
    	//仅当修改前后信息发生变化时，才有必要进行修改操作并刷新空间导航菜单显示
    	//boolean isReAlign = !Arrays.equals(sortArr, orginalSortArr);
    	//if(isReAlign) {
    		List<String[]> sortList = this.parseSpaceSortInfo(sortArr);
    		List<String[]> disSort = this.parseSpaceSortInfo(request.getParameterValues("deletedSpaceSort"));
    		this.spaceManager.updateSpaceSort(sortList, disSort, userId, loginAccountId);
    	//}
    	
    	PrintWriter out = response.getWriter();
    	super.printV3XJS(out);
    	out.println("<script>");
    	//if(isReAlign)
    		out.println("getA8Top().contentFrame.topFrame.realignSpaceMenu('" + loginAccountId + "');");
    	out.println("alert('" + Constant.getValueFromMainRes("personalSetting.spacenavigation.ok") + "');");
    	out.println("</script>");
        out.flush();
        return super.redirectModelAndView("/space.do?method=showSpaceNavigation");
    }
    
    /**
     * 辅助方法：解析空间排序信息
     * @param sortArr
     */
    private List<String[]> parseSpaceSortInfo(String[] sortArr) {
    	if(sortArr!=null && sortArr.length>0) {
    		List<String[]> result = new ArrayList<String[]>(sortArr.length);
    		for(String str : sortArr) {
    			result.add(str.split(","));
    		}
    		return result;
    	}
    	return null;
    }
    
    /**
     * 单位/集团空间设置外边框
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView home(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/space/iframePage");
        User user = CurrentUser.get();
        Long accountId = user.getLoginAccount();
        if("true".equals(request.getParameter("isGroup")) || user.isGroupAdmin()){
            accountId = 1L;
        }
        mav.addObject("accountId", accountId);
        return mav;
    }
    /**
     * 单位/集团空间设置frameSet
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView frameSetPage(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("sysMgr/space/frameSetPage");
        return mav;
    }
    
    /**
     * 单位/集团空间设置列表
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	String spaceTypeStr = request.getParameter("type");
        ModelAndView mav = new ModelAndView("sysMgr/space/unitList");
        List<SpaceModel> spaceList = null;
        String showSpace = request.getParameter("showSpace");
        String condition = request.getParameter("condition");
        String value = request.getParameter("textfield");
        SpaceTypeClass spaceType = null;
        if(Strings.isNotBlank(showSpace)){
        	spaceType = SpaceTypeClass.valueOf(showSpace);
        }
        //集团空间管理
        if("true".equals(request.getParameter("isGroup"))){
            //创建系统空间
            spaceManager.createGroupSpace();
            if(spaceType == null){
            	spaceType = SpaceTypeClass.corporation;
            }
            spaceList = spaceManager.getAdminCanManagerSpace(1L,spaceType,condition,value);
        }
        else{
    	 	if(spaceType == null){
         		spaceType = SpaceTypeClass.personal;
    	 	}
            User user = CurrentUser.get();
            
            spaceManager.initAccountSpace(user.getLoginAccount());
            /*spaceManager.createDefaultDepartmentSpace(user.getLoginAccount());
            spaceManager.createCorporationSpace(user.getLoginAccount());
            //创建默认领导空间
            spaceManager.createDefaultLeaderSpace(user.getLoginAccount());
            */
            
            spaceList = spaceManager.getAdminCanManagerSpace(user.getLoginAccount(),spaceType,condition,value);
        }
        mav.addObject("type", spaceTypeStr);
        mav.addObject("spaceList", spaceList);
        mav.addObject("spaceCount", spaceList.size());
        return mav;
    }
    
    /**
     * 单位管理员　修改空间的配置　－　显示
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
    public ModelAndView showSpace(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("sysMgr/space/editSpace");
        String _pagePath = "";
        String spaceType = request.getParameter("type");
        boolean toDefault = "true".equals(request.getParameter("toDefault"));
        User user = CurrentUser.get();
        String spaceIdStr = request.getParameter("space_id");
        if(!user.isAdmin()){
        	//判断权限
        	if(Strings.isNotBlank(spaceIdStr) && !spaceManager.canManagerSpace(user.getId(), Long.parseLong(spaceIdStr))){
        		showErrorMessage(request, response, user.getLoginAccount());
				return null;
        	}
        	modelAndView = new ModelAndView("sysMgr/space/editCustomSpace");
        }
        long accountId = user.getLoginAccount();
        //集团管理员新建空间
        if(user.isGroupAdmin()) accountId = 1L;
        String showFlag = request.getParameter("showFlag");
        String showSpace = request.getParameter("showSpace");
        //新建
        SpaceType spaceTypeEnum = SpaceType.valueOf(spaceType);
        if("new".equals(showFlag)){
        	spaceTypeEnum = Constants.getSpaceTypeByClass(showSpace);
        	if(spaceTypeEnum == SpaceType.public_custom && user.isGroupAdmin()){
        		spaceTypeEnum = SpaceType.public_custom_group;
        	}
        }
        spaceType = spaceTypeEnum.name();
       
        // 是否显示恢复默认
        boolean showToDefault = true;
        SpaceFix spaceFix = null;
        String decoration = null;
        if(toDefault){
        	if(spaceTypeEnum == SpaceType.department){
        		_pagePath= spaceManager.createDefaultDepartmentSpace(accountId);
        	}else{
        		_pagePath = Constants.getDefaultPagePath(spaceTypeEnum);
        	}
        	String editKeyId = request.getParameter("editKeyId");
        	if(Strings.isNotBlank(editKeyId)){
        		decoration = spaceManager.toDefaultSpace(user.getId(), Long.valueOf(spaceIdStr), _pagePath);
        	}
        	modelAndView.addObject("toDefault", "true");
        	modelAndView.addObject("editKeyId", editKeyId);
        	showToDefault = false;
        }else{
        	switch(spaceTypeEnum){
        	//查看 默认个人空间
        	case Default_personal:
        		_pagePath = spaceManager.createDefaultPersonalSpace(accountId);
        		break;
        	//查看 默认领导空间
        	case default_leader:
        		 spaceFix = spaceManager.createDefaultLeaderSpace(accountId);
        		 _pagePath = spaceFix.getPagePath();
        		 break;
        	//查看 默认外部人员空间
        	case Default_out_personal:
        		 spaceFix = spaceManager.createDefaultOutSpace(accountId);
        		 _pagePath = spaceFix.getPagePath();
        		 break;
        	//查看 自定义个人空间
        	case Default_personal_custom:
        		if(Strings.isNotBlank(spaceIdStr)){
        			spaceFix = spaceManager.getSpaceFix(SpaceType.Default_personal_custom, accountId, Long.parseLong(spaceIdStr));
        			_pagePath = spaceFix.getPagePath();
        		}else{
        			_pagePath = Constants.DEFAULT_CUSTUM_PERSONAL;
        		}
        		showToDefault = false;
        		break;
        	//查看 协作空间
        	//查看 公共自定义空间
        	case public_custom:
        		if(Strings.isBlank(spaceIdStr)){
        			_pagePath = Constants.DEFAULT_PUBLIC_PAGE_PATH;
        		}
        		showToDefault = false;
        		break;
        	case public_custom_group:
        		if(Strings.isBlank(spaceIdStr)){
        			_pagePath = Constants.DEFAULT_PUBLIC_PAGE_PATH;
        		}
        		showToDefault = false;
        		break;
        	case custom:
        		if(user.isGroupAdmin()){
        			accountId = 1L;
        		}
        		if(Strings.isNotBlank(spaceIdStr)){
        			spaceFix = spaceManager.getSpaceFix(spaceTypeEnum, accountId, Long.parseLong(spaceIdStr));
        			_pagePath = spaceFix.getPagePath();
        		}else if(Strings.isBlank(_pagePath)){
        			_pagePath = Constants.DEFAULT_CUSTOM_PAGE_PATH;;
        		}
        		showToDefault = false;
        		break;
        		//查看 单位空间
        	case Default_department:
        	case department:
        		spaceFix = spaceManager.getSpace(Long.parseLong(spaceIdStr));
        		if(spaceFix != null){
        			_pagePath = spaceFix.getPagePath();
        		}
        		break;
        	case corporation:
        		spaceFix = spaceManager.createCorporationSpace(accountId);
                _pagePath = spaceFix.getPagePath();
        		break;
        		//查看 集团空间
        	case group:
        		 //集团空间 按照单位授权
        		spaceFix = spaceManager.createGroupSpace();
                _pagePath = spaceFix.getPagePath();
                break;
        	}
        }
        
        String spaceName = "";
        boolean allowdefined = true;
        boolean isEnabled = true;
        if(Strings.isNotBlank(spaceIdStr)){
        	spaceFix = spaceManager.getSpace(Long.parseLong(spaceIdStr));
        	_pagePath = spaceFix.getPagePath();
        	isEnabled = spaceFix.getState()==SpaceState.normal.ordinal()?true:false;
        }
        if(null != spaceFix){
            if(toDefault){
            	allowdefined = true;
            }else{
            	allowdefined = spaceFix.isAllowdefined();
            }
            spaceName = Constants.getSpaceName(spaceFix);
            //if(!toDefault){
            	List<SpaceSecurity> securities = spaceFix.getSpaceUsers();
            	List<SpaceSecurity> securities0 = new ArrayList<SpaceSecurity>();
            	if(spaceTypeEnum == SpaceType.department){ //部门空间，不要显示自己部门，因为他是缺省的
            		for (SpaceSecurity s : securities) {
						if(s.getEntityType().equals("Department") && s.getEntityId().equals(spaceFix.getEntityId())){
							continue;
						}
						securities0.add(s);
					}
            		modelAndView.addObject("securities", securities0);
            	}
            	else{
            		modelAndView.addObject("securities", securities);
            	}
                List<SpaceSecurity> managements = spaceFix.getSpaceManagements();
                modelAndView.addObject("managements", managements);
                List<SpaceSecurity> vistors = spaceFix.getSpaceSecurity(SpaceSecurity.SecurityType.vistor);
                modelAndView.addObject("vistors", vistors);
            //}
        }
        if(Strings.isBlank(spaceName)){
            spaceName = Constants.getDefaultSpaceName(SpaceType.valueOf(spaceType));
        }
        modelAndView.addObject("showFlag", showFlag);
        
        modelAndView.addObject("spaceFix", spaceFix);
        modelAndView.addObject("showSpace", showSpace);
        modelAndView.addObject("spaceName", spaceName);
        modelAndView.addObject("isEnabled", isEnabled);
        
        modelAndView.addObject("allowdefined", allowdefined);
        modelAndView.addObject("isGroupAdmin", user.isGroupAdmin());
        modelAndView.addObject("space_id", spaceIdStr);  
        modelAndView.addObject("showToDefault", showToDefault);  
        modelAndView.addObject("pagePath", _pagePath);  
        
        modelAndView.addObject("type", spaceType);
        modelAndView.addObject("spaceType", spaceType);
        
        Map<String,Map<String,Fragment>> fragments = spaceManager.getFragments(_pagePath);
        modelAndView.addObject("fragments", fragments);
        
        String[] layout = spaceManager.getLayoutType(_pagePath);
        modelAndView.addObject("layoutType", layout[0]);
        if(Strings.isNotBlank(decoration)){
        	modelAndView.addObject("decoration", decoration);
        }else{
        	modelAndView.addObject("decoration", layout[1]);
        }
        modelAndView.addObject("allLayout", PortalDecorationManager.getAllLayoutType());
        modelAndView.addObject("layoutTypes", LayoutConstants.lagoutToDecorations);
        
        return modelAndView;
    }
    private void showErrorMessage(HttpServletRequest request,
            HttpServletResponse response,Long accountId) throws Exception{
    	PrintWriter out = response.getWriter();
        out.println("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + request.getContextPath() + "/common/js/V3X.js\"></script>");
		out.println("<script>");
		//刷新空间
		out.println("alert(\"" + Strings.escapeJavascript(Constants.getValueOfKey("space.notExist.label")) + "\");");
		out.println("getA8Top().contentFrame.topFrame.realignAndBack('" + accountId + "')");
		out.println("</script>");
		out.close();
    }
    /**
     * 单位管理员　修改空间的配置　－　更新处理
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
    public ModelAndView updateSpace(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
    	User user = CurrentUser.get();
    	String spaceType = request.getParameter("type");
    	String spaceName = request.getParameter("spaceName");
        Long accountId = user.getLoginAccount();
        //集团管理员新建空间
        if(user.isGroupAdmin()) accountId = 1L;
        //恢复默认
        String toDefault = request.getParameter("toDefault");
        Long spaceId = null;
    	if(Strings.isNotBlank(request.getParameter("space_id"))){
    		spaceId = Long.parseLong(request.getParameter("space_id"));
    	}
    	String showFlag = request.getParameter("showFlag");
    	String editKeyId = request.getParameter("editKeyId");
        String decoration = request.getParameter("decorationId");
    	if("new".equals(showFlag)){
    		SpaceType type = null;
    		if(spaceType.equals(SpaceType.Default_personal_custom.name())){
    			type = SpaceType.Default_personal_custom;
    		}else if(spaceType.equals(SpaceType.custom.name())){
    			type = SpaceType.custom;
    		}else if(spaceType.equals(SpaceType.public_custom.name())){
    			type = SpaceType.public_custom;
    		}else if(spaceType.equals(SpaceType.public_custom_group.name())){
    			type = SpaceType.public_custom_group;
    		}
    		SpaceFix fix = spaceManager.createCustomSpace(type, spaceId, accountId);
    		spaceId = fix.getId();
    	}
    	
    	boolean spaceMenuEnabled = BooleanUtils.toBoolean(request.getParameter("spaceMenuEnabled"));

        if(!user.isAdmin()){
        	//判断权限
        	if(spaceId != null && !spaceManager.canManagerSpace(user.getId(), spaceId)){
        		showErrorMessage(request, response, user.getLoginAccount());
				return null;
        	}
        }
        
        String[][] securities = Strings.getSelectPeopleElements(request.getParameter("security"));
        String[][] managerments = Strings.getSelectPeopleElements(request.getParameter("manager"));
        String[][] vistors =  Strings.getSelectPeopleElements(request.getParameter("vistor"));

        SpaceType theType = SpaceType.valueOf(spaceType);
        SpaceFix space = spaceManager.getSpace(spaceId);
		if(space != null){
			accountId = space.getEntityId();
		}
        if("true".equals(toDefault)){
        	this.toDefaultSpace(theType, managerments, securities, vistors, accountId);
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('"+Constants.getValueOfKey("space.update.ok")+"')\n");
            if(user.isAdmin()){
            	out.println("parent.listFrame.location.href=parent.listFrame.location.href;");
            }
            out.println("</script>");
            out.flush();
            if(!user.isAdmin()){
            	return super.redirectModelAndView("/space.do?method=showSpace&space_id="+spaceId+"&showFlag=edit&type="+spaceType+"&showSpace="+request.getParameter("showSpace"));
            }
            return null;
        }
        
        int state = Constants.SpaceState.normal.ordinal();
        if("false".equals(request.getParameter("isEnabled"))){
        	state = Constants.SpaceState.invalidation.ordinal();
        }
        String pagePath = request.getParameter("pagePath");
        //默认空间修改时没有直接获得pagePath
        if(Strings.isBlank(pagePath)&&space!=null){
        	pagePath = space.getPagePath();
        }
        String slogan = request.getParameter("slogan");
        String motto = request.getParameter("motto");
        //允许前端用户自定义
        boolean allowdefined = request.getParameterValues("allowdefined") != null;
        //获取设置为默认空间参数
        boolean setAsDefault = request.getParameter("setAsDefault")!=null;
        String bannerFileName = request.getParameter("bannerFileName");
        String isShowSearch = request.getParameter("isSearch");
        
        
        Banner banner = null;
        if(slogan != null){
        	banner = new Banner();
        	banner.setSlogan(slogan);
        	banner.setBanner(bannerFileName);
        	banner.setShowSearch(isShowSearch);
        }
        
        //收回自定义空间逻辑
        if(!allowdefined&&spaceId!=null){
        	spaceManager.deleteCustomedSpace(spaceId);
        }
        if(SpaceType.Default_personal.name().equals(spaceType)||
        		SpaceType.default_leader.name().equals(spaceType)||
        		SpaceType.Default_out_personal.name().equals(spaceType)||
        		SpaceType.Default_personal_custom.name().equals(spaceType)){
        	spaceManager.updateCustomedSpaceName(spaceName, spaceType, accountId,spaceId);
        }
        if(SpaceType.Default_personal.name().equals(spaceType)){
            spaceManager.updateFixPersonalSpaceInfo(accountId, spaceName,banner, allowdefined);
        }else{
            //自定义空间，更新前，先创建
            if(SpaceType.Default_personal_custom.name().equals(spaceType) ||
            		SpaceType.custom.name().equals(spaceType)				||
            		SpaceType.public_custom.name().equals(spaceType)		||
            		SpaceType.public_custom_group.name().equals(spaceType)){
            	SpaceFix spaceFix = spaceManager.createCustomSpace(SpaceType.valueOf(spaceType),spaceId, accountId);
            	pagePath = spaceFix.getPagePath();
            	spaceId = spaceFix.getId();
            }
            
            if(SpaceType.group.name().equals(spaceType) && Strings.isNotBlank(request.getParameter("securityName")) && Strings.isNotBlank(request.getParameter("security"))){
            	appLogManager.insertLog(user, AppLogAction.GroupSpaceManager_Set, request.getParameter("securityName"));
            }
            
            //授权变更后，更新空间排序表
            if(spaceId != null){
            	//spaceManager.updateSpaceSortBySecurity(spaceId, securities);
            	spaceManager.updateSpaceFix(spaceId, state);
            	
				// 更新自定义团队空间对应公共信息板块名称等
				if (SpaceType.custom.name().equals(spaceType)) {
					updatePublicInfo(spaceId, spaceName);
				}
            	
		    	//选择授权范围时，更新授权范围内人员的默认空间
		    	if(setAsDefault||SpaceType.default_leader.name().equals(spaceType)){
		    		List<V3xOrgMember> memberIds = spaceManager.getSecurityMembers(securities);
		    		if(CollectionUtils.isNotEmpty(memberIds)){
		    			spaceManager.updateSpaceSortForPersonalCustom(spaceId.toString(), memberIds,accountId, setAsDefault);
		    		}
				}
				//更新userFix表信息
				//userFixManager.updateUserFixBySecurity(spaceId, memberIds);
				//更新权限信息
	            spaceManager.updateFixDCGSpaceInfo(pagePath, managerments,securities,vistors ,banner, motto, allowdefined, spaceName, state, spaceMenuEnabled);
            }
        }
       
        
        if(Strings.isNotBlank(editKeyId)){
        	spaceManager.updateSpace(Long.valueOf(editKeyId),user.getId(),spaceId,decoration);
        }
        
		// 空间菜单配置
		if (!"personal".equals(request.getParameter("showSpace"))) {
			menuManager.deleteSpaceMenu(spaceId);
			if (spaceMenuEnabled) {
				String[] menuIdsStr = request.getParameterValues("menuIds");
				List<Long> menuIds = new ArrayList<Long>();
				if (menuIdsStr != null && menuIdsStr.length > 0) {
					for (String menuId : menuIdsStr) {
						menuIds.add(NumberUtils.toLong(menuId));
					}
				}
				menuManager.saveSpaceMenu(spaceId, menuIds);
			}
		}
        
        PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('"+Constants.getValueOfKey("space.update.ok")+"')");
        if(user.isAdmin()){
        	out.println("parent.listFrame.location.href=parent.listFrame.location.href;");
        }else{
        	boolean isManagerOfThis = spaceManager.isManagerOfThisSpace(user.getId(), spaceId);
    		if(!isManagerOfThis){
    			out.println("parent.getA8Top().contentFrame.topFrame.realignSpaceMenu('" + user.getAccountId() + "');");
    		}
        }
        out.println("</script>");
        out.flush();
        if(!user.isAdmin()){
        	return super.redirectModelAndView("/space.do?method=showSpace&space_id="+spaceId+"&showFlag=edit&type="+spaceType+"&showSpace="+request.getParameter("showSpace"));
        }
        //return modelAndView;
        ///return super.refreshWorkspace();
        return null;
    }
    private void showSpacesIsFullMessage(V3xOrgMember member,HttpServletResponse response) throws IOException{
    	PrintWriter out = response.getWriter();
        out.println("<script>");
        out.println("alert('"+Constants.getValueOfKey("space.hasPushed.more")+"【"+member.getName()+"】')");
        out.println("parent.listFrame.location.href=parent.listFrame.location.href;");
        out.println("</script>");
        out.flush();
    }
    /**
     * 删除空间
     */
    @CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
    public ModelAndView deleteSpace(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = CurrentUser.get();
        boolean isGroupAdmin = user.isGroupAdmin();
        Long accountId = isGroupAdmin ? 1L : user.getLoginAccount();
        String[] spaceIdStr = request.getParameterValues("space_id");
        String[] spaceType = request.getParameterValues("type");
		if(spaceIdStr != null && spaceIdStr.length > 0){
        	for (int i = 0; i < spaceType.length; i++) {
        		Long spaceId = Long.parseLong(spaceIdStr[i]);
                spaceManager.deleteCustomSpace(spaceId, accountId, false);
                if(Constants.SpaceType.custom.name().equals(spaceType[i])){
                	List<Long> newsList = new ArrayList<Long>();
                	newsList.add(spaceId);
                	//删除团队空间时暂不删除无用新闻类型
                	//newsTypeManager.setTypeDeleted(newsList);//删除新闻类型
                	bbsBoardManager.deleteV3xBbsBoard(spaceId);//删除讨论类型
                	InquirySurveytype surveytype = inquiryManager.getSurveyTypeById(spaceId);
                	if (surveytype != null) {
        				surveytype.setFlag(1);// 设置调查类型为删除状态
        				inquiryManager.updateInquiryType(surveytype);
        			}
                }else if(Constants.SpaceType.public_custom.name().equals(spaceType[i]) || Constants.SpaceType.public_custom_group.name().equals(spaceType[i])){
                	List<InquirySurveytype> surveyTypes = inquiryManager.getInquiryTypeListByUserAuth(spaceId);
                	if(surveyTypes != null && surveyTypes.size() > 0){
                		for(InquirySurveytype surveyType : surveyTypes){
                			surveyType.setFlag(1);// 设置调查类型为删除状态
            				inquiryManager.updateInquiryType(surveyType);
                		}
                	}
                }
			}
        }
        String jsAction = isGroupAdmin ? "parent.location.href = parent.location;" : "parent.parent.changeLabel(parent.parent.currentLabel);";
        super.rendJavaScript(response, jsAction);
        return null;
    }
    /**
     * 前端保存
     */
    public ModelAndView updateSpaceByFront(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String editKeyId = request.getParameter("editKeyId");
    	String spaceId = request.getParameter("spaceId");
    	String decoration = request.getParameter("decorationId");
    	String toDefault = request.getParameter("toDefault");
    	User user = CurrentUser.get();
    	JSONObject obj = new JSONObject();
    	if(Strings.isNotBlank(editKeyId)){
    		String path = spaceManager.updateSpaceByCache(Long.valueOf(editKeyId),user.getId(),Long.valueOf(spaceId),decoration,toDefault);
    		obj.put("pagePath", path);
    	}
    	PrintWriter out = response.getWriter();
    	out.write(obj.toString());
    	out.flush();
    	out.close();
    	return null;
    }
	private void chanageLayoutType(HttpServletRequest request, String pagePath){
    	String newLayoutType = request.getParameter("layoutType");
    	String newDecoration = request.getParameter("decoration");
    	String[] fragmentIds = request.getParameterValues("fragmentId");
 
    	if(Strings.isBlank(newLayoutType)
    		|| Strings.isBlank(newDecoration)
    		|| fragmentIds == null
    	){
    		return;
    	}
    	
		Map<Long, Map<String, String>> portletEntityProperties = new HashMap<Long, Map<String,String>>();
		List<Fragment> newFragments = new ArrayList<Fragment>();
		for (String fragmentId : fragmentIds) {
			int row = Integer.parseInt(request.getParameter("X_" + fragmentId));
			int column = Integer.parseInt(request.getParameter("Y_" + fragmentId));
			Fragment fragment = new Fragment();
			fragment.setIdIfNew();
			fragment.setType(Fragment.Type.portlet);
			fragment.setLayoutColumn(column);
			fragment.setLayoutRow(row);
			
			Map<String, String> properties = PortalConstants.doPortletEntityProperty(request, fragmentId);
			String sections = properties.get(PortletEntityProperty.PropertyName.sections.name());
			if(Strings.isNotBlank(sections) && sections.indexOf("::") > 0){ //标准的Portlet
//				String decorator = this.sectionRegisterManager.getPortletDecorator(sections);
//				
//				fragment.setName(sections);
//				fragment.setDecorator(decorator);
				log.warn("不支持: " + sections);
			}
			else{
				fragment.setName("seeyon::sectionPortlet");
			}
			
			newFragments.add(fragment);
			portletEntityProperties.put(fragment.getId(), properties);
		}
		
		this.spaceManager.updatePage(pagePath, newLayoutType, newDecoration, newFragments, portletEntityProperties);
    }
	
	/**
	 * 添加栏目选择器
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView portletSelector(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/space/portletSelector");
		User user = CurrentUser.get();
		SpaceType spaceType = SpaceType.valueOf(request.getParameter("spaceType"));
		boolean showBanner = "true".equals(request.getParameter("showBanner"));

		List<SectionType> spaceSectionTypes = new ArrayList<SectionType>();
		Map<String, List<String[]>> type2Sections = new LinkedHashMap<String, List<String[]>>();
		if (spaceType != null) {
			spaceType = Constants.parseDefaultSpaceType(spaceType);
			spaceSectionTypes = Constants.getSpaceSectionTypes(spaceType);
			type2Sections = sectionRegisterManager.getSections(spaceType, user.getId(), user.getLoginAccount(), false, showBanner);
		}

		modelAndView.addObject("allSpaceSectionTypes", Constants.getAllSpaceSectionTypes());
		modelAndView.addObject("spaceSectionTypes", spaceSectionTypes);
		modelAndView.addObject("type2Sections", type2Sections);

		List<String[]> allSections = new ArrayList<String[]>();
		for (List<String[]> sections : type2Sections.values()) {
			allSections.addAll(sections);
		}

		boolean isShowDocTreePanel = false;
		if (allSections != null) {
			for (String[] strings : allSections) {
				if ("docFolderSection".equals(strings[0])) {
					isShowDocTreePanel = true;
					break;
				}
			}
		}
		modelAndView.addObject("isShowDocTreePanel", isShowDocTreePanel);

		boolean showBizConfigSection = false;
		if (allSections != null) {
			for (String[] strings : allSections) {
				if ("singleBoardFormBizConfigSection".equals(strings[0])) {
					showBizConfigSection = true;
					break;
				}
			}
		}
		modelAndView.addObject("showBizConfigSection", showBizConfigSection);

		boolean showformSection = false;
		if (allSections != null) {
			for (String[] strings : allSections) {
				if (FormSectionWebModel.QUERTSECTION.equals(strings[0]) || FormSectionWebModel.REPORT_TABLE_SECTION.equals(strings[0]) || FormSectionWebModel.REPORT_CHART_SECTION.equals(strings[0])) {
					showformSection = true;
					break;
				}
			}
		}
		modelAndView.addObject("showformSection", showformSection);

		return modelAndView;
	}
	
	/**
	 * 添加栏目选择器－公共信息
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView publicInformationTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/space/publicInformationTree");

		Map<String, List<String[]>> type2Sections = this.getSections(request);
		modelAndView.addObject("type2Sections", type2Sections);

		return modelAndView;
	}

	/**
	 * 添加栏目选择器－表单栏目
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView formbizconfigsTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/space/formbizconfigsTree");
		User user = CurrentUser.get();

		Map<String, List<String[]>> type2Sections = this.getSections(request);
		modelAndView.addObject("type2Sections", type2Sections);

		List<FormBizConfig> allBizConfigs = this.formBizConfigManager.getFormBizConfigs4Column(user.getId());
		List<FormBizConfig> bizConfigs = new ArrayList<FormBizConfig>();
		for (FormBizConfig formBizConfig : allBizConfigs) {
			BaseSection sectionMgr = sectionRegisterManager.getSection("singleBoardFormBizConfigSection");
			if (sectionMgr != null && sectionMgr.isAllowUserUsed(String.valueOf(formBizConfig.getId()))) {
				bizConfigs.add(formBizConfig);
			}

		}
		modelAndView.addObject("allBizConfigs", bizConfigs);

		Set<TempleteCategory> templeteCategories = new LinkedHashSet<TempleteCategory>();
		modelAndView.addObject("formQueryPlan", getFormSectionWebModel(templeteCategories));
		modelAndView.addObject("templeteCategories", templeteCategories);

		return modelAndView;
	}

	/**
	 * 添加栏目选择器－扩展栏目
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView forumTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/space/forumTree");

		Map<String, List<String[]>> type2Sections = this.getSections(request);
		modelAndView.addObject("type2Sections", type2Sections);

		return modelAndView;
	}

	/**
	 * 获取所有可选栏目
	 */
	private Map<String, List<String[]>> getSections(HttpServletRequest request) {
		SpaceType spaceType = SpaceType.valueOf(request.getParameter("spaceType"));
		boolean showBanner = "true".equals(request.getParameter("showBanner"));
		User user = CurrentUser.get();
		Map<String, List<String[]>> type2Sections = new LinkedHashMap<String, List<String[]>>();
		if (spaceType != null) {
			spaceType = Constants.parseDefaultSpaceType(spaceType);
			type2Sections = sectionRegisterManager.getSections(spaceType, user.getId(), user.getLoginAccount(), false, showBanner);
		}
		return type2Sections;
	}
	
	/**
	 * 添加栏目到空间
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView updateSpacePortlets(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
        JSONObject req = readerJSON(request);
        
		String pagePath = req.getString("pagePath");
		String editKeyId = req.getString("editKeyId");
		String decoration = req.getString("decorationId");
		if(Strings.isNotBlank(editKeyId)){
			request.setAttribute("editKeyId", editKeyId);
			String size = req.getString("size");
			//新建空间时不允许添加栏目
			if(Strings.isNotBlank(size)){
				int length = Integer.parseInt(size);
				String[] sectionIds = new String[length];
				String[] sectionNames = new String[length];
				String[] singleBoards = new String[length];
				String[] entityIds = new String[length];
				String[] ordinals = new String[length];
				if(length>0){
					for(int i=0; i<length; i++){
						sectionIds[i] = req.getString("sections_"+i);
						sectionNames[i] = req.getString("columnsName_"+i);
						singleBoards[i] = req.getString("singleBoardId_"+i);
						entityIds[i] = req.getString("entityId_"+i);
						ordinals[i] = req.getString("ordinal_"+i);
					}
				}
				spaceManager.addPortlet(pagePath, sectionIds,sectionNames,singleBoards,entityIds,ordinals,editKeyId,user.getId(),decoration);
			}
			response.setContentType("application/text;charset=UTF-8"); 
	        response.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = response.getWriter();
			out.write(editKeyId);
			out.flush();
		}
		return null;
	}
	/**
	 * 添加section到fragment
	 */
	public ModelAndView updateSectionsToFragment(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		JSONObject req = readerJSON(request);
		String editKeyId = req.getString("editKeyId");
		if(Strings.isNotBlank(editKeyId)){
			request.setAttribute("editKeyId", editKeyId);
			//fragmentId
			String entityId = req.getString("entityId");
			
			String size = req.getString("size");
			
			//新建空间时不允许添加栏目
			if(Strings.isNotBlank(size)){
				int length = Integer.parseInt(size);
				String sectionIds = req.getString("sectionIds");
				String[] sectionNames = new String[length];
				String[] singleBoards = new String[length];
				String[] entityIds = new String[length];
				String[] ordinals = new String[length];
				for(int i=0; i<length; i++){
					sectionNames[i] = req.getString("columnsName_"+i);
					singleBoards[i] = req.getString("singleBoardId_"+i);
					entityIds[i] = req.getString("entityId_"+i);
					ordinals[i] = req.getString("ordinal_"+i);
				}
				spaceManager.updateSectionsToFragment(Long.valueOf(entityId), sectionIds, sectionNames,singleBoards,entityIds,ordinals,user.getId());
			}
			
			response.setContentType("application/text;charset=UTF-8"); 
	        response.setCharacterEncoding("UTF-8"); 
	        PrintWriter out = response.getWriter();
			out.write(editKeyId);
			out.flush();
		}
		return null;
	}
	/**
	 * 个人用户更新栏目坐标信息
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView updateLayoutIndex(HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		
		JSONObject jsonObj = readerJSON(request);
		User user = CurrentUser.get();
		String pagePath = jsonObj.getString("pagePath");
    	
		String size = jsonObj.getString("size");
    	int length = Integer.parseInt(size);
    	List<Fragment> fragments = new ArrayList<Fragment>();
    	for(int i=0 ; i<length; i++){
    		String sectionId = jsonObj.getString("sectionId_"+i);
    		String x = jsonObj.getString("x_"+i);
    		String y = jsonObj.getString("y_"+i);
    		Fragment frag = new Fragment();
    		frag.setId(Long.valueOf(sectionId));
    		frag.setLayoutRow(Integer.valueOf(x));
    		frag.setLayoutColumn(Integer.valueOf(y));
    		fragments.add(frag);
    	}
    	String editKeyId = jsonObj.getString("editKeyId");
		if(Strings.isNotBlank(editKeyId)){
			request.setAttribute("editKeyId", editKeyId);
			spaceManager.updateLayoutIndex(pagePath,fragments,editKeyId,user.getId());
			response.setContentType("application/text;charset=UTF-8"); 
			response.setCharacterEncoding("UTF-8"); 
			PrintWriter out = response.getWriter();
			if(pagePath != null){
				out.write(editKeyId);
			}
			out.flush();
		}
		return null;
	}
	/**
	 * 管理员用户更新栏目坐标信息,在updateSpace方法中调用
	 */
	public void updateLayoutIndex(HttpServletRequest request,
			String pagePath)throws Exception {
		User user = CurrentUser.get();
		String jsonData = request.getParameter("jsonData");
		if(Strings.isNotBlank(jsonData)){
			JSONObject jsonObj = new JSONObject(jsonData);
			//String pagePath = jsonObj.getString("pagePath");
			String size = jsonObj.getString("size");
			int length = Integer.parseInt(size);
			List<Fragment> fragments = new ArrayList<Fragment>();
	    	for(int i=0 ; i<length; i++){
	    		String sectionId = jsonObj.getString("sectionId_"+i);
	    		String x = jsonObj.getString("x_"+i);
	    		String y = jsonObj.getString("y_"+i);
	    		Fragment frag = new Fragment();
	    		frag.setId(Long.valueOf(sectionId));
	    		frag.setLayoutRow(Integer.valueOf(x));
	    		frag.setLayoutColumn(Integer.valueOf(y));
	    		fragments.add(frag);
	    	}
	    	String editKeyId = jsonObj.getString("editKeyId");
			if(Strings.isNotBlank(editKeyId)){
				request.setAttribute("editKeyId", editKeyId);
				spaceManager.updateLayoutIndex(pagePath,fragments,editKeyId,user.getId());
			}
		}
	}
	/**
	 * 更新布局信息
	 */
	public ModelAndView updateLayoutDeco(HttpServletRequest request,
			HttpServletResponse response)throws Exception {
		User user = CurrentUser.get();
		String layout = request.getParameter("layout");
		String pagePath = request.getParameter("pagePath");
		
		Long accountId = user.getAccountId();
		 //集团管理员新建空间
        if(user.isGroupAdmin()) accountId = 1L;
        
		Long spaceId = null;
		if(Strings.isNotBlank(request.getParameter("space_id"))){
			spaceId = Long.parseLong(request.getParameter("space_id"));
		}
		//非管理员编辑自定义空间,管理员编辑默认空间
		if(!user.isAdmin()){
			//判断权限
        	if(spaceId != null){
        		SpaceFix fix = spaceManager.getSpace(spaceId);
        		SpaceType type = Constants.parseDefaultSpaceType(EnumUtil.getEnumByOrdinal(SpaceType.class, fix.getType()));
        		//空间管理员判断
        		if(type.equals(SpaceType.public_custom_group)||type.equals(SpaceType.public_custom)||type.equals(SpaceType.custom)||type.equals(SpaceType.corporation)||type.equals(SpaceType.department)||type.equals(SpaceType.group)){
        			if(!spaceManager.canManagerSpace(user.getId(), spaceId)){
        				showErrorMessage(request, response, user.getLoginAccount());
        				return null;
        			}
        		}else{
        			//个人用户编辑个人空间
        			SpaceFix personalFix = spaceManager.createPersonalDefineSpace(user.getId(), accountId,Long.valueOf(spaceId));
        			pagePath = personalFix.getPagePath();
        		}
        	}
		}
		spaceManager.updatePage(pagePath,layout);
        PrintWriter out = response.getWriter();
		if(pagePath != null){
			out.write(pagePath);
		}
		out.flush();
		return null;
	}
	@SuppressWarnings("unchecked")
	protected JSONObject readerJSON(HttpServletRequest request) throws Exception{
		JSONObject jsonObject = new JSONObject();  
        Map parameterMap = request.getParameterMap();  
        // 通过循环遍历的方式获得key和value并set到JSONObject中  
        Iterator paIter = parameterMap.keySet().iterator();  
        while (paIter.hasNext()) {  
            String key = paIter.next().toString();  
            String[] values = (String[])parameterMap.get(key);  
            jsonObject.accumulate(key, values[0]);  
        }  
        log.debug("从客户端获得json=" + jsonObject.toString());  
	    return jsonObject;
	}
	public String getcategoryHTML(Set<TempleteCategory> templeteCategories){
		StringBuffer categoryHTML = new StringBuffer();
		//categoryHTML.append("<option value=\"\"><option>") ;
		List<Long> categorys = new ArrayList<Long>();
		categorys.add(new Long(4));
		categorys.add(new Long(0));
		List<TempleteCategory> t = new ArrayList<TempleteCategory>(templeteCategories);
		OperBaseManager.category2HTML(t,categoryHTML,categorys,1);
		//categoryHTML.append(getIOperBase().categoryHTML(templeteCategoryManager));
		return categoryHTML.toString() ;
	}
	
	
	private Map<Long,Set<FormSectionWebModel>> getFormSectionWebModel(Set<TempleteCategory> templeteCategories) throws Exception{
		User user = CurrentUser.get();
		long memberId = user.getId();
		//long orgAccountId = user.getLoginAccount();
		List<FormQueryPlan> queryPlans = getFormDaoManager().findByUserId(memberId , IPagePublicParam.C_iObjecttype_Query);
		List<FormQueryPlan> reportPlans = getFormDaoManager().findByUserId(memberId , IPagePublicParam.C_iObjecttype_Report);
		List<FormQueryPlan> all = new ArrayList<FormQueryPlan>();
		Map<String,Set<FormQueryPlan>> formQueryPlanMap = new HashMap<String,Set<FormQueryPlan>>();
		
		if(queryPlans != null){
			all.addAll(queryPlans) ;
		}
		if(reportPlans != null){
			all.addAll(reportPlans) ;
		}
		
		if(all != null){
			for(FormQueryPlan formQueryPlan : all){
				if(formQueryPlanMap.get(formQueryPlan.getAppmainId()+"") == null ){
					Set<FormQueryPlan> list = new HashSet<FormQueryPlan>();
					list.add(formQueryPlan) ;
					formQueryPlanMap.put(formQueryPlan.getAppmainId()+"", list) ;
				}else{
					formQueryPlanMap.get(formQueryPlan.getAppmainId()+"").add(formQueryPlan) ;
				}
			}
		}
		
		Map<Long,Set<FormSectionWebModel>> map = new HashMap<Long,Set<FormSectionWebModel>>();
		//获取未停用表单列表
		List <FormAppMain> newAppList = new ArrayList<FormAppMain>();
        // 获取表单分类列表
       
        // 获取表单名称列表
        Set<String> formNameList = new LinkedHashSet<String>();
		
        getIOperBase().getformAccess(newAppList, templeteCategories, formNameList, null, IPagePublicParam.C_iObjecttype_Report) ;
		getIOperBase().getformAccess(newAppList, templeteCategories, formNameList, null, IPagePublicParam.C_iObjecttype_Query) ;
		
		if(templeteCategories != null){
			for(TempleteCategory templeteCategory : templeteCategories){
				Set<FormSectionWebModel> list = new HashSet<FormSectionWebModel>() ;
				for(FormAppMain formAppMain : newAppList){					
					if(templeteCategory.getId().equals(formAppMain.getCategory())){
						FormSectionWebModel  formSectionWebModel = null ;
						if(Strings.isNotBlank(formAppMain.getQueryname())){
							formSectionWebModel = new FormSectionWebModel(formAppMain.getQueryname(),formAppMain.getQueryname(),formAppMain.getCategory(),formAppMain.getId(),FormSectionWebModel.SectionType.FormQuery);
						}else if(Strings.isNotBlank(formAppMain.getReportname())){
							formSectionWebModel = new FormSectionWebModel(formAppMain.getReportname(),formAppMain.getReportname(),formAppMain.getCategory(),formAppMain.getId(),FormSectionWebModel.SectionType.FormReport);
						}
						
						if(formSectionWebModel != null){
							list.add(formSectionWebModel) ;
						}
						
						
						if(formQueryPlanMap != null  ){
							
							Set<FormQueryPlan> formQueryPlanList =	formQueryPlanMap.get(formAppMain.getId()+"") ;
							if(formQueryPlanList != null){								
								for(FormQueryPlan formQueryPlan : formQueryPlanList){
									FormSectionWebModel.SectionType sectionType = FormSectionWebModel.SectionType.FormQueryMyPlan ;
									if(formQueryPlan.getPlanType() == 2){
										sectionType = FormSectionWebModel.SectionType.FormReportMyPlan ;
									}
									
									FormSectionWebModel  formSectionWebModelL = new FormSectionWebModel(formQueryPlan.getId()+"",formQueryPlan.getPlanName(),formAppMain.getCategory(),formAppMain.getId(),sectionType);
									list.add(formSectionWebModelL) ;
								}
							}
							
						}
						
					}
				}
				
				if(list != null && !list.isEmpty()){
					map.put(templeteCategory.getId(), list) ;	
				}				
			}
		}
		
		
		return map ;
	}
	
	public ModelAndView getFormReportToFormSection(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("sysMgr/space/formReportToFormSection") ;
		
		String sectionType = request.getParameter("sectionType") ;
		String refAppformMainId = request.getParameter("refAppformMainId") ;
		String reportId = request.getParameter("reportId") ;
		if(Strings.isBlank(sectionType) || Strings.isBlank(reportId) || Strings.isBlank(refAppformMainId) ){
			return null ;
		}
		SeeyonForm_ApplicationImpl fapp = (SeeyonForm_ApplicationImpl) SeeyonForm_Runtime.getInstance().getAppManager().findById(Long.valueOf(refAppformMainId)) ;
		if(fapp == null){
			return null ;
		}
		
		SeeyonReportImpl seeyonReport = null ;
		FormSectionWebModel.SectionType setcionType = null ;
		FormQueryPlan formQueryPlan = null;
		String reportName = "";
		if(FormSectionWebModel.SectionType.FormReport.toString().endsWith(sectionType)){
			seeyonReport = (SeeyonReportImpl)fapp.findReportByName(reportId) ;
			setcionType = FormSectionWebModel.SectionType.FormReport ;
			reportName = seeyonReport.getReportName();
		}else if(FormSectionWebModel.SectionType.FormReportMyPlan.toString().endsWith(sectionType)){			
			formQueryPlan  = getFormDaoManager().getFormQueryPlanById(Long.valueOf(reportId)) ;
			if(formQueryPlan != null){
				seeyonReport = (SeeyonReportImpl)fapp.findReportByName(formQueryPlan.getQueryName());
				reportName = formQueryPlan.getPlanName();
			}
			setcionType = FormSectionWebModel.SectionType.FormReportMyPlan ;
		}
		
		List<FormSectionWebModel> list = formReportToFormSection(seeyonReport,fapp.getId(),fapp.getCategory(),setcionType,formQueryPlan) ;
		mav.addObject("list", list) ;
		mav.addObject("reportName" ,reportName) ;
		return mav ;
		
		
	}
	
	/**
	 *  构造FormSectionWebModel List
	 * @param seeyonReport 
	 * @param refappFormId
	 * @param cartyId
	 * @param sectiontype
	 * @param formQueryPlanName    我的统计
	 * @return
	 */
	private  List<FormSectionWebModel> formReportToFormSection(SeeyonReportImpl seeyonReport,Long refappFormId , Long cartyId ,FormSectionWebModel.SectionType sectiontype, FormQueryPlan formQueryPlan){
		if( seeyonReport == null ){
			return new ArrayList<FormSectionWebModel>(0) ;
		}
		
		List<FormSectionWebModel> list = new ArrayList<FormSectionWebModel>() ;
		String sectionWebModelId = seeyonReport.getReportName();
		String sectionWebModelName = seeyonReport.getReportName();
		if(sectiontype == FormSectionWebModel.SectionType.FormReportMyPlan){
			sectionWebModelId = String.valueOf(formQueryPlan.getId());
			sectionWebModelName = formQueryPlan.getPlanName();
		}
		FormSectionWebModel formSectionWebModel = new FormSectionWebModel(sectionWebModelId,sectionWebModelName,cartyId,refappFormId,sectiontype);
		formSectionWebModel.setReportShowType(FormSectionWebModel.ReportShowType.Talbe) ;
		list.add(formSectionWebModel) ;
		
		
		Map<String,ReportChartInfo> map = seeyonReport.getChartInfos() ;
		
		if(map == null || map.isEmpty()){
			return list ;
		}
		
		for(String key : map.keySet()){
			ReportChartInfo reportChartInfo = map.get(key) ;
			FormSectionWebModel sectionWebModel = new FormSectionWebModel(sectionWebModelId,reportChartInfo.getName(),cartyId,refappFormId,sectiontype);	
			sectionWebModel.setReportChartInfoName(reportChartInfo.getName()) ;
			sectionWebModel.setReportShowType(FormSectionWebModel.ReportShowType.Picture) ;
			list.add(sectionWebModel) ;
		}
		
		return list ;
	}
	
	protected List<Long> getUserDomainIds() {
		return CommonTools.getUserDomainIds(CurrentUser.get().getId(), null);
	}
	
	public IOperBase getIOperBase() {
		return  (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase") ;
	}
	public FormDaoManager getFormDaoManager() {
		return (FormDaoManager)SeeyonForm_Runtime.getInstance().getBean("formDaoManager");
	}
	
	
	/**
	 * 选择关联系统
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView linkSystemSelector(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/space/linkSystemSelector");
		List<LinkSystem> linkSystemList = outerlinkManager.findOutLinkOfCurrentUserByPage();	//得到该用户能看到的所有关联系统
		Map<Long, String> categoryName = new HashMap<Long, String>();
		if(linkSystemList != null && !linkSystemList.isEmpty()){
			for(LinkSystem s : linkSystemList){
				LinkCategory lc = outerlinkManager.getCategoryById(s.getLinkCategoryId());
				categoryName.put(s.getId(), lc.getName());
			}
		}
		modelAndView.addObject("linkSystemList", linkSystemList);
		modelAndView.addObject("categoryMap", categoryName);
		return modelAndView;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		return null;
	}
    
    
	public ModelAndView showFragmentsOfDefaultPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("sysMgr/space/fragmentDefined");
		int type = Integer.parseInt(request.getParameter("type"));
		Constants.SpaceType spaceType = EnumUtil.getEnumByOrdinal(Constants.SpaceType.class, type);
		if(spaceType == null){
			return null;
		}
		
		User user = CurrentUser.get();
		long accountId = user.getLoginAccount();
		
		String pagePath = null;
		switch (spaceType) {
		case personal:
			pagePath = this.spaceManager.getDefaultPersonalSpacePath(accountId);
			break;
		case Default_personal:
			pagePath = Constants.DEFAULT_PERSONAL_PAGE_PATH;
			break;
		case corporation:
			pagePath = Constants.DEFAULT_CORPORATION_PAGE_PATH;
			break;
		}
		
		if(pagePath == null){
			return null;
		}
		
		Map<String,Map<String,Fragment>> fragments = spaceManager.getFragments(pagePath);
		
		return mv.addObject("fragments", fragments);
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}
	public void setFormBizConfigManager(FormBizConfigManager formBizConfigManager) {
		this.formBizConfigManager = formBizConfigManager;
	}
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	
	/**
	 * 前端用户修改配置。
	 * 策略:<br>
	 * fragmentId
	 * if 是默认的个人空间
	 *       copy空间一份
	 *       查询出要修改的fragmentId1
	 *       fragmentId = fragmentId1;
	 * update by fragmentId
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateProperty(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String entityId = request.getParameter("entityId");
		Map<String,String> properties = PortalConstants.doPortletEntityProperty(request, entityId);
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		String personalPagePath = null;
		String showState = request.getParameter("showState");
		String tabIndex = request.getParameter("tab");
		User user = CurrentUser.get();
		JSONObject obj = new JSONObject();
		
		if(showState.equals("view")){
			if("default_leader".equals(spaceType) || "Default_personal".equals(spaceType) || "Default_out_personal".equals(spaceType)  || "Default_personal_custom".equals(spaceType)){
				/**
				 * 更改个人类型空间个性化创建方法
				 */
				SpaceFix personalFix = spaceManager.createPersonalDefineSpace(user.getId(), user.getAccountId(), Long.valueOf(spaceId));
				personalPagePath = personalFix.getPagePath();
				String x = request.getParameter("x");
				String y = request.getParameter("y");
				Map<String,Map<String,Fragment>> fragments = spaceManager.getFragments(personalPagePath);
				if(fragments != null){
					Map<String,Fragment> columnF = fragments.get(y);
					if(fragments != null){
						Fragment row = columnF.get(x);
						if(row != null){
							entityId = row.getId().toString();
						}
					}
				}
				obj.put("pagePath", personalPagePath);
				obj.put("result", "customed");
			}else{
				obj.put("result", "true");
			}
			portletEntityPropertyManager.save(Long.parseLong(entityId), properties,tabIndex);
		}else{
			String editKeyId = request.getParameter("editKeyId");
			if(Strings.isNotBlank(editKeyId)){
				request.setAttribute("editKeyId", editKeyId);
				spaceManager.updateProperty(Long.parseLong(entityId),properties,tabIndex,Long.valueOf(editKeyId),user.getId());
				obj.put("editKeyId", editKeyId);
				obj.put("result", "true");
			}
		}
		
		PrintWriter out = response.getWriter();
		out.write(obj.toString());
		out.flush();
		out.close();
		return null;
	}
	/**
	 * 前端用户修改配置。
	 * 策略:<br>
	 * fragmentId
	 * if 是默认的个人空间
	 *       copy空间一份
	 *       查询出要修改的fragmentId1
	 *       fragmentId = fragmentId1;
	 * update by fragmentId
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteFrament(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String entityId = request.getParameter("entityId");
		String spaceType = request.getParameter("spaceType");
		String spaceId = request.getParameter("spaceId");
		String personalPagePath = request.getParameter("pagePath");
		String showState = request.getParameter("showState");
		String index = request.getParameter("index");
		User user = CurrentUser.get();
		
		JSONObject obj = new JSONObject();
		if(showState.equals("view")){
			if("default_leader".equals(spaceType) || "Default_personal".equals(spaceType) || "Default_out_personal".equals(spaceType)  || "Default_personal_custom".equals(spaceType)){
				// 更改个人类型空间个性化创建方法
				SpaceFix personalFix = spaceManager.createPersonalDefineSpace(user.getId(), user.getAccountId(), Long.valueOf(spaceId));
				personalPagePath = personalFix.getPagePath();
				String x = request.getParameter("x");
				String y = request.getParameter("y");
				Map<String,Map<String,Fragment>> fragments = spaceManager.getFragments(personalPagePath);
				if(fragments != null){
					Map<String,Fragment> columnF = fragments.get(y);
					if(fragments != null){
						Fragment row = columnF.get(x);
						if(row != null){
							entityId = row.getId().toString();
						}
					}
				}
			}
			spaceManager.deleteFragment(Long.valueOf(entityId),personalPagePath,Integer.parseInt(index));
			if(personalPagePath != null){
				obj.put("pagePath", personalPagePath);
			}
			
		}else{
			//编辑状态，缓存删除
			String editKeyId = request.getParameter("editKeyId");
			if(Strings.isNotBlank(editKeyId)){
				request.setAttribute("editKeyId", editKeyId);
				spaceManager.deleteFragment(Long.valueOf(entityId),spaceId,editKeyId,user.getId(),Integer.parseInt(index));
				obj.put("editKeyId", editKeyId);
				obj.put("pagePath", personalPagePath);
			}
		}
		PrintWriter out = response.getWriter();
		out.write(obj.toString());
		out.flush();
		out.close();
		return null;
	}
	/**
	 * 个人类型空间恢复默认
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView toDefaultPersonalSpace(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		String editKeyId = request.getParameter("editKeyId");
		String decorationId = request.getParameter("decorationId");
		User user = CurrentUser.get();
		if(Strings.isNotBlank(editKeyId)){
			if("leader".equals(spaceType) || "personal".equals(spaceType) || "outer".equals(spaceType)  || "personal_custom".equals(spaceType)){
				String decoration = spaceManager.toDefaultPersonalSpace(user.getId(), user.getAccountId(), Long.valueOf(spaceId),spaceType);
				if(Strings.isNotBlank(decorationId)){
					decorationId = decoration;
				}
			}
			JSONObject obj = new JSONObject();
			obj.put("decorationId", decorationId);
			obj.put("editKeyId", editKeyId);
			obj.put("toDefault", "toDefault");
			PrintWriter out = response.getWriter();
			out.write(obj.toString());
			out.flush();
			out.close();
		}
		return null;
	}
	public SpaceFix toDefaultSpace(SpaceType spaceType, String[][] manages, String[][] users, String[][] vistors, Long entityId) throws SpaceException {
		if (spaceType == null) {
			return null;
		}
		
		SpaceFix spaceFix = spaceManager.getSpaceFix(spaceType, entityId, null);
		// 默认空间不存在，返回空
		if (spaceFix == null) {
			return null;
		}
		
		menuManager.deleteSpaceMenu(spaceFix.getId());
		return spaceManager.toDefaultSpace(spaceType, manages, users, vistors, entityId, spaceFix);
	}
	/**
     * 空间导航配置 - 显示
     */
    public ModelAndView showSpacesSetting(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("sysMgr/space/spacesSetting");
    	User user = CurrentUser.get();
    	Long userId = user.getId();
    	Long loginAccountId = user.getLoginAccount();
    	
    	//已选空间：当前用户设置显示的访问空间
    	List<String[]> spaceList = spaceManager.getSpaceSort(userId, loginAccountId, user.getLocale(), false, null);
    	List<String[]> list = new ArrayList<String[]>();
    	for(String[] space : spaceList){
    		if(space[3].equals("false")&&(space[0].equals("0")|| space[0].equals("5") || space[0].equals("10") || space[0].equals("13") || space[0].equals("15") || space[0].equals("16"))){
    			String[] sp = new String[5];
    			sp[0] = space[0];
    			sp[1] = space[1];
    			sp[2] = space[2];
    			String spaceId = space[1];
    			SpaceFix _space = spaceManager.getSpace(Long.valueOf(spaceId));
    			if(_space==null){
    				log.error("导航中空间【"+spaceId+"】不存在。");
    			}else{
    				boolean isAllowdefined = _space.isAllowdefined();
    				if(isAllowdefined){
    					sp[4] = "true";
    				}else{
    					sp[4] = "false";
    				}
    				boolean canAccess = _space.getState()!=1;
        			if(canAccess){
        				String pagePath = _space.getPagePath();
        				sp[3] = pagePath;
        				list.add(sp);
        			}
    			}
    			
    		}
    	}
    	String defaultSpacePath = null;
    	String defaultSpaceId = null;
    	if(list.size()>0){
    		String[] defaultSpace = list.get(0);
    		defaultSpacePath = defaultSpace[3];
    		defaultSpaceId = defaultSpace[1];
    		mav.addObject("allowed", defaultSpace[4]);
    	}
    	return mav.addObject("spaceList", list).addObject("defaultSpacePath", defaultSpacePath).addObject("defaultSpaceId", defaultSpaceId);
    }

	/**
	 * 更新自定义团队空间对应公共信息板块名称
	 */
	private void updatePublicInfo(Long spaceId, String spaceName) throws Exception {
		BulType bulType = bulTypeManager.getById(spaceId);
		if (bulType != null) {
			bulType.setTypeName(spaceName);
			bulTypeManager.save(bulType);
		}

		NewsType newsType = newsTypeManager.getById(spaceId);
		if (newsType != null) {
			newsType.setTypeName(spaceName);
			newsTypeManager.save(newsType);
		}

		InquirySurveytype surveytype = inquiryManager.getSurveyTypeById(spaceId);
		if (surveytype != null) {
			surveytype.setTypeName(spaceName);
			inquiryManager.updateInquiryType(surveytype);
		}

		// 更新讨论版块，设定版块管理员，完成持久化与内存同步
		List<V3xOrgMember> members = spaceManager.getSpaceMemberBySecurity(spaceId, 1);
		List<Long> admins = CommonTools.getEntityIds(members);
		V3xBbsBoard board = bbsBoardManager.getBoardById(spaceId);
		if (board != null) {
			board.setName(spaceName);
			bbsBoardManager.updateV3xBbsBoard(board, admins);
		}
	}

}