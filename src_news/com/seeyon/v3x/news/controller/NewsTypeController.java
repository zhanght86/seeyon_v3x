package com.seeyon.v3x.news.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * 新闻类型的Controller
 * 处理新闻类型的列表、详细、添加、删除、修改等操作
 * @author wolf
 */
@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
public class NewsTypeController extends BaseController {
	private MetadataManager metadataManager;
	private NewsTypeManager newsTypeManager;
	private OrgManager orgManager;
	private NewsDataManager newsDataManager;
	private AppLogManager appLogManager;

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	
    /**
     * 公共信息管理-新闻管理首页
     * 需要将该管理页嵌入页签中，所以添加此方法
     * added by Mazc 07-12-11
     */
    public ModelAndView newsManageIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {       
        return new ModelAndView("news/admin/index");
    }
    
	/**
	 * 创建新闻类型
	 */
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("news/admin/type_create");
		User user = CurrentUser.get();
		boolean isGroup = user.isGroupAdmin();
		NewsType bean = new NewsType();
		bean.setAuditFlag(isGroup);
		bean.setUsedFlag(true);
		bean.setCreateDate(new Date());
		bean.setCreateUser(user.getId());
		
		mav.addObject("bean", bean);
		mav.addObject("topCountMetaData", metadataManager.getMetadata("news_type_topCount"));
		mav.addObject("templateList", newsTypeManager.getNewsTemplateManager().findByPropertyNoInit("usedFlag",true));
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		//已创建新闻类型列表，用于在前端判断是否存在重名板块
		if (Strings.isNotBlank(spaceId)) {
			mav.addObject("typeNameList", newsTypeManager.findAllOfCustomAcc(Long.parseLong(spaceId), spaceTypeInt));
		} else {
			mav.addObject("typeNameList", isGroup ? newsTypeManager.groupFindAll() : newsTypeManager.findAll(user.getLoginAccount()));
		}
		return mav.addObject("isGroup", isGroup);
	}
	
	/**
	 * 创建集团新闻类型
	 */
	public ModelAndView groupCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.create(request, response);
	}
	
	/**
	 * 编辑新闻类型
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		NewsType bean=null;
		String idStr=request.getParameter("id");
		User user=CurrentUser.get();
		boolean isGroup = user.isGroupAdmin();
		
		if(StringUtils.isBlank(idStr)){
			//如果不存在，新文件新闻类型
			bean=new NewsType();
			bean.setAuditUser(user.getId());
			bean.setAuditFlag(isGroup);	//单位新闻修改缺省是不需要审核，集团新闻板块需要
			bean.setUsedFlag(true);
			bean.setCreateDate(new Date());
			bean.setCreateUser(user.getId());
		}else{
			bean=newsTypeManager.getById(Long.valueOf(idStr));
		}
		ModelAndView mav = new ModelAndView("news/admin/type_create");
		mav.addObject("bean", bean);
		// 取得是否是详细页面标志
		mav.addObject("readOnly", "readOnly".equals(request.getParameter("isDetail")));
		
		// 判断是否有待办
		boolean hasPending = false;
		if(bean.isAuditFlag()){			
			Long auditorId = bean.getAuditUser();
			//如果审核员已不可用，此处需加以判断，避免在审核员停用后有人发起公告的情况下，审核员无法修改
			V3xOrgMember auditor = this.orgManager.getMemberById(auditorId);
			boolean isAuditorValid = auditor!=null && auditor.isValid();
			//单位、集团预置板块审核员ID为0，需加以判断，此种情况下允许修改为不审核
			if(auditorId!=0 && auditorId!=-1l && !isAuditorValid) {
				//添加标识参数：表明该板块待审的公告需要转到新的审核员名下
				mav.addObject("needTransfer2NewAuditor", true).addObject("oldAuditorId", bean.getAuditUser());
			} else {
				hasPending = newsDataManager.hasPendingOfUser(bean.getAuditUser(), bean.getId());
			}			
		}
		mav.addObject("hasPending", hasPending);
		
		mav.addObject("topCountMetaData", metadataManager.getMetadata("news_type_topCount"));
		mav.addObject("templateList", newsTypeManager.getNewsTemplateManager().findByPropertyNoInit("usedFlag",true));
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		List<NewsType> namelist = new ArrayList<NewsType>();
		if (Strings.isNotBlank(spaceId)) {
			namelist = newsTypeManager.findAllOfCustomAcc(Long.parseLong(spaceId), spaceTypeInt);
		} else {
			namelist = isGroup ? newsTypeManager.groupFindAll() : newsTypeManager.findAll(user.getLoginAccount());
		}
		namelist.remove(bean);
		mav.addObject("typeNameList",namelist);//已创建新闻类型列表//前端做判断是否重名
		return mav.addObject("isGroup", isGroup);
	}
	
	/**
	 * 编辑集团新闻类型
	 */
	public ModelAndView groupEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.edit(request, response);
	}
	
	/**
	 * 保存新闻类型
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		NewsType bean=null;
		String idStr=request.getParameter("id");
        String spaceId = request.getParameter("spaceId");
        String spaceType = request.getParameter("spacetype");
        int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
        try { 
        	boolean isNew = StringUtils.isBlank(idStr);
        	bean = isNew ? new NewsType() : newsTypeManager.getById(Long.valueOf(idStr));
                
    		NewsType temp=new NewsType(); 
            super.bind(request, temp); 
            temp.setAccountId(bean.getAccountId()); 
            if(temp.getAccountId()==null){ 
                temp.setAccountId(CurrentUser.get().getLoginAccount()); 
            } 
            
            super.bind(request,bean); 
            
            if(bean.isNew()){ 
                bean.setCreateDate(new Date()); 
                bean.setCreateUser(CurrentUser.get().getId()); 
                bean.setSortNum(0);
            } 
            bean.setUpdateDate(new Date()); 
            bean.setUpdateUser(CurrentUser.get().getId()); 
            
            if(!bean.isAuditFlag()){ 
                bean.setAuditUser(0l); 
            }               
            if (Strings.isNotBlank(spaceId) && Strings.isBlank(idStr)) {
            	bean.setAccountId(Long.parseLong(spaceId));
            	bean.setSpaceType(spaceTypeInt);
            }
            newsTypeManager.save(bean); 
            User user = CurrentUser.get();
            if(user.isGroupAdmin()) {
            	appLogManager.insertLog(user, AppLogAction.Group_NewsManagers_Update, user.getName(), bean.getTypeName(), Constants.getActionText(isNew));
            } else {
            	String accountName = this.orgManager.getAccountById(user.getLoginAccount()).getName();
            	appLogManager.insertLog(user, AppLogAction.Account_NewsManagers_Update, user.getName(), accountName, bean.getTypeName(), Constants.getActionText(isNew));
            }
            //该板块存在待审核新闻，但当时的审核员已不可用，如果随后该板块设定了新的审核员，需要将原先的待审核新闻转给新的审核员
            if(!bean.isNew() && "true".equals(request.getParameter("needTransfer2NewAuditor")) && bean.isAuditFlag()) {
            	Long oldAuditorId = Long.parseLong(request.getParameter("oldAuditorId"));
            	this.newsDataManager.transferWait4AuditBulDatas2NewAuditor(bean.getId(), oldAuditorId, bean.getAuditUser());
            }
        } catch (BusinessException e) {                 
        	ModelAndView mav = new ModelAndView("news/admin/type_list_main");
			mav.addObject("topCountMetaData", metadataManager.getMetadata("news_type_topCount"));
			mav.addObject("templateList", newsTypeManager.getNewsTemplateManager().findByProperty("usedFlag",true));
			mav.addObject("bean", bean);
			request.getSession().setAttribute("_my_exception", e);
			return mav;
        } 
        super.rendJavaScript(response, "parent.getA8Top().reFlesh();");
        return null;	
	}
	
	/**
	 * 删除新闻类型，支持批量删除
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> typeIds = CommonTools.parseStr2Ids(request.getParameter("id"));
		newsTypeManager.setTypeDeleted(typeIds);
		List<NewsData> newsList = new ArrayList<NewsData>();
		for (Long typeId : typeIds) {
			newsList = newsDataManager.getNewsByTypeId(typeId);
			for (NewsData newsData : newsList) {
				newsDataManager.delete(newsData.getId());
			}
		}
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		return this.redirectModelAndView("/newsType.do?method=listMain&spaceType="+spaceType+"&spaceId=" + spaceId);
	}
	/**
	 * 删除新闻类型，支持批量删除
	 */
	public ModelAndView groupDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.delete(request, response);
	}
	
	/**
	 * 显示新闻类型列表主页面
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("news/admin/type_list_main");
		if(request.getParameter("id")!=null)
			mav.addObject("id", request.getParameter("id"));
		return mav.addObject("isGroup", CurrentUser.get().isGroupAdmin());
		
	}
	/**
	 * 显示集团新闻类型列表主页面
	 */
	public ModelAndView groupListMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		return this.listMain(request, response);
	}
	
	/**
	 * 显示新闻的列表页面
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		boolean isGroup = user.isGroupAdmin();
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		List<NewsType> list = new ArrayList<NewsType>();
		if (Strings.isNotBlank(spaceId)) {
			list = newsTypeManager.findAllByCustomAccId(Long.parseLong(spaceId), spaceTypeInt, true);
		} else {
			list = isGroup ? newsTypeManager.groupFindAllByPage() : newsTypeManager.findAllByPage(user.getLoginAccount());
		}
		return new ModelAndView("news/admin/type_list_iframe").addObject("list", list).addObject("isGroup", isGroup);
	}
	
	/**
	 * 显示集团新闻的列表页面
	 */
	public ModelAndView groupList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.list(request, response);
	}
	
	/**
	 * 显示新闻类型的详细页面，现在更改了显示方式，列表页面的下面使用新建或编辑页面了
	 * @deprecated
	 */
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		NewsType bean = Strings.isBlank(idStr) ? new NewsType() : newsTypeManager.getById(Long.valueOf(idStr));
		return new ModelAndView("news/admin/type_list_detail_iframe").addObject("bean", bean).addObject("topCountMetaData", metadataManager.getMetadata("news_type_topCount"));
	}
	
	/**
	 * 新闻板块排序转向页面
	 */
	public ModelAndView orderNewsType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("news/admin/orderNewsType");
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		if (Strings.isNotBlank(spaceId)) {
			mav.addObject("typelist", newsTypeManager.findAllByCustomAccId(Long.parseLong(spaceId), spaceTypeInt, false));
		} else {
			mav.addObject("typelist", user.isGroupAdmin() ? newsTypeManager.groupFindAllByNoPage() : newsTypeManager.findAllByNoPage(user.getLoginAccount()));
		}
		return mav;
	}
	
	/**
	 * 保存新闻板块排序结果
	 */
	public ModelAndView saveOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		newsTypeManager.updateNewsTypeOrder(request.getParameterValues("projects"));
		return super.refreshWorkspace();
	}
	
	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
}
