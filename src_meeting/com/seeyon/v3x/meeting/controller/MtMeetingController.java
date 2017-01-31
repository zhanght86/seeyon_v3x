package com.seeyon.v3x.meeting.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.calendar.manager.CalEventManagerCAP;
import com.seeyon.cap.collaboration.domain.ColSummaryCAP;
import com.seeyon.cap.collaboration.manager.ColManagerCAP;
import com.seeyon.cap.common.security.SecurityCheckCAP;
import com.seeyon.cap.doc.domain.DocResourceCAP;
import com.seeyon.cap.doc.manager.DocHierarchyManagerCAP;
import com.seeyon.cap.indexInterface.IndexManager.UpdateIndexManagerCAP;
import com.seeyon.cap.project.domain.ProjectPhaseEventCAP;
import com.seeyon.cap.project.domain.ProjectSummaryCAP;
import com.seeyon.cap.project.manager.ProjectManagerCAP;
import com.seeyon.cap.project.manager.ProjectPhaseEventManagerCAP;
import com.seeyon.cap.project.webmodel.ProjectComposeCAP;
import com.seeyon.cap.resource.ResourceTypeCAP;
import com.seeyon.cap.resource.domain.ResourceCAP;
import com.seeyon.cap.resource.manager.ResourceManagerCAP;
import com.seeyon.cap.videoconference.manager.CreateVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.DeleteVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.JoinVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.StartVideoConferenceManagerCAP;
import com.seeyon.cap.videoconference.manager.UpdateVideoConferenceManagerCAP;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.MessageEncoder;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.MessageUtil;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.domain.MtContentTemplate;
import com.seeyon.v3x.meeting.domain.MtMeeting;
import com.seeyon.v3x.meeting.domain.MtReply;
import com.seeyon.v3x.meeting.domain.MtReplyWithAgentInfo;
import com.seeyon.v3x.meeting.domain.MtResources;
import com.seeyon.v3x.meeting.domain.MtSummaryTemplate;
import com.seeyon.v3x.meeting.domain.MtTemplate;
import com.seeyon.v3x.meeting.manager.BaseMeetingManager;
import com.seeyon.v3x.meeting.manager.MtContentTemplateManager;
import com.seeyon.v3x.meeting.manager.MtMeetingManager;
import com.seeyon.v3x.meeting.manager.MtReplyManager;
import com.seeyon.v3x.meeting.manager.MtResourcesManager;
import com.seeyon.v3x.meeting.manager.MtSummaryTemplateManager;
import com.seeyon.v3x.meeting.manager.MtTemplateManager;
import com.seeyon.v3x.meeting.util.Constants;
import com.seeyon.v3x.meeting.util.MeetingMsgHelper;
import com.seeyon.v3x.meetingroom.domain.MeetingRoom;
import com.seeyon.v3x.meetingroom.manager.MeetingRoomManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plugin.videoconf.VideoConferenceSysInit;
import com.seeyon.v3x.plugin.videoconf.util.AddMember;
import com.seeyon.v3x.plugin.videoconf.util.VideoConferenceConfig;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;
import com.seeyon.v3x.videoconference.manager.VideoConferenceManager;
import com.seeyon.v3x.videoconference.util.ParseXML;
import com.seeyon.v3x.videoconference.util.VideoConfUtil;

/**
 * 会议的Controller
 * @author wolf
 * @editer xut、Rookie Young 、radishlee
 */
public class MtMeetingController extends BaseController {

    private MtMeetingManager mtMeetingManager;
    private AffairManager affairManager;
    private AttachmentManager attachmentManager;
    private MtReplyManager replyManager;
    private MtSummaryTemplateManager mtSummaryTemplateManager;
    private MetadataManager metadataManager;
    private MtContentTemplateManager mtContentTemplateManager;
    private MtTemplateManager mtTemplateManager;
    private UserMessageManager userMessageManager = null;
    private ProjectManagerCAP projectManagerCAP;
    private static HashMap<String, String> meetingroomAndAppIdCacheMap = new HashMap<String, String>();
    private ColManagerCAP colManagerCAP;
    private ResourceManagerCAP resourceManagerCAP;
    private MtResourcesManager mtResourcesManager;
    private IndexManager indexManager;
    private OrgManager orgManager;
	private AppLogManager appLogManager;
	private DocHierarchyManagerCAP docHierarchyManagerCAP;
    private MeetingRoomManager meetingRoomManager;
    private CalEventManagerCAP calEventManagerCAP;
    private ProjectPhaseEventManagerCAP projectPhaseEventManagerCAP;
    private UpdateIndexManagerCAP updateIndexManagerCAP;
    private SecurityCheckCAP securityCheckCAP;
    
	@Override
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }

    /**
     * 创建会议
     */
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	super.noCache(response);
    	
        String oper = request.getParameter("formOper");
        String projectId = request.getParameter("projectId");
        ModelAndView mav = new ModelAndView("meeting/user/meeting_create");
        
	    //radishlee add 2012-1-7视频会议状态判断
        mav.addObject("videoConfStatus", VideoConferenceConfig.VIDEO_CONF_STATUS);
        //radishlee add 2012-1-7视频会议点数判断
        mav.addObject("videoConfPoints", VideoConferenceConfig.VIDEO_CONF_POINT);
        
        String remindFlag = request.getParameter("remindFlag");
        mav.addObject("isContainCal", Boolean.FALSE);
        mav.addObject("meetingroomId", "noMeetingroom");
        MtMeeting bean = (MtMeeting) request.getAttribute("message");
        if(bean == null){
        	bean = new MtMeeting();
        }
        if (Strings.isNotBlank(projectId)) {
            bean.setProjectId(Long.parseLong(projectId));
            mav.addObject("project", "project");
        }
        
      //radishlee add 2012-4-25视频会议报错显示附件
        if(request.getAttribute("fromMethod")!=null){
    		if(request.getAttribute("fromMethod").equals("create")){
    			 if(attachmentManager.getAttachmentsFromRequestNotRelition(request)!=null) {
                	 mav.addObject("attachments", attachmentManager.getAttachmentsFromRequestNotRelition(request));
                	 bean.setAttachmentsFlag(true);
                }
    		}
    	}
        
        // 时间管理-新建
		Date date = new Date();
		String timeS = request.getParameter("time");
		if (Strings.isNotBlank(timeS)) {
			date = Datetimes.parseDatetime(timeS);
		}
		// 时间去整：当分钟小于30时将分钟变为30；当分钟大于等于30时进入下一小时
		Date time = Datetimes.getNextPeriodMinute(date, 30, true);
     
        // 处理正文格式加载
        if (StringUtils.isNotBlank(oper) && "loadTemplate".equals(oper)) {
            String templateId = request.getParameter("contentTemplateId");
            String oldSelectMeetingrooms = request.getParameter("selectMeetingrooms");
            
            
            //处理会议类型 2012-6-26 radishlee add
            String meetingType = request.getParameter("meetingNature");
            if(StringUtils.isNotBlank(meetingType)){
            	if(meetingType.equals(Constants.ORID_MEETING)){
            		bean.setMeetingType(Constants.ORID_MEETING);
            	}else if(meetingType.equals(Constants.VIDEO_MEETING)){
            		bean.setMeetingType(Constants.VIDEO_MEETING);
            	}
            }
            
            
            try {
                // 处理模板
                this.bind(request, bean);
                if ("".equals(templateId)) {
                    bean.setDataFormat("HTML");
                    bean.setContent(null);
                    bean.setCreateDate(new Date());
                } else {
                    bean.setTemplateId(Long.valueOf(templateId));
                }
                // 处理会议室
                if (oldSelectMeetingrooms == null || "fristOption".equals(oldSelectMeetingrooms)){
                    mav.addObject("meetingroomId", "noMeetingroom");
                } else {
                    mav.addObject("meetingroomId", oldSelectMeetingrooms);
                    Long meetingroomId = Long.parseLong(oldSelectMeetingrooms.substring(0, oldSelectMeetingrooms.indexOf("|")));
                    MeetingRoom mr = meetingRoomManager.getRoom(meetingroomId);
                    mav.addObject("meetingroomName", mr.getName());
                }
            } catch (Exception e) {
                logger.error("绑定数据出错", e);
            }

            bean.setRemindFlag("false".equals(remindFlag));

            if (StringUtils.isNotBlank(templateId)) {
                MtContentTemplate template = this.mtContentTemplateManager.getById(Long.valueOf(templateId));

                if (template != null) {
                	mav.addObject("originalBodyNeedClone", true);
                	
                	bean.setDataFormat(template.getTemplateFormat());
                    bean.setContent(template.getContent());
                    bean.setCreateDate(template.getCreateDate());
                    bean.setTemplateId(template.getId());
                    //附件重现  yangzd 20081223
                    if(attachmentManager.getAttachmentsFromRequestNotRelition(request)!=null) {
                    	 mav.addObject("attachments", attachmentManager.getAttachmentsFromRequestNotRelition(request));
                    	 bean.setAttachmentsFlag(true);
                    }
                } else {
                    PrintWriter out = response.getWriter();
                    out.println("<script>");
                    out.println("alert(parent.v3x.getMessage(\"meetingLang.select_format_null\"))");
                    out.println("</script>");
                    out.flush();
                    return this.redirectModelAndView("/mtMeeting.do?method=create");
                }
            }
        }
        
        List<V3xOrgEntity> confereeList = new ArrayList<V3xOrgEntity>();
        if (StringUtils.isNotBlank(oper) && "createByTemplate".equals(oper)) {
            String templateId = request.getParameter("templateId");
            MtTemplate template = mtTemplateManager.getById(Long.valueOf(templateId));
            if (template == null) {
                MeetingException e = new MeetingException("meeting_template_has_delete");
                request.getSession().setAttribute("_my_exception", e);
                return this.redirectModelAndView("/mtMeeting.do?method=create");
            }
            try {
            	mav.addObject("originalBodyNeedClone", true);
                BeanUtils.copyProperties(bean, template);
                // 去掉模板的时间段，改为调用模板的时候自动生成的时间段
                bean.setBeginDate(time);
                bean.setEndDate(Datetimes.addHour(time, 1)); // 默认结束时间比开始时间大一小时
            } catch (Exception e) {
                logger.error("copyProperties异常", e);
            }
            bean.setId(null);
            // 获取与会资源信息，将其拼起传到前台显示。
            List<MtResources> recourceList = mtResourcesManager.findByPropertyNoInit("meetingId", Long.valueOf(templateId));
            ResourceCAP re = null;
            String resourcesName = "";
            String resourcesId = "";
            if (recourceList.size() != 0)
            {
                for (MtResources resource : recourceList)
                {
                    re = resourceManagerCAP.getResourceByPk(resource.getResourceId());
                    resourcesName += re.getName() + ",";
                    resourcesId += re.getId().toString() + ",";
                }
                bean.setResourcesId(resourcesId.substring(0, resourcesId.lastIndexOf(",")));
                bean.setResourcesName(resourcesName.substring(0, resourcesName.lastIndexOf(",")));
            }

            // 调用模版时回显与会人员
            try {
                confereeList = orgManager.getEntities(bean.getConferees());
                //过滤到离职人员
                for(V3xOrgEntity org : confereeList){
                	if(org instanceof V3xOrgMember && !org.isValid()){
                		confereeList.remove(org);
                	}
                }
            } catch (Exception e) {
                logger.error("获取与会人出错", e);
            }
            mav.addObject("confereeList", confereeList);
            // 获取模版附件
            List<Attachment> attachments = attachmentManager.getByReference(Long.valueOf(templateId), Long.valueOf(templateId));
            mav.addObject("attachments", attachments);
        } else {
            if (bean.getBeginDate() == null)
                bean.setBeginDate(time);

            if (bean.getEndDate() == null)
                bean.setEndDate(Datetimes.addHour(time, 1)); // 默认结束时间比开始时间大一小时

            // 新建会议时默认选中会议提醒并设置创建时间
            if ("new".equals(oper)) {
                bean.setRemindFlag(true);
                bean.setCreateDate(new Date());
               
                
                //radishlee add 2012-2-9协同转发视频会议
                String summaryId = request.getParameter("summaryId");
                String affairId = request.getParameter("affairId");
            	mav.addObject("summaryId", summaryId);
            	mav.addObject("affairId", affairId);
                
            	String collaborationFrom = request.getParameter("collaborationFrom");
            	//如果是已发。什么都不做 2012-4-3 radishlee add
            	if("Sent".equals(collaborationFrom)){
            		//donothing
            	//如果是待办	
            	}else if("Pending".equals(collaborationFrom)){
            		//并且主持人和记录人是同一人
            		if (!StringUtils.isNotBlank(oper) || !"loadTemplate".equals(oper)) {
            			List<Affair> affairsList = affairManager.getAffairBySummaryId(ApplicationCategoryEnum.collaboration, Long.parseLong(summaryId), CurrentUser.get().getId());
            			if(affairsList.size()>1){
	            			if(affairsList.get(0).getId()==Long.parseLong(affairId)){
	            				mav.addObject("affairId", affairsList.get(1).getId());
	            			}else{
	            				mav.addObject("affairId", affairsList.get(0).getId());
	            			}
            			}
            		}
            	}
                if(StringUtils.isNotBlank(summaryId)&& StringUtils.isNotBlank(affairId)){
                    ColSummaryCAP colSummaryCAP = colManagerCAP.getColSummaryById(Long.parseLong(request.getParameter("summaryId")), false);
                	bean.setTitle(colSummaryCAP.getSubject());
                	List<Affair> affairsList =  affairManager.getALLAvailabilityAffairList(ApplicationCategoryEnum.collaboration, colSummaryCAP.getId(),false);
                	List<Long> members = new UniqueList<Long>();
            		for (Affair affair : affairsList) {
        				members.add(affair.getMemberId());
            		}
            		members.remove(CurrentUser.get().getId());
            		
            		if(members.size()!=0){
	            		StringBuffer sb = new StringBuffer();
	            		String s = "Member|";
	        			for(int i = 0; i < members.size();i++ ){
	        				sb.append(s).append(members.get(i)).append(",");
	        			}
	            		confereeList = orgManager.getEntities(sb.substring(0, sb.length()-1));
	            		mav.addObject("confereeList", confereeList);
	            		bean.setConferees(sb.substring(0, sb.length()-1));
            		}
                }
            }

            if (!StringUtils.isNotBlank(oper) || !"loadTemplate".equals(oper)) {
                bean.setRecorderId(CurrentUser.get().getId());
                bean.setEmceeId(CurrentUser.get().getId());
            }

            bean.setCreateUser(CurrentUser.get().getId());
            if (bean.getDataFormat() == null) {
                bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
            }
            bean.setState(Constants.DATA_STATE_SAVE);

            if (bean.getConferees() != null && !bean.getConferees().equals("")){
                // 调用模版时回显与会人员
                try {
                    confereeList = orgManager.getEntities(bean.getConferees());
                } catch (Exception e) {
                    logger.error("获取与会人出错", e);
                }
                mav.addObject("confereeList", confereeList);
            }
        }

        // 页面构造回显人员
        List<V3xOrgMember> emceeList = new ArrayList<V3xOrgMember>();
        List<V3xOrgMember> recorderList = new ArrayList<V3xOrgMember>();

        try {
            emceeList.add(orgManager.getMemberById(bean.getEmceeId()));
            if(bean.getRecorderId().longValue()!=com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID)
            	recorderList.add(orgManager.getMemberById(bean.getRecorderId()));
        } catch (Exception e) {
            logger.error("获取主持人记录人失败", e);
        }

        mav.addObject("emceeList", emceeList);
        mav.addObject("recorderList", recorderList);
        mav.addObject("bean", bean);
        List<ProjectSummaryCAP> projectList = new ArrayList<ProjectSummaryCAP>();
        try {
            projectList = projectManagerCAP.getProjectList();
        } catch (Exception e) {
            logger.error("获取项目列表失败", e);
        }
        // 如果是项目发起人从项目新建的会议，那么直接导入项目的信息为默认信息
        if (oper == null && Strings.isNotBlank(projectId)){
            try {
                StringBuffer projectArea = new StringBuffer();
                ProjectComposeCAP projectCompose = projectManagerCAP.getProjectComposeByID(Long.parseLong(projectId), false);

                List<V3xOrgMember> principal = projectCompose.getPrincipalLists();// 项目负责人
                List<V3xOrgMember> memberLists = projectCompose.getMemberLists();// 项目成员列表

                if (principal != null) {
                	for (V3xOrgMember member : principal) {
                        int exitFlag = 0;
                        if (emceeList != null && emceeList.size() > 0) {
                            V3xOrgMember emceeMember = (V3xOrgMember) emceeList.get(0);
                            if (emceeMember.getId() == member.getId()) {
                                exitFlag = 1;
                            }
                        }
                        if (recorderList != null && recorderList.size() > 0) {
                            V3xOrgMember recorderMember = (V3xOrgMember) recorderList.get(0);
                            if (recorderMember.getId() == member.getId()) {
                                exitFlag = 1;
                            }
                        }
                        if (exitFlag == 0) {
                            projectArea.append(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|" + member.getId() + ",");
                        }
                    }
                }

                if (memberLists != null && memberLists.size() > 0) {
                    for (V3xOrgMember member : memberLists) {
                        int exitFlag = 0;
                        if (emceeList != null && emceeList.size() > 0) {
                            V3xOrgMember emceeMember = (V3xOrgMember) emceeList.get(0);
                            if (emceeMember.getId() == member.getId()) {
                                exitFlag = 1;
                            }
                        }
                        if (recorderList != null && recorderList.size() > 0) {
                            V3xOrgMember recorderMember = (V3xOrgMember) recorderList.get(0);
                            if (recorderMember.getId() == member.getId()) {
                                exitFlag = 1;
                            }
                        }
                        if (exitFlag == 0) {
                            projectArea.append(V3xOrgEntity.ORGENT_TYPE_MEMBER + "|" + member.getId() + ",");
                        }
                    }
                }
                bean.setConferees(projectArea.toString());
                confereeList = orgManager.getEntities(projectArea.toString());
                mav.addObject("confereeList", confereeList);
            } catch (Exception e) {
                logger.error("获取与会人出错", e);
            }

        }
        mav.addObject("projectMap", projectList);
        mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
        mav.addObject("contentTemplateList", mtContentTemplateManager.findTypeAll("1"));// 会议格式--标志为1
        mav.addObject("nowTime", new Date());
        return mav;
    }

    /**
     * 编辑会议
     */
    @SuppressWarnings("unchecked")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idStr = request.getParameter("id");
        String remindFlag = request.getParameter("remindFlag");
        MtMeeting bean;
        if (StringUtils.isBlank(idStr)){
            bean = new MtMeeting();
            bean.setBeginDate(new Date());
            bean.setEndDate(new Date());
            bean.setRecorderId(CurrentUser.get().getId());
            bean.setEmceeId(CurrentUser.get().getId());
            bean.setCreateDate(new Date());
            bean.setCreateUser(CurrentUser.get().getId());
            bean.setDataFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
            //radishlee add 2012-1-7 增加会议类型字段
            String meetingNature = request.getParameter("meetingNature")==null?Constants.ORID_MEETING:request.getParameter("meetingNature");
            bean.setMeetingType(meetingNature);
        } else {
        	Object temp = request.getAttribute("message");
        	if(temp != null){
        		bean  = (MtMeeting)temp;
        	}else{
	        	bean = mtMeetingManager.getById(Long.valueOf(idStr));
	            if (bean == null) {
	                MeetingException e = new MeetingException("meeting_has_delete");
	                request.getSession().setAttribute("_my_exception", e);
	                return this.redirectModelAndView("/mtMeeting.do?method=listMain");
	            }
	            
	            if ("loadTemplate".equals(request.getParameter("formOper"))) { // 如果是更改会议格式提交的edit，则先将提交的东西保存再更改格式，否则刚才修改的内容就丢失了
	                onlySave(request, response);
	            }
        	}
            // TODO kuanghs 这里将来会议对应的日程事件也要同步修改
        }

        ModelAndView mav = new ModelAndView("meeting/user/meeting_create");
        
        //radishlee add 2012-1-7视频会议状态判断
        mav.addObject("videoConfStatus", VideoConferenceConfig.VIDEO_CONF_STATUS);
        //radishlee add 2012-1-7视频会议点数判断
        mav.addObject("videoConfPoints", VideoConferenceConfig.VIDEO_CONF_POINT);
        
        List<Attachment> attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
        mav.addObject("attachments", attachments);

        String oper = request.getParameter("formOper");

        // 处理正文格式加载
        if (StringUtils.isNotBlank(oper) && "loadTemplate".equals(oper))
        {
            String templateId = request.getParameter("contentTemplateId");
            String oldSelectMeetingrooms = request.getParameter("selectMeetingrooms");
            try
            {
                // 处理模板
                this.bind(request, bean);
                if ("".equals(templateId))
                {
                    bean.setDataFormat("HTML");
                    bean.setContent(null);
                    bean.setCreateDate(new Date());
                    bean.setTemplateId(null);
                }
                else
                {
                    bean.setTemplateId(Long.valueOf(templateId));
                }
                // 处理会议室
                if (oldSelectMeetingrooms == null || "fristOption".equals(oldSelectMeetingrooms))
                {
                    mav.addObject("meetingroomId", "noMeetingroom");
                }
                else
                {
                    mav.addObject("meetingroomId", oldSelectMeetingrooms);
                    Long meetingroomId = Long.parseLong(oldSelectMeetingrooms.substring(0,
                            oldSelectMeetingrooms.indexOf("|")));
                    MeetingRoom mr = meetingRoomManager.getRoom(meetingroomId);
                    mav.addObject("meetingroomName", mr.getName());
                }
            }
            catch (Exception e) {
                logger.error("绑定数据出错", e);
            }

            bean.setRemindFlag("false".equals(remindFlag));

            if (StringUtils.isNotBlank(templateId)) {
                MtContentTemplate template = this.mtContentTemplateManager.getById(Long
                        .valueOf(templateId));

                bean.setDataFormat(template.getTemplateFormat());
                bean.setContent(template.getContent());
                bean.setCreateDate(template.getCreateDate());
                bean.setTemplateId(template.getId());
            }
        }

        List<ProjectSummaryCAP> projectList = new ArrayList<ProjectSummaryCAP>();
        try {
            projectList = projectManagerCAP.getProjectList();
        } catch (Exception e) {
            logger.error("获取项目列表失败", e);
        }
        // 获取会议资源列表
        // List<Resource> meetingRomeList =
        // resourceManager.findResourcesByType(ResourceType.OFFICE.getValue());
        // 获取与会资源信息，将其拼起传到前台显示。
        if(Strings.isNotBlank(idStr)){
	        List<MtResources> recourceList = mtResourcesManager.findByPropertyNoInit("meetingId", Long
	                .valueOf(idStr));
	        ResourceCAP re = null;
	        String resourcesName = "";
	        String resourcesId = "";
	        if (recourceList.size() != 0)
	        {
	            for (MtResources resource : recourceList)
	            {
	                re = resourceManagerCAP.getResourceByPk(resource.getResourceId());
	                resourcesName += re.getName() + ",";
	                resourcesId += re.getId().toString() + ",";
	            }
	            bean.setResourcesId(resourcesId.substring(0, resourcesId.lastIndexOf(",")));
	            bean.setResourcesName(resourcesName.substring(0, resourcesName.lastIndexOf(",")));
	        }
        }
        // 页面构造回显人员
        List<V3xOrgMember> emceeList = new ArrayList<V3xOrgMember>();
        List<V3xOrgMember> recorderList = new ArrayList<V3xOrgMember>();
        List<V3xOrgEntity> confereeList = new ArrayList<V3xOrgEntity>();
        try
        {
            emceeList.add(orgManager.getMemberById(bean.getEmceeId()));
            if(bean.getRecorderId().longValue()!=com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID)
            	recorderList.add(orgManager.getMemberById(bean.getRecorderId()));
            String conferees = bean.getConferees();
            confereeList = orgManager.getEntities(conferees);
        }
        catch (Exception e)
        {
            logger.error("获取会议人员出错", e);
        }

        // 处理会议室
        if (bean == null)
        {
            mav.addObject("meetingroomId", "noMeetingroom");
        }
        else
        {
            // 在这里用Id取暂存会议室的信息并放进mav
            String oldSelectIds = meetingroomAndAppIdCacheMap.get(bean.getId().toString());
            mav.addObject("meetingroomId", oldSelectIds == null ? "noMeetingroom" : oldSelectIds);
            try
            {
                String oldMeetingroomId = oldSelectIds.substring(0, oldSelectIds.indexOf("|"));
                MeetingRoom mr = meetingRoomManager.getRoom(Long.valueOf(oldMeetingroomId));
                String oldMeetingroomName = mr.getName();
                mav.addObject("meetingroomName", oldMeetingroomName);
            }
            catch (Exception e)
            {
                mav.addObject("meetingroomId", "noMeetingroom");
                mav.addObject("meetingroomName", "");
            }
            //meetingroomAndAppIdCacheMap.remove(bean.getId().toString());
        }

        mav.addObject("emceeList", emceeList);
        mav.addObject("confereeList", confereeList);
        mav.addObject("recorderList", recorderList);
        mav.addObject("projectMap", projectList);
        mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
        mav.addObject("contentTemplateList", mtContentTemplateManager.findTypeAll("1"));// 会议格式--标志为1
        mav.addObject("bean", bean);
        mav.addObject("nowTime", new Date());
        mav.addObject("meetingId", idStr);
        return mav;
    }
    
    /**
     * 发送会议
     * @deprecated 查看CVS历史，此方法实际已被废弃
     * （在列表中选中若干会议然后点击发送按钮，将其中暂存待发状态的会议悉数发送，这个操作方式至少在2008-10-28以后被取消）
     */
    public ModelAndView send(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MtMeeting bean = null;
        String idStr = request.getParameter("id");
        if (StringUtils.isBlank(idStr)) {
//            bean = new MtMeeting();
        } else {
            String[] ids = idStr.split(",");
            for (String id : ids) {
                if (StringUtils.isBlank(id))
                    continue;
                bean = mtMeetingManager.getById(Long.valueOf(id));
                if (bean.getState() == Constants.DATA_STATE_SEND)
                    continue;

                bean.setState(Constants.DATA_STATE_SEND);

                List<Long> msgReceivers = this.mtMeetingManager.getMsgReceivers(bean);
                
                userMessageManager.sendSystemMessage(
                		MessageContent.get("mt.send", bean.getTitle(), CurrentUser.get().getName(), bean.getBeginDate())
                			.setBody(bean.getContent(), bean.getDataFormat(), bean.getCreateDate()),
                        ApplicationCategoryEnum.meeting, 
                        bean.getCreateUser(), 
                        MessageReceiver.get(bean.getId(), msgReceivers, "message.link.mt.send", bean.getId().toString()));

                this.mtMeetingManager.save(bean);
                IndexEnable indexEnable = (IndexEnable) mtMeetingManager;
                IndexInfo indexInfo = indexEnable.getIndexInfo(bean.getId());
                indexManager.index(indexInfo);
            }
        }
        return this.redirectModelAndView("/mtMeeting.do?method=listMain");
    }

    /**
     * 保存会议为个人模板
     * @deprecated
     * @see #saveAsTemplateNew
     */
    public ModelAndView saveAsTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MtTemplate bean = new MtTemplate();
        User user = CurrentUser.get();
        Long accountId = user.getLoginAccount();

        super.bind(request, bean);
        bean.setRecorderIdFromStr(request.getParameter("recorderId"));
        bean.setDataFormat(request.getParameter("bodyType"));

        bean.setState(Constants.DATA_STATE_SAVE);
        if (bean.isNew()) {
            bean.setCreateDate(new Date());
            bean.setCreateUser(user.getId());
            bean.setAccountId(accountId);
        }
        bean.setUpdateDate(new Date());
        bean.setUpdateUser(user.getId());

        bean.setTemplateName(bean.getTitle() + ResourceBundleUtil.getString(Constants.MT_I18N_RES, "mt.personaltemplete"));
        bean.setTemplateType(Constants.MEETING_TEMPLATE_TYPE_PERSON);

        mtTemplateManager.save(bean);

        if (bean.isNew()) {
            attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
        } else {
            attachmentManager.deleteByReference(bean.getId(), bean.getId());
            attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
        }
        
        super.rendJavaScript(response, "parent.enableBtnAndPrintMsg();");
        return null;
    }

    /**
     * 保存会议为个人模板(一直为另存为，模板不覆盖，只要点击另存模板就会产生新模板，用在新建会议时另存模板之用)
     */
    @SuppressWarnings("unchecked")
    public ModelAndView saveAsTemplateNew(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = CurrentUser.get();
        Long accountId = user.getLoginAccount();
        MtTemplate bean = new MtTemplate();

        super.bind(request, bean);
        bean.setRecorderIdFromStr(request.getParameter("recorderId"));

        // ID直接设null，保证每次都是另存为最新模板
        bean.setId(null);
        bean.setDataFormat(request.getParameter("bodyType"));

        bean.setState(Constants.DATA_STATE_SAVE);
        bean.setCreateDate(new Date());
        bean.setCreateUser(user.getId());
        bean.setAccountId(accountId);

        bean.setUpdateDate(new Date());
        bean.setUpdateUser(user.getId());

        bean.setTemplateName(bean.getTitle() + ResourceBundleUtil.getString(Constants.MT_I18N_RES, "mt.personaltemplete"));
        bean.setTemplateType(Constants.MEETING_TEMPLATE_TYPE_PERSON);

        mtTemplateManager.save(bean);
        try {
            attachmentManager.deleteByReference(bean.getId(), bean.getId());
            attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
        } catch (Exception e) {
            attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
        }
        super.rendJavaScript(response, "parent.enableBtnAndPrintMsg();");
        return null;
    }
    
    /**
     * 保存会议
     */
    @SuppressWarnings("unchecked")
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String idStr = request.getParameter("id");
        String room = request.getParameter("room");
        User user = CurrentUser.get();
        Long accountId = user.getLoginAccount();
        MtMeeting bean = Strings.isBlank(idStr) ? new MtMeeting() : mtMeetingManager.getById(Long.valueOf(idStr));
        
        //在完成保存或发送的动作之前，会议状态是否为暂存待发或新会议，或者是已发送未召开的会议
        boolean isTargetNotSent = bean.getState()==null || bean.getState()==Constants.DATA_STATE_SAVE;
        MtMeeting oldBean = (MtMeeting)bean.clone();
        super.bind(request, bean);
        
        bean.setRecorderIdFromStr(request.getParameter("recorderId"));
        bean.setDataFormat(request.getParameter("bodyType"));
        bean.setRemindFlag(request.getParameter("remindFlag") != null);
        bean.setAccountId(accountId);
        
        //会议保存时  保存格式
        String templateId = request.getParameter("contentTemplateId");
        if(templateId!=null && !"".equals(templateId)){
            bean.setTemplateId(Long.valueOf(templateId));
        }else{
            bean.setTemplateId(null);
        }
        //radishlee add 2012-1-7 增加会议类型字段
        String meetingNature = request.getParameter("meetingNature")==null?Constants.ORID_MEETING:request.getParameter("meetingNature");
        bean.setMeetingType(meetingNature);
        //radishlee add 2012-4-17 会议性质 默认公开 公开是true 不公开是false
        String meetingCharacter = request.getParameter("meetingCharacter")==null? Constants.MEETING_NOT_OPEN:Constants.MEETING_OPEN;
        bean.setMeetingCharacter(meetingCharacter);
        //radishlee add 2012-4-17 如果视频会议系统坏了。普通会议会议密码制空
        if("".equals(request.getParameter("meetingPassword"))||request.getParameter("meetingPassword")==null){
        	bean.setMeetingPassword("");
        }
        
        if (request.getParameter("beginDate").length() < 17) {
            bean.setBeginDate(Datetimes.parseDatetimeWithoutSecond(request.getParameter("beginDate")));
            bean.setEndDate(Datetimes.parseDatetimeWithoutSecond(request.getParameter("endDate")));
        }
        
        if (Strings.isBlank(room))
            bean.setRoom(null);


        String formOper = request.getParameter("formOper");
        if (Strings.isBlank(formOper) || "save".equals(formOper)) { //暂存
            if (bean.isNew())
                bean.setState(Constants.DATA_STATE_SAVE);
        } else if (formOper.equals("send")) {  //发送
        	//radishlee add 2012-1-17
        	Map videoParamMap = new HashMap();
        	//radishlee add 2012-2-4 普通会议没有这两个参数 
         	if(meetingNature.equals(Constants.VIDEO_MEETING)){
		     	videoParamMap.put("subject", bean.getTitle());
		     	Date beginDate = bean.getBeginDate();
		     	
		     	if(beginDate.before(new Date())){
		     		beginDate = new Date();
		     	}
		     	
		     	//radishlee add 2012-4-13 防止创建会议时间不同步。A8系统是1点59分。点击创建会议时候。红杉树正好变成2点钟。会议创建不成功bug!
		     	beginDate = DateUtils.addMinutes(bean.getBeginDate(),+5);
		     	
		     	Date endDate = DateUtils.addMinutes(bean.getEndDate(),+5);
		     	videoParamMap.put("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(VideoConfUtil.toGMTDate(beginDate, "-480")).replace(" ", "T"));
		     	videoParamMap.put("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(VideoConfUtil.toGMTDate(endDate, "-480")).replace(" ", "T"));
		     	videoParamMap.put("hostName",orgManager.getMemberById(bean.getEmceeId()).getLoginName());
		     	videoParamMap.put("agenda","欢迎您参加此次会议！");
		     	videoParamMap.put("creator", CurrentUser.get().getLoginName());
		     	videoParamMap.put("openType", meetingCharacter); //radishlee add 2012-4-14
		     	videoParamMap.put("passwd", bean.getMeetingPassword());//radishlee add 2012-4-20 会议密码不加密
		     	videoParamMap.put("userName", CurrentUser.get().getLoginName());
		     	videoParamMap.put("password", new MessageEncoder().encode(CurrentUser.get().getLoginName(), "111111"));
		     	videoParamMap.put("attendeeAmount", VideoConferenceConfig.VIDEO_CONF_POINT);
		     	videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);

		     	V3xOrgMember mem = orgManager.getMemberById(CurrentUser.get().getId());
                if(mem!=null && mem.getEmailAddress()!=null){
            		videoParamMap.put("email",mem.getEmailAddress());//radishlee add 2012-4-24 给创建者发email邮件
                }else{
                	videoParamMap.put("email","");
                }
         	}
         	
         	String meetingOperate = request.getParameter("meetingOperate")==null?"":request.getParameter("meetingOperate");
         	if (!bean.isNew()){//如果是修改会议（4种可能）
         		//视频会议==》普通会议 在第三方系统里删除会议
            	if(Constants.VIDEO_MEETING.equals(oldBean.getMeetingType())&&Constants.ORID_MEETING.equals(bean.getMeetingType())){
               		Map paramMap = new HashMap();
               		paramMap.put("confKey", bean.getConfKey());
               		paramMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
               		paramMap.put("userName", CurrentUser.get().getLoginName());
               		paramMap.put("password", new MessageEncoder().encode(CurrentUser.get().getLoginName(), "111111"));
               		try{
               			this.deleteVideoConference(paramMap);//删除第三方系统创建的视频会议实例
               		}catch(Exception e){
               			request.setAttribute("message", bean);
               			request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.warning")+"');"));
     	            	return this.listHome(request, response);
               		}
                //普通会议==》视频会议 在第三方系统里创建该会议	
            	}else if(Constants.ORID_MEETING.equals(oldBean.getMeetingType())&&Constants.VIDEO_MEETING.equals(bean.getMeetingType())){
            		try{
     					this.createVideoConference(videoParamMap,bean);//创建视频会议
     				}catch(Exception e){
     					request.setAttribute("message", bean);
     			    	String errorCode = e.getMessage().trim();
     					if(errorCode.equals("0x0000008")){//如果没有此人同步此人到红杉树视频会议系统
     						request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.warning")+"');"));
     						//同步此人
     						AddMember.addMember(com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_URL,
     								            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_USER_NAME, 
     								            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_PASSWORD, 
     								           orgManager.getMemberById(CurrentUser.get().getId()), new MessageEncoder());
     					}else if(errorCode.equals("0x0902063")){
     						request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.net.warning")+"');"));
     					}
     					
 						if("create".equals(request.getParameter("fromMethod"))){
     				 		bean.setId(null);
     				 		request.setAttribute("fromMethod", "create");
     				 		return this.create(request, response);
     				 	}else{
     				 		request.setAttribute("fromMethod", "edit");
     				 		return this.edit(request, response);
     				 	}
     				}
            	//普通会议==》普通会议 	
            	}else if(Constants.ORID_MEETING.equals(oldBean.getMeetingType())&&Constants.ORID_MEETING.equals(bean.getMeetingType())){
                    //什么也不做
            	//视频会议==》视频会议 	保存旧的conkfey和meetingid
            	}else if(Constants.VIDEO_MEETING.equals(oldBean.getMeetingType())&&Constants.VIDEO_MEETING.equals(bean.getMeetingType())){
            		//如果是是视频会议 从暂存待发状态点修改点发送 应该新建视频会议 不是修改视频会议
            		if(bean.getState()==Constants.DATA_STATE_SAVE){
            			try{
         					this.createVideoConference(videoParamMap,bean);//创建视频会议
         				}catch(Exception e){
         					request.setAttribute("message", bean);
         			    	String errorCode = e.getMessage().trim();
         					if(errorCode.equals("0x0000008")){//如果没有此人同步此人到红杉树视频会议系统
         						request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.warning")+"');"));
         						//同步此人
         						AddMember.addMember(com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_URL,
         								            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_USER_NAME, 
         								            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_PASSWORD, 
         								           orgManager.getMemberById(CurrentUser.get().getId()), new MessageEncoder());
         					}else if(errorCode.equals("0x0902063")){
         						request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.net.warning")+"');"));
         					}
         					
     						if("create".equals(request.getParameter("fromMethod"))){
         				 		bean.setId(null);
         				 		request.setAttribute("fromMethod", "create");
         				 		return this.create(request, response);
         				 	}else{
         				 		request.setAttribute("fromMethod", "edit");
         				 		return this.edit(request, response);
         				 	}
         				}
            		}else{
	            		videoParamMap.put("confKey",oldBean.getConfKey());
	            		try{
		                    String meetingInf = ((UpdateVideoConferenceManagerCAP)ApplicationContextHolder.getBean("updateVideoConferenceManagerCAP")).updateVideoConferenceCap(videoParamMap);
		                    logger.info("调用红杉树修改预约会议接口：  "+meetingInf);
		                 	
		                 	if(StringUtils.contains(meetingInf, "0x")||!StringUtils.contains(meetingInf,"SUCCESS")){
		                 		throw new Exception(meetingInf);
		                 	}
	            		}catch(Exception e){
	            			request.setAttribute("message", bean);
         			    	String errorCode = e.getMessage().trim();
         					if(errorCode.equals("0x0000008")){//如果没有此人同步此人到红杉树视频会议系统
         						request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.warning")+"');"));
         						//同步此人
         						AddMember.addMember(com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_URL,
         								            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_USER_NAME, 
         								            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_PASSWORD, 
         								           orgManager.getMemberById(CurrentUser.get().getId()), new MessageEncoder());
         					}else if(errorCode.equals("0x0902063")){
         						request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.net.warning")+"');"));
         					}
         					
     						if("create".equals(request.getParameter("fromMethod"))){
         				 		bean.setId(null);
         				 		request.setAttribute("fromMethod", "create");
         				 		return this.create(request, response);
         				 	}else{
         				 		request.setAttribute("fromMethod", "edit");
         				 		return this.edit(request, response);
         				 	}
	            		}
	            		//更新后把视频会议confkey和视频会议Id保存进来
	                	bean.setVideoMeetingId(oldBean.getVideoMeetingId());
	                	bean.setConfKey(oldBean.getConfKey());
            		}
            	}
         		
         	}else{
         		////新建会议（2种可能） 如果视频会议系统坏掉 只能建立普通会议
         		if(meetingNature.equals(Constants.VIDEO_MEETING)){
         			if(VideoConferenceConfig.VIDEO_CONF_POINT!=null){
         				try{
         					this.createVideoConference(videoParamMap,bean);//创建视频会议
         				}catch(Exception e){
         					request.setAttribute("message", bean);
         			    	String errorCode = e.getMessage().trim();
         					if(errorCode.equals("0x0000008")){//如果没有此人同步此人到红杉树视频会议系统
         						request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.warning")+"');"));
         						//同步此人
         						AddMember.addMember(com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_URL,
         								            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_USER_NAME, 
         								            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_PASSWORD, 
         								           orgManager.getMemberById(CurrentUser.get().getId()), new MessageEncoder());
         					}else if(errorCode.equals("0x0902063")){
         						request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.video.failure.net.warning")+"');"));
         					}
         					
     						if("create".equals(request.getParameter("fromMethod"))){
         				 		bean.setId(null);
         				 		request.setAttribute("fromMethod", "create");
         				 		return this.create(request, response);
         				 	}else{
         				 		request.setAttribute("fromMethod", "edit");
         				 		return this.edit(request, response);
         				 	}
         				}
         			}
         		}else if(meetingNature.equals(Constants.ORID_MEETING)){
         			//do nothing
         		}
         	}

			Date cDate = new Date();
			if (cDate.after(bean.getEndDate())) {
				bean.setState(Constants.DATA_STATE_FINISH);
			} else if (cDate.after(bean.getBeginDate())) {
				bean.setState(Constants.DATA_STATE_START);
			} else {
				bean.setState(Constants.DATA_STATE_SEND);
			}

        	this.mtMeetingManager.checkIfFields4QuartzChanged(oldBean, bean);
        }

        if (bean.isNew()) {
            bean.setCreateDate(new Date());
            bean.setCreateUser(CurrentUser.get().getId());
            bean.setCreateUserName(CurrentUser.get().getName());
            //将 新建操作 写入日志操作列表中
            appLogManager.insertLog(user, AppLogAction.Meeting_New, user.getName(),bean.getTitle());
        } else {
        	//将 修改操作 写入日志操作列表中
        	appLogManager.insertLog(user, AppLogAction.Meeting_Update, user.getName(),bean.getTitle());
        }
        bean.setUpdateDate(new Date());
        bean.setUpdateUser(CurrentUser.get().getId());

        try {
            bean.setHasAttachments("true".equals(request.getParameter("isHasAtt")));
            if(!bean.isNew())
            	attachmentManager.deleteByReference(bean.getId(), bean.getId());
            
            MtMeeting savedBean = null;
            try {
            	savedBean = mtMeetingManager.save(bean);
            } catch(Exception e){
            	request.setAttribute("message", bean);
            	logger.error("保存会议出现错误：", e);
            	request.setAttribute("alert", ("alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "save.failure.warning")+"');"));
            	if("create".equals(request.getParameter("fromMethod"))){
            		bean.setId(null);
            		request.setAttribute("fromMethod", "create");
            		return this.create(request, response);
            	}else{
            		request.setAttribute("fromMethod", "edit");
            		return this.edit(request, response);
            	}
            }
            
            //处理协同会议不能保存conferees问题   daiy 2012-2-16  start
            //保存与会对象记录
//            String oldConferees = request.getParameter("oldConferees");
            String conferees = request.getParameter("conferees");
            
//            if(!conferees.equals(oldConferees)) {
            	this.mtMeetingManager.deleteConferees(bean.getId());
            	this.mtMeetingManager.saveMeetingConferees(bean.getId(), conferees);
//            }
            //处理协同会议不能保存conferees问题   daiy 2012-2-16  end
            
            if("send".equals(formOper)) {
            	MeetingMsgHelper.sendMessage(isTargetNotSent, bean, oldBean, mtMeetingManager, userMessageManager, replyManager);
            }

            // 关联会议室的相关操作
            String selectMeetingroomsValue = request.getParameter("selectMeetingrooms"); // 选择的会议室ID

            if (formOper == null || formOper.equals("") || formOper.equals("save")) { // 暂存会议
                if (bean.isNew()) { // 暂存&新会议
                    if (!"fristOption".equals(selectMeetingroomsValue))
                        meetingroomAndAppIdCacheMap.put(savedBean.getId().toString(), selectMeetingroomsValue);
                } else { // 暂存&旧会议
                    if ("fristOption".equals(selectMeetingroomsValue)) { // 没有选择会议室，则删除会议室
                        meetingroomAndAppIdCacheMap.remove(savedBean.getId().toString());
                    } else { // 选择了会议室,删除缓存中的会议室，再提交会议关联
                        meetingroomAndAppIdCacheMap.put(savedBean.getId().toString(), selectMeetingroomsValue);
                        meetingRoomManager.execCancelMeeting(savedBean.getId());
                        Long meetingRoomId = Long.valueOf(selectMeetingroomsValue.substring(0,
                                selectMeetingroomsValue.indexOf("|")));
                        Long meetingRoomAppId = null;
                        if ("".equals(selectMeetingroomsValue.substring(selectMeetingroomsValue.indexOf("|") + 1))) {
                            meetingRoomAppId = null;
                        }  else {
                            try {
                                meetingRoomAppId = Long.valueOf(selectMeetingroomsValue.substring(selectMeetingroomsValue.indexOf("|") + 1));
                            } catch (Exception e) {
                                meetingRoomAppId = null;
                            }
                        }
                        meetingRoomManager.execMeeting(savedBean.getId(), meetingRoomId,
                                meetingRoomAppId, savedBean.getBeginDate().getTime(),
                                savedBean.getEndDate().getTime());
                    
                    }
                }
            } else if (formOper.equals("send")) { // 发送
                if (bean.isNew()) { // 发送&新会议
                    if (!"fristOption".equals(selectMeetingroomsValue)) { // 选择了会议室,提交关联关系
                        Long meetingRoomId = Long.valueOf(selectMeetingroomsValue.substring(0,
                                selectMeetingroomsValue.indexOf("|")));
                        Long meetingRoomAppId = null;
                        if ("".equals(selectMeetingroomsValue.substring(selectMeetingroomsValue
                                .indexOf("|") + 1))) {
                            meetingRoomAppId = null;
                        } else {
                            try {
                                meetingRoomAppId = Long.valueOf(selectMeetingroomsValue
                                        .substring(selectMeetingroomsValue.indexOf("|") + 1));
                            } catch (Exception e) {
                                meetingRoomAppId = null;
                            }
                        }
                        meetingRoomManager.execMeeting(savedBean.getId(), meetingRoomId,
                                meetingRoomAppId, savedBean.getBeginDate().getTime(),
                                savedBean.getEndDate().getTime());
                    }
                } else { // 发送&旧会议
                    if ("fristOption".equals(selectMeetingroomsValue)) { // 没有选择会议室，则删除会议室
                        meetingroomAndAppIdCacheMap.remove(savedBean.getId().toString());
                        meetingRoomManager.execCancelMeeting(savedBean.getId());
                    } else { // 选择了会议室,删除缓存中的会议室，再提交会议关联
                        meetingroomAndAppIdCacheMap.put(savedBean.getId().toString(),
                                selectMeetingroomsValue);
                        meetingRoomManager.execCancelMeeting(savedBean.getId());
                        Long meetingRoomId = Long.valueOf(selectMeetingroomsValue.substring(0,
                                selectMeetingroomsValue.indexOf("|")));
                        Long meetingRoomAppId = null;
                        if ("".equals(selectMeetingroomsValue.substring(selectMeetingroomsValue.indexOf("|") + 1))) {
                            meetingRoomAppId = null;
                        }  else {
                            try {
                                meetingRoomAppId = Long.valueOf(selectMeetingroomsValue.substring(selectMeetingroomsValue.indexOf("|") + 1));
                            } catch (Exception e) {
                                meetingRoomAppId = null;
                            }
                        }
                        meetingRoomManager.execMeeting(savedBean.getId(), meetingRoomId,
                                meetingRoomAppId, savedBean.getBeginDate().getTime(),
                                savedBean.getEndDate().getTime());
                    }
                }
            }

            attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
        } catch (BusinessException e){
            ModelAndView mav = new ModelAndView("meeting/user/meeting_create");
            mav.addObject("bean", bean);
            List<ProjectSummaryCAP> projectList = projectManagerCAP.getProjectList();
            mav.addObject("projectMap", projectList);
            mav.addObject("resourceMap", ((BaseMeetingManager) mtMeetingManager).getMeetingUtils().getMeetingResources());
            mav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
            mav.addObject("contentTemplateList", mtContentTemplateManager.findAll());
            return mav;
        }
        if (bean.getState().equals(Constants.DATA_STATE_SEND)){
            // 在此加入全文检索
            IndexEnable indexEnable = (IndexEnable) mtMeetingManager;
            IndexInfo indexInfo = indexEnable.getIndexInfo(bean.getId());
            indexManager.index(indexInfo);
        }
        if(logger.isDebugEnabled()){
        	logger.debug("当前会议状态：" + bean.getState());
        }
        
        //如果是项目会议,存入该项目下当前阶段
        if(bean.getProjectId() != null && bean.getProjectId() != -1){
        	ProjectSummaryCAP projectSummary = projectManagerCAP.getProject(bean.getProjectId());
        	if(projectSummary != null){
        		if(projectSummary.getPhaseId() != 1){
        			ProjectPhaseEventCAP projectPhaseEvent = new ProjectPhaseEventCAP(ApplicationCategoryEnum.meeting.key(), bean.getId(), projectSummary.getPhaseId());
        			projectPhaseEventManagerCAP.save(projectPhaseEvent);
        		}
        	}
        }
        
        if (Strings.isNotBlank(request.getParameter("project"))){
            return this.redirectModelAndView("/mtMeeting.do?method=listHome&stateStr=" + bean.getState());
        } else {
        	return this.redirectModelAndView("/mtMeeting.do?method=listHome&stateStr=" + bean.getState());
        }
    }
    
    /**
     * 检查会议是否存在
     */
	public boolean isMeetingExist(Long id) {
		return this.mtMeetingManager.getById(id) != null;
	}
    
    /**
     * 防止正在邀请的同时会议发布人删除当前人员
     */
	public void checkInvite(Long id, String allConferee, Long curUser) {
		MtMeeting mt = this.mtMeetingManager.getById(id);
		Long userId = curUser != null ? curUser : CurrentUser.get().getId();
		
		boolean canInvite = false;
		if (userId.longValue() == mt.getCreateUser().longValue() || userId.longValue() == mt.getEmceeId().longValue() || 
				userId.longValue() == mt.getRecorderId().longValue()) {
			canInvite = true;
		} else {
			List<V3xOrgMember> conferees = this.getReplyMember(mt);
			List<Long> confereeIds = CommonTools.getEntityIds(conferees);
			canInvite = confereeIds.contains(userId);
		}
		
		if(canInvite){
			invitePeople(id, allConferee);
		}
	}

	/**
	 * 会议邀请
	 */
	public void invitePeople(Long id, String allConferee) {
		User user = CurrentUser.get();
		MtMeeting mt = this.mtMeetingManager.getById(id);
		String oldConferee = mt.getConferees();
		oldConferee += "," + allConferee;
		
		try {
			this.mtMeetingManager.deleteConferees(id);
        	this.mtMeetingManager.saveMeetingConferees(id, oldConferee);
			mt.setConferees(oldConferee);
			this.mtMeetingManager.update(mt);

			// 给被邀请人员发送待办事项提醒
			List<Long> idsNew = CommonTools.getMemberIdsByTypeAndId(allConferee, orgManager);
			if (mt.beginAndEndTimeGreaterThanNow()) {
				this.mtMeetingManager.createAffairs(mt, idsNew);
			}
			
			// 给会议发起人发送新增了人员的消息
			userMessageManager.sendSystemMessage(MessageContent.get("mt.mtCreater", mt.getTitle(), user.getName(), 
					mt.getBeginDate()).setBody(mt.getContent(), mt.getDataFormat(), mt.getCreateDate()),
					ApplicationCategoryEnum.meeting, user.getId(), 
					MessageReceiver.get(mt.getId(), mt.getCreateUser(), "message.link.mt.mtCreater", mt.getId().toString()));
			
			
			// 给被邀请人员发送被邀请消息
			userMessageManager.sendSystemMessage(MessageContent.get("mt.invite", mt.getTitle(), user.getName(),
					mt.getBeginDate()).setBody(mt.getContent(), mt.getDataFormat(), mt.getCreateDate()),
					ApplicationCategoryEnum.meeting, user.getId(),
					MessageReceiver.get(mt.getId(), idsNew, "message.link.mt.invite", mt.getId().toString()));
			
			
			// 全文检索
			IndexEnable indexEnable = (IndexEnable) mtMeetingManager;
			IndexInfo indexInfo = indexEnable.getIndexInfo(id);
			indexManager.index(indexInfo);
		} catch (Exception e) {
			logger.error("邀请人员出错", e);
		}
	}

    /**
     * 保存会议（不跳转）
     */
    public void onlySave(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idStr = request.getParameter("id");
        String room = request.getParameter("room");
        User user = CurrentUser.get();
        Long accountId = user.getLoginAccount();
        MtMeeting bean = Strings.isBlank(idStr) ? new MtMeeting() : mtMeetingManager.getById(Long.valueOf(idStr));
        MtMeeting oldMt4CheckQuartz = (MtMeeting)bean.clone();
        
        super.bind(request, bean);
        bean.setRecorderIdFromStr(request.getParameter("recorderId"));
        bean.setDataFormat(request.getParameter("bodyType"));
        bean.setRemindFlag(request.getParameter("remindFlag") != null);
        bean.setAccountId(accountId);
        
        if (request.getParameter("beginDate").length() < 17) {
            bean.setBeginDate(Datetimes.parseDatetimeWithoutSecond(request.getParameter("beginDate")));
            bean.setEndDate(Datetimes.parseDatetimeWithoutSecond(request.getParameter("endDate")));
        }

        if (Strings.isBlank(room))
            bean.setRoom(null);

        String formOper = request.getParameter("formOper");
        if (formOper == null || formOper.equals("") || formOper.equals("save")){
            if (bean.isNew())
                bean.setState(Constants.DATA_STATE_SAVE);
        } else if (formOper.equals("send")) {
            bean.setState(Constants.DATA_STATE_SEND);
        	this.mtMeetingManager.checkIfFields4QuartzChanged(oldMt4CheckQuartz, bean);
        }

        if (bean.isNew()) {
            bean.setCreateDate(new Date());
            bean.setCreateUser(CurrentUser.get().getId());
        }
        bean.setUpdateDate(new Date());
        bean.setUpdateUser(CurrentUser.get().getId());

        try {
            bean.setHasAttachments("true".equals(request.getParameter("isHasAtt")));
            MtMeeting theBeanAfterSave = mtMeetingManager.save(bean);

            // 关联会议室的相关操作
            String selectMeetingroomsValue = request.getParameter("selectMeetingrooms"); // 选择的会议室ID

            if ("loadTemplate".equals(formOper)) { // 暂存会议
                if (bean.isNew()) { // 载入会议模板&新会议
                    if (!"fristOption".equals(selectMeetingroomsValue)) { // 选择了会议室,先保存到缓存
                        meetingroomAndAppIdCacheMap.put(theBeanAfterSave.getId().toString(),
                                selectMeetingroomsValue);
                    }
                } else { // 载入会议模板&旧会议
                    if ("fristOption".equals(selectMeetingroomsValue)) { // 没有选择会议室，则删除会议室
                        meetingroomAndAppIdCacheMap.remove(theBeanAfterSave.getId().toString());
                    } else { // 选择了会议室,覆盖到缓存
                        meetingroomAndAppIdCacheMap.put(theBeanAfterSave.getId().toString(),
                                selectMeetingroomsValue);
                    }
                }
            }

            if (bean.isNew())
                attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
            else {
                attachmentManager.deleteByReference(bean.getId(), bean.getId());
                attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), bean.getId(), request);
            }

            if (formOper != null && formOper.equals("send") || formOper.equals("edit")) {
                //this.replyManager.deleteByMeetingId(bean.getId());

                String key = "send".equals(formOper) ? "mt.send" : "mt.edit";
                List<Long> msgReceivers = this.mtMeetingManager.getMsgReceivers(bean);

                Collection<MessageReceiver> receivers = MessageReceiver.get(bean.getId(), msgReceivers,
                        "message.link.mt.send", bean.getId().toString());
                userMessageManager.sendSystemMessage(MessageContent.get(key, bean.getTitle(),
                        CurrentUser.get().getName(), bean.getBeginDate()).setBody(
                        bean.getContent(), bean.getDataFormat(), bean.getCreateDate()),
                        ApplicationCategoryEnum.meeting, bean.getCreateUser(), receivers);
            }
        } catch (BusinessException e) {
            ModelAndView mav = new ModelAndView("meeting/user/meeting_create");
            mav.addObject("bean", bean);
            List<ProjectSummaryCAP> projectList = projectManagerCAP.getProjectList();
            mav.addObject("projectMap", projectList);
            mav.addObject("resourceMap", ((BaseMeetingManager) mtMeetingManager).getMeetingUtils()
                    .getMeetingResources());
            mav.addObject("remindTimeMetaData", metadataManager
                    .getMetadata(MetadataNameEnum.common_remind_time));
            mav.addObject("contentTemplateList", mtContentTemplateManager.findAll());
            request.getSession().setAttribute("_my_exception", e);
            return;
        }
        if (bean.getState().equals(Constants.DATA_STATE_SEND)) {
            // 在此加入全文检索
            IndexEnable indexEnable = (IndexEnable) mtMeetingManager;
            IndexInfo indexInfo = indexEnable.getIndexInfo(bean.getId());
            indexManager.index(indexInfo);
        }
    }

    /**
     * 删除会议，支持批量删除(已发未召开的会议取消时也通过这个方法，不删除会议本身，而是将其置为暂存待发状态)
     */
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse response)throws Exception{
        String idStr = request.getParameter("id");
        if (Strings.isNotBlank(idStr)){
            String[] idStrs = idStr.split(",");
            for (String str : idStrs) {
                if (Strings.isNotBlank(str)){
                	Long meetingId = Long.valueOf(str);
                    MtMeeting mt = this.mtMeetingManager.getById(meetingId);
                    if (mt.getState() == Constants.DATA_STATE_SEND) {
                    	//radishlee add 2012-3-8 增加删除视频会议提示
                    	String contentKey = null;
                    	if(mt.getMeetingType().equals(Constants.VIDEO_MEETING)){
                    		contentKey = "mt.cancel.video";
                    	}else{
                    		contentKey = "mt.cancel";
                    	}
                    	userMessageManager.sendSystemMessage(
                             	MessageContent.get(contentKey, mt.getTitle(), CurrentUser.get().getName()),
                                ApplicationCategoryEnum.meeting, 
                                mt.getCreateUser(),
                                MessageReceiver.get(mt.getId(), this.mtMeetingManager.getMsgReceivers(mt)));   
                    }

                    // 删除会议回复
                    this.replyManager.deleteByMeetingId(meetingId);

                    // 删除会议总结
                    List<MtSummaryTemplate> summaryList = mtSummaryTemplateManager.findByPropertyNoInit("meetingId", meetingId);
                    for (MtSummaryTemplate summary : summaryList){
                        mtSummaryTemplateManager.delete(summary.getId());
                    }

                    // 删除会议室的相关信息
                    meetingroomAndAppIdCacheMap.remove(meetingId.toString());

                    if (Constants.DATA_STATE_SAVE == mt.getState() || Constants.DATA_STATE_SEND == mt.getState()){
                        meetingRoomManager.execCancelMeetingRec(meetingId);
                    } else {
                        meetingRoomManager.execCancelMeeting(meetingId);
                    }
                    
                    if("delete".equals(request.getParameter("flag"))) { //删除会议
                    	// 删除与会对象记录
                    	this.mtMeetingManager.deleteConferees(meetingId);                    	
                    	mtMeetingManager.delete(meetingId);
                    	calEventManagerCAP.deleteCalEventFromOtherAppId(meetingId, ApplicationCategoryEnum.meeting.ordinal(), null);
                    } else if(mt.getState()==Constants.DATA_STATE_SEND){ //取消已发送未召开的会议
                    	mtMeetingManager.updateState(meetingId, Constants.DATA_STATE_SAVE);
                    	affairManager.deleteByObject(ApplicationCategoryEnum.meeting, meetingId);
                    	calEventManagerCAP.deleteCalEventFromOtherAppId(meetingId, ApplicationCategoryEnum.meeting.ordinal(), null);
                    }
                
                	//radishlee add-2012-1-11 如果是视频会议调用删除接口 
                    //modifyByradishlee at 2012-4-16 不分是取消还是删除。视频会议系统里的会议信息一律删除
                	if(Constants.VIDEO_MEETING.equals(mt.getMeetingType())&&mt.getState()>Constants.DATA_STATE_SAVE){
                		Map videoParamMap = new HashMap();
                		videoParamMap.put("confKey", mt.getConfKey());
                		videoParamMap.put("webBaseUrl",VideoConferenceConfig.WEBBASEURL);
                     	videoParamMap.put("userName", CurrentUser.get().getLoginName());
                     	VideoConferenceManager videoConferenceManager = (VideoConferenceManager) ApplicationContextHolder.getBean("videoConferenceManager");
                     	videoParamMap.put("password", new MessageEncoder().encode(CurrentUser.get().getLoginName(), "111111"));
                     	try{
                     		this.deleteVideoConference(videoParamMap);//删除第三方系统里创建的视频会议实例
                     	}catch(Exception e){
                     		request.setAttribute("message", mt);
         	            	return this.listHome(request, response);
                     	}
                	}
                    
                    // 删除全文检索
                    try{
                        IndexEnable indexEnable = (IndexEnable) mtMeetingManager;
                        IndexInfo indexInfo = indexEnable.getIndexInfo(meetingId);
                        indexManager.deleteFromIndex(indexInfo.getAppType(), indexInfo.getEntityID());
                    } catch (Exception e) {
                        logger.error("全文检索删除会议失败" + e);
                    }
                    
                    //将 会议的删除 写入操作日志
                    appLogManager.insertLog(CurrentUser.get(), AppLogAction.Meeting_Delete, CurrentUser.get().getName(), mt.getTitle());
                }
            }
        }
        String stateStr = request.getParameter("stateStr");
        return this.redirectModelAndView("/mtMeeting.do?method=listMain&stateStr=" + (Strings.isBlank(stateStr) ? "" : stateStr));
    }

    /**
     * 会议列表主页面
     */
    public ModelAndView listHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("meeting/user/homeEntry");
    }

    /**
     * 会议列表主页面
     */
    public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("meeting/user/meeting_list_main");
    }
    
    public ModelAndView templatecreate(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return new ModelAndView("meeting/user/template_create_col");
    }

    /**
     * 会议列表：分未召开、待发会议和已召开三种类型
     */
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	List<MtMeeting> meetings = findMeetings(request);
    	ModelAndView listMav = new ModelAndView("meeting/user/meeting_list_iframe");
   		//radishlee add 2012-1-17
        listMav.addObject("videoConfStatus", VideoConferenceConfig.VIDEO_CONF_STATUS);
    	listMav.addObject("list", meetings);
    	listMav.addObject("remindTimeMetaData", metadataManager.getMetadata(MetadataNameEnum.common_remind_time));
        return listMav;
    }
    
    /**
     * 用于关联文档的会议列表
     */
    public ModelAndView list4Quote(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	List<MtMeeting> meetings = findMeetings(request);
    	return new ModelAndView("meeting/user/list4Quote", "meetings", meetings);
    }

	private List<MtMeeting> findMeetings(HttpServletRequest request) {
		String stateStr = request.getParameter("stateStr");
        String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");
        Long userId = CurrentUser.get().getId();
        List<MtMeeting> meetings = this.mtMeetingManager.findMeetings4User(stateStr, userId, condition, textfield, textfield1);
        
        // 更新会议状态，用于当会议结束，但状态未更新
        if(CollectionUtils.isNotEmpty(meetings)) {
	        Date date = new Date(System.currentTimeMillis());
	        for(MtMeeting meeting : meetings){
	        	if(meeting.getState().equals(Constants.DATA_STATE_START)){
	        		if(meeting.getEndDate().compareTo(date) < 0){
	        			meeting.setState(Constants.DATA_STATE_FINISH);
          			    mtMeetingManager.updateState(meeting.getId(), Constants.DATA_STATE_FINISH);
	        		}
	        	}
	        }
        }
		return meetings;
	}

    /**
     * 显示会议详细页面，或预览会议
     */
    public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idStr = request.getParameter("id");
        MtMeeting bean = null;
        Long meetingId = null;
        if(Strings.isNotBlank(idStr)){
        	meetingId = Long.parseLong(idStr);
        	bean = mtMeetingManager.getById(meetingId);
        }
        else{
        	bean = new MtMeeting();
        }
        ModelAndView mav = null;
        List<Attachment> attachments = null;
        if (request.getParameter("preview") != null) {
            mav = new ModelAndView("meeting/user/template_preview");
        }
        else {
        	User user = CurrentUser.get();
        	//SECURITY 访问安全检查
        	if(!securityCheckCAP.isLicit(request, response, ApplicationCategoryEnum.meeting, user, meetingId, null, null)){
        		return null;
        	}
            if (request.getParameter("oper") != null) { // 只显示正文
                mav = new ModelAndView("meeting/user/showContent");
                List<MtReply> replyList = replyManager.findByProperty("meetingId", bean.getId());
                mav.addObject("replySize", replyList.size());
                mav.addObject("replyList", replyList);
                attachments = attachmentManager.getByReference(bean.getId());

                MtSummaryTemplate summary = null;
                List<MtSummaryTemplate> list = mtSummaryTemplateManager.findByProperty("meetingId",
                        Long.valueOf(idStr));
                if (list.size() > 0) {
                    summary = list.get(0);
                    List<Attachment> sattachments = attachmentManager.getByReference(summary.getId(), summary.getId());
                    if (sattachments != null)
                        attachments.addAll(sattachments);
                }

                mav.addObject("summary", summary);
            }
            else {// 只显示标题时间
                mav = new ModelAndView("meeting/user/meeting_list_detail_iframe");
                // 处理会议室
                if (bean != null) {
                    String[] meetingRoomInfo = meetingRoomManager.getByMeeting(bean);
                    if (meetingRoomInfo == null) {
                        mav.addObject("meetingroomId", "noMeetingroom");
                    } else {
                        mav.addObject("meetingroomId", meetingRoomInfo[0] + "|" + meetingRoomInfo[1]);
                        mav.addObject("meetingroomName", meetingRoomInfo[2]);
                    }
                }
                attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
            }
        }
        //radish lee add 2012-2-5
        if(bean.getMeetingType().equals(Constants.VIDEO_MEETING)){
        		mav.addObject("videoConfStatus", VideoConferenceConfig.VIDEO_CONF_STATUS);
        }
        
        //radish lee add 2012-4-10
        if(VideoConferenceConfig.MULTIPLE_MASTER_SERVER_ENABLE){
        	mav.addObject("multipleMasterServerEnable", "enable");
        }
        mav.addObject("bodyType", bean.getDataFormat());
        boolean mtHoldTimeInSameDay = Datetimes.format(bean.getBeginDate(), Datetimes.dateStyle).equals(Datetimes.format(bean.getEndDate(), Datetimes.dateStyle));
        return mav.addObject("bean", bean).addObject("attachments", attachments).addObject("mtHoldTimeInSameDay", mtHoldTimeInSameDay);
    }

    /**
     * 查看会议Frame页面
     */
    public ModelAndView myDetailFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("meeting/user/showMeetingFrame");
    	String id = request.getParameter("id");
        Long meetingId = Long.parseLong(id);
        MtMeeting bean = mtMeetingManager.getByMtId(meetingId);
        if(bean != null)
        	mav.addObject("meetingTitle", bean.getTitle());
        return mav;
    }
    
    /**
     * 查看会议详细内容页面
     */
    public ModelAndView mydetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
        ModelAndView mav = new ModelAndView("meeting/user/meeting_detail_iframe");
        String fisearch=request.getParameter("fisearch");
        String proxy = request.getParameter("proxy");
        String proxyId = request.getParameter("proxyId");
        String fromdoc = request.getParameter("fromdoc");
        String eventId = request.getParameter("eventId");
        String affairId = request.getParameter("affairId");
       
        User user = CurrentUser.get();
        Long userId = user.getId();
       
        mav.addObject("fisearch", fisearch==null ? 0 : fisearch);
        if(Strings.isNotBlank(affairId)){
        	Affair affair = affairManager.getById(Long.valueOf(affairId));
        	if(affair!=null){
        		if(affair.getMemberId().longValue() != userId.longValue());
        		proxy = Constants.PASSIVE_AGENT_FLAG;
        		proxyId = String.valueOf(affair.getMemberId());
        	}
        }
        
    	//从代理事项里过来的
    	String fagent =request.getParameter("fagent");
    	mav.addObject("fagent", Constants.PASSIVE_AGENT_FLAG.equals(fagent) ? fagent : 0 );
        String id = request.getParameter("id");
        
        Long meetingId = Long.parseLong(id);
        MtMeeting bean = mtMeetingManager.getByMtId(meetingId);
      

        if(bean != null && bean.getState() != Constants.DATA_STATE_SEND && bean.getState() != Constants.DATA_STATE_START){
        	affairManager.deleteByObject(ApplicationCategoryEnum.meeting, meetingId);
        }
        
        //如果是从文档中心打开，则不验证是否为与会人员
        if(Strings.isBlank(fromdoc)){
        	if (bean == null) {
        		//修改此处代码防止页面死循环 youhb
            	super.rendJavaScript(response, "alert('"+ResourceBundleUtil.getString("com.seeyon.v3x.meeting.resources.i18n.MeetingResources", "mt.mtMeeting.canceled")+"');" +
				   "if(window.dialogArguments) {window.close();} else {if (parent.parent.getA8Top().document.getElementById('main') != null ){parent.parent.location.reload(true); }else {window.close();}}");
        		//super.rendJavaScript(response, "parent.refreshIfInvalid();");
        		return null;
        	} else {
        		if ((bean.getState()==Constants.DATA_STATE_SAVE  && "10".equals(request.getParameter("state")))
            			|| (!this.mtMeetingManager.isStillInConferees(userId, bean) && Strings.isBlank(eventId))){
					if (Strings.isNotBlank(proxyId)) {
						if (!this.mtMeetingManager.isStillInConferees(Long.valueOf(proxyId), bean)) {
							//修改此处代码防止页面死循环 youhb
			            	super.rendJavaScript(response, "alert("+ResourceBundleUtil.getString("com.seeyon.v3x.meeting.resources.i18n.MeetingResources", "mt.mtMeeting.canceled")+"');" +
 						   "if(window.dialogArguments) {window.close();} else {if (parent.parent.getA8Top().document.getElementById('main') != null ){parent.parent.location.reload(true); }else {window.close();}}");
							//super.rendJavaScript(response, "parent.refreshIfInvalid();");
							return null;
						}
					} else {
						//修改此处代码防止页面死循环 youhb
		            	super.rendJavaScript(response, "alert('"+ResourceBundleUtil.getString("com.seeyon.v3x.meeting.resources.i18n.MeetingResources", "mt.mtMeeting.canceled")+"');" +
						   "if(window.dialogArguments) {window.close();} else {if (parent.parent.getA8Top().document.getElementById('main') != null ){parent.parent.location.reload(true); }else {window.close();}}");
						//super.rendJavaScript(response, "parent.refreshIfInvalid();");
						return null;
					}
            	}
        	}
        }
        
        //SECURITY 访问安全检查
        if(!securityCheckCAP.isLicit(request, response, ApplicationCategoryEnum.meeting, user, meetingId, null, null)){
        	return null;
        }
        
        if (bean != null) {
            String feedBack = request.getParameter("feedback");
            String feedBackFlag = request.getParameter("feedbackFlag");
            
            Long senderId = userId;
            String senderName = user.getName();
            
            MtReply myReply = null;
        	if(Constants.PASSIVE_AGENT_FLAG.equals(proxy)) {
        		Long agentToMemberId =  NumberUtils.toLong(proxyId, -1L);
        		 if (agentToMemberId != null && agentToMemberId != -1L) {
        			 //是否被代理人自己在查看
                     List<MtReply> mrList1 = replyManager.findByMeetingIdAndUserId(bean.getId(), agentToMemberId);
                     myReply = CollectionUtils.isNotEmpty(mrList1) ? mrList1.get(0) : new MtReply();
                     
                     boolean userIsProxyId = agentToMemberId.equals(userId);
                     try {
                    	 V3xOrgMember agentMember = orgManager.getMemberById(userId);
                         if (agentMember != null && agentMember.isValid()) {
                         	V3xOrgMember m = orgManager.getMemberById(agentToMemberId);
                         	if(!userIsProxyId){
                         		senderId = agentToMemberId;
                         		senderName = m != null ? m.getName() : "";
                         	}
                     		myReply.setExt1(userIsProxyId ? Constants.Not_Agent : Constants.PASSIVE_AGENT_FLAG);
                     		myReply.setExt2(userIsProxyId ? Constants.Not_Agent : agentMember.getName());
                         	myReply.setMeetingId(bean.getId());
                         	myReply.setFeedback(feedBack);
                         	myReply.setUserId(agentToMemberId);
                         	myReply.setUserName(m != null ? m.getName() : "");
                         }
                     } catch (Exception e) {
                         logger.error("", e);
                     }
                 }
        	} 
        	else {
        		List<MtReply> mrList = replyManager.findByMeetingIdAndUserId(bean.getId(), userId);
        		
        		if (mrList != null && mrList.size() > 0) {
                	myReply = mrList.get(0);
                } else {
                	myReply = new MtReply();
                	myReply.setMeetingId(bean.getId());
                	myReply.setUserId(userId);
                	myReply.setUserName(CurrentUser.get().getName());
                	try {
    					myReply.setUserAccountName(orgManager.getAccountById(CurrentUser.get().getAccountId()).getName());
    				} catch (BusinessException e1) {
    					logger.error("", e1);
    				}
                }
        		myReply.setExt1(Constants.Not_Agent);
        		myReply.setExt2(Constants.Not_Agent);
        	}
        	
			if (myReply != null) {
				if (Strings.isNotBlank(feedBackFlag)) {
					myReply.setFeedback(feedBack);
					myReply.setFeedbackFlag(NumberUtils.toInt(feedBackFlag, -100));

					try {
						MtReply savedMtReply = replyManager.save(myReply);
						
						// 回执参加，自动将会议转为对应人员的日程事件，如为其他选择，删除生成的日程事件
						if (savedMtReply.getFeedbackFlag() == Constants.FEEDBACKFLAG_ATTEND) {
							this.mtMeetingManager.createCalEvent(bean, savedMtReply.getUserId());
						} else {
							this.mtMeetingManager.deleteCalEvent(bean.getId(), savedMtReply.getUserId());
						}

						// 保存附件
						try {
							if (attachmentManager.hasAttachments(bean.getId(), savedMtReply.getId())) {
								attachmentManager.update(ApplicationCategoryEnum.meeting, bean.getId(), savedMtReply.getId(), request);
							} else {
								attachmentManager.create(ApplicationCategoryEnum.meeting, bean.getId(), savedMtReply.getId(), request);
							}
						} catch (Exception e) {
							logger.error("附件保存失败：", e);
						}
						
						// 发送消息
						List<Long> listId = new ArrayList<Long>();
						listId.add(bean.getCreateUser());
						Collection<MessageReceiver> receivers = MessageReceiver.get(bean.getId(), listId, "message.link.mt.reply", bean.getId().toString(), savedMtReply.getId().toString());
						try {
							String feedback = MessageUtil.getComment4Message(savedMtReply.getFeedback());
							int contentType = Strings.isBlank(feedback) ? -1 : 1;
							int proxyType = senderId.equals(user.getId()) ? 0 : 1;
							userMessageManager.sendSystemMessage(MessageContent.get("mt.reply", bean.getTitle(), senderName, savedMtReply.getFeedbackFlag(), contentType, feedback, proxyType, user.getName()), ApplicationCategoryEnum.meeting, senderId, receivers);
						} catch (MessageException e) {
							logger.error("发送消息失败：", e);
						}
						
						// 更新全文检索
						updateIndexManagerCAP.update(bean.getId(), ApplicationCategoryEnum.meeting.key());
						
						super.rendJavaScript(response, "parent.closeMtWindow('saveMtReply');");
						return null;
					} catch (BusinessException e) {
						logger.error("保存回执信息失败：", e);
					}
				}
			}
        }
        mav.addObject("id", id);
        return mav;
    }

    public ModelAndView showMtDiagram(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("meeting/user/showMtDiagram");
        mav.addObject("fisearch", StringUtils.defaultString(request.getParameter("fisearch"), "0"));
        mav.addObject("fagent", StringUtils.defaultString(request.getParameter("fagent"), "0"));
        
        String idStr = request.getParameter("id");
        String proxy = request.getParameter("proxy");
        String proxyId = request.getParameter("proxyId");
        MtMeeting bean = mtMeetingManager.getById(Long.valueOf(idStr));
        mav.addObject("conferee", bean.getConferees());
        List<MtReplyWithAgentInfo> replyExList =  getMtReplyInfo(bean);
        
        //邀请时选人界面过滤人员
        List<MtReplyWithAgentInfo> excludeReplyExList =  new ArrayList<MtReplyWithAgentInfo>();
        excludeReplyExList.addAll(replyExList);
        MtReplyWithAgentInfo emcee = new MtReplyWithAgentInfo();
        emcee.setReplyUserId(bean.getEmceeId());
        MtReplyWithAgentInfo recorder = new MtReplyWithAgentInfo();
        recorder.setReplyUserId(bean.getRecorderId());
        excludeReplyExList.add(emcee);
        excludeReplyExList.add(recorder);
        
        int[] itemCount = getItemCount(bean);
        Object[] feedbackUsers = getMtReplyUsersByFeedback(bean);
        
        boolean viewAsAgent = Constants.PASSIVE_AGENT_FLAG.equals(proxy) && Strings.isNotBlank(proxyId);
        MtReply reply_1 = null;
        Long userId = viewAsAgent ? NumberUtils.toLong(proxyId) : CurrentUser.get().getId();
        
    	List<MtReply> replyList = replyManager.findByMeetingIdAndUserId(bean.getId(), userId);
    	if(CollectionUtils.isNotEmpty(replyList)) {
    		reply_1 = replyList.get(0);
    		mav.addObject("myReply", reply_1);
    	}
    	//当创建人为与会人时，允许创建人又回执选项
    	String replyFlag = "false"; //标志创建人是否可以回执
    	List<Long> conferees = mtMeetingManager.getConfereeIds(bean.getConferees(), bean.getCreateUser(), bean.getEmceeId(), bean.getRecorderId());
    	if(conferees.contains(bean.getCreateUser()))
    		replyFlag = "true";
    	mav.addObject("replyFlag", replyFlag);  	
    	
        // 获取与会资源信息，将其拼起传到前台显示。
        List<MtResources> recourceList = mtResourcesManager.findByPropertyNoInit("meetingId", Long.valueOf(idStr));
        List<String> recourceNameList = new ArrayList<String>();
        for (MtResources re : recourceList) {
            ResourceCAP res = resourceManagerCAP.getResourceByPk(re.getResourceId());
            recourceNameList.add(res.getName());
        }

        if (bean.getProjectId() != null && bean.getProjectId()!=com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID) {
            ProjectSummaryCAP project = projectManagerCAP.getProject(bean.getProjectId());
            if(project!=null && !ProjectSummaryCAP.state_delete.equals(project.getProjectState()))
            	mav.addObject("projectName", project.getProjectName());
        }

        mav.addObject("recourceNameList", recourceNameList);
        mav.addObject("bean", bean);
        mav.addObject("id", idStr);
        mav.addObject("replyExList", replyExList);
        mav.addObject("excludeReplyExList", excludeReplyExList);
        mav.addObject("itemCount", itemCount);
        mav.addObject("feedbackUsers", feedbackUsers);
       
       if(bean!=null){
    	   List<Attachment> sattachments = attachmentManager.getByReference(bean.getId(),reply_1!=null?reply_1.getId():0L);
    	   if(sattachments!=null && sattachments.size()!=0){
    		   mav.addObject("attachments", sattachments);
    	   }
       }

        // 处理会议室
        if (bean != null) {
            String[] meetingRoomInfo = meetingRoomManager.getByMeeting(bean);
            if (meetingRoomInfo == null) {
                mav.addObject("meetingroomId", "noMeetingroom");
            } else {
                mav.addObject("meetingroomId", meetingRoomInfo[0] + "|" + meetingRoomInfo[1]);
                mav.addObject("meetingroomName", meetingRoomInfo[2]);
            }
        }
        return mav;
    }
    /**
     * 得到不同处理的会议与会人员
     * @param bean
     * @return
     */
    public Object[] getMtReplyUsersByFeedback(MtMeeting bean){
    	//待定的人员
    	Set<String> pending = new HashSet<String>();
    	//未回执的人员
    	Set<String> noFeedback = new HashSet<String>();
    	//参加的人员
    	Set<String> join = new HashSet<String>();
    	//不参加的人员
    	Set<String> noJoin = new HashSet<String>();
    	List<MtReplyWithAgentInfo> replyExList =  getMtReplyInfo(bean);
    	for(MtReplyWithAgentInfo m : replyExList){
    		switch(m.getFeedbackFlag()){
    			case 1:
    				join.add(m.getReplyUserName());
    				break;
    			case 0:
    				noJoin.add(m.getReplyUserName());
    				break;
    			case -1:
    				pending.add(m.getReplyUserName());
    				break;
    			default:
    				noFeedback.add(m.getReplyUserName());
    				break;
    		}
    	}
    	return new Object[]{join,noJoin,pending,noFeedback};
    }
   /**
    * 得到 会议与会人的处理情况
    * @param bean
    */
    private int[] getItemCount(MtMeeting bean){
    	int[] itemCount = new int[] { 0, 0, 0, 0 };
    	List<MtReply> l = replyManager.findByProperty("meetingId", bean.getId());
    	List<V3xOrgMember> li = getReplyMember(bean);
    	 if(!bean.getEmceeId().equals(bean.getCreateUser())){
     		try {
 				V3xOrgMember emcee = orgManager.getMemberById(bean.getEmceeId());
 				if(emcee!=null && !emcee.getIsDeleted())
 					li.add(emcee);
 			} catch (BusinessException e) {
 				logger.error("", e);
 			}
     	}
     	if(!bean.getRecorderId().equals(bean.getCreateUser()) && !bean.getRecorderId().equals(bean.getEmceeId()) 
     			&& bean.getRecorderId().longValue()!=com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID){
     		try {
 				V3xOrgMember recorder = orgManager.getMemberById(bean.getRecorderId());
 				if(recorder!=null && !recorder.getIsDeleted())
 					li.add(recorder);
 			} catch (BusinessException e) {
 				logger.error("", e);
 			}
     	}
     	
     	
    	if(l!=null && l.size()!=0){
    		itemCount[3] = li!=null ? li.size() : 0;
    		for(MtReply m : l){
    			switch (m.getFeedbackFlag()){
                  case 1:
                	  itemCount[0] += 1;
                      break; // 参加
                  case 0:
                	  itemCount[1] += 1;
                      break; // 不参加
                  case -1:
                	  itemCount[2] += 1;
                      break; // 待定
    			}
    			itemCount[3] -= 1;
    		}
    	}else{
    		itemCount[3] = li!=null ? li.size() : 0;
    	}
    	return itemCount;
    }
    
    /**
     * 得到 与会人的回执情况
     * @param conferees 与会人的字符串
     */
    private List<MtReplyWithAgentInfo> getMtReplyInfo(MtMeeting bean){
    	List<MtReplyWithAgentInfo>  replyExList = new ArrayList<MtReplyWithAgentInfo>();
    	List<V3xOrgMember> list = getReplyMember(bean);
    	if(!bean.getEmceeId().equals(bean.getCreateUser())){
    		try {
				V3xOrgMember emcee = orgManager.getMemberById(bean.getEmceeId());
				if(emcee!=null && !emcee.getIsDeleted())
					list.add(emcee);
			} catch (BusinessException e) {
				logger.error("", e);
			}
    	}
    	if(!bean.getRecorderId().equals(bean.getCreateUser()) 
    			&& bean.getRecorderId().longValue()!=com.seeyon.v3x.common.constants.Constants.GLOBAL_NULL_ID) {
    		try {
				V3xOrgMember recorder = orgManager.getMemberById(bean.getRecorderId());
				if(recorder!=null && !recorder.getIsDeleted())
					list.add(recorder);
			} catch (BusinessException e) {
				logger.error("", e);
			}
    	}
    	if(list!=null && list.size()!=0){
    		for(V3xOrgMember m : list){
    			if(m==null || m.getIsDeleted())
    				continue;
    			
    				Long agentId = Constants.getAgentId(m.getId());
    			 	if (agentId != null)
    	            {
    	                V3xOrgMember agentMember = null;
						try {
							agentMember = orgManager.getMemberById(agentId);
						} catch (BusinessException e) {
							logger.error("", e);
						}
						List<MtReply> replys = replyManager.findByMeetingIdAndUserId(bean.getId(), m.getId());
    	                MtReplyWithAgentInfo exMr = new MtReplyWithAgentInfo();
    	                exMr.setReplyUserId(m.getId());
    	                exMr.setReplyUserName(m.getName());
    	                
    	                if(CollectionUtils.isNotEmpty(replys)) {
    	                	MtReply reply = replys.get(0);
    	                	exMr.setFeedbackFlag(reply.getFeedbackFlag());
    	                	if(Constants.PASSIVE_AGENT_FLAG.equals(reply.getExt1())) {
    	                		exMr.setAgentId(agentId);
    	                		exMr.setAgentName(agentMember!=null? agentMember.getName() : "");
    	                	} 
    	                } else {
    	                	exMr.setFeedbackFlag(Constants.FEEDBACKFLAG_NOREPLY);
    	                	exMr.setAgentId(agentId);
    	                	exMr.setAgentName(agentMember!=null? agentMember.getName() : "");
    	                }
    	                replyExList.add(exMr);
    	            }
    	            else{
    	            	List<MtReply> l = replyManager.findByMeetingIdAndUserId(bean.getId(), m.getId());
    	                MtReplyWithAgentInfo exMr = new MtReplyWithAgentInfo();
    	                exMr.setFeedbackFlag((l!=null && l.size()!=0 && l.get(0).getFeedbackFlag()!=null)?l.get(0).getFeedbackFlag():Constants.FEEDBACKFLAG_NOREPLY);
    	                exMr.setReplyUserId(m.getId());
    	                exMr.setReplyUserName(m.getName());
    	                replyExList.add(exMr);
    	            }
    		}
    	}
    	return replyExList;
    }
    
    private static MtMeetingComparator mtMeetingComparator = new MtMeetingComparator();

	//一个部门、组...根据sortId排序
    private static class MtMeetingComparator implements Comparator<V3xOrgMember> {
		public int compare(V3xOrgMember m1, V3xOrgMember m2) {
			return m1.getSortId().compareTo(m2.getSortId());
		}
	}
	
	/**
	 * 获取会议的与会人员,不包括主持人、记录人
	 */
	private List<V3xOrgMember> getReplyMember(MtMeeting meeting) {
		List<V3xOrgMember> confereeSet = new ArrayList<V3xOrgMember>();
		String typeAndIds = meeting.getConferees();
		try {
			if (StringUtils.isNotBlank(typeAndIds)) {
				String items[] = typeAndIds.split(",");
				for (int i = 0; i < items.length; i++) {
					String item = items[i];
					String data[] = item.split("[|]");
					List<V3xOrgMember> list = new ArrayList<V3xOrgMember>(this.orgManager.getMembersByType(data[0], Long.valueOf(data[1])));
					Collections.sort(list, mtMeetingComparator);
					confereeSet.addAll(list);
				}
			}
		} catch (BusinessException e) {
			logger.error("获取与会人员出现错误", e);
		}

		List<V3xOrgMember> conferees = new ArrayList<V3xOrgMember>();
		Set<Long> memberIds = new HashSet<Long>();
		Long emcee = meeting.getEmceeId();
		Long recorder = meeting.getRecorderId();

		for (V3xOrgMember conferee : confereeSet) {
			if (conferee == null || !conferee.isValid())
				continue;
			Long confereeId = conferee.getId();
			if (confereeId.equals(emcee) || confereeId.equals(recorder))
				continue;
			if (!memberIds.contains(confereeId)) {
				conferees.add(conferee);
				memberIds.add(confereeId);
			}
		}
		return conferees;
	}
    
    public ModelAndView showSummary(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("meeting/user/showSummary");
        String idStr = request.getParameter("id");
        MtSummaryTemplate summary = null;
        List<Attachment> attachments = new ArrayList<Attachment>();
        List<MtSummaryTemplate> list = mtSummaryTemplateManager.findByProperty("meetingId", Long
                .valueOf(idStr));
        if (list.size() > 0)
        {
            summary = list.get(0);
            attachments = attachmentManager.getByReference(summary.getId(), summary.getId());
        }
        mav.addObject("attachments", attachments);
        mav.addObject("summary", summary);
        return mav;
    }


    /**
     * 与会资源列表
     */
    @SuppressWarnings("unchecked")
    public ModelAndView selectResources(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("meeting/include/selectResources");
        List<ResourceCAP> meetingResourceList = resourceManagerCAP
                .findResourcesByType(ResourceTypeCAP.MEETINTRESOURCE.getValue());
        mav.addObject("resourceMap", meetingResourceList);
        String selectedResourceIds = request.getParameter("type");
        if (selectedResourceIds != null && !selectedResourceIds.trim().equals(""))
        {
            mav.addObject("oldResourceIds", selectedResourceIds);
        }
        // String[] resourceIdList =
        // request.getParameter("resourceId").split(",");
        // mav.addObject("resourceIdMap", resourceIdList);
        return mav;
    }

    public ModelAndView pigeonhole(HttpServletRequest request, HttpServletResponse response)throws Exception{
        String ids = request.getParameter("id");
        String folders = request.getParameter("folders");
        MtMeeting bean = null;
        boolean result = true;
        if (StringUtils.isNotBlank(ids)){
            String[] idA = ids.split(",");
            List<Long> idList = new ArrayList<Long>();
            List<MtMeeting> beans = new ArrayList<MtMeeting>();
            for (String id : idA){
                if (StringUtils.isNotBlank(id)){
                    idList.add(Long.valueOf(id));
                }
                bean = mtMeetingManager.getById(Long.valueOf(id));
                beans.add(bean);
                if (bean.getState() == Constants.DATA_STATE_SAVE
                        || bean.getState() == Constants.DATA_STATE_SEND
                        || bean.getState() == Constants.DATA_STATE_START){
                	
                    result = false;
                    break;
                }
            }
            if (result){
                this.mtMeetingManager.pigeonhole(idList);
                if(Strings.isNotBlank(folders)){
                	User user =  CurrentUser.get();
                	String[] folderArray = folders.split(",");
                	for(int i=0;i<beans.size();i++){
                		Long fid = Long.parseLong(folderArray[i]);
                		DocResourceCAP res = docHierarchyManagerCAP.getDocResourceById(fid);
                    	String forderName = docHierarchyManagerCAP.getNameById(res.getParentFrId());
                    	appLogManager.insertLog(user, AppLogAction.Meeting_Document, user.getName(),beans.get(i).getTitle(),forderName);
                	}
                	
                }
                
                // 归档后删除原来的全文检索信息
                for (Long id : idList)
                    this.indexManager.deleteFromIndex(ApplicationCategoryEnum.meeting, id);
            }
            else{
                MeetingException e = new MeetingException("meeting_no_pigeonhole", bean.getTitle());
                request.getSession().setAttribute("_my_exception", e);
                return this.redirectModelAndView("/mtMeeting.do?method=listMain&stateStr=20");
            }
        }

        super.rendJavaScript(response, "window.location.href='mtMeeting.do?method=listMain&stateStr=20';");
        return null;
    }

    // 会议转发协同
    public ModelAndView meetingToCol(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String id = request.getParameter("id");
        MtMeeting bean = mtMeetingManager.getByMtId(Long.parseLong(id));
        ModelAndView mav = new ModelAndView("collaboration/newCollaboration");
        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
        mav = colManagerCAP.appToColl(bean.getTitle(), bean.getDataFormat(), bean
                .getCreateDate(), bean.getContent(), attachments, true);
        return mav;
    }

    // 会议总结转发协同
    public ModelAndView summaryToCol(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String id = request.getParameter("id");
        MtSummaryTemplate bean = null;
        List<MtSummaryTemplate> list = mtSummaryTemplateManager.findByProperty("meetingId", Long
                .valueOf(id));
        if (list.size() > 0)
        {
            bean = list.get(0);
        }
        ModelAndView mav = new ModelAndView("collaboration/newCollaboration");
        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments = attachmentManager.getByReference(bean.getId(), bean.getId());
        mav = colManagerCAP.appToColl(bean.getTemplateName(), bean.getTemplateFormat(),
                bean.getCreateDate(), bean.getContent(), attachments, true);
        return mav;
    }

    /**
     * 显示用户自己创建的会议模板，方便调用
     */
    public ModelAndView showTemplate(HttpServletRequest request, HttpServletResponse response) {
        List<MtTemplate> personTemplateList = mtTemplateManager.findAllNoPaginate(Constants.MEETING_TEMPLATE_TYPE_PERSON);
        return new ModelAndView("meeting/user/showMtTemplate", "personTemplateList", personTemplateList);
    }

	/**
	 * 进入关联文档添加页面框架
	 */
	public ModelAndView list4QuoteFrame(HttpServletRequest request, HttpServletResponse response)  {
		return new ModelAndView("collaboration/list4QuoteFrame");
	}

	
	
	
	/**
	 * 开启视频会议
	 * @author radishlee
	 * @throws NoSuchAlgorithmException 
	 * @throws SecurityException 
	 */
	public ModelAndView startVideoMeeting(HttpServletRequest request, HttpServletResponse response) throws SecurityException, NoSuchAlgorithmException  {
		MtMeeting bean = new MtMeeting();
		String confKey = request.getParameter("confKey");
		String meeting_id = request.getParameter("meeting_id");
		String serverName = request.getParameter("serverName");
		bean = mtMeetingManager.getById(Long.parseLong(meeting_id));
		
		Map videoParamMap = new HashMap();
		String loginName = CurrentUser.get().getLoginName();
		try {
			//radishlee add 2012-6-26
     		if(VideoConferenceConfig.MULTIPLE_MASTER_SERVER_ENABLE){
     			if(serverName!=null){
     				videoParamMap.put("serverName",serverName);
     			}
     		}
			
			videoParamMap.put("hostName", loginName);
			videoParamMap.put("displayName", CurrentUser.get().getName());
			videoParamMap.put("confKey", bean.getConfKey());
			videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
			videoParamMap.put("userName", loginName);
			videoParamMap.put("meetingPwd", bean.getMeetingPassword());
			
			String email = orgManager.getMemberById(CurrentUser.get().getId()).getEmailAddress();
			if(email==null||"".equals(email)){
				videoParamMap.put("email",loginName+"@seeyon.com");
			}else{
				videoParamMap.put("email",email);
			}
			
			videoParamMap.put("password", new MessageEncoder().encode(CurrentUser.get().getLoginName(), "111111"));
		}  catch (BusinessException e) {
			logger.error(e);
		}
		
		String result = ((StartVideoConferenceManagerCAP)ApplicationContextHolder.getBean("startVideoConferenceManagerCAP")).startVideoConferenceCap(videoParamMap);
		
		if(StringUtils.contains(result,"FAILURE")||StringUtils.contains(result,"0x0604008")){
			try {
				logger.info("开启红杉树视频会议失败！："+result);
				response.getWriter().print("<script>alert(parent.v3x.getMessage(\"meetingLang.meeting_null_or_notopen\"))</script>");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		if(!StringUtils.contains(result,"SUCCESS")){
			try {
				logger.info("开启红杉树视频会议失败！："+result);
				response.getWriter().print("<script>alert(parent.v3x.getMessage(\"meetingLang.meeting_null_or_notopen\"))</script>");
			    return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.info("开启红杉树视频会议失败！："+result);
		}
		
		logger.info("开启红杉树视频会议："+result);
		Map paramMap = ParseXML.parseXML(result);
        
		if(paramMap==null){
			request.setAttribute("alertMsg", "会议开启失败！");
			return this.redirectModelAndView("/mtMeeting.do?method=listMain&stateStr=10");
		}
		String token = StringUtils.trim((String) paramMap.get("token"));
		String ciURL = StringUtils.trim((String) paramMap.get("ciURL"));
		//改变会议bean状态
		//mtMeetingManager.updateState(Long.parseLong(meeting_id),20);
		//logger.info("启动视频会议并且改变会议状态");
		return new ModelAndView("meeting/user/joinMeeting")
		           .addObject("ciURL",ciURL)
		           .addObject("token",token);
	}
	
	/**
	 
	 * 参加视频会议
	 * @author radishlee
	 * @throws NoSuchAlgorithmException 
	 * @throws SecurityException 
	 */
	public ModelAndView joinVideoMeeting(HttpServletRequest request, HttpServletResponse response) throws Exception{
		MtMeeting bean = new MtMeeting();
		String confKey = request.getParameter("confKey");
		String meeting_id = request.getParameter("meeting_id");
		String serverName = request.getParameter("serverName");
		bean = mtMeetingManager.getById(Long.parseLong(meeting_id));
  
		Map videoParamMap = new HashMap();
     	try {
     		//radishlee add 2012-4-11
     		if(VideoConferenceConfig.MULTIPLE_MASTER_SERVER_ENABLE){
     			if(serverName!=null){
     				videoParamMap.put("serverName",serverName);
     			}
     		}
     		videoParamMap.put("attendeeName", CurrentUser.get().getName());
			videoParamMap.put("confKey", bean.getConfKey());
			videoParamMap.put("webBaseUrl", VideoConferenceConfig.WEBBASEURL);
			String email = orgManager.getMemberById(CurrentUser.get().getId()).getEmailAddress();
			videoParamMap.put("userName", CurrentUser.get().getLoginName());
			videoParamMap.put("meetingPwd",bean.getMeetingPassword());
			
			if(email==null||"".equals(email)){
				videoParamMap.put("email",CurrentUser.get().getLoginName()+"@seeyon.com");
			}else{
				videoParamMap.put("email",email);
			}
			videoParamMap.put("password", new MessageEncoder().encode(CurrentUser.get().getLoginName(), "111111"));
		} catch (BusinessException e) {
			logger.error(e);
		}
		
		String result = ((JoinVideoConferenceManagerCAP)ApplicationContextHolder.getBean("joinVideoConferenceManagerCAP")).joinVideoConferenceCap(videoParamMap);
		
		if(StringUtils.contains(result,"0x0600003")){
			try {
				response.getWriter().print("<script>alert(parent.v3x.getMessage(\"meetingLang.meeting_null_or_notopen\"))</script>");
				logger.info("参加红杉树视频会议失败！："+result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		if(StringUtils.contains(result,"0x0604008")){
			try {
				response.getWriter().print("<script>alert(parent.v3x.getMessage(\"meetingLang.meeting_notopen\"))</script>");
				logger.info("参加红杉树视频会议失败！："+result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		if(StringUtils.contains(result,"0x0000008")){
			try {
				response.getWriter().print("<script>alert('"+ResourceBundleUtil.getString(Constants.MT_I18N_RES, "join.video.failure.warning")+"');</script>");
				logger.info("参加红杉树视频会议失败！："+result);
				//同步此人
				AddMember.addMember(com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_URL,
						            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_USER_NAME, 
						            com.seeyon.v3x.plugin.videoconf.util.Constants.SYN_PASSWORD, 
						           orgManager.getMemberById(CurrentUser.get().getId()), new MessageEncoder());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		logger.info("参加红杉树视频会议："+result);
		Map paramMap = ParseXML.parseXML(result);
        
		if(paramMap==null){
			request.setAttribute("alertMsg", "参加会议失败！");
			return this.redirectModelAndView("/mtMeeting.do?method=listMain&stateStr=10");
		}
		String token = StringUtils.trim((String) paramMap.get("token"));
		String ciURL = StringUtils.trim((String) paramMap.get("ciURL"));

		return new ModelAndView("meeting/user/joinMeeting")
		           .addObject("ciURL",ciURL)
		           .addObject("token",token)
		           .addObject("dt","GMT");
	}
	
	
	/**
	 * @describe 删除视频会议
	 * @author radishlee
	 * @param Map videoParamMap
	 * @return String
	 * @throws Exception 
	 * @since 2012-1-16
	 * 
	 */
	public String deleteVideoConference(Map videoParamMap) throws Exception{
		String deleteInf = ((DeleteVideoConferenceManagerCAP)ApplicationContextHolder.getBean("deleteVideoConferenceManagerCAP")).deleteVideoConferenceCap(videoParamMap);
		
		//如果会议在红杉树系统中不存在。已经删除或者怎样。删除时候不报错
		if(StringUtils.isNotBlank(deleteInf)&&StringUtils.contains(deleteInf,"0x0600001")){
			return deleteInf;
		}
		
		if(!StringUtils.contains(deleteInf,"SUCCESS")){
			throw new Exception("删除失败！Error Code(RedFir):"+deleteInf);
     	}
		return deleteInf;
	}
	
	/**
	 * @describe 创建视频会议
	 * @author radishlee
	 * @param Map videoParamMap
	 * @throws Exception 
	 * @since 2012-1-16
	 * 
	 */
	public void createVideoConference(Map videoParamMap,MtMeeting bean) throws Exception{
		//String meetingInf = ((CreateVideoConferenceManagerCAP)ApplicationContextHolder.getBean("createVideoConferenceManagerCAP")).createVideoConferenceCap(videoParamMap);
		String meetingInf = ((CreateVideoConferenceManagerCAP)ApplicationContextHolder.getBean("createInfoWareInstantMeetingManagerImplCAP")).createVideoConferenceCap(videoParamMap);
     	logger.info("调用红杉树新建预约会议接口：  "+meetingInf);
     	if(!StringUtils.contains(meetingInf,"SUCCESS")){
     		throw new Exception(meetingInf);
     	}
     	
     	//保存会议ID信息到会议对象(红杉树返回的)
     	String infoWareVideoConfId = meetingInf.substring(meetingInf.indexOf("<confId>")+8, meetingInf.lastIndexOf("</confId>"));
    	bean.setVideoMeetingId(infoWareVideoConfId);
    	//保存会议key信息到会议对象
    	meetingInf.replace("\"", "");
    	String confKey = meetingInf.substring(meetingInf.indexOf("<confKey>")+9, meetingInf.lastIndexOf("</confKey>"));
    	bean.setConfKey(confKey);
     
    	bean.setTitle(bean.getTitle());
	}
	
	
	/**
     * @describe 选择会议服务器窗口
	 * @author radishlee
	 * @throws Exception 
	 * @since 2012-4-10
     */
    public ModelAndView choseServerWindow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("meeting/user/choseServerWindow");
    }
    
    
    /**
     * @describe 取得同级多服务器版本服务器列表信息
	 * @author radishlee
	 * @since 2012-4-10
     */
    public String[][] getMeetingServerList(){ 
    	if(SystemEnvironment.hasPlugin("videoconf")){
    		  if(VideoConferenceConfig.MULTIPLE_MASTER_SERVER_ENABLE){
    				CacheAccessable factory = CacheFactory.getInstance(VideoConferenceSysInit.class);
    				CacheMap<String,LinkedList<Map<String,String>>>  meetingServerListcache = factory.getMap("meetingServerList");
    				List<Map<String,String>>  meetingServerList = meetingServerListcache.get("meetingServerList");
    				
    				String[][] ids =  new String[meetingServerList.size()][2];
    				for(int i=0;i<meetingServerList.size();i++){
    					ids[i][0] = meetingServerList.get(i).get("serverName");
    					ids[i][1] = meetingServerList.get(i).get("serverNickName");
    				}
    				
    				return ids;
    		    }else{
    		    	return null;
    		    }
    	}else{
    		return null;
    	}
    }
     
       
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setMtResourcesManager(MtResourcesManager mtResourcesManager) {
		this.mtResourcesManager = mtResourcesManager;
	}

	public void setResourceManagerCAP(ResourceManagerCAP resourceManagerCAP) {
		this.resourceManagerCAP = resourceManagerCAP;
	}

	public void setColManagerCAP(ColManagerCAP colManagerCAP) {
		this.colManagerCAP = colManagerCAP;
	}

	public void setProjectManagerCAP(ProjectManagerCAP projectManagerCAP) {
		this.projectManagerCAP = projectManagerCAP;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setMtMeetingManager(MtMeetingManager mtMeetingManager) {
		this.mtMeetingManager = mtMeetingManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setDocHierarchyManagerCAP(DocHierarchyManagerCAP docHierarchyManagerCAP) {
		this.docHierarchyManagerCAP = docHierarchyManagerCAP;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setMeetingRoomManager(MeetingRoomManager meetingRoomManager) {
		this.meetingRoomManager = meetingRoomManager;
	}

	public void setReplyManager(MtReplyManager replyManager) {
		this.replyManager = replyManager;
	}

	public void setMtSummaryTemplateManager(MtSummaryTemplateManager mtSummaryTemplateManager) {
		this.mtSummaryTemplateManager = mtSummaryTemplateManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setMtContentTemplateManager(MtContentTemplateManager mtContentTemplateManager) {
		this.mtContentTemplateManager = mtContentTemplateManager;
	}

	public void setMtTemplateManager(MtTemplateManager mtTemplateManager) {
		this.mtTemplateManager = mtTemplateManager;
	}

	public void setCalEventManagerCAP(CalEventManagerCAP calEventManagerCAP) {
		this.calEventManagerCAP = calEventManagerCAP;
	}

	public void setProjectPhaseEventManagerCAP(ProjectPhaseEventManagerCAP projectPhaseEventManagerCAP) {
		this.projectPhaseEventManagerCAP = projectPhaseEventManagerCAP;
	}

	public void setUpdateIndexManagerCAP(UpdateIndexManagerCAP updateIndexManagerCAP) {
		this.updateIndexManagerCAP = updateIndexManagerCAP;
	}

	public void setSecurityCheckCAP(SecurityCheckCAP securityCheckCAP) {
		this.securityCheckCAP = securityCheckCAP;
	}
	
}