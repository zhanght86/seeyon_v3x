package com.seeyon.v3x.link.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.link.domain.LinkAcl;
import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkOptionValue;
import com.seeyon.v3x.link.domain.LinkSpace;
import com.seeyon.v3x.link.domain.LinkSpaceAcl;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.link.util.Constants;
import com.seeyon.v3x.link.webmodel.LinkMenuVo;
import com.seeyon.v3x.link.webmodel.LinkModifyVo;
import com.seeyon.v3x.link.webmodel.LinkMoreVO;
import com.seeyon.v3x.link.webmodel.LinkOptionVO;
import com.seeyon.v3x.link.webmodel.LinkSectionVo;
import com.seeyon.v3x.link.webmodel.LinkShowVO;
import com.seeyon.v3x.link.webmodel.LinkUserVo;
import com.seeyon.v3x.link.webmodel.WebLinkOptionValueImportResultVO;
import com.seeyon.v3x.main.section.definition.SectionDefinitionManager;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionProps;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.LightWeightEncoder;
import com.seeyon.v3x.util.Strings;

public class LinkManagerController extends BaseController {
	private static final Log logger = LogFactory.getLog(LinkManagerController.class);
	
	private OuterlinkManager outerlinkManager;
	
	private SectionDefinitionManager sectionDefinitionManager;
	
	private OrgManager orgManager;
	
	private AttachmentManager attachmentManager;
	
	private FileToExcelManager fileToExcelManager;
	
    private FileManager fileManager;
	
	private AppLogManager appLogManager;

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
    public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
        this.fileToExcelManager = fileToExcelManager;
    }
    
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }    
    
    public SectionDefinitionManager getSectionDefinitionManager() {
        return sectionDefinitionManager;
    }
    
    public void setSectionDefinitionManager(SectionDefinitionManager sectionDefinitionManager) {
        this.sectionDefinitionManager = sectionDefinitionManager;
    }

    @Override
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("link/linkIndex");
		return modelView;
	}
	
	//进入总框架页面
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView linkIframe(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("link/linkIframe");
//		List<LinkCategory> category=outerlinkManager.getAllLinkCategory();	//获取所有的类别
//		if(category != null && category.isEmpty()==false){
//			modelView.addObject("category", category.get(0));
//		}
		LinkCategory category = this.outerlinkManager.getCategoryById(Constants.LINK_CATEGORY_COMMON_ID);
		modelView.addObject("category", category);
		return modelView;
	}
	
	//进入页面菜单项
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView linkToolBar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("link/linkToolBar");
		String categoryId=request.getParameter("categoryId");
		String categoryName=request.getParameter("categoryName");
		String isSystem=request.getParameter("isSystem");
		modelView.addObject("categoryId", categoryId);
		modelView.addObject("isSystem", isSystem);
		modelView.addObject("categoryName", categoryName);
		return modelView;
	}
	
	//进入树状页面
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView linkTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("link/linkTree");
		List<LinkCategory> category=outerlinkManager.getAllLinkCategory();	//获取所有的类别
		modelView.addObject("theCategory", category);		//
		return modelView;
	}
	
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView linkMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("link/linkMain");
		return modelView;
	}
	
	//获取常用链接的菜单列表
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView getMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelView=new ModelAndView("link/linkMenu");
		String categoryId=request.getParameter("categoryId");			//类别ID
		List<LinkSystem> list=outerlinkManager.getLinkSystemByCategoryId(Long.valueOf(categoryId));	 	//获取所有的常用连接
		//Collections.sort(list);
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		
		List<LinkMenuVo> the_list=new ArrayList<LinkMenuVo>();
		for(int i=first;i<pageSize+first;i++){
			if(i>list.size()-1){
				break;
			}
			LinkMenuVo menu=new LinkMenuVo();
			LinkSystem link=list.get(i);
			menu.setLinkSystem(link);
			if(link.getIsSystem() == 1){
				menu.setIsSystem("link.system.type");
			}else{
				menu.setIsSystem("link.customer.type");
			}
			LinkCategory lc = outerlinkManager.getCategoryById(link.getLinkCategoryId());
			menu.setLinkCategory(lc.getName());	//类别
		//	menu.setCreatorName(orgManager.getMemberById(link.getCreateUserId()).getName());
			the_list.add(menu);
		}
		modelView.addObject("linkMenu", the_list);
		return modelView;
	}
	
	//进入常用链接添加界面
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView getAddLinkPage(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long categoryId = Long.parseLong(request.getParameter("categoryId"));
		//获得最大排序号
		Integer maxSortNum = outerlinkManager.getMaxLinkSystemOrder(categoryId);
		LinkSystem link = new LinkSystem();
		link.setOrderNum(maxSortNum+1);
		
		LinkModifyVo linkVo=new LinkModifyVo();
		linkVo.setLinkSystem(link);
		
		return new ModelAndView("link/linkAdd")
					.addObject("link", linkVo)
					.addObject("addLink", true)
					.addObject("categoryId", request.getParameter("categoryId"))
					.addObject("flag", request.getParameter("flag"))
					.addObject("OPENTYPE_OPEN", LinkSystem.OPENTYPE_OPEN)
					.addObject("OPENTYPE_WORKSPACE", LinkSystem.OPENTYPE_WORKSPACE);
	}
	
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView validateUrlView(HttpServletRequest request, HttpServletResponse response) throws Exception{
		return new ModelAndView("link/validateUrlView");
	}
	
	//添加一个链接
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView addLink(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String linkName = request.getParameter("linkName");		//链接名称
		long categoryId = Long.valueOf(request.getParameter("theCategory"));	//分类ID
		String members = request.getParameter("members");			//授权人员ID串
		String linkURL = request.getParameter("linkURL");			//链接得URL地址
		String needContentCheck = request.getParameter("needContentCheck");
		String contentForCheck = request.getParameter("contentForCheck");
		String sameRegion = request.getParameter("sameRegion");
		String agentUrl = request.getParameter("agentUrl");
		String theMethod = request.getParameter("theMethod");		//参数传递方式
		String theWay = request.getParameter("way");
		String[] paramName = request.getParameterValues("paramName");	//参数名称数组
		String[] paramSign = request.getParameterValues("paramSign");
		String[] initValue = request.getParameterValues("initValue");
		String[] hidPassword = request.getParameterValues("hidPassword");
		
		//是否允许配置成空间导航
		boolean allowedAsSpace = Strings.isNotBlank(request.getParameter("allowedAsSpace"));
		//是否允许配置到栏目
        boolean allowedAsSection = Strings.isNotBlank(request.getParameter("allowedAsSection"));
		
		//如果允许配置成空间导航，其打开方式：新窗口或工作区
		//int openType = allowedAsSpace ? NumberUtils.toInt(request.getParameter("openType")) : LinkSystem.OPENTYPE_NONE;
		
		String number = request.getParameter("number");			//排序号 如果为空，给予一个值(排在末尾)
		int theNumber = NumberUtils.toInt(number, -1);
		if(theNumber==-1)
			theNumber = outerlinkManager.getMaxLinkSystemOrder(categoryId);
		String method = null;
		if(theWay == null){
		    method = Integer.valueOf(theMethod) == 0 ? "post" : "get";
		}else{
		    method = Integer.valueOf(theWay) == 3 ? "yes" : "no";
		}
		String description = StringUtils.defaultIfEmpty(request.getParameter("description"), " ");
		String theFileURL = StringUtils.defaultIfEmpty(request.getParameter("theFileURL"), " ");
		try {
			//添加一个链接
			long theId = outerlinkManager.addLinkSystem(linkName, theNumber, description, linkURL, needContentCheck.equals("1"), contentForCheck, sameRegion.equals("1"), agentUrl, method, theFileURL, 
					categoryId, allowedAsSpace, allowedAsSection);
	        if(allowedAsSpace){
	            //如果允许配置成空间导航，则增加一个或多个扩展空间
	            String[] spaceName = request.getParameterValues("spaceName");
	            String[] targetPageUrlForSpace = request.getParameterValues("targetPageUrlForSpace");
	            String[] openType = request.getParameterValues("openType");
	            String[] linkSpaceAuths = request.getParameterValues("linkSpaceAuths");
    	        if(spaceName != null && spaceName.length > 0){
    	            List<LinkSpace> linkSpaceList = new ArrayList<LinkSpace>(spaceName.length);
    	            List<LinkSpaceAcl> linkSpaceAclList = new ArrayList<LinkSpaceAcl>();
    	            for(int spaceNum = 0; spaceNum < spaceName.length; spaceNum++){
    	                LinkSpace linkSpace = new LinkSpace();
    	                linkSpace.setIdIfNew();
                        linkSpace.setSpaceName(spaceName[spaceNum]);
                        linkSpace.setTargetPageUrl(targetPageUrlForSpace[spaceNum]);
                        linkSpace.setOpenType(NumberUtils.toInt(openType[spaceNum]));
    	                linkSpace.setLinkSystemId(theId);
    	                linkSpaceList.add(linkSpace);
    	                //增加一个或多个扩展空间的权限
    	                if(linkSpaceAuths[spaceNum] != null && !linkSpaceAuths[spaceNum].equals("")){
    	                    String[][] linkSpaceAuthsInfoList = Strings.getSelectPeopleElements(linkSpaceAuths[spaceNum]);
        	                for(String[] linkSpaceAuthsInfo : linkSpaceAuthsInfoList){
            	                LinkSpaceAcl linkSpaceAcl = new LinkSpaceAcl();
            	                linkSpaceAcl.setIdIfNew();
            	                linkSpaceAcl.setUserType((String)linkSpaceAuthsInfo[0]);
            	                linkSpaceAcl.setUserId(Long.valueOf(linkSpaceAuthsInfo[1]));
            	                linkSpaceAcl.setLinkSpaceId(linkSpace.getId());
            	                linkSpaceAclList.add(linkSpaceAcl);
        	                }
    	                }
    	            }
    	            outerlinkManager.addLinkSpace(linkSpaceList);
    	            outerlinkManager.addLinkSpaceAcl(linkSpaceAclList);
	            }
	        }
	        if(allowedAsSection){
                //如果允许配置到栏目，则增加一个或多个扩展栏目
                String[] sectionName = request.getParameterValues("sectionName");
                String[] sectionType = request.getParameterValues("sectionType");
                String[] state = request.getParameterValues("state");
                String[] ssoWebcontentURL = request.getParameterValues("ssoWebcontentURL");
                String[] ssoIframeURL = request.getParameterValues("ssoIframeURL");
                String[] iframeURL = request.getParameterValues("iframeURL");
                String[] ssoWebcontentSessionTimeout = request.getParameterValues("ssoWebcontentSessionTimeout");
                String[] ssoWebcontentPageHeight = request.getParameterValues("ssoWebcontentPageHeight");
                String[] ssoIframePageHeight = request.getParameterValues("ssoIframePageHeight");
                String[] iframePageHeight = request.getParameterValues("iframePageHeight");
                //String[] ssoWebcontentLinkSystemId = request.getParameterValues("ssoWebcontentLinkSystemId");
                //String[] ssoIframeLinkSystemId = request.getParameterValues("ssoIframeLinkSystemId");
                //String[] iframeLinkSystemIds = request.getParameterValues("iframeLinkSystemId");
                String[] linkSectionAuths = request.getParameterValues("linkSectionAuths");
                for(int i = 0; i < sectionName.length; i++){
                    Map<String, String> props = new HashMap<String, String>();
                    if(sectionType[i].equals("0")){
                        props.put("ssoWebcontentURL", Strings.escapeNULL(ssoWebcontentURL[i], ""));
                        props.put("ssoWebcontentSessionTimeout", Strings.escapeNULL(ssoWebcontentSessionTimeout[i], ""));
                        props.put("ssoWebcontentPageHeight", Strings.escapeNULL(ssoWebcontentPageHeight[i], ""));
                        props.put("ssoWebcontentLinkSystemId", Strings.escapeNULL(String.valueOf(theId), ""));
                    } else if(sectionType[i].equals("1")){
                        props.put("ssoIframeURL", Strings.escapeNULL(ssoIframeURL[i], ""));
                        props.put("ssoIframePageHeight", Strings.escapeNULL(ssoIframePageHeight[i], ""));
                        props.put("ssoIframeLinkSystemId", Strings.escapeNULL(String.valueOf(theId), ""));
                    } else {
                        props.put("iframeURL", Strings.escapeNULL(iframeURL[i], ""));
                        props.put("iframePageHeight", Strings.escapeNULL(iframePageHeight[i], ""));
                        props.put("iframeLinkSystemId", Strings.escapeNULL(String.valueOf(theId), ""));
                    }
                    this.sectionDefinitionManager.save(sectionName[i], Integer.valueOf(sectionType[i]), Integer.valueOf(state[i]), linkSectionAuths[i], props);
                }
            }
			List<Attachment> atts = attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.global,
					theId, theId, request);
			attachmentManager.create(atts);
			User user = CurrentUser.get();
			appLogManager.insertLog(user, AppLogAction.InterSystem_Create,linkName);
			//对链接授权
			if(Strings.isNotBlank(members)){
				StringTokenizer token=new StringTokenizer(members,",");
				while(token.hasMoreTokens()){
					StringTokenizer str=new StringTokenizer(token.nextToken(),"|");		//得到类型及ID
					String userType=str.nextToken();	//类型
					long userId=Long.valueOf(str.nextToken());	//ID	
					outerlinkManager.addLinkAcl(theId, userType, userId);
				}
			}
			//添加高级选项
			boolean password=false;
			if(paramName != null  && paramSign != null){			//名称不能为空
				for(int i=0;i<paramName.length;i++){
					password=hidPassword[i].equals("1");
					outerlinkManager.addLinkOption(paramName[i], paramSign[i], initValue[i], password, i, theId);
				}
			}
			super.rendJavaScript(response, "parent.parent.refreshAfterAction();");
		} catch (Exception e) {
			logger.error("为关联系统[" + linkName + "]添加授权信息和高级选项出现异常", e);
		}
		return null;
	}
	
	//删除链接
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView deleteLink(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String deleteId=request.getParameter("deleteId");
		StringTokenizer token=new StringTokenizer(deleteId,",");
		List<Long> list=new ArrayList<Long>();
		while(token.hasMoreTokens()){
			list.add(Long.valueOf(token.nextToken()));
		}
		StringBuffer sb = new StringBuffer();
		if(!list.isEmpty()){
			List<LinkSystem> linkList = outerlinkManager.getLinkSystemByIds(list);
			for(LinkSystem link : linkList){
				if(sb.length() != 0){
					sb.append(",");
				}
				sb.append(link.getName());
			}
			for(Long linkSystemId : list){
			    List<SectionProps> sps = this.sectionDefinitionManager.getSectionPropsByLinkSystemId(String.valueOf(linkSystemId));
			    for(SectionProps sp : sps){
			        this.sectionDefinitionManager.delete(sp.getSectionDefinitionId());
			    }
			}
		}
		outerlinkManager.deleteLinkSystem(list);
		User user = CurrentUser.get();
		appLogManager.insertLog(user, AppLogAction.InterSystem_Delete,sb.toString());
		super.rendJavaScript(response, "parent.parent.refreshAfterAction();");
		return null;
	}
	
	//进入链接修改界面
	@SuppressWarnings("rawtypes")
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView getModifyLink(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView=new ModelAndView("link/linkAdd");
		String linkId=request.getParameter("linkId");		//要修改得ID
		String flag=StringUtils.defaultIfEmpty(request.getParameter("flag"), "false");
		LinkSystem link=outerlinkManager.getLinkSystemById(Long.valueOf(linkId));
		Set linkAcl=link.getLinkAcl();			//授权对象
		List<LinkOption> linkOption=outerlinkManager.getlinkOptionBySystemId(link.getId()); //高级选项
		LinkCategory category=outerlinkManager.getLinkCategoryBylinkId(link.getId());
		Set<LinkSpace> linkSpaceSet = link.getLinkSpaces();
		for(LinkSpace linkSpaceTmp : linkSpaceSet){
		    Set<LinkSpaceAcl> linkSpaceAclSet= linkSpaceTmp.getLinkSpaceAcls();
		    List<Long> authList = new ArrayList<Long>();
		    List<V3xOrgEntity> entitylList = new ArrayList<V3xOrgEntity>();
		    if(linkSpaceAclSet != null){
	            Iterator it = linkSpaceAclSet.iterator();
	            String ids = "";
	            while(it.hasNext()){
	                LinkSpaceAcl acl = (LinkSpaceAcl)it.next();
	                entitylList.add(orgManager.getEntity(acl.getUserType(), acl.getUserId())); 
	                authList.add(acl.getId());
	                ids += acl.getUserType() + "|" + acl.getUserId();
	                ids += ",";
	            }
	            if(ids.length()>1 && ids.endsWith(",")){
	                ids = ids.substring(0,ids.length()-1);
	            }
	            linkSpaceTmp.setEntitys(entitylList);
	            linkSpaceTmp.setLinkSpaceAclStrs(ids);
	        }
		}
		List<LinkSectionVo> linkSectionVoList = this.getLinkSectionVosByLinkSystemId(linkId);
		LinkModifyVo linkVo=new LinkModifyVo();
		linkVo.setCategory(category);
		linkVo.setLinkOption(linkOption);
		linkVo.setLinkSystem(link);
		
		List<Long> authList = new ArrayList<Long>();
		List<V3xOrgEntity> list=new ArrayList<V3xOrgEntity>();
		if(linkAcl != null){
			Iterator it=linkAcl.iterator();
			String ids = "";
			while(it.hasNext()){
				LinkAcl acl=(LinkAcl)it.next();
				list.add(orgManager.getEntity(acl.getUserType(), acl.getUserId()));	
				authList.add(acl.getId());
				ids += acl.getUserType() + "|" + acl.getUserId();
				ids += ",";
			}
			if(ids.length()>1 && ids.endsWith(",")){
				ids = ids.substring(0,ids.length()-1);
			}
			modelView.addObject("ids", ids);
		}
		linkVo.setEntity(list);
		modelView.addObject("authList", authList);
		if(Strings.isBlank(link.getImage())){
			modelView.addObject("notUploadPic", true);
		}else{
			modelView.addObject("_theImage", link.getImage());
		}
		modelView.addObject("isAnonymity", link.getMethod());
		modelView.addObject("link", linkVo);
		modelView.addObject("linkSpaceSet", linkSpaceSet);
		modelView.addObject("linkSectionVoList", linkSectionVoList);
		modelView.addObject("showOption", CollectionUtils.isNotEmpty(linkOption));
		modelView.addObject("flag", flag);
		if ("true".equals(flag)) {
			modelView.addObject("add", "add");
		}
		modelView.addObject("categoryId", category.getId());
		return modelView.addObject("OPENTYPE_OPEN", LinkSystem.OPENTYPE_OPEN).addObject("OPENTYPE_WORKSPACE", LinkSystem.OPENTYPE_WORKSPACE);
	}
	
	//修改链接
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView modifyLink(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String linkName=request.getParameter("linkName");		//链接名称
		long categoryId=Long.valueOf(request.getParameter("theCategory"));	//分类ID
		String members=request.getParameter("members");			//授权人员ID串
		String linkURL=request.getParameter("linkURL");		//链接得URL地址
        String needContentCheck = request.getParameter("needContentCheck");
        String contentForCheck = request.getParameter("contentForCheck");
        String sameRegion = request.getParameter("sameRegion");
        String agentUrl = request.getParameter("agentUrl");
		String theMethod=request.getParameter("theMethod");		//参数传递方式
		String[] paramIds =request.getParameterValues("theId");
		String[] paramName=request.getParameterValues("paramName");	//参数名称数组
		String[] paramSign=request.getParameterValues("paramSign");
		String[] initValue=request.getParameterValues("initValue");
		String[] hidPassword=request.getParameterValues("hidPassword");
		String linkSystemId=request.getParameter("linkId");
		String theWay = request.getParameter("way");
		//是否允许配置到空间
		boolean allowedAsSpace = Strings.isNotBlank(request.getParameter("allowedAsSpace"));
		//是否允许配置到栏目
        boolean allowedAsSection = Strings.isNotBlank(request.getParameter("allowedAsSection"));
		//int openType = allowedAsSpace ? NumberUtils.toInt(request.getParameter("openType")) : LinkSystem.OPENTYPE_NONE;
		
		String number=request.getParameter("number");			//排序号 如果为空，给予一个值
		int theNumber = NumberUtils.toInt(number, -1);
		if(theNumber==-1)
			theNumber = outerlinkManager.getMaxLinkSystemOrder(categoryId);
//		String method = Integer.valueOf(theMethod) == 0 ? "post" : "get";
		
		String description = StringUtils.defaultIfEmpty(request.getParameter("description"), " ");
		String theFileURL = StringUtils.defaultIfEmpty(request.getParameter("theFileURL"), " ");
		
		LinkSystem linkSystem=outerlinkManager.getLinkSystemById(Long.valueOf(linkSystemId));
        if(allowedAsSpace){
            //如果允许配置成空间导航，则增加一个或多个扩展空间
            String[] linkSpaceIds = request.getParameterValues("linkSpaceId");
            String[] linkSpaceDeletedFlags = request.getParameterValues("linkSpaceDeletedFlag");
            String[] spaceName = request.getParameterValues("spaceName");
            String[] targetPageUrlForSpace = request.getParameterValues("targetPageUrlForSpace");
            String[] openType = request.getParameterValues("openType");
            String[] linkSpaceAuths = request.getParameterValues("linkSpaceAuths");
            if(linkSpaceIds != null && linkSpaceIds.length > 0){
                for(int i = 0; i < linkSpaceIds.length; i++){
                    if(linkSpaceDeletedFlags[i].equals("1")){
                        LinkSpace linkSpaceForDeleteTmp = outerlinkManager.getLinkSpaceById(Long.valueOf(linkSpaceIds[i]));
                        linkSystem.getLinkSpaces().remove(linkSpaceForDeleteTmp);
                    } else if(linkSpaceIds[i].equals("")){
                        LinkSpace linkSpaceNew = new LinkSpace();
                        linkSpaceNew.setIdIfNew();
                        linkSpaceNew.setSpaceName(spaceName[i]);
                        linkSpaceNew.setTargetPageUrl(targetPageUrlForSpace[i]);
                        linkSpaceNew.setOpenType(NumberUtils.toInt(openType[i]));
                        linkSpaceNew.setLinkSystemId(linkSystem.getId());
                        //增加一个或多个扩展空间的权限
                        if(linkSpaceAuths[i] != null && !linkSpaceAuths[i].equals("")){
                            String[][] linkSpaceAuthsInfoList = Strings.getSelectPeopleElements(linkSpaceAuths[i]);
                            for(String[] linkSpaceAuthsInfo : linkSpaceAuthsInfoList){
                                LinkSpaceAcl linkSpaceAclNew = new LinkSpaceAcl();
                                linkSpaceAclNew.setIdIfNew();
                                linkSpaceAclNew.setUserType((String)linkSpaceAuthsInfo[0]);
                                linkSpaceAclNew.setUserId(Long.valueOf(linkSpaceAuthsInfo[1]));
                                linkSpaceAclNew.setLinkSpaceId(linkSpaceNew.getId());
                                linkSpaceNew.getLinkSpaceAcls().add(linkSpaceAclNew);
                            }
                        }
                        linkSystem.getLinkSpaces().add(linkSpaceNew);
                    } else {
                        LinkSpace linkSpaceForUpdateTmp = outerlinkManager.getLinkSpaceById(Long.valueOf(linkSpaceIds[i]));
                        linkSpaceForUpdateTmp.setSpaceName(spaceName[i]);
                        linkSpaceForUpdateTmp.setTargetPageUrl(targetPageUrlForSpace[i]);
                        linkSpaceForUpdateTmp.setOpenType(NumberUtils.toInt(openType[i]));
                        linkSpaceForUpdateTmp.setLinkSystemId(linkSystem.getId());
                        //更新扩展空间时先删除其权限再添加
                        linkSpaceForUpdateTmp.getLinkSpaceAcls().clear();
                        //增加一个或多个扩展空间的权限
                        if(linkSpaceAuths[i] != null && !linkSpaceAuths[i].equals("")){
                            String[][] linkSpaceAuthsInfoList = Strings.getSelectPeopleElements(linkSpaceAuths[i]);
                            for(String[] linkSpaceAuthsInfo : linkSpaceAuthsInfoList){
                                LinkSpaceAcl linkSpaceAclNew = new LinkSpaceAcl();
                                linkSpaceAclNew.setIdIfNew();
                                linkSpaceAclNew.setUserType((String)linkSpaceAuthsInfo[0]);
                                linkSpaceAclNew.setUserId(Long.valueOf(linkSpaceAuthsInfo[1]));
                                linkSpaceAclNew.setLinkSpaceId(linkSpaceForUpdateTmp.getId());
                                linkSpaceForUpdateTmp.getLinkSpaceAcls().add(linkSpaceAclNew);
                            }
                        }
                    }
                }
            }
        } else {
            linkSystem.getLinkSpaces().clear();
        }
        if(allowedAsSection){
            //如果允许配置到栏目，则增加一个或多个扩展栏目
            String[] linkSectionIds = request.getParameterValues("linkSectionId");
            String[] linkSectionDeletedFlags = request.getParameterValues("linkSectionDeletedFlag");
            String[] sectionNames = request.getParameterValues("sectionName");
            String[] sectionTypes = request.getParameterValues("sectionType");
            String[] states = request.getParameterValues("state");
            String[] ssoWebcontentURLs = request.getParameterValues("ssoWebcontentURL");
            String[] ssoIframeURL = request.getParameterValues("ssoIframeURL");
            String[] iframeURLs = request.getParameterValues("iframeURL");
            String[] ssoWebcontentSessionTimeouts = request.getParameterValues("ssoWebcontentSessionTimeout");
            String[] ssoWebcontentPageHeights = request.getParameterValues("ssoWebcontentPageHeight");
            String[] ssoIframePageHeights = request.getParameterValues("ssoIframePageHeight");
            String[] iframePageHeights = request.getParameterValues("iframePageHeight");
            String[] ssoWebcontentLinkSystemIds = request.getParameterValues("ssoWebcontentLinkSystemId");
            String[] ssoIframeLinkSystemIds = request.getParameterValues("ssoIframeLinkSystemId");
            String[] iframeLinkSystemIds = request.getParameterValues("iframeLinkSystemId");
            String[] linkSectionAuths = request.getParameterValues("linkSectionAuths");
            if(linkSectionIds != null && linkSectionIds.length > 0){
                for(int i = 0; i < linkSectionIds.length; i++){
                    Map<String, String> props = new HashMap<String, String>();
                    if(sectionTypes[i].equals("0")){
                        props.put("ssoWebcontentURL", Strings.escapeNULL(ssoWebcontentURLs[i], ""));
                        props.put("ssoWebcontentSessionTimeout", Strings.escapeNULL(ssoWebcontentSessionTimeouts[i], ""));
                        props.put("ssoWebcontentPageHeight", Strings.escapeNULL(ssoWebcontentPageHeights[i], ""));
                        props.put("ssoWebcontentLinkSystemId", Strings.escapeNULL(ssoWebcontentLinkSystemIds[i], ""));
                    } else if(sectionTypes[i].equals("1")){
                        props.put("ssoIframeURL", Strings.escapeNULL(ssoIframeURL[i], ""));
                        props.put("ssoIframePageHeight", Strings.escapeNULL(ssoIframePageHeights[i], ""));
                        props.put("ssoIframeLinkSystemId", Strings.escapeNULL(ssoIframeLinkSystemIds[i], ""));
                    } else {
                        props.put("iframeURL", Strings.escapeNULL(iframeURLs[i], ""));
                        props.put("iframePageHeight", Strings.escapeNULL(iframePageHeights[i], ""));
                        props.put("iframeLinkSystemId", Strings.escapeNULL(iframeLinkSystemIds[i], ""));
                    }
                    if(linkSectionDeletedFlags[i].equals("1")){
                        this.sectionDefinitionManager.delete(Long.valueOf(linkSectionIds[i]));
                    } else if(linkSectionIds[i].equals("")){
                        this.sectionDefinitionManager.save(sectionNames[i], Integer.valueOf(sectionTypes[i]), Integer.valueOf(states[i]), linkSectionAuths[i], props);
                    } else {
                        this.sectionDefinitionManager.update(Long.valueOf(linkSectionIds[i]), sectionNames[i], Integer.valueOf(sectionTypes[i]), Integer.valueOf(states[i]), linkSectionAuths[i], props);
                    }
                }
            }
        } else {
            List<SectionProps> sectionPropsList = this.sectionDefinitionManager.getSectionPropsByLinkSystemId(linkSystemId);
            for(SectionProps sectionProps : sectionPropsList){
                this.sectionDefinitionManager.delete(sectionProps.getSectionDefinitionId());
            }
        }
		String method = null;
		if(theWay == null){
		    method = Integer.valueOf(theMethod) == 0 ? "post" : "get";
		}else{
		    method = Integer.valueOf(theWay) == 3 ? "yes" : "no";
		}
		if(members != null && !members.equals("")){
			Set<LinkAcl> acl=linkSystem.getLinkAcl();
			if(acl != null )
				acl.clear();
			else
				acl = new HashSet<LinkAcl>();

			StringTokenizer token=new StringTokenizer(members,",");
			while(token.hasMoreTokens()){
				StringTokenizer _token=new StringTokenizer(token.nextToken(),"|");
				LinkAcl theAcl=new LinkAcl();
				theAcl.setIdIfNew();
				theAcl.setLinkSystemId(Long.valueOf(linkSystemId));
				theAcl.setUserType(_token.nextToken());
				theAcl.setUserId(Long.valueOf(_token.nextToken()));
				acl.add(theAcl);
			}
			linkSystem.setLinkAcl(acl);
		}else if(members !=null && members.equals("")){
			Set<LinkAcl> acl = linkSystem.getLinkAcl();
			acl.clear();
			linkSystem.setLinkAcl(acl);
		}
		
		boolean password=false;
		Map<Long, String> newOpt = new HashMap<Long, String>();
		List<LinkOption> oldOptions=outerlinkManager.getlinkOptionBySystemId(Long.valueOf(linkSystemId));
		Set<LinkOption> option=linkSystem.getLinkOption();
		if(paramName != null  && paramSign != null){			//名称不能为空
		    	for(int i=0;i<paramName.length;i++){
					password=hidPassword[i].equals("1");
		    	      if(paramIds == null || paramIds[i].length()==0){
			                 LinkOption theOption=new LinkOption();	
			                 theOption.setIdIfNew();
			                 theOption.setLinkSystemId(Long.valueOf(linkSystemId));
			                 theOption.setParamName(paramName[i]);
			                 theOption.setParamSign(paramSign[i]);
			                 theOption.setIsPassword(password);
			                 theOption.setOrderNum(i);
			                 if(Strings.isBlank(initValue[i])){
				                 theOption.setIsDefault(false);
				                 theOption.setParamValue("");
			                 }else{
			                	 theOption.setParamValue(LightWeightEncoder.encodeString(initValue[i]));
				                 theOption.setIsDefault(true);
			                  }
			                 option.add(theOption);
			
			             }else{
			            	 List<LinkOption> theOptions=outerlinkManager.getlinkOptionBySystemId(Long.valueOf(linkSystemId));
			            	 if(CollectionUtils.isNotEmpty(theOptions)){
			            		 for(LinkOption opt: theOptions){
			            			 if(opt.getId().equals(Long.valueOf(paramIds[i]))){
			            				 option.remove(opt);
			            				 opt.setId(Long.valueOf(paramIds[i]));
			            				 opt.setLinkSystemId(Long.valueOf(linkSystemId));
			            				 opt.setParamName(paramName[i]);
			            				 opt.setParamSign(paramSign[i]);
			            				 opt.setIsPassword(password);
			            				 opt.setOrderNum(i);
			            				 if(Strings.isBlank(initValue[i])){
							                 opt.setIsDefault(false);
							                 opt.setParamValue("");
						                 }else{
						                	 opt.setParamValue(LightWeightEncoder.encodeString(initValue[i]));
							                 opt.setIsDefault(true);
						                 }
			            				 option.add(opt);
			            			 }
			            		 }
			            	 }
			            	
			            	 String value = paramIds[i]+","+paramName[i]+","+paramSign[i]+","+initValue[i]+","+hidPassword;
			 			     newOpt.put(Long.valueOf(paramIds[i]), value);
			             }
		         }
		    	
		    	Set<Long> keySet = newOpt.keySet();
		    	if(keySet!=null&&keySet.size()!=0){
	            	 if(oldOptions!=null&&oldOptions.size()!=0){
	            		 for(LinkOption opt: oldOptions){
	            			 boolean remain = false;
	            			 if(keySet!=null&&keySet.size()!=0){
	            				 for(long id :keySet){
	            					 if(id == opt.getId())   
	            						 remain =true;
	            				 }
	            			 }
	            			 
	            			 if(!remain) 
	            				 option.remove(opt);
	            		  }
		    	      }
		    	}
		}else{
			option.clear();
		}
		linkSystem.setLinkOption(option);
		
		try {
			outerlinkManager.updateLinkSystem(linkName, theNumber, description, linkURL, needContentCheck.equals("1"), contentForCheck, sameRegion.equals("1"), agentUrl, method, theFileURL, categoryId, allowedAsSpace, allowedAsSection, linkSystem);
			User user = CurrentUser.get();
			appLogManager.insertLog(user, AppLogAction.InterSystem_Update,linkName);
			super.rendJavaScript(response, "parent.parent.refreshAfterAction();");
		} catch (Exception e) {
			logger.error(e);
		}
		
		return null;
	}
	
	//进入类别操作页面
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView getCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView=new ModelAndView("link/linkCategory");
		String categoryId=request.getParameter("categoryId");
		String isSystem=request.getParameter("isSystem");
		String isModify=request.getParameter("isModify");
		if(isModify != null && isModify.equals("true")){
			LinkCategory category=outerlinkManager.getCategoryById(Long.valueOf(categoryId));
			modelView.addObject("category", category);	
			List<LinkAcl> list=outerlinkManager.getLinkAclByCategoryId(Long.valueOf(categoryId));
			List<V3xOrgEntity> the_list=new ArrayList<V3xOrgEntity>();
			String theUser="";
			for(int i=0;i<list.size();i++){
				LinkAcl acl=list.get(i);
				the_list.add(orgManager.getEntity(acl.getUserType(), acl.getUserId()));
				theUser+=acl.getUserType();
				theUser+="|";
				theUser+=acl.getUserId();
				if(i != list.size()-1){
					theUser+=",";
				}
			}
			
			modelView.addObject("manager", the_list);
			modelView.addObject("backView", theUser);	//回显
		}
		modelView.addObject("isSystem", isSystem);
		modelView.addObject("isModify", isModify);
		return modelView;
	}
	
	/**
	 * 添加、修改类别
	 */
	@CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
	public ModelAndView addCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String categoryId = request.getParameter("categoryId");
		String categoryName = request.getParameter("categoryName");
		String isModify = request.getParameter("isModify");
		PrintWriter out = response.getWriter();
		try {
			if ("true".equals(isModify)) {
				LinkCategory category = outerlinkManager.getCategoryById(Long.valueOf(categoryId));
				if (category.getIsSystem() != 1) {
					category.setName(categoryName);
				}
				outerlinkManager.updateCategory(category);
			} else {
				outerlinkManager.addCategory(categoryName);
			}

			out.print("<script>");
			out.print("parent.window.returnValue=\"true\";");
			out.print("window.close()");
			out.print("</script>");
		} catch (Exception e) {
			logger.error(e);
		}

		return null;
	}
	
	//删除类别
	@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
	public ModelAndView deleteCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String categoryId=request.getParameter("categoryId");	//要删除的类别ID
		PrintWriter out=response.getWriter();
		if(Strings.isNotBlank(categoryId)){
			List<LinkSystem> list = outerlinkManager.getLinkSystemByCategoryId(Long.valueOf(categoryId)) ;
			if( list != null && list.size()!= 0) {
				out.print("<script>");
				out.println("alert(parent.v3x.getMessage('LinkLang.system_alert_source_deleted'));") ;
				out.print("</script>");
				return null;				
			}
		}
		outerlinkManager.deleteCategory(categoryId);
		out.print("<script>");
		out.print("parent.parent.location.reload(true)");
		out.print("</script>");
		return null;
	}
	
	///////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////
	////////////////////个人关联系统设置//////////////////////////////////
	//////////////////////////////////////////////////////////////////
	
	//进入个人关联系统主设置页面
	public ModelAndView userLinkMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView = new ModelAndView("link/userBorderFrame");
		return modelView;
	}
	public ModelAndView userLinkBorderMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView = new ModelAndView("link/userMain");
		return modelView;
	}
	
	//进入个人关联系统菜单页面
	public ModelAndView userLinkMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView=new ModelAndView("link/userMenu");
		String userId=request.getParameter("userId");
		List<LinkSystem> list=outerlinkManager.findOutLinkOfCurrentUserByPage();	//得到该用户能看到的所有关联系统
		List<LinkMenuVo> the_list=new ArrayList<LinkMenuVo>();
		for(int i=0;i<list.size();i++){
			LinkSystem linkSystem= list.get(i);
			LinkMenuVo menuVo=new LinkMenuVo();
			menuVo.setLinkSystem(linkSystem);
			LinkCategory lc = outerlinkManager.getCategoryById(linkSystem.getLinkCategoryId());
			menuVo.setLinkCategory(lc.getName());
			menuVo.setUrl(outerlinkManager.getFinalUrlBySystemId(linkSystem.getId(), Long.valueOf(userId)));
			
			List<LinkOption> los=outerlinkManager.getlinkOptionBySystemId(linkSystem.getId());
			if(los == null || los.size() == 0)
				menuVo.setHasOptions(false);
			else
				menuVo.setHasOptions(true);
			
			the_list.add(menuVo);
		}
		modelView.addObject("userId", userId);
		modelView.addObject("linkUserMenu", the_list);
		return modelView;
	}
	
	//进入关联系统设置页面
	public ModelAndView userLinkOperator(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView=new ModelAndView("link/userOperator");
		String linkSystemId=request.getParameter("linkSystemId");		//获取要设置的关联系统ID
		String userId=request.getParameter("userId");					//用户ID
		List<LinkOption> list=outerlinkManager.getlinkOptionBySystemId(Long.valueOf(linkSystemId)); //获取所有的能设置的高级选项
		LinkSystem linkSystem=outerlinkManager.getLinkSystemById(Long.valueOf(linkSystemId));  		//关联系统对象
		String dbClick=request.getParameter("dbClick");
		if(linkSystem == null ){
			PrintWriter out = response.getWriter() ;
			out.println("<Script>") ;
			out.println("alert(parent.v3x.getMessage('LinkLang.system_alert_source_deleted_system'));") ;
			out.println("window.parent.location.reload(true);") ;
			out.println("</Script>") ;
			return null;
		}
		
		List<LinkUserVo> the_list=new ArrayList<LinkUserVo>();
		if(CollectionUtils.isNotEmpty(list)){
			for(LinkOption link : list){
				LinkUserVo userVo=new LinkUserVo();
				LinkOptionValue optionValue = outerlinkManager.getOptionValueById(link.getId(), Long.valueOf(userId));
				if(optionValue ==  null){
					optionValue	 = new LinkOptionValue() ;
					optionValue.setValue(link.getParamValue()) ;
					userVo.setDefaultValue(true);
				}
				userVo.setLinkOption(link);
				userVo.setLinkOptionValue(optionValue);
				userVo.setPassword(link.getIsPassword());
				the_list.add(userVo);
			}
		}
		
		modelView.addObject("linkSystem", linkSystem);
		modelView.addObject("userVo", the_list);
		modelView.addObject("userId", userId);
		modelView.addObject("dbClick", dbClick);
		modelView.addObject("hasImage", Strings.isNotBlank(linkSystem.getImage()));
		return modelView;
	}
	
	public ModelAndView addLinkOptionValue(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String[] optionValue = request.getParameterValues("optionValue") ;
		String[] optionIds = request.getParameterValues("optionIds") ;
		String userid = request.getParameter("userId") ;
		long userId = Long.valueOf(userid) ;
		if(optionValue == null) {
			return super.refreshWorkspace(); 
		}
		for(int i = 0 ; i < optionValue.length ; i++){
			long optionId = Long.valueOf(optionIds[i]) ;
			outerlinkManager.addLinkOptionValue(optionId, optionValue[i], userId);
		}
		/**
		String optionValue=request.getParameter("optionValue");
		long userId=Long.valueOf(request.getParameter("userId"));
		StringTokenizer token=new StringTokenizer(optionValue,";");
		while(token.hasMoreTokens()){
			StringTokenizer the_token=new StringTokenizer(token.nextToken(),",");
			String value=the_token.nextToken();
			long optionId=Long.valueOf(the_token.nextToken());
			outerlinkManager.addLinkOptionValue(optionId, value, userId);
		}*/
		Boolean f = (Boolean)(BrowserFlag.PageBreak.getFlag(request));
		if(!f.booleanValue()){
			 super.rendJavaScript(response, "window.top.close();");
			 return null;
		}
		return super.refreshWorkspace();
	}
	
	public OuterlinkManager getOuterlinkManager() {
		return outerlinkManager;
	}

	public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
		this.outerlinkManager = outerlinkManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}


	//更多
	public ModelAndView linkMore(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView ret = new ModelAndView("link/linkMore");
		List<List<LinkSystem>> list = outerlinkManager.findAllLinkSystems();
		List<LinkMoreVO> vos = new ArrayList<LinkMoreVO>();		
		for(int i = 0; i < list.size(); i++){
			List<LinkSystem> li = list.get(i);			
			LinkMoreVO vo = new LinkMoreVO(this.getLinkShowVOs(li));			
			if(li != null && li.size() > 0){
				LinkCategory lc = outerlinkManager.getCategoryById(li.get(0).getLinkCategoryId());
				String name = "";
				if(lc != null) {
					name = lc.getName();
				}
				vo.setCategoryName(name);
			}else if(i == 0){
				vo.setCategoryName("link.category.in");
			}else if(i == 1){
				vo.setCategoryName("link.category.out");
			}else if(i == 2){
				vo.setCategoryName("link.category.common");
			}
			vos.add(vo);			
		}
		//关联系统类别数
		int vosSize = vos.size();		
		ret.addObject("vos", vos);
		//每个关联系统类别中所包含的关联系统数
		List<Integer> sizeList = new ArrayList<Integer>();
		for(LinkMoreVO t: vos){
			if(t.getLinks() == null)
				sizeList.add(0);
			else
				sizeList.add(t.getLinks().size());
		}
		ret.addObject("sizeList", sizeList);
		ret.addObject("vosSize", vosSize);
		return ret;
	}
	
	//
	private List<LinkShowVO> getLinkShowVOs(List<LinkSystem> lss){
		List<LinkShowVO> vos = new ArrayList<LinkShowVO>();
		if(lss == null || lss.size() == 0)
			return vos;		
//		long userId = CurrentUser.get().getId();
		for(LinkSystem ls : lss){
			LinkShowVO vo = new LinkShowVO(ls);
			
//			String url = outerlinkManager.getFinalUrlBySystemId(ls.getId(), userId);
//			if(url == null || "".equals(url))
//				url = "/linkManager.do?method=userLinkOperator&linkSystemId="+ls.getId()+"&userId="+userId;
			String url = "/linkManager.do?method=linkConnect&linkId=" + ls.getId();
			String icon = ls.getImage();
			if(icon == null || "".equals(icon.trim()))
				icon = "/apps_res/link/images/default.gif";
			else if(icon.trim().indexOf("default.gif") != -1){
				icon = "/apps_res/link/images/default.gif";
			}else{
				int start = icon.indexOf("/fileUpload.do");
				int end = icon.indexOf("width=") - 2;
				icon = icon.substring(start, end);
			}
			if(ls.getLinkCategoryId() == Constants.LINK_COMMON)
				url = ls.getUrl();
			vo.setIcon(icon);
			vo.setLink(url);
			
			vos.add(vo);
		}
			
		return vos;	
	}
	
	// 常用链接
	public ModelAndView commonLinkMore(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView ret = new ModelAndView("link/commonLinkMore");

		List<LinkSystem> list = outerlinkManager.findAllCommonLinks();
		List<LinkShowVO> vos = this.getLinkShowVOs(list);

		int row = 0;
		if (vos != null && vos.size() > 0) {
			if (vos.size() % 6 == 0)
				row = vos.size() / 6;
			else
				row = vos.size() / 6 + 1;
		} else {
			row = 1;
		}

		ret.addObject("vos", vos);
		ret.addObject("emptySize", 20 - row);
		ret.addObject("emptyGrid", 6 - (vos.size() % 6));
		return ret;
	}
	
	//关联系统类别下更多
	public ModelAndView linkMoreByCategory(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView ret = new ModelAndView("link/commonLinkMore");
		long categoryId = Long.parseLong(request.getParameter("categoryId"));
		List<LinkSystem> list = outerlinkManager.findMoreLinks(categoryId);
		List<LinkShowVO> vos = this.getLinkShowVOs(list);
		
		int row = 0;
		if(vos != null && vos.size() > 0){
			if(vos.size() % 6 == 0)
				row = vos.size() / 6;
			else
				row = vos.size() / 6 + 1;
		}else{
			row = 1;
		}
		
		ret.addObject("vos", vos);
		ret.addObject("emptySize", 20 - row);
		ret.addObject("emptyGrid", 6 - (vos.size() % 6));
		return ret;
	}
	
	/**
	 * 如果是扩展空间，兼容3.5以前的
	 */
	public ModelAndView linkConnect(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView ret = new ModelAndView("link/linkConnecting");
		
		try {
			Long linkSpaceId = Long.valueOf(request.getParameter("linkId"));
			LinkSpace lsp = outerlinkManager.getLinkSpaceById(linkSpaceId);
			LinkSystem ls = null;
			boolean canAccess = false;
			if(lsp == null){
			    ls = outerlinkManager.getLinkSystemById(linkSpaceId);
			    Set<LinkSpace> linkSpaceSet = ls.getLinkSpaces();
			    if(linkSpaceSet != null && linkSpaceSet.size() > 0){
			    	lsp = (LinkSpace)ls.getLinkSpaces().toArray()[0];
			    }
			    canAccess = outerlinkManager.canUseTheSystem(CurrentUser.get().getId(), ls.getId(), ls.getLinkCategoryId());
			} else {
	            ls = outerlinkManager.getLinkSystemById(lsp.getLinkSystemId());
	            canAccess = outerlinkManager.canUseTheLinkSpace(CurrentUser.get().getId(), lsp.getId());
			}
			//如果是通过点击空间导航菜单而来，需进行防护：关联系统已被删除、关联系统不允许作为空间导航配置及用户无权使用关联系统
			if("spaceMenu".equals(request.getParameter("spaceFlag")) && 
					(ls ==null || !ls.isAllowedAsSpace() || !canAccess)) {
				Long accountId = CurrentUser.get().getLoginAccount();
				super.printV3XJS(response.getWriter());
				super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.system") + "\\n" +
														   ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.reason1") + "\\n" + 
														   ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.reason2") + "\\n" + 
														   ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.reason3") + "\\n" + 
														   ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.plscontact") + 
													  "');" + 
											   "if(window.opener) {" +
											   "	window.opener.getA8Top().contentFrame.topFrame.realignSpaceMenu('" + accountId + "');" + 
											   "	window.close();" + 
											   "} else {" +
											   "	getA8Top().contentFrame.topFrame.realignLinkSpaceMenu('" + accountId + "');" +
											   "}");
				return null;
			}
			
			
			if(ls == null && Strings.isBlank(request.getParameter("spaceFlag"))){
				ret.addObject("dataExist", false);
				return ret;
			}
			
			Set<LinkOption> options = ls.getLinkOption();
			List<LinkOptionVO> optionVos = new ArrayList<LinkOptionVO>();
			List<LinkOptionValue> values = new ArrayList<LinkOptionValue>();
			if(CollectionUtils.isNotEmpty(options)){
				List<Long> idlist = new ArrayList<Long>();
				Map<Long, LinkOption> map = new HashMap<Long, LinkOption>();
				for(LinkOption lo : options){
					idlist.add(lo.getId());
					map.put(lo.getId(), lo);
				}
				Long userId = CurrentUser.get().getId();
				values = outerlinkManager.findOptionValueById(idlist, userId);	
				if(CollectionUtils.isNotEmpty(values)){
					for(LinkOptionValue value : values){
						LinkOptionVO vo = new LinkOptionVO(map.get(value.getLinkOptionId()), value);
						optionVos.add(vo);
					}
				}else{
					for(LinkOption lo : options){
						LinkOptionValue newOptionValue = new LinkOptionValue();
						newOptionValue.setLinkOptionId(lo.getId());
						newOptionValue.setUserId(userId);
						newOptionValue.setValue(lo.getParamValue());
						LinkOptionVO vo = new LinkOptionVO(lo, newOptionValue);
						optionVos.add(vo);
					}
				}
			}
			ret.addObject("optionVos", optionVos);
			ret.addObject("link", ls);
		    ret.addObject("linkSpaceOrSectionVO", lsp);
			ret.addObject("firstLinked", true) ;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}		
		
		return ret;
	}
	
	/**
     * 如果是扩展栏目
     */
    public ModelAndView linkConnectForSectionDefinition(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView ret = new ModelAndView("link/linkConnecting");
        
        try {
            Long linkSystemId = Long.valueOf(request.getParameter("linkSystemId"));
            LinkSystem ls = outerlinkManager.getLinkSystemById(linkSystemId);
            Long sectionDefinitionId = Long.valueOf(request.getParameter("sectionDefinitionId"));
            Map<String, String> sectionProps = this.sectionDefinitionManager.getSectionProps(sectionDefinitionId);
            sectionProps.put("targetPageUrl", sectionProps.get("ssoIframeURL"));
            //如果是通过点击空间导航菜单而来，需进行防护：关联系统已被删除、关联系统不允许作为空间导航配置及用户无权使用关联系统
            if("spaceMenu".equals(request.getParameter("spaceFlag")) && 
                    (ls ==null || !ls.isAllowedAsSpace() || !outerlinkManager.canUseTheSystem(CurrentUser.get().getId(), ls.getId(), ls.getLinkCategoryId()))) {
                Long accountId = CurrentUser.get().getLoginAccount();
                super.printV3XJS(response.getWriter());
                super.rendJavaScript(response, "alert('" + ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.system") + "\\n" +
                                                           ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.reason1") + "\\n" + 
                                                           ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.reason2") + "\\n" + 
                                                           ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.reason3") + "\\n" + 
                                                           ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "cannot.visit.plscontact") + 
                                                      "');" + 
                                               "if(window.opener) {" +
                                               "    window.opener.getA8Top().contentFrame.topFrame.realignSpaceMenu('" + accountId + "');" + 
                                               "    window.close();" + 
                                               "} else {" +
                                               "    getA8Top().contentFrame.topFrame.realignSpaceMenu('" + accountId + "');" +
                                               "    getA8Top().contentFrame.topFrame.backToPersonalSpace();" + 
                                               "}");
                return null;
            }
            
            
            if(ls == null && Strings.isBlank(request.getParameter("spaceFlag"))){
                ret.addObject("dataExist", false);
                return ret;
            }
            
            Set<LinkOption> options = ls.getLinkOption();
            List<LinkOptionVO> optionVos = new ArrayList<LinkOptionVO>();
            List<LinkOptionValue> values = new ArrayList<LinkOptionValue>();
            if(options != null && options.size() > 0){
                List<Long> idlist = new ArrayList<Long>();
                Map<Long, LinkOption> map = new HashMap<Long, LinkOption>();
                for(LinkOption lo : options){
                    idlist.add(lo.getId());
                    map.put(lo.getId(), lo);
                }
                values = outerlinkManager.findOptionValueById(idlist, CurrentUser.get().getId());   
                for(LinkOptionValue value : values){
                    LinkOptionVO vo = new LinkOptionVO(map.get(value.getLinkOptionId()), value);
                    optionVos.add(vo);
                }
                
            }
            ret.addObject("optionVos", optionVos);
            ret.addObject("linkSpaceOrSectionVO", sectionProps);
            ret.addObject("link", ls);
            ret.addObject("firstLinked", true) ;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }       
        
        return ret;
    }
	
	//关联系统排序界面获取当前用户所有关联系统，按照原排序显示
	public ModelAndView linkOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("link/linkOrder");
		List<LinkSystem> linkSystemList = outerlinkManager.findOutLinkByUserId(CurrentUser.get().getId());
		if(linkSystemList != null && linkSystemList.size() > 0) {
			List<Long> ids = new ArrayList<Long>(linkSystemList.size());
			for(LinkSystem ls : linkSystemList) {
				ids.add(ls.getId());
			}
			mav.addObject("oldLinks", StringUtils.join(ids, ';'));
		}
		mav.addObject("linkSystemList", linkSystemList);
		return mav;
	}
	
	//保存用户对关联系统排序所做的更改
	public ModelAndView saveOrderLink(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String linkSystemsStr = request.getParameter("linkSystems");
		String oldSystemLinks = request.getParameter("oldLinks");
		if(Strings.isNotBlank(linkSystemsStr) && !linkSystemsStr.equals(oldSystemLinks)) {
			String [] linkSystemIds = linkSystemsStr.split(";");
			outerlinkManager.updateLinkOrder(linkSystemIds, CurrentUser.get().getId());
			super.rendJavaScript(response, "parent.endOrderSave()");
		} else {
			super.rendJavaScript(response, "parent.window.close();");
		}
		return null;
	}
	
	//进入导入excel的页面
    public ModelAndView importExcel(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("link/selectImportExcel");
        modelAndView.addObject("linkSystemId", request.getParameter("linkSystemId"));
        return modelAndView;
    }
    
    //导入LinkOptionValue
    public ModelAndView doImport(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        StringBuffer sbf = new StringBuffer();
        sbf.append("<script>");
        sbf.append("parent.hideProcDiv();");
        sbf.append("window.close();");
        sbf.append("</script>");
        PrintWriter out = response.getWriter();
        User u = CurrentUser.get();
        if(u == null){
            return null;
        }
        if(DataUtil.doingImpExp(u.getId())){
            DataUtil.removeImpExpAction(u.getId());
            return null;
        }
               
        DataUtil.putImpExpAction(u.getId(), "import");
        String linkSystemId = request.getParameter("linkSystemId");
        long linkSystemId_long = 0;
        try{
            linkSystemId_long = Long.parseLong(linkSystemId);
        } catch(NumberFormatException nfe){
            logger.error("error when parse linkSystemId to long", nfe);
            DataUtil.removeImpExpAction(u.getId());
            sbf.append("<script>window.returnValue = false;</script>");
            out.println(sbf.toString());
            out.flush();
            return null;
        }
        try{
            List<LinkOption> linkOptionList = outerlinkManager.getlinkOptionBySystemId(linkSystemId_long);
            if(linkOptionList == null || linkOptionList.size() == 0){
                DataUtil.removeImpExpAction(u.getId());
                String optionValueEmpty = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.prompt.optionValueEmpty");
                out.println("<script>alert('" + optionValueEmpty + "');</script>");
                sbf.append("<script>window.returnValue = false;</script>");
                out.println(sbf.toString());
                out.flush();
                return null;
            }
            File file = getUploadFile(request);
            String path = file.getAbsolutePath()+".xls";
            File realfile = new File(path);
            DataUtil.CopyFile(file,realfile);
            List<List<String>> linkOptionValueList = fileToExcelManager.readExcel(realfile);
            List<List<String>> realLinkOptionValueList = new ArrayList<List<String>>();
            //验证模板有效性--start
            if(linkOptionValueList == null || linkOptionValueList.size() <= 2){
                DataUtil.removeImpExpAction(u.getId());
                out.println("<script>alert('导入的excel模板无效，请重新下载模板!');</script>");
                sbf.append("<script>window.returnValue = false;</script>");
                out.println(sbf.toString());
                out.flush();
                return null;
            }
            if(linkOptionList.size() != linkOptionValueList.get(1).size() - 2){
                DataUtil.removeImpExpAction(u.getId());
                out.println("<script>alert('导入的excel模板无效，请重新下载模板!');</script>");
                sbf.append("<script>window.returnValue = false;</script>");
                out.println(sbf.toString());
                out.flush();
                return null;
            }
            for(int i = 0; i < linkOptionList.size(); i++){
                LinkOption loTmp = linkOptionList.get(i);
                if(!loTmp.getParamName().equals(linkOptionValueList.get(1).get(i + 1).trim())){
                    DataUtil.removeImpExpAction(u.getId());
                    out.println("<script>alert('导入的excel模板无效，请重新下载模板!');</script>");
                    sbf.append("<script>window.returnValue = false;</script>");
                    out.println(sbf.toString());
                    out.flush();
                    return null;
                }
            }
            //验证模板有效性--end
            realLinkOptionValueList = linkOptionValueList.subList(1, linkOptionValueList.size());
            String repeat = request.getParameter("repeat");
            List<WebLinkOptionValueImportResultVO> webLinkOptionValueImportResultVOList = outerlinkManager.importLinkOptinValue(repeat, linkSystemId_long, realLinkOptionValueList);
            HttpSession session = request.getSession();
            session.setAttribute("webLinkOptionValueImportResultVOList", webLinkOptionValueImportResultVOList);
            String impURL = request.getParameter("impURL");
            session.setAttribute("impURL", impURL);
            session.setAttribute("repeat", repeat);
        }catch(Exception e){
            DataUtil.removeImpExpAction(u.getId());
            throw e;
        }
        DataUtil.removeImpExpAction(u.getId());
        sbf.append("<script>window.returnValue = true;</script>");
        out.println(sbf.toString());
        out.flush();
        return null;
    }
    
    //进入导入结果页面
    public ModelAndView doImportResult(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("link/importReport");
        HttpSession session = request.getSession();
        mav.addObject("impURL", session.getAttribute("impURL"));
        mav.addObject("repeat", session.getAttribute("repeat"));
        mav.addObject("webLinkOptionValueImportResultVOList", session.getAttribute("webLinkOptionValueImportResultVOList"));
        session.removeAttribute("impURL");
        session.removeAttribute("repeat");
        session.removeAttribute("webLinkOptionValueImportResultVOList");
        return mav;
    }
    
    //从request中获取上传的文件
    private File getUploadFile(HttpServletRequest request) throws Exception {
        Map<String, V3XFile> v3xFiles = new HashMap<String, V3XFile>();     
        File fil = null;
        try {
            V3XFile v3x = null;
            v3xFiles = fileManager.uploadFiles(request, "xls", null);           
            String key="";
            if(v3xFiles != null) {
                Iterator<String> keys = v3xFiles.keySet().iterator();
                while(keys.hasNext()) {
                    key = keys.next();
                    v3x = (V3XFile)v3xFiles.get(key);                   
                }
            }
            fil = fileManager.getFile(v3x.getId(), v3x.getCreateDate());
        } catch (Exception e) {
            logger.error("", e);
        }
        return fil;
    }
    
    //下载模板
    public ModelAndView downloadTemplate(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        User u = CurrentUser.get();
        if(u == null){
            //DataUtil.outNullUserAlertScript(out);
            return null;
        }
        if(DataUtil.doingImpExp(u.getId())){
            //DataUtil.outDoingImpExpAlertScript(out);
            return null;
        }
        String linkSystemId = request.getParameter("linkSystemId");
        long linkSystemId_long = 0;
        try{
            linkSystemId_long = Long.parseLong(linkSystemId);
        } catch(NumberFormatException nfe){
            logger.error("error when parse linkSystemId to long", nfe);
            DataUtil.removeImpExpAction(u.getId());
            throw nfe;
        }
        List<LinkOption> linkOptionList = outerlinkManager.getlinkOptionBySystemId(linkSystemId_long);
        if(linkOptionList == null || linkOptionList.size() == 0){
            DataUtil.removeImpExpAction(u.getId());
            PrintWriter pw = response.getWriter();
            String optionValueEmpty = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.prompt.optionValueEmpty");
            pw.println("<script>alert('" + optionValueEmpty + "');</script>");
            pw.flush();
            return null;
        }
        String fName = "LinkOptionValue_" + u.getLoginName();
        
        DataUtil.putImpExpAction(u.getId(), "export");
        DataRecord dataRecord = null;
        try{
            dataRecord = outerlinkManager.exportLinkOptionTemplate(linkOptionList);
        }catch(Exception e){
            DataUtil.removeImpExpAction(u.getId());
            throw e;
        }
        DataUtil.removeImpExpAction(u.getId());
        try {
            logger.info("expLinkOptionTemplate");
            fileToExcelManager.save(request, response, fName, dataRecord);
        } catch (Exception e) {
            logger.error("error", e);
        }     
        return null;       
    }
    
    //进入参数管理界面
    @SuppressWarnings("rawtypes")
    @CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
    public ModelAndView getOptionValueManage(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        ModelAndView modelView = new ModelAndView("link/linkOptionValueManage");
        String linkSystemId = request.getParameter("linkId");
        long linkSystemId_long = 0;
        try{
            linkSystemId_long = Long.parseLong(linkSystemId);
        } catch(NumberFormatException nfe){
            logger.error("error when parse linkSystemId to long", nfe);
            throw nfe;
        }
        List<LinkOption> linkOptionList = outerlinkManager.getlinkOptionBySystemId(linkSystemId_long);
        List<Object[]> linkOptionValueManageVOList = outerlinkManager.getLinkOptionValueStatistics(linkOptionList);
        modelView.addObject("linkId", linkSystemId_long);
        modelView.addObject("linkOptionList", linkOptionList);
        modelView.addObject("linkOptionValueManageVOList", linkOptionValueManageVOList);
        String flag = StringUtils.defaultIfEmpty(request.getParameter("flag"), "false");        
        modelView.addObject("flag", flag);
        if ("true".equals(flag)) {
            modelView.addObject("add", "add");
        }
        return modelView;
    }
    
    //删除管理系统参数
    public ModelAndView deleteParamValues(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();
        String[] userIds = request.getParameterValues("userId");
        String[] linkOptionIds = request.getParameterValues("linkOptionId");
        List<Long> userIds_long = new ArrayList<Long>(userIds.length);
        List<Long> linkOptionIds_long = new ArrayList<Long>(linkOptionIds.length);
        try{
            for(int i = 0; i < userIds.length; i++){
                userIds_long.add(Long.parseLong(userIds[i]));
            }
            for(int i = 0; i < linkOptionIds.length; i++){
                linkOptionIds_long.add(Long.parseLong(linkOptionIds[i]));
            }
        } catch(NumberFormatException nfe){
            logger.error("error when parse userIds to long", nfe);
            throw nfe;
        }
        try {
            outerlinkManager.deleteParamValues(linkOptionIds_long, userIds_long);
            String deletesuccess = ResourceBundleUtil.getString(Constants.LINK_RESOURCE_BASENAME, "link.prompt.deletesuccess");
            out.print("<script>");
            out.print("alert('" + deletesuccess + "')");
            out.print("</script>");
            out.flush();
        } catch(RuntimeException re){
            out.print("<script>");
            out.print("alert('删除失败')");
            out.print("</script>");
            out.flush();
            logger.error("error when deleteParamValues", re);
        }
        String linkSystemId = request.getParameter("linkId");
        long linkSystemId_long = 0;
        try{
            linkSystemId_long = Long.parseLong(linkSystemId);
        } catch(NumberFormatException nfe){
            logger.error("error when parse linkSystemId to long", nfe);
            throw nfe;
        }
        String flag = StringUtils.defaultIfEmpty(request.getParameter("flag"), "false");
        return super.redirectModelAndView("/linkManager.do?method=getOptionValueManage&linkId="+linkSystemId_long+"&flag="+flag);
    }
    
    //获取某关联系统下的扩展栏目
    public List<LinkSectionVo> getLinkSectionVosByLinkSystemId(String linkSystemId){
        List<SectionProps> sectionPropsList = this.sectionDefinitionManager.getSectionPropsByLinkSystemId(linkSystemId);
        List<LinkSectionVo> linkSectionVoList = new ArrayList<LinkSectionVo>(); 
        for(SectionProps sectionProps : sectionPropsList){
            long sectionDefinitionid = sectionProps.getSectionDefinitionId();
            LinkSectionVo linkSectionVo = new LinkSectionVo();
            SectionDefinition sectionDefinition = this.sectionDefinitionManager.getSectionDefinition(sectionDefinitionid);
            linkSectionVo.setSectionDefinition(sectionDefinition);
            Map<String, String> props = this.sectionDefinitionManager.getSectionProps(sectionDefinitionid);
            linkSectionVo.setSectionProps(props);
            List<SectionSecurity> sectionSecurities = this.sectionDefinitionManager.getSectionSecurity(sectionDefinitionid);
            linkSectionVo.setSectionSecurities(sectionSecurities);
            linkSectionVoList.add(linkSectionVo);
        }
        return linkSectionVoList;
    }
    
    //下载SSOProxy模板
    public ModelAndView downloadSSOProxyTemplate(HttpServletRequest request,
            HttpServletResponse response) throws Exception {     
        String path = "";
        String filename = "";
        
        response.setContentType("application/octet-stream; charset=UTF-8");
        
        path = SystemEnvironment.getA8ApplicationFolder() + "/ssoproxy/jsp/";
        filename = URLEncoder.encode("ssoproxy.zip", "UTF-8"); 
        
        response.setHeader("Content-disposition", "attachment;filename=\"" +filename+ "\"");
        
        OutputStream out = null;
        InputStream in = null;
        try {
            in = new FileInputStream(new File(path + filename));
            out = response.getOutputStream();
            
            IOUtils.copy(in, out);
        }
        catch (Exception e) {
            if (e.getClass().getSimpleName().equals("ClientAbortException")) {
                logger.debug("用户关闭下载窗口: " + e.getMessage());
            }
            else{
                logger.error("", e);
            }
        }
        finally{
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }        
        return null;        
    }
}
