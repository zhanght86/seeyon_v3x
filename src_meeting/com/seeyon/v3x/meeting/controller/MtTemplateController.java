package com.seeyon.v3x.meeting.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.project.domain.ProjectSummaryCAP;
import com.seeyon.cap.project.manager.ProjectManagerCAP;
import com.seeyon.cap.resource.ResourceTypeCAP;
import com.seeyon.cap.resource.domain.ResourceCAP;
import com.seeyon.cap.resource.manager.ResourceManagerCAP;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meeting.domain.MtContentTemplate;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.domain.MtReply;
import com.seeyon.v3x.meeting.domain.MtResources;
import com.seeyon.v3x.meeting.domain.MtSummaryTemplate;
import com.seeyon.v3x.meeting.domain.MtTemplate;
import com.seeyon.v3x.meeting.domain.MtTemplateUser;
import com.seeyon.v3x.meeting.manager.MtContentTemplateManager;
import com.seeyon.v3x.meeting.manager.MtMeetingManager;
import com.seeyon.v3x.meeting.manager.MtReplyManager;
import com.seeyon.v3x.meeting.manager.MtResourcesManager;
import com.seeyon.v3x.meeting.manager.MtSummaryTemplateManager;
import com.seeyon.v3x.meeting.manager.MtTemplateManager;
import com.seeyon.v3x.meeting.manager.MtTemplateUserManager;
import com.seeyon.v3x.meeting.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.ObjectUtil;
import com.seeyon.v3x.util.Datetimes;

/**
 * 会议的Controller
 * @author wolf
 *
 */
public class MtTemplateController extends BaseController {
	private AttachmentManager attachmentManager;
	private MtReplyManager replyManager;
	private MtSummaryTemplateManager mtSummaryTemplateManager;
	private MetadataManager metadataManager;
	private MtContentTemplateManager mtContentTemplateManager;
	private MtTemplateManager mtTemplateManager;
	@SuppressWarnings("unused")
	private MtMeetingManager mtMeetingManager;
	private MtTemplateUserManager mtTemplateUserManager;
	private ProjectManagerCAP projectManagerCAP;
	private ResourceManagerCAP resourceManagerCAP;
	private MtResourcesManager mtResourcesManager;
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setMtResourcesManager(MtResourcesManager mtResourcesManager) {
		this.mtResourcesManager = mtResourcesManager;
	}

	public void setResourceManagerCAP(ResourceManagerCAP resourceManagerCAP) {
		this.resourceManagerCAP = resourceManagerCAP;
	}

	public void setProjectManagerCAP(ProjectManagerCAP projectManagerCAP) {
		this.projectManagerCAP = projectManagerCAP;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
		
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	

	/**
	 * 创建会议
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String oper=request.getParameter("formOper");
		V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
		String url="meeting/user/template_create";
		if(request.getParameter("templateType")!=null && request.getParameter("templateType").equals("1")){
			url="meeting/admin/template_create";
		}
		ModelAndView mav = new ModelAndView(url);
		MtTemplate bean=new MtTemplate();
		List<V3xOrgMember> emceeList = new ArrayList<V3xOrgMember>();
		List<V3xOrgMember> recorderList = new ArrayList<V3xOrgMember>();
		User user = CurrentUser.get();
//		if(bean.getBeginDate()==null)
//			bean.setBeginDate(new Date());
//		if(bean.getEndDate()==null)
//			bean.setEndDate(new Date());
		
		bean.setRemindFlag(false);
		
//		单位管理员新建的时候不初始化主持、记录人
		if(!member.getIsAdmin()){
			bean.setRecorderId(CurrentUser.get().getId());
			bean.setEmceeId(CurrentUser.get().getId());
		}
		
		bean.setCreateDate(new Date());
		bean.setCreateUser(CurrentUser.get().getId());
		bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		
		bean.setState(Constants.DATA_STATE_SAVE);
		
		//处理正文格式加载
		if(StringUtils.isNotBlank(oper) && "loadTemplate".equals(oper)){
			this.bind(request, bean);
			if(bean.getConferees()!=null&&!bean.getConferees().equals("")){
//				调用模版时回显与会人员
				List<V3xOrgEntity> confereeList = new ArrayList<V3xOrgEntity>();
				confereeList = orgManager.getEntities(bean.getConferees());
				mav.addObject("confereeList", confereeList);
			}
			String templateId=request.getParameter("templateId");
			if(StringUtils.isNotBlank(templateId)){
				MtContentTemplate template=this.mtContentTemplateManager.getById(Long.valueOf(templateId));
				bean.setDataFormat(template.getTemplateFormat());
				bean.setContent(template.getContent());
				bean.setCreateDate(template.getCreateDate());
				bean.setTemplateId(template.getId());
				mav.addObject("originalNeedClone",true);
			}
		}
		
		V3xOrgMember emcee = orgManager.getMemberById(bean.getEmceeId());
		if(emcee!=null){
			emceeList.add(emcee);
		}
		V3xOrgMember recorder = orgManager.getMemberById(bean.getRecorderId());
		if(recorder!=null){
			recorderList.add(recorder);
		}
		
		mav.addObject("bean", bean);
		mav.addObject("emceeList", emceeList);
		mav.addObject("recorderList", recorderList);
		List<ProjectSummaryCAP> projectList = projectManagerCAP.getProjectList();
		if(user.isAdministrator()){
			projectList = projectManagerCAP.getProjects(user.getLoginAccount());
		}else{
			projectList = projectManagerCAP.getProjectList();
		}
		mav.addObject("projectMap",projectList);
//		List<Resource> meetingRomeList = resourceManager.findResourcesByType(ResourceType.OFFICE.getValue());
//		mav.addObject("meetingRomeList",meetingRomeList);
		mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
		mav.addObject("contentTemplateList", mtContentTemplateManager.findAll());
//		取出模板列表判断是否有重名用
		if(request.getParameter("templateType")!=null && request.getParameter("templateType").equals("1")){
			mav.addObject("systemTemplateList",mtTemplateManager.findAllTempNoPaginate(Constants.MEETING_TEMPLATE_TYPE_SYSTEM));
		}else{
			mav.addObject("personTemplateList",mtTemplateManager.findAllNoPaginate(Constants.MEETING_TEMPLATE_TYPE_PERSON));
		}
		return mav;
	}
	
	/**
	 * 编辑会议
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		MtTemplate bean;
		if(StringUtils.isBlank(idStr)){
			bean=new MtTemplate();	
			bean.setBeginDate(new Date());
			bean.setEndDate(new Date());
			bean.setRecorderId(CurrentUser.get().getId());
			bean.setEmceeId(CurrentUser.get().getId());
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
			bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		}else
			bean=mtTemplateManager.getById(Long.valueOf(idStr));
		
		String url="meeting/user/template_create";
		if(request.getParameter("templateType")!=null && request.getParameter("templateType").equals("1")){
			url="meeting/admin/template_create";
			bean.setTemplateType(Constants.MEETING_TEMPLATE_TYPE_SYSTEM);
		}else{
			bean.setTemplateType(Constants.MEETING_TEMPLATE_TYPE_PERSON);
		}
		ModelAndView mav = new ModelAndView(url);
		
		List<Attachment> attachments=attachmentManager.getByReference(bean.getId(), bean.getId());
		mav.addObject("attachments", attachments);
		
		Set set = bean.getTemplateUsers();
		Iterator it = set.iterator();
		String typeAndIds = "";
		while(it.hasNext()){
			MtTemplateUser mtu = (MtTemplateUser)it.next();
			typeAndIds+=mtu.getAuthType()+"|"+mtu.getAuthId()+",";
		}
		if(typeAndIds!=null&&!typeAndIds.equals("")){
			List<V3xOrgEntity> authUsers = orgManager.getEntities(typeAndIds.substring(0,typeAndIds.lastIndexOf(",")));
			mav.addObject("authUsers", authUsers);
		}
		
		//处理正文格式加载
		String oper=request.getParameter("formOper");
		if(StringUtils.isNotBlank(oper) && "loadTemplate".equals(oper)){
			String templateId=request.getParameter("templateId");
			if(StringUtils.isNotBlank(templateId)){
				MtContentTemplate template=this.mtContentTemplateManager.getById(Long.valueOf(templateId));
				
				bean.setDataFormat(template.getTemplateFormat());
				bean.setContent(template.getContent());
				bean.setCreateDate(template.getCreateDate());
				bean.setTemplateId(template.getId());
				mav.addObject("originalNeedClone",true);
			}
		}
		
		//获取与会资源信息，将其拼起传到前台显示。
		List<MtResources> recourceList = mtResourcesManager.findByPropertyNoInit("meetingId", Long.valueOf(idStr));
		ResourceCAP re = null;
		String resourcesName = "";
		String resourcesId = "";
		if(recourceList.size()!=0){
			for(MtResources resource : recourceList){
				re = resourceManagerCAP.getResourceByPk(resource.getResourceId());
				resourcesName+=re.getName()+",";
				resourcesId+=re.getId().toString()+",";
			}
			bean.setResourcesId(resourcesId.substring(0, resourcesId.lastIndexOf(",")));
			bean.setResourcesName(resourcesName.substring(0, resourcesName.lastIndexOf(",")));
		}
		
//		页面构造回显人员
		List<V3xOrgMember> emceeList = new ArrayList<V3xOrgMember>();
		emceeList.add(orgManager.getMemberById(bean.getEmceeId()));
		List<V3xOrgMember> recorderList = new ArrayList<V3xOrgMember>();
		recorderList.add(orgManager.getMemberById(bean.getRecorderId()));
//		List<V3xOrgMember> confereeList = new ArrayList<V3xOrgMember>();
//		String confereesId = bean.getConferees();
//		String[] confereesIdArr = confereesId.split(",");
//		for(String id : confereesIdArr){
//			confereeList.add(orgManager.getMemberById(new Long(id)));
//		}
		List<V3xOrgEntity> confereeList = new ArrayList<V3xOrgEntity>();
		String conferees = bean.getConferees();
		confereeList = orgManager.getEntities(conferees);
		
		mav.addObject("emceeList", emceeList);
		mav.addObject("confereeList", confereeList);
		mav.addObject("recorderList", recorderList);
		
		mav.addObject("bean", bean);
		List<ProjectSummaryCAP> projectList = projectManagerCAP.getProjectList();
		mav.addObject("projectMap",projectList);
//		List<Resource> meetingRomeList = resourceManager.findResourcesByType(ResourceType.OFFICE.getValue());
//		mav.addObject("meetingRomeList",meetingRomeList);
		mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
		mav.addObject("contentTemplateList", mtContentTemplateManager.findAll());
		
//		取出模板列表判断是否有重名用
		if(request.getParameter("templateType")!=null && request.getParameter("templateType").equals("1")){
			mav.addObject("systemTemplateList",mtTemplateManager.findAllTempNoPaginate(Constants.MEETING_TEMPLATE_TYPE_SYSTEM));
		}else{
			mav.addObject("personTemplateList",mtTemplateManager.findAllNoPaginate(Constants.MEETING_TEMPLATE_TYPE_PERSON));
		}
		return mav;
	}
	
	/**
	 * 发送会议
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 * @throws Exception
	 */
	public ModelAndView send(HttpServletRequest request, HttpServletResponse response) throws Exception{
		MtTemplate bean=null;
		String idStr=request.getParameter("id");
		if(StringUtils.isBlank(idStr)){
//			bean=new MtTemplate();		
		}else{
			String[] ids=idStr.split(",");
			for(String id:ids){
				if(StringUtils.isBlank(id)) continue;
				bean=mtTemplateManager.getById(Long.valueOf(id));
				if(bean.getState()==Constants.DATA_STATE_SEND) continue;
				
				bean.setState(Constants.DATA_STATE_SEND);
				//应该设置与会人员关联表
				List<MtReply> replyList=replyManager.findByProperty("meetingId",Long.valueOf(id));
				for(MtReply reply:replyList){
					replyManager.delete(reply.getId());
				}
				
				String[] conferees=bean.getConferees().split(",");
				for(String userId:conferees){
					if(StringUtils.isBlank(userId)) continue;
					MtReply reply=new MtReply();
					reply.setMeetingId(bean.getId());
					reply.setUserId(Long.valueOf(userId));
					reply.setFeedbackFlag(-1);
					replyManager.save(reply);
				}
				
				this.mtTemplateManager.save(bean);
			}
		}
		
		return this.redirectModelAndView("/mtTemplate.do?method=listMain");
	}
	
	/**
	 * 保存会议为个人模板
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView saveAsTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MtMeeting bean=null;
		List<Attachment> attachments= new ArrayList<Attachment>();
		attachments = attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.meeting, null, null, request);
		bean=new MtMeeting();	
		
		super.bind(request,bean);
		bean.setId(null);
		
		bean.setState(Constants.DATA_STATE_SAVE);
		if(bean.isNew()){
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
		}
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());
		ModelAndView mav = new ModelAndView("meeting/user/meeting_create");
//		页面构造回显人员
		List<V3xOrgMember> emceeList = new ArrayList<V3xOrgMember>();
		emceeList.add(orgManager.getMemberById(bean.getCreateUser()));
		List<V3xOrgMember> recorderList = new ArrayList<V3xOrgMember>();
		recorderList.add(orgManager.getMemberById(bean.getRecorderId()));
//		List<V3xOrgMember> confereeList = new ArrayList<V3xOrgMember>();
//		String confereesId = bean.getConferees();
//		String[] confereesIdArr = confereesId.split(",");
//		for(String id : confereesIdArr){
//			confereeList.add(orgManager.getMemberById(new Long(id)));
//		}
		List<V3xOrgEntity> confereeList = new ArrayList<V3xOrgEntity>();
		String conferees = bean.getConferees();
		confereeList = orgManager.getEntities(conferees);
		
		mav.addObject("emceeList", emceeList);
		mav.addObject("confereeList", confereeList);
		mav.addObject("recorderList", recorderList);
		
		
		mav.addObject("bean", bean);
		List<ProjectSummaryCAP> projectList = projectManagerCAP.getProjectList();
		mav.addObject("projectMap",projectList);
		mav.addObject("attachments", attachments);
//		List<Resource> meetingRomeList = resourceManager.findResourcesByType(ResourceType.OFFICE.getValue());
//		mav.addObject("meetingRomeList",meetingRomeList);
		mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
		mav.addObject("contentTemplateList", mtContentTemplateManager.findAll());
		return mav;
	}
	
	/**
	 * 保存会议模版0个人1系统
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*
		// 如果是管理员，单独执行adminSave()
		V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());
		if(member.getIsAdmin()){
			return this.adminSave(request, response);
		}
		*/
		MtTemplate bean=null;
		String idStr=request.getParameter("id");
		
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		if(StringUtils.isBlank(idStr)){
			bean=new MtTemplate();
		}else{
			bean=mtTemplateManager.getById(Long.valueOf(idStr));
		}
		super.bind(request,bean);
		bean.setDataFormat(request.getParameter("bodyType"));
		
		bean.setRemindFlag(request.getParameter("remindFlag")!=null);
		if(beginDate.length()<17){
			if(beginDate.equals("")){
				bean.setBeginDate(null);
			}else{
				bean.setBeginDate(Datetimes.parseDatetimeWithoutSecond(beginDate));
			}
			
			if(endDate.equals("")){
				bean.setEndDate(null);
			}else{
				bean.setEndDate(Datetimes.parseDatetimeWithoutSecond(endDate));
			}
		}
		String formOper=request.getParameter("formOper");
		if(formOper==null || formOper.equals("") || formOper.equals("save")){
			bean.setState(Constants.DATA_STATE_SAVE);
		}else if(formOper.equals("send")){
			bean.setState(Constants.DATA_STATE_SEND);
		}
		
		if(bean.isNew()){
			bean.setAccountId(accountId);
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
		}
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());
		
		if(bean.getTemplateName()==null) bean.setTemplateName(bean.getTitle());
		
		try {
			
			// 保存的时候时候判断是否点击了授权，如果点了，一并保存授权信息。
			String authId=request.getParameter("authId");
			if(authId!=null && !authId.trim().equals("")){
				mtTemplateManager.save(bean,authId);
			}else{
				mtTemplateManager.save(bean);
			}
			if(bean.isNew())
				attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
			else{
				attachmentManager.deleteByReference(bean.getId(), bean.getId());
				attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
			}
		} catch (BusinessException e) {			
			String url="meeting/user/template_create";
			if(bean.getTemplateType().equals(Constants.MEETING_TEMPLATE_TYPE_SYSTEM)){
				url="meeting/admin/template_create";
			}
			ModelAndView mav = new ModelAndView(url);
			mav.addObject("bean", bean);
			List<ProjectSummaryCAP> projectList = projectManagerCAP.getProjectList();
			mav.addObject("projectMap",projectList);
//			List<Resource> meetingRomeList = resourceManager.findResourcesByType(ResourceType.OFFICE.getValue());
//			mav.addObject("meetingRomeList",meetingRomeList);
			mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
			mav.addObject("contentTemplateList", mtContentTemplateManager.findAll());
			request.getSession().setAttribute("_my_exception", e);
			return mav;
		}
		String url="/mtTemplate.do?method=listMain";
		if(bean.getTemplateType().equals(Constants.MEETING_TEMPLATE_TYPE_SYSTEM)){
			url="/mtTemplate.do?method=listMain&templateType=1";
		}
		return this.redirectModelAndView(url);
	}
	
	/**
	 * 单位管理员保存会议模版0个人1系统
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView adminSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MtTemplate bean=null;
		String idStr=request.getParameter("id");
		
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
//		String beginDate = request.getParameter("beginDate");
//		String endDate = request.getParameter("endDate");
		if(StringUtils.isBlank(idStr)){
			bean=new MtTemplate();
		}else{
			bean=mtTemplateManager.getById(Long.valueOf(idStr));
		}
		super.bind(request,bean);
//		bean.setDataFormat(request.getParameter("bodyType"));
//		
//		bean.setRemindFlag(request.getParameter("remindFlag")!=null);
//		if(beginDate.length()<17){
//			if(beginDate.equals("")){
//				bean.setBeginDate(null);
//			}else{
//				bean.setBeginDate(Datetimes.parse(beginDate), "yyyy-MM-dd HH:mm");
//			}
//			
//			if(endDate.equals("")){
//				bean.setEndDate(null);
//			}else{
//				bean.setEndDate(Datetimes.parse(endDate), "yyyy-MM-dd HH:mm");
//			}
//		}
//		String formOper=request.getParameter("formOper");
//		if(formOper==null || formOper.equals("") || formOper.equals("save")){
//			bean.setState(Constants.DATA_STATE_SAVE);
//		}else if(formOper.equals("send")){
//			bean.setState(Constants.DATA_STATE_SEND);
//		}
		
		if(bean.isNew()){
			bean.setAccountId(accountId);
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
		}
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());
		
		if(bean.getTemplateName()==null) bean.setTemplateName(bean.getTitle());
		
		try {
			
			// 保存的时候时候判断是否点击了授权，如果点了，一并保存授权信息。
			String authId=request.getParameter("authId");
			if(authId!=null && !authId.trim().equals("")){
				mtTemplateManager.save(bean,authId);
			}else{
				mtTemplateManager.save(bean);
			}
			if(bean.isNew())
				attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
			else{
				attachmentManager.deleteByReference(bean.getId(), bean.getId());
				attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
			}
		} catch (BusinessException e) {			
			String url="meeting/user/template_create";
			if(bean.getTemplateType().equals(Constants.MEETING_TEMPLATE_TYPE_SYSTEM)){
				url="meeting/admin/template_create";
			}
			ModelAndView mav = new ModelAndView(url);
			mav.addObject("bean", bean);
			List<ProjectSummaryCAP> projectList = projectManagerCAP.getProjectList();
			mav.addObject("projectMap",projectList);
//			List<Resource> meetingRomeList = resourceManager.findResourcesByType(ResourceType.OFFICE.getValue());
//			mav.addObject("meetingRomeList",meetingRomeList);
			mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
			mav.addObject("contentTemplateList", mtContentTemplateManager.findAll());
			request.getSession().setAttribute("_my_exception", e);
			return mav;
		}
		String url="/mtTemplate.do?method=listMain";
		if(bean.getTemplateType().equals(Constants.MEETING_TEMPLATE_TYPE_SYSTEM)){
			url="/mtTemplate.do?method=listMain&templateType=1";
		}
		return this.redirectModelAndView(url);
	}
	
	/**
	 * 删除会议，支持批量删除
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		if(StringUtils.isBlank(idStr)){
			idStr="";		
		}else{
			String[] idStrs=idStr.split(",");
			List<Long> ids=new ArrayList<Long>();
			for(String str:idStrs){
				if(StringUtils.isNotBlank(str)){
					ids.add(Long.valueOf(str));
					List<MtReply> replyList=replyManager.findByProperty("meetingId",Long.valueOf(str));
					for(MtReply reply:replyList){
						replyManager.delete(reply.getId());
					}
					List<MtSummaryTemplate> summaryList=mtSummaryTemplateManager.findByPropertyNoInit("meetingId", Long.valueOf(str));
					for(MtSummaryTemplate summary:summaryList){
						mtSummaryTemplateManager.delete(summary.getId());
					}
				}
			}
			if(ids.size()>0)
				mtTemplateManager.deletes(ids);
		}
		
		String url="/mtTemplate.do?method=listMain";
		if(request.getParameter("templateType")!=null && request.getParameter("templateType").equals("1")){
			url="/mtTemplate.do?method=listMain&templateType=1";
		}
		return this.redirectModelAndView(url);
	}
	
	/**
	 * 会议列表主页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String url="meeting/user/template_list_main";
		if(request.getParameter("templateType")!=null && request.getParameter("templateType").equals("1")){
			url="meeting/admin/template_list_main";
		}
		ModelAndView mav = new ModelAndView(url);
		return mav;
	}

	/**
	 * 会议列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<MtTemplate> list=null;
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		String templateType=Constants.MEETING_TEMPLATE_TYPE_PERSON;
		if(request.getParameter("templateType")!=null && request.getParameter("templateType").trim().equals(Constants.MEETING_TEMPLATE_TYPE_SYSTEM)){
			templateType=request.getParameter("templateType");
		}
		if(StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)){
			Object value=ObjectUtil.getPropertyObject(MtTemplate.class, condition, textfield);
			list=mtTemplateManager.findByProperty(templateType,condition, value);
		}else{
			list=mtTemplateManager.findAll(templateType);
		}
		
		Map<Long,List<V3xOrgEntity>> authMap = new HashMap<Long,List<V3xOrgEntity>>();
		
		for(MtTemplate mt : list){
			String typeAndIds = "";
			Set set = mt.getTemplateUsers();
			Iterator it = set.iterator();
			while(it.hasNext()){
				MtTemplateUser mtu = (MtTemplateUser)it.next();
				typeAndIds+=mtu.getAuthType()+"|"+mtu.getAuthId()+",";
			}
			if(typeAndIds!=null&&!typeAndIds.equals("")){
				List<V3xOrgEntity> authUsers = orgManager.getEntities(typeAndIds.substring(0,typeAndIds.lastIndexOf(",")));
				authMap.put(mt.getId(), authUsers);
			}else{
				authMap.put(mt.getId(), null);
			}
		}
		
		String url="meeting/user/template_list_iframe";
		if(request.getParameter("templateType")!=null && request.getParameter("templateType").equals("1")){
			url="meeting/admin/template_list_iframe";
		}
		
		ModelAndView mav = new ModelAndView(url);
		
		mav.addObject("authMap", authMap);
		mav.addObject("list", list);
		return mav;
	}
	
	/**
	 * 显示会议详细页面，或预览会议
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String replyId=request.getParameter("replyId");
		if(StringUtils.isNotBlank(replyId)){
			MtReply myReply=replyManager.getById(Long.valueOf(replyId));
			//this.bind(request, myReply);
			myReply.setFeedback(request.getParameter("feedback"));
			myReply.setFeedbackFlag(Integer.valueOf(request.getParameter("feedbackFlag")));
			myReply.setMeetingId(Long.valueOf(request.getParameter("id")));
			myReply.setUserId(CurrentUser.get().getId());
			myReply.setReadDate(new Date());
			replyManager.save(myReply);  
		}
		
		String idStr=request.getParameter("id");
		MtTemplate bean=null;
		if(StringUtils.isBlank(idStr)){
			bean=new MtTemplate();
		}else{
			bean=mtTemplateManager.getById(Long.valueOf(idStr));
		}
		
		String view="meeting/user/template_list_detail_iframe";
		if(request.getParameter("preview")!=null){
			view="meeting/user/template_preview";
		}else if(request.getParameter("oper")!=null){
			view="meeting/user/showContent";
		}
		ModelAndView mav = new ModelAndView(view);
		List<Attachment> attachments=attachmentManager.getByReference(bean.getId(), bean.getId());
		mav.addObject("attachments", attachments);
		
		mav.addObject("bean", bean);
		
		MtSummaryTemplate summary=null;
		List<MtSummaryTemplate> list=mtSummaryTemplateManager.findByProperty("meetingId", Long.valueOf(idStr));
		if(list.size()>0)
			summary=list.get(0);
		mav.addObject("summary", summary);
		List<MtReply> replyList=replyManager.findByProperty("meetingId",bean.getId());
		for(MtReply reply:replyList){
			if(reply.getUserId()==CurrentUser.get().getId()){
				mav.addObject("myReply", reply);
			}
		}
		mav.addObject("replyList", replyList);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView configUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idsStr=request.getParameter("id");
		String userIdsStr=request.getParameter("userIds");

		List<MtTemplate> templateList=new ArrayList<MtTemplate>();
		for(String idStr:idsStr.split(",")){
			if(StringUtils.isNotBlank(idStr)){
				MtTemplate template=this.mtTemplateManager.getById(Long.valueOf(idStr));
				if(template!=null){
					templateList.add(template);
				}
			}
		}
		this.mtTemplateUserManager.configUser(templateList, userIdsStr);
		String url="common/redirect";
		ModelAndView mav = new ModelAndView(url);
		mav.addObject("redirectURL", "/mtTemplate.do?method=list&templateType=1");
		return mav;
	}

	public void setReplyManager(MtReplyManager replyManager) {
		this.replyManager = replyManager;
	}

	public void setMtSummaryTemplateManager(
			MtSummaryTemplateManager mtSummaryTemplateManager) {
		this.mtSummaryTemplateManager = mtSummaryTemplateManager;
	}
	
	/**
	 * 与会资源列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView selectResources(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("meeting/include/selectResources");
		List<ResourceCAP> meetingResourceList = resourceManagerCAP.findResourcesByType(ResourceTypeCAP.MEETINTRESOURCE.getValue());
		mav.addObject("resourceMap", meetingResourceList);
		return mav;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setMtContentTemplateManager(
			MtContentTemplateManager mtContentTemplateManager) {
		this.mtContentTemplateManager = mtContentTemplateManager;
	}

	public void setMtTemplateManager(MtTemplateManager mtTemplateManager) {
		this.mtTemplateManager = mtTemplateManager;
	}


	public void setMtMeetingManager(MtMeetingManager mtMeetingManager) {
		this.mtMeetingManager = mtMeetingManager;
	}


	public MtTemplateUserManager getMtTemplateUserManager() {
		return mtTemplateUserManager;
	}


	public void setMtTemplateUserManager(MtTemplateUserManager mtTemplateUserManager) {
		this.mtTemplateUserManager = mtTemplateUserManager;
	}
}
