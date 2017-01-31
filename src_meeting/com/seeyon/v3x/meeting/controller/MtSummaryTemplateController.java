package com.seeyon.v3x.meeting.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.indexInterface.IndexManager.UpdateIndexManagerCAP;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.domain.MtSummaryTemplate;
import com.seeyon.v3x.meeting.manager.MtMeetingManager;
import com.seeyon.v3x.meeting.manager.MtSummaryTemplateManager;
import com.seeyon.v3x.meeting.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.ObjectUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 会议总结格式的Controller
 * @author wolf
 *
 */
public class MtSummaryTemplateController extends BaseController {
	
	private MtSummaryTemplateManager MtSummaryTemplateManager;
	private MtMeetingManager mtMeetingManager;
	private AttachmentManager attachmentManager;
	private UserMessageManager userMessageManager;
	private UpdateIndexManagerCAP updateIndexManagerCAP;
	private OrgManager orgManager;
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setUpdateIndexManagerCAP(UpdateIndexManagerCAP updateIndexManagerCAP) {
		this.updateIndexManagerCAP = updateIndexManagerCAP;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
        this.userMessageManager = userMessageManager;
    }
	
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	
	public void setMtSummaryTemplateManager(MtSummaryTemplateManager MtSummaryTemplateManager) {
		this.MtSummaryTemplateManager = MtSummaryTemplateManager;
	}

	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	

	/**
	 * 创建会议总结格式
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("meeting/admin/summary_template_create");
		MtSummaryTemplate bean=new MtSummaryTemplate();
		//bean.setUsedFlag(true);
		bean.setCreateDate(new Date());
		bean.setCreateUser(CurrentUser.get().getId());
		bean.setTemplateFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		
		mav.addObject("bean", bean);
		return mav;
	}
	
	/**
	 * 编辑会议总结格式
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		MtSummaryTemplate bean;
		ModelAndView mav = new ModelAndView("meeting/admin/summary_template_create");
		if(request.getParameter("fisearch")!=null)
        {
        	mav.addObject("fisearch",request.getParameter("fisearch"));
        }
        else
        {
        	mav.addObject("fisearch",0);
        }
		if(StringUtils.isBlank(idStr)){
			return null;
		}else{
			MtMeeting mt=this.mtMeetingManager.getById(Long.valueOf(idStr));
			
			if(mt==null){
				MeetingException e=new MeetingException("meeting_has_delete");
				request.getSession().setAttribute("_my_exception", e);
				return this.redirectModelAndView("/mtMeeting.do?method=listMain");
			}
			
			if(mt.getRecorderId().longValue()!=CurrentUser.get().getId()){
				MeetingException e=new MeetingException("you_can_not_summary_the_meeting",mt.getTitle());
				request.getSession().setAttribute("_my_exception", e);
				return this.redirectModelAndView("/mtMeeting.do?method=listMain");
			}
			
			if(mt.getState().equals(Constants.DATA_STATE_SAVE)){
				MeetingException e=new MeetingException("meeting_no_finish_save_summary",mt.getTitle());
				request.getSession().setAttribute("_my_exception", e);
				return this.redirectModelAndView("/mtMeeting.do?method=listMain");
			}
			if(mt.getState().equals(Constants.DATA_STATE_SEND)){
				MeetingException e=new MeetingException("meeting_no_finish_no_state_summary",mt.getTitle());
				request.getSession().setAttribute("_my_exception", e);
				return this.redirectModelAndView("/mtMeeting.do?method=listMain");
			}
			if(mt.getState().equals(Constants.DATA_STATE_START)){
				MeetingException e=new MeetingException("meeting_no_finish_no_continue_summary",mt.getTitle());
				request.getSession().setAttribute("_my_exception", e);
				return this.redirectModelAndView("/mtMeeting.do?method=listMain");
			}
			
			List<MtSummaryTemplate> list=MtSummaryTemplateManager.findByProperty("meetingId", Long.valueOf(idStr));
			if(list.size()>0){
				bean=list.get(0);
			    List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
			    mav.addObject("attachments", attachments);
			    mav.addObject("canDeleteOriginalAtts", true);
			}
			else{
				bean=new MtSummaryTemplate();	
				//bean.setUsedFlag(true);
				bean.setTemplateName(mt.getTitle());
				bean.setCreateDate(new Date());
				bean.setCreateUser(CurrentUser.get().getId());
				bean.setTemplateFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
				bean.setMeetingId(Long.valueOf(idStr));
			}
		}
		
		
		mav.addObject("bean", bean);
				
		return mav;
	}
	
	/**
	 * 保存会议总结格式
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {
		MtSummaryTemplate bean=null;
		String idStr=request.getParameter("id");
		String from = request.getParameter("fromdoc");
		String oper = "";
		String key = "";
		if(StringUtils.isBlank(idStr)){
			bean=new MtSummaryTemplate();		
		}else{
			bean=MtSummaryTemplateManager.getById(Long.valueOf(idStr));
		}
		try {
			super.bind(request,bean);
		} catch (Exception e1) {
			logger.error("绑定数据异常", e1);
		}
		
		if(bean.isNew()){
			oper = "create";
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
		}
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());
		Long len = 4294967295L/3;
		if (bean.getContent().length() > len) {
			try {
				PrintWriter out = response.getWriter();
				out.println("<script>");
		    	out.println("alert('保存会议记录失败，请检查内容是否过长')");
		    	out.println("</script>");
		    	return null;
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		try {
			MtSummaryTemplateManager.save(bean);
			if(bean.isNew())
				try {
					attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
				} catch (Exception e) {
					logger.error("保存附件异常", e);
				}
			else{
				attachmentManager.deleteByReference(bean.getId(), bean.getId());
				try {
					attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
				} catch (Exception e) {
					logger.error("保存附件异常", e);
				}
			}
			
			
			MtMeeting mt=this.mtMeetingManager.getById(bean.getMeetingId());
			if(mt==null){
				MeetingException e=new MeetingException("meeting_has_delete");
				request.getSession().setAttribute("_my_exception", e);
				return this.redirectModelAndView("/mtMeeting.do?method=listMain");
			}
			
//			修改会议状态为已总结
			if(mt.getState().intValue()!=Constants.DATA_STATE_SUMMARY){
				mt.setState(Constants.DATA_STATE_SUMMARY);
//				this.mtMeetingManager.save(mt);
				this.mtMeetingManager.update(mt);
			}
			
//			在此更新索引
			updateIndexManagerCAP.update(mt.getId(), ApplicationCategoryEnum.meeting.getKey());
			
//			给与会人、主持人、记录人发总结消息
			
//			消息接收者ids
			List<Long> listId = new ArrayList<Long>();
			List<Long> confereesIds = new ArrayList<Long>();
			String[][] authInfos = Strings.getSelectPeopleElements(mt.getConferees());
			
			for(String[] strings : authInfos){
//				strings[0]type类型	strings[1]是id
				if(strings[0].equals(V3xOrgEntity.ORGENT_TYPE_MEMBER)){
					listId.add(new Long(strings[1]));
					confereesIds.add(new Long(strings[1]));
				}else if(strings[0].equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)){
					V3xOrgDepartment dept = orgManager.getDepartmentById(new Long(strings[1]));
					List<V3xOrgMember> v3xMermberList = orgManager.getMembersByDepartment(new Long(strings[1]), false,dept.getOrgAccountId());
					for(V3xOrgMember deptMember : v3xMermberList){
						if(deptMember.getId().intValue()!=mt.getCreateUser().intValue()&&deptMember.getId().intValue()!=mt.getEmceeId().intValue()&&deptMember.getId().intValue()!=mt.getRecorderId().intValue()){
							listId.add(deptMember.getId());
							confereesIds.add(deptMember.getId());
						}
					}
				}else if(strings[0].equals(V3xOrgEntity.ORGENT_TYPE_TEAM)){
					List<V3xOrgMember> v3xMermberList = orgManager.getTeamMember(new Long(strings[1]));
					for(V3xOrgMember teamMember : v3xMermberList){
						if(teamMember.getId().intValue()!=mt.getCreateUser().intValue()&&teamMember.getId().intValue()!=mt.getEmceeId().intValue()&&teamMember.getId().intValue()!=mt.getRecorderId().intValue()){
							listId.add(teamMember.getId());
							confereesIds.add(teamMember.getId());
						}
					}
				}
			}
			
			if(oper.equals("create")){
				key = "mt.summary_send";
			}else{
				key = "mt.summary_edit";
			}
			
	        if(mt.getRecorderId().intValue()!=mt.getEmceeId().intValue()&&mt.getRecorderId()!=CurrentUser.get().getId()&&mt.getEmceeId()!=CurrentUser.get().getId()){
	        	listId.add(mt.getRecorderId());
	        	listId.add(mt.getEmceeId());
	        }else if(mt.getRecorderId()!=CurrentUser.get().getId()){
	        	listId.add(mt.getRecorderId());
	        }
			
	        Collection<MessageReceiver> receivers = MessageReceiver.get(mt.getId(),listId, "message.link.mt.send", mt.getId().toString());
	        userMessageManager.sendSystemMessage(MessageContent.get(key, mt.getTitle(),CurrentUser.get().getName()), ApplicationCategoryEnum.meeting, bean.getCreateUser(), receivers);
		
		} catch (BusinessException e) {		
			ModelAndView mav = new ModelAndView("meeting/admin/summary_template_create");
			mav.addObject("bean", bean);
			mav.addObject("exception",e);
			return mav;
		}
		if(request.getParameter("fisearch")!=null)
		{
			if(request.getParameter("fisearch").equals("1"))
			{
				try {
					PrintWriter out = response.getWriter();
					out.println("<script>");
			    	out.println("window.close();");
			    	out.println("</script>");
			    	return null;
				} catch (Exception e) {
					logger.error("", e);
				}
  	
			}
		}
		if(Strings.isNotBlank(from)){
			
			return this.redirectModelAndView("/doc.do?method=rightNew");
		}else{
            Boolean f = (Boolean)(BrowserFlag.PageBreak.getFlag(request));
            if(f.booleanValue()){
            	return this.redirectModelAndView("/mtMeeting.do?method=listMain&stateStr=20");
            }else{
            	try {
					PrintWriter out = response.getWriter();
					out.println("<script>");
			    	out.println("window.close();");
			    	out.println("</script>");
			    	return null;
				} catch (Exception e) {
					logger.error("", e);
				}
            }
            return null;
		}
	}
	
	
	/**
	 * 删除会议总结格式，支持批量删除
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
				}
			}
			if(ids.size()>0)
				MtSummaryTemplateManager.deletes(ids);
		}
		
		return this.redirectModelAndView("/MtSummaryTemplate.do?method=listMain");
	}
	
	/**
	 * 会议总结格式列表主页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("meeting/admin/summary_template_list_main");
		return mav;
	}

	/**
	 * 会议总结格式列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<MtSummaryTemplate> list=null;
		String condition=request.getParameter("condition");
		String textfield=request.getParameter("textfield");
		if(StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)){
			Object value=ObjectUtil.getPropertyObject(MtSummaryTemplate.class, condition, textfield);
			list=MtSummaryTemplateManager.findByProperty(condition, value);
		}else{
			list=MtSummaryTemplateManager.findAll();
		}
		
		
		ModelAndView mav = new ModelAndView("meeting/admin/summary_template_list_iframe");
		mav.addObject("list", list);
		return mav;
	}
	
	/**
	 * 显示会议总结格式详细页面，或预览会议总结格式
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		MtSummaryTemplate bean=null;
		if(StringUtils.isBlank(idStr)){
			bean=new MtSummaryTemplate();
		}else{
			bean=MtSummaryTemplateManager.getById(Long.valueOf(idStr));
		}
		
		String view="meeting/admin/summary_template_list_detail_iframe";
		if(request.getParameter("preview")!=null)
			view="meeting/admin/template_preview";
		
		ModelAndView mav = new ModelAndView(view);
		mav.addObject("bean", bean);
		return mav;
	}


	public void setMtMeetingManager(MtMeetingManager mtMeetingManager) {
		this.mtMeetingManager = mtMeetingManager;
	}
	
	
}
