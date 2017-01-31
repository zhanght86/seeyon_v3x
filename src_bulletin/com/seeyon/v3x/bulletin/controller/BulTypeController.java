package com.seeyon.v3x.bulletin.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.bulletin.domain.BulData;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
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
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * 单位管理员、集团管理员登陆后对单位、集团公告板块的增删改查操作处理
 */
@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin, RoleType.SpaceManager})
public class BulTypeController extends BaseController {
	private MetadataManager metadataManager;
	private BulTypeManager bulTypeManager;
	private BulDataManager bulDataManager;
	private OrgManager orgManager;
	private AppLogManager appLogManager;	

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

    /**
     * 公共信息管理-公告管理首页
     * 需要将该管理页嵌入页签中，所以添加此方法
     * added by Mazc 07-12-11
     */
    public ModelAndView bulletinManageIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    return new ModelAndView("bulletin/admin/index");
	}
    
    /**
	 * 显示公告类型列表主页面
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		return new ModelAndView("bulletin/admin/type_list_main");
	}
	/**
	 * 显示集团公告类型列表主页面
	 * @deprecated  使用{@link #listMain}，便于单点维护，同时兼容之前的写法
	 */
	public ModelAndView groupListMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		return this.listMain(request, response);
	}
	
	/**
	 * 显示公告类型的列表页面：包括单位公告和集团公告类型
	 * 其查询功能实际已被屏蔽掉
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean isGroup = CurrentUser.get().isGroupAdmin();
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		List<BulType> list = new ArrayList<BulType>();
		if(Strings.isNotBlank(spaceId)) {
			list = bulTypeManager.customAccBoardFindAllByPage(Long.parseLong(spaceId), spaceTypeInt, true);
		} else {
			list = isGroup ? bulTypeManager.groupFindAllByPage() : bulTypeManager.boardFindAllByPage();
		}
		return new ModelAndView("bulletin/admin/type_list_iframe").addObject("list", list).addObject("isGroup", isGroup);
	}
	
	/**
	 * 显示集团公告的列表页面
	 * @deprecated  使用{@link #list}，便于单点维护，同时兼容之前的写法
	 */
	public ModelAndView groupList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.list(request, response);
	}
    
	/**
	 * 创建公告类型：单位或集团公告类型
	 */
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("bulletin/admin/type_create");
		User user = CurrentUser.get();
		boolean isGroup = user.isGroupAdmin();
		BulType bean = new BulType();
		//新建时，集团默认需要审核员，单位默认不需审核员
		bean.setAuditFlag(isGroup);
		bean.setUsedFlag(true);
		bean.setCreateDate(new Date());
		bean.setCreateUser(user.getId());
		mav.addObject("bean", bean);
		
		mav.addObject("topCountMetaData", metadataManager.getMetadata("bulletin_type_topCount"));
		mav.addObject("templateList", bulTypeManager.getBulTemplateManager().findByPropertyNoInit("usedFlag",true));
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		//记录已有公告板块名称，以便随后在前端作重名判断
		if (Strings.isNotBlank(spaceId)) {
			mav.addObject("typeNameList", bulTypeManager.customAccBoardAllBySpaceId(Long.parseLong(spaceId), spaceTypeInt));
		} else {
			mav.addObject("typeNameList", isGroup ? bulTypeManager.groupFindAll() : bulTypeManager.boardFindAll());
		}
		return mav.addObject("isGroup", isGroup);
	}
	/**
	 * 创建集团公告类型
	 * @deprecated 使用{@link #create}，便于单点维护，同时兼容之前的写法
	 */
	public ModelAndView groupCreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.create(request, response);
	}
	
	/**
	 * 编辑公告类型：单位或集团公告类型
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		boolean isGroup = user.isGroupAdmin();
		
		BulType bean = null;
		String idStr = request.getParameter("id");
		if(StringUtils.isBlank(idStr)) {
			//如果不存在，新建公告类型(这种情况似乎不会出现)
			bean = new BulType();
			bean.setAuditFlag(isGroup);
			bean.setUsedFlag(true);
			bean.setCreateDate(new Date());
			bean.setCreateUser(user.getId());
		} else {
			bean = bulTypeManager.getById(Long.valueOf(idStr));
		}
		ModelAndView mav = new ModelAndView("bulletin/admin/type_create");
		mav.addObject("bean", bean);
		mav.addObject("readOnly", "readOnly".equals(request.getParameter("isDetail")));
		
		boolean hasPending = false;
		if(bean.isAuditFlag()){			
			//如果审核员已不可用，此处需加以判断，避免在审核员停用后有人发起公告的情况下，审核员无法修改
			Long auditorId = bean.getAuditUser();
			V3xOrgMember auditor = this.orgManager.getMemberById(bean.getAuditUser());
			boolean isAuditorValid = auditor!=null && auditor.isValid();
			//单位、集团预置板块审核员ID为0，需加以判断，此种情况下允许修改为不审核
			if(auditorId!=0 && auditorId!=-1l && !isAuditorValid) {
				//添加标识参数：表明该板块待审的公告需要转到新的审核员名下
				mav.addObject("needTransfer2NewAuditor", true).addObject("oldAuditorId", bean.getAuditUser());
			} else {
				hasPending = bulDataManager.hasPendingOfUser(bean.getAuditUser(), bean.getId());
			}			
		}
		mav.addObject("hasPending", hasPending);
		
		mav.addObject("topCountMetaData", metadataManager.getMetadata("bulletin_type_topCount"));
		mav.addObject("templateList", bulTypeManager.getBulTemplateManager().findByPropertyNoInit("usedFlag",true));
		List<BulType> types = new ArrayList<BulType>();
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		if (Strings.isNotBlank(spaceId)) {
			types = bulTypeManager.customAccBoardAllBySpaceId(Long.parseLong(spaceId), spaceTypeInt);
		} else {
			types = isGroup ? bulTypeManager.groupFindAll() : bulTypeManager.boardFindAll();
		}
		//记录已有公告板块(排除自己)名称，以便随后在前端作重名判断
		types.remove(bean);
		return mav.addObject("typeNameList",types).addObject("isGroup", isGroup);
	}
	
	/**
	 * 编辑集团公告类型
	 * @deprecated 使用{@link #edit}，便于单点维护，同时兼容之前的写法
	 */
	public ModelAndView groupEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.edit(request, response);
	}
	
	/**
	 * 保存公告类型：单位或集团公告类型
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BulType bean = null; 
        String idStr=request.getParameter("id"); 
        String spaceId = request.getParameter("spaceId");
        String spaceType = request.getParameter("spacetype");
        int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
        try { 
        	boolean isNew = Strings.isBlank(idStr) ;
            bean = isNew ? new BulType() : bulTypeManager.getById(Long.valueOf(idStr));             
            
            BulType temp = new BulType(); 
            super.bind(request, temp); 
            temp.setUsedFlag(true);
            temp.setAccountId(bean.getAccountId()); 
            if(temp.getAccountId()==null){ 
               temp.setAccountId(CurrentUser.get().getLoginAccount()); 
            } 
            
            super.bind(request, bean); 
            
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
            bulTypeManager.save(bean);
            User user = CurrentUser.get();
            if(user.isGroupAdmin()) {
            	appLogManager.insertLog(user, AppLogAction.Group_BulManagers_Update, user.getName(), bean.getTypeName(), Constants.getActionText(isNew));
            } else {
            	String accountName = this.orgManager.getAccountById(user.getLoginAccount()).getName();
            	appLogManager.insertLog(user, AppLogAction.Account_BulManagers_Update, user.getName(), accountName, bean.getTypeName(), Constants.getActionText(isNew));
            }
            //处理公告板块置顶个数变化之后的情况，已置顶的该板块公告置顶数需同步调整
            if(!bean.isNew()) {
                String oldTopCountStr = request.getParameter("oldTopCount");
                String newTopCountStr = request.getParameter("topCount");
                this.bulDataManager.updateTopOrder(oldTopCountStr, newTopCountStr, bean.getId());
            }
            //该板块存在待审核公告，但当时的审核员已不可用，如果随后该板块设定了新的审核员，需要将原先的待审核公告转给新的审核员
            if(!bean.isNew() && "true".equals(request.getParameter("needTransfer2NewAuditor")) && bean.isAuditFlag()) {
            	Long oldAuditorId = Long.parseLong(request.getParameter("oldAuditorId"));
            	this.bulDataManager.transferWait4AuditBulDatas2NewAuditor(bean.getId(), oldAuditorId, bean.getAuditUser());
            }
        } catch (BusinessException e) {                 
            ModelAndView mav = new ModelAndView("bulletin/admin/type_list_main"); 
            mav.addObject("topCountMetaData", metadataManager.getMetadata("bulletin_type_topCount")); 
            mav.addObject("templateList", bulTypeManager.getBulTemplateManager().findByProperty("usedFlag",true));
            mav.addObject("bean", bean); 
            request.getSession().setAttribute("_my_exception", e); 
            return mav; 
        } 

        return super.refreshWorkspace();
	}

	/**
	 * 保存集团公告类型
	 * @deprecated 使用{@link #save}，便于单点维护，同时兼容之前的写法
	 */
	public ModelAndView groupSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.save(request, response);
	}
	
	/**
	 * 删除公告类型，支持批量删除：单位或集团公告类型
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> typeids = CommonTools.parseStr2Ids(request.getParameter("id"));
		List<BulData> l = new ArrayList<BulData>();
		bulTypeManager.setTypeDeleted(typeids);
		for(Long typeid:typeids){
			 l=bulDataManager.searchBulDatas(typeid);
			 List<Long> ids = new ArrayList<Long>();
			 for(BulData b:l){
				 long id=b.getId();
				 ids.add(id);
			 }
			 bulDataManager.deletes(ids);
		}
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		return this.redirectModelAndView("/bulType.do?method=listMain&spaceType=" + spaceType + "&spaceId=" + spaceId);
	}
	
	/**
	 * 删除集团公告类型，支持批量删除
	 * @deprecated 使用{@link #delete}，便于单点维护，同时兼容之前的写法
	 */
	public ModelAndView groupDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return this.delete(request, response);
	}
	
	/**
	 * 公告板块排序转向页面
	 */
	public ModelAndView orderBulType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String spaceId = request.getParameter("spaceId");
		String spaceType = request.getParameter("spaceType");
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		List<BulType> typelist = new ArrayList<BulType>();
		if (Strings.isNotBlank(spaceId)) {
			typelist = bulTypeManager.customAccBoardFindAllByPage(Long.parseLong(spaceId), spaceTypeInt, false);
		} else {
			typelist = CurrentUser.get().isGroupAdmin() ? bulTypeManager.groupFindAllByNoPage() : bulTypeManager.boardFindAllByNoPage();
		}
		return new ModelAndView("bulletin/admin/orderBulType", "typelist", typelist);
	}
		
	/**
	 * 保存公告板块排序结果
	 */
	public ModelAndView saveOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		bulTypeManager.updateBulTypeOrder(request.getParameterValues("projects"));			
		return super.refreshWorkspace();
	}
	
	/**
	 * @deprecated 此功能实际被废弃
	 */
	public ModelAndView bulQuery (HttpServletRequest request, HttpServletResponse response) throws Exception{
		Long userId=CurrentUser.get().getId();
		ModelAndView mav = new ModelAndView("bulletin/manager/list_board");
		List<BulType> typeList= new ArrayList<BulType>();
		boolean isShowAudit = false;//判断是否有管理板块的权限
		boolean isShowBoard = true ;
		isShowAudit = !bulTypeManager.getAuditUnitBulType(userId).isEmpty();		
		String type = request.getParameter("condition") ;
		if(type == null ){
			type = "" ;
		}
		String textfield = request.getParameter("textfield") ;
		if(textfield == null ){
			textfield = "" ;
		}
		String condition  = request.getParameter("numCondition") ;
		if(condition == null ){
			condition = "" ;
		}
		String spaceTypeStr = request.getParameter("spaceType");
		int spaceTypeInt = Constants.BulTypeSpaceType.corporation.ordinal();		
		if(Strings.isNotBlank(spaceTypeStr)){
			spaceTypeInt = Integer.valueOf(spaceTypeStr);
			
		}
		mav.addObject("spaceType", spaceTypeInt);	
		 
		if(type != null && type.equals("typeName")){
			typeList = bulTypeManager.getBulByTypeName(userId,textfield,true,spaceTypeInt) ;
		}else if(type != null && type.equals("totals")){
			typeList = bulTypeManager.getBulByTol(userId, textfield, condition ,true ,spaceTypeInt) ;
		}else if(type != null && type.equals("auditFlag")){
			typeList = bulTypeManager.findByAuditFlag(userId, condition,true,spaceTypeInt) ;	
		}else if(type != null && type.equals("auditUser")){
			typeList = bulTypeManager.findByAuditUserName(userId, textfield ,true ,spaceTypeInt) ;
		}else {
			if(spaceTypeInt == Constants.BulTypeSpaceType.corporation.ordinal()){
				typeList = bulDataManager.getTypeList(userId, true);
			}else if(spaceTypeInt == Constants.BulTypeSpaceType.group.ordinal()){
				typeList = bulDataManager.getManagerGroupBulType(userId, true);
			}
		}
		
		mav.addObject("typeList", typeList);
		mav.addObject("showBoard", isShowBoard);
		mav.addObject("showAudit", isShowAudit);	
		return mav ;
	}
	
	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}
	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

}