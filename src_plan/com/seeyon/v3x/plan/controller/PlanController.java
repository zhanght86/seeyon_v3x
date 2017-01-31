package com.seeyon.v3x.plan.controller;

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import com.seeyon.cap.meeting.domain.MtContentTemplateCAP;
import com.seeyon.cap.meeting.manager.MtContentTemplateManagerCAP;
import com.seeyon.v3x.collaboration.controller.CollaborationController;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.SecurityCheck;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.MessageUtil;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.edoc.util.EdocUtil;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plan.Constant;
import com.seeyon.v3x.plan.PlanStatus;
import com.seeyon.v3x.plan.PlanType;
import com.seeyon.v3x.plan.PublishStatus;
import com.seeyon.v3x.plan.Constant.PlanOperAction;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.domain.PlanBody;
import com.seeyon.v3x.plan.domain.PlanCountModel;
import com.seeyon.v3x.plan.domain.PlanRelevantUser;
import com.seeyon.v3x.plan.domain.PlanReply;
import com.seeyon.v3x.plan.domain.PlanSummary;
import com.seeyon.v3x.plan.domain.PlanUserScope;
import com.seeyon.v3x.plan.manager.PlanManager;
import com.seeyon.v3x.plan.manager.PlanUserScopeManager;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.manager.ProjectPhaseEventManager;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.webmail.manager.WebMailManager;

@SuppressWarnings({ "unchecked", "deprecation" })
public class PlanController extends BaseController {
    private PlanManager planManager;
    private OrgManager orgManager;
    private PlanUserScopeManager planUserScopeManager;
    private UserMessageManager userMessageManager;
    private AttachmentManager attachmentManager;
    private ProjectManager projectManager;
    private CollaborationController collaborationController;
    private WebMailManager webMailManager;
    private MtContentTemplateManagerCAP mtContentTemplateManagerCAP;
    private AppLogManager appLogManager;
    private IndexManager indexManager;
	private UpdateIndexManager updateIndexManager;
	private ProjectPhaseEventManager projectPhaseEventManager;
    private static final Log log = LogFactory.getLog(PlanController.class);

    @Override
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }

    /**
     * 初始化添加 
     */
    public ModelAndView initAdd(HttpServletRequest request, HttpServletResponse response) throws Exception  {
    	User user = CurrentUser.get();
        Long userId = user.getId();
        ModelAndView mav = new ModelAndView("plan/addPlan");
        String projectId = request.getParameter("projectId");
        Long curUserDepartmentId = null;
        String planDeptID = request.getParameter("planDeptId");
        String planType = request.getParameter("type");
        
        if (StringUtils.isNotBlank(planDeptID)) {
        	mav.addObject("isDeptPlan", true);
        	curUserDepartmentId = Long.valueOf(planDeptID);// 获取前台传过来的(允许)切换的部门ID
        	planType = PlanType.ANY_SCOPE_PLAN.getValue();
        } else {
        	mav.addObject("isDeptPlan", false);
        }
        
        // 获得详细样式
        List<MtContentTemplateCAP> list = mtContentTemplateManagerCAP.findTypeAll("2");// 计划格式--标志为1--------改成统一接口了
        List<ProjectSummary> projectList = projectManager.getProjectList();
        // 获得关联项目
        mav.addObject("planStyle", list);
        mav.addObject("project", projectList);
        
        try {
            // 初始化Plan
            Plan plan = new Plan();
            PlanBody planBody = new PlanBody();
            // 加载样式
            Long styleId = RequestUtils.getLongParameter(request, "styleId");
            if (styleId != null) {
                List<Attachment> attachments = null;
                try {
                    attachments = attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.meeting, -1l, -1l, request);
                } catch (Exception e) {
                    logger.error("加载会议格式时，保存附件异常", e);
                }
                mav.addObject("attachments", attachments);
                if (styleId.toString().equals(Constant.SELECT_NONE_FLAG)) {
                    mav.addObject("isPlanStyleNull", "false");
                } else {
                    // 测试这个planStyle是否还存在 第一步：
                    MtContentTemplateCAP planStyle = null;
                    try {
                        planStyle = mtContentTemplateManagerCAP.getById(styleId);
                    } catch (Exception e) {
                        logger.error("获取计划格式时出现异常：", e);
                    }
                    
                    if (planStyle == null) {
                        styleId = Long.valueOf(Constant.SELECT_NONE_FLAG);
                        mav.addObject("isPlanStyleNull", "true");
                    } else {
                        mav.addObject("isPlanStyleNull", "false");
                    }
                }

                setPlanStyleParameters(request, plan); // 当改变计划格式时，除了正文，其他内容需要保存
                
				boolean hasAttachmens = "true".equals(request.getParameter("isHasAtt"));
				plan.setHasAttachments(hasAttachmens);
				if(hasAttachmens){
					List<Attachment>  attachmentPlan = attachmentManager.getAttachmentsFromRequestNotRelition(request);
		            mav.addObject("attachments", attachmentPlan);
		        }
				
                if (plan.getRefDepartmentId() != null) {
                    mav.addObject("department", orgManager.getDepartmentById(plan.getRefDepartmentId()));
                }
                
                if (Long.valueOf(Constant.SELECT_NONE_FLAG).equals(styleId)) {
                    if (plan.getPlanBody() == null)
                        plan.setPlanBody(new PlanBody());
                    plan.getPlanBody().setContent("");
                    plan.getPlanBody().setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
                    plan.getPlanBody().setCreateDate(new Date());
                } else {
                    loadStyle(request, plan); // 当有计划格式时，需加载planstylebody
                }
            }
            else {
                // 获取用户最新保存的计划 初始化信息用
                Plan oldPlan = planManager.getPersonalLastDatePlan(userId, request.getParameter("type"));
                
                if(oldPlan != null && !oldPlan.isNew()) {
                	HttpSession session = request.getSession();
                	session.removeAttribute("initModel");
                	session.setAttribute("initModel", oldPlan);
	                //yangzd  显示上一次计划的主送，抄送，告知
	                this.copyRelevantUsers(plan, oldPlan);
                } else {
                	HttpSession session = request.getSession();
                    Plan initModel = (Plan)session.getAttribute("initModel");
                    if(initModel != null && initModel.getType().equals(request.getParameter("type"))) {
    	                this.copyRelevantUsers(plan, initModel);
                    }
                }
                
                plan.setRefDepartmentId(curUserDepartmentId);
                plan.setId(null);
                plan.setTitle(null);
                plan.setType(planType);
                plan.setPlanBody(planBody);
                plan.setPlanReply(null);
                plan.setPlanSummary(null);
                plan.setCreateUserId(userId);
                
                // 时间管理-新建
        		Date date = new Date();
        		String timeS = request.getParameter("time");
        		if (Strings.isNotBlank(timeS)) {
        			date = Datetimes.parseDatetime(timeS);
        		}
                this.setStartAndEndTimeAccordingType(plan, date);
                // 测试这个planStyle是否还存在 第二步：
                mav.addObject("isPlanStyleNull", "false");
            }

            if (Strings.isNotBlank(projectId)) {
                if (Strings.isBlank(plan.getType()))
                    plan.setType(request.getParameter("type"));
                
                setPlanProjectInfo(Long.valueOf(projectId), plan);
            }

            // 如果抄送人列表中有发起人，则删除这条记录
            List<PlanRelevantUser> planCcLeaderUser = plan.getPlanCcLeaderUser();
            List<PlanRelevantUser> newCcUser = null;
            if (CollectionUtils.isNotEmpty(planCcLeaderUser)) {
                newCcUser = new ArrayList<PlanRelevantUser>();
                for (PlanRelevantUser ccUser : planCcLeaderUser) {
                    if (userId.equals(ccUser.getRefUserId()) && "Member".equals(ccUser.getTypeProperty()))
                        continue;
                    else
                        newCcUser.add(ccUser);
                }
                mav.addObject("ccUser", newCcUser);
            }

            // 初始化用户id
            mav.addObject("plan", plan);
            mav.addObject("body", plan.getPlanBody());
            mav.addObject("userid", userId);
            return mav;
        } catch (Exception e) {
            log.warn("警告：PlanController 中 initPlan 方法中的异常已经被防护处理", e);
        }

        // 初始化Plan
        Plan plan = new Plan();
        // 当改变计划格式时，除了正文，其他内容需要保存
        setPlanStyleParameters(request, plan); 
        mav.addObject("isPlanStyleNull", "true");
        if (plan.getRefDepartmentId() != null)
            mav.addObject("department", orgManager.getDepartmentById(plan.getRefDepartmentId()));
        
        if (plan.getPlanBody() == null)
            plan.setPlanBody(new PlanBody());
        
        plan.getPlanBody().setContent("");
        plan.getPlanBody().setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
        plan.getPlanBody().setCreateDate(new Date());

        if (Strings.isNotBlank(projectId)) {
            if (Strings.isBlank(plan.getType()))
                plan.setType(request.getParameter("type"));
            
            setPlanProjectInfo(Long.valueOf(projectId), plan);
        }
        // 初始化用户id
        mav.addObject("plan", plan);
        mav.addObject("body", plan.getPlanBody());
        mav.addObject("userid", userId);
        return mav;
    }

    /**
     * 将最新一次计划的相关人员设置到新建的计划中去，显示为"记忆功能"
     * @param plan
     * @param oldPlan
     */
	private void copyRelevantUsers(Plan plan, Plan oldPlan) {
		this.setProp(plan, oldPlan);
		Hibernate.initialize(oldPlan.getAllPlanRefUser());
		List<PlanRelevantUser> allPlanRefUser = oldPlan.getAllPlanRefUser();
		this.setRelevantUsers(plan, allPlanRefUser);
	}

    /**
     * 初始化部门空间计划添加
     */
    public ModelAndView initDeptAdd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("plan/deptspace/addPlan");
        String projectId = request.getParameter("projectId");
        Long curUserDepartmentId = null;
        String planDeptID = request.getParameter("planDeptId");
        String planType = request.getParameter("type");

        if (StringUtils.isNotBlank(planDeptID)) {
        	mav.addObject("isDeptPlan", true);
        	curUserDepartmentId = Long.valueOf(planDeptID);// 获取前台传过来的(允许)切换的部门ID
        	planType = PlanType.ANY_SCOPE_PLAN.getValue();
        } else {
        	mav.addObject("isDeptPlan", false);
        }

        // 获得详细样式
        List<MtContentTemplateCAP> list = mtContentTemplateManagerCAP.findTypeAll("2");// 计划格式--标志为1--------改成统一接口了
        List<ProjectSummary> projectList = projectManager.getProjectList();
        // 获得关联项目
        mav.addObject("planStyle", list);
        mav.addObject("project", projectList);
        
        Long userId = CurrentUser.get().getId();
        
        try {
            // 初始化Plan
            Plan plan = new Plan();
            PlanBody planBody = new PlanBody();
            // 加载样式
            Long styleId = RequestUtils.getLongParameter(request, "styleId");
			if (styleId != null) {
				setPlanStyleParameters(request, plan); // 当改变计划格式时，除了正文，其他内容需要保存
				if (plan.getRefDepartmentId() != null) {
					mav.addObject("department", orgManager.getDepartmentById(plan.getRefDepartmentId()));
				}
				if (styleId.longValue() == new Long(Constant.SELECT_NONE_FLAG).longValue()) {
					if (plan.getPlanBody() == null) {
						plan.setPlanBody(new PlanBody());
					}
					plan.getPlanBody().setContent("");
					plan.getPlanBody().setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
					plan.getPlanBody().setCreateDate(new Date());
				} else {
					loadStyle(request, plan); // 当有计划格式时，需加载planstylebody
				}
			} else {
                // 获取用户上次建的计划 初始化信息用
                Plan oldPlan = planManager.getPersonalLastDatePlan(userId, request.getParameter("type"));
                this.setProp(plan, oldPlan);
                // 过滤
                plan.setRefDepartmentId(curUserDepartmentId);
                plan.setId(null);
                plan.setStartTime(null);
                plan.setEndTime(null);
                plan.setTitle(null);
                plan.setType(planType);
                plan.setPlanBody(planBody);
                plan.setPlanReply(null);
                plan.setPlanSummary(null);
            }

            if (Strings.isNotBlank(projectId)) {
                if (Strings.isBlank(plan.getType()))
                    plan.setType(request.getParameter("type"));
                
                setPlanProjectInfo(Long.valueOf(projectId), plan);
            }
            // 初始化用户id
            mav.addObject("plan", plan);
            mav.addObject("body", plan.getPlanBody());
            mav.addObject("userid", userId);
            mav.addObject("isPlanStyleNull", "false");
            return mav;
        } catch (Exception e) {
            logger.warn("选择的部门计划格式在刚才被管理员删除了，已提示用户重新选择格式。");
            mav.addObject("isPlanStyleNull", "true");
        }

        // 初始化Plan
        Plan plan = new Plan();
        setPlanStyleParameters(request, plan); // 当改变计划格式时，除了正文，其他内容需要保存
        if (plan.getRefDepartmentId() != null)
            mav.addObject("department", orgManager.getDepartmentById(plan.getRefDepartmentId()));

        if (plan.getPlanBody() == null)
            plan.setPlanBody(new PlanBody());
        
        plan.getPlanBody().setContent("");
        plan.getPlanBody().setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
        plan.getPlanBody().setCreateDate(new Date());

        if (Strings.isNotBlank(projectId)) {
            if (Strings.isBlank(plan.getType()))
                plan.setType(request.getParameter("type"));
            
            setPlanProjectInfo(Long.valueOf(projectId), plan);
        }

        // 初始化用户id
        mav.addObject("plan", plan);
        mav.addObject("body", plan.getPlanBody());
        mav.addObject("userid", userId);
        return mav;

    }

    private void setProp(Plan newPlan, Plan oldPlan) {
        newPlan.setCreateUserId(oldPlan.getCreateUserId());
        newPlan.setPlanApprizeUser(oldPlan.getPlanApprizeUser());
        newPlan.setPlanCcLeaderUser(oldPlan.getPlanCcLeaderUser());
        newPlan.setPlanToLeaderUser(oldPlan.getPlanToLeaderUser());
    }

    /**
     * 初始化更新<br>
     * 根据计划会议日程设计文档V1.5,除总结了的计划不能修改外，其他情况都可以修改。
     */
    public ModelAndView initUpdatePlan(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String strId = request.getParameter("id");
        Long id = new Long(strId);
        // 判断是否为数字，是数字取Style，不是返回null
        if (NumberUtils.isNumber(strId)) {
            ModelAndView mav = new ModelAndView("plan/editPlan");
            Plan plan = planManager.getPlanByPk(id);
            // 原本是如果计划此时已经完成或者总结了，则提示用户，并禁止编辑。已经废弃。下面是新做法
            if (PublishStatus.SUMMARY.getValue().equals(plan.getPublishStatus()))
            { // TODO kuanghs plan 总结了的计划不能修改
                ModelAndView mav3 = new ModelAndView("plan/myPlanHomeEntry");
                mav3.addObject("planStatusNum", PublishStatus.SUMMARY.getValue());
                mav3.addObject("type", plan.getType());
                return mav3;
            }
            Hibernate.initialize(plan.getPlanBody());
            Hibernate.initialize(plan.getAllPlanRefUser());
            List<PlanRelevantUser> allPlanRefUser = plan.getAllPlanRefUser();
            this.setRelevantUsers(plan, allPlanRefUser);
            
            Long styleId = RequestUtils.getLongParameter(request, "styleId");
            if (styleId != null)
            {
                // 选择无时的加载情况
                if (styleId.longValue() == new Long(Constant.SELECT_NONE_FLAG).longValue())
                {
                    if (plan.getPlanBody() == null)
                    {
                        plan.setPlanBody(new PlanBody());
                    }
                    plan.getPlanBody().setContent("");
                    plan.getPlanBody().setBodyType(
                            com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
                    plan.getPlanBody().setCreateDate(new Date());
                    setPlanStyleParameters(request, plan); // 当改变计划格式时，除了正文，其他内容需要保存
                }
                else
                {
                    // 加载样式
                    // TODO: stlyid need set
                    loadStyle(request, plan);
                }
            }
            // by Yongzhang 20080922修改,显示附件
            List<PlanReply> replyList = planManager.getReplyByPlanid(plan.getId());

            List<Attachment> attachmentAll = attachmentManager.getByReference(plan.getId());
            List<Attachment> attachmentPlan = new ArrayList<Attachment>();
			if (replyList != null && replyList.size() > 0) {
				// 把附件中的附件过滤掉（要过滤的附件是用户回复计划时添加的）
				for (int y = 0; y < attachmentAll.size(); y++) {
					boolean flag = false;
					for (int i = 0; i < replyList.size(); i++) {
						PlanReply planReply = (PlanReply) replyList.get(i);

						if (planReply.getRefPlanReplyId().toString().equals(attachmentAll.get(y).getSubReference().toString())) {
							flag = true;
							break;
						}

					}
					if (!flag) {
						attachmentPlan.add(attachmentAll.get(y));
					}
				}
			} else {
				attachmentPlan = attachmentAll;
			}
            mav.addObject("attachments", attachmentPlan);
            mav.addObject("plan", plan);
            mav.addObject("body", plan.getPlanBody());

            mav.addObject("userid", CurrentUser.get().getId());

            // 获得详细样式
            List<MtContentTemplateCAP> list = mtContentTemplateManagerCAP.findTypeAll("2");// 计划格式--标志为1--------改成统一接口了
            mav.addObject("planStyle", list);

            // 获得关联项目
            List<ProjectSummary> projectList = projectManager.getProjectList();
            mav.addObject("project", projectList);

            // 获得关联部门
            Long departmentId = plan.getRefDepartmentId()!=null?plan.getRefDepartmentId():0L;
            V3xOrgDepartment department = orgManager.getDepartmentById(departmentId);
            mav.addObject("department", department);

            return mav;
        }
        else
            return null;
    }

    /**
     * 初始化更新部门计划
     */
	public ModelAndView initUpdateDeptPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 得到URL传送的ID
		String strId = request.getParameter("id");
		Long id = new Long(strId);
		// 判断是否为数字，是数字取Style，不是返回null
		if (NumberUtils.isNumber(strId)) {
			ModelAndView mav = new ModelAndView("plan/deptspace/editPlan");
			Plan plan = planManager.getPlanByPk(id);
			Hibernate.initialize(plan.getPlanBody());
			Hibernate.initialize(plan.getAllPlanRefUser());
			List<PlanRelevantUser> allPlanRefUser = plan.getAllPlanRefUser();
			this.setRelevantUsers(plan, allPlanRefUser);

			Long styleId = RequestUtils.getLongParameter(request, "styleId");

			if (styleId != null) {
				// 选择无时的加载情况
				if (styleId.longValue() == new Long(Constant.SELECT_NONE_FLAG).longValue()) {
					if (plan.getPlanBody() == null) {
						plan.setPlanBody(new PlanBody());
					}
					plan.getPlanBody().setContent("");
					plan.getPlanBody().setBodyType(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
					plan.getPlanBody().setCreateDate(new Date());
					setPlanStyleParameters(request, plan); // 当改变计划格式时，除了正文，其他内容需要保存
				} else {
					// 加载样式
					// TODO: stlyid need set
					loadStyle(request, plan);
				}
			}

			mav.addObject("plan", plan);
			mav.addObject("body", plan.getPlanBody());

			// 获得详细样式
			List<MtContentTemplateCAP> list = mtContentTemplateManagerCAP.findTypeAll("2");// 计划格式--标志为1--------改成统一接口了
			mav.addObject("planStyle", list);

			// 获得关联项目
			List<ProjectSummary> projectList = projectManager.getProjectList();
			mav.addObject("project", projectList);

			mav.addObject("userid", CurrentUser.get().getId());
			// 获得关联部门
			mav.addObject("department", orgManager.getDepartmentById(plan.getRefDepartmentId()));
			mav.addObject("attachments", attachmentManager.getByReference(plan.getId()));
			return mav;
		} else
			return null;
	}

    /**
     * 初始化详细信息
     */
	public ModelAndView initMessageViewer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String strId = request.getParameter("id");
		// 判断是否为数字，是数字取Style，不是返回null
		if (NumberUtils.isNumber(strId)) {
			//SECURITY 访问安全检查
			Long planId = Long.parseLong(strId);
        	if(!SecurityCheck.isLicit(request, response, ApplicationCategoryEnum.plan, CurrentUser.get(), planId, null, null)){
        		return null;
        	}
			ModelAndView mav = new ModelAndView("plan/messageViewer");
			try {
				Plan plan = planManager.getPlanByPk(planId);
				if(plan != null){
					mav.addObject("plan", plan);
					mav.addObject("attachments", attachmentManager.getByReference(plan.getId()));
					mav.addObject("bodyType", plan.getPlanBody().getBodyType());
				} else {
					super.rendJavaScript(response, "alert('" + Constant.getPlanI18NValue("plan.canceled.label") + "');window.close();");
					return null;
				}
			} catch (Exception e) {
				logger.error("查看计划异常", e);
			}
			return mav;
		} else{
			return null;
		}
	}
	
	/**
     * 初始化计划属性信息
     */
	public ModelAndView initPlanProperties(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("plan/planProperties");
		Plan plan = planManager.getPlanByPk(NumberUtils.toLong(request.getParameter("id")));
		Hibernate.initialize(plan.getAllPlanRefUser());
		List<PlanRelevantUser> allPlanRefUser = plan.getAllPlanRefUser();
		setRelevantUsers(plan, allPlanRefUser);
		mav.addObject("plan", plan);
		if (plan.getRefProjectId() != null && plan.getRefProjectId().intValue() != -1) {
			try {
				ProjectSummary project = projectManager.getProject(plan.getRefProjectId());
				mav.addObject("projectName", project.getProjectName());
			} catch (Exception e) {
				logger.error("没有找到项目信息", e);
			}
		}
		return mav;
	}
    
    /**
     * 设置计划的主送、抄送、告知人员并过滤掉离职人员
     * @param plan				计划
     * @param allPlanRefUser	计划对应的全部相关人员，由hibernate按照一对多关系获取时，已经按类型、排序号升序排列<br>
     * 							如需显式排序，可以使用<tt>Collections.sort(list)</tt>，PlanRelevantUser实现了排序接口<br>
     * @see PlanRelevantUser#compareTo(PlanRelevantUser)
     */
	private void setRelevantUsers(Plan plan, List<PlanRelevantUser> allPlanRefUser) {
		List<PlanRelevantUser> toLeaderUser = new ArrayList<PlanRelevantUser>();
		List<PlanRelevantUser> toCCUser = new ArrayList<PlanRelevantUser>();
		List<PlanRelevantUser> toAppUser = new ArrayList<PlanRelevantUser>();
		
		if(CollectionUtils.isNotEmpty(allPlanRefUser)) {
		    for (PlanRelevantUser refUser : allPlanRefUser) {
		        if (PlanRelevantUser.UserType.Leader.getValue().equals(refUser.getType()) && isRelevantUserValid(refUser))
		            toLeaderUser.add(refUser);
		        else if (PlanRelevantUser.UserType.Copy_To.getValue().equals(refUser.getType()) && isRelevantUserValid(refUser))
		            toCCUser.add(refUser);
		        else if (PlanRelevantUser.UserType.Apprize.getValue().equals(refUser.getType()) && isRelevantUserValid(refUser))
		            toAppUser.add(refUser);
		    }
		}
		plan.setPlanToLeaderUser(toLeaderUser);
		plan.setPlanCcLeaderUser(toCCUser);
		plan.setPlanApprizeUser(toAppUser);
	}
	
	private boolean isRelevantUserValid(PlanRelevantUser refUser) {
		V3xOrgEntity ent = null;
		try {
			ent = this.orgManager.getEntity(refUser.getTypeProperty() + "|" + refUser.getRefUserId());
		} catch (BusinessException e) {
			log.error("获取计划对应关联人员时出现异常：", e);
		}
		return ent != null && ent.isValid();
		
	}

    /**
     * 根据用户列出计划
     */
    public ModelAndView listPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String calSelectedYear = request.getParameter("calSelectedYear");
		String calSelectedMonth = request.getParameter("calSelectedMonth");
		String calSelectedDate = request.getParameter("calSelectedDate");
		
		String type = RequestUtils.getStringParameter(request, "type");
		int year = 0;
		int month = 0;
		int date = 1;

		if (calSelectedYear != null && NumberUtils.isNumber(calSelectedYear)
				&& !calSelectedYear.equals("-1")) {
			year = Integer.valueOf(calSelectedYear);
		}
		if (calSelectedMonth != null && NumberUtils.isNumber(calSelectedMonth)
				&& !calSelectedMonth.equals("-1")) {
			month = Integer.valueOf(calSelectedMonth);
		}
		if (calSelectedDate != null && NumberUtils.isNumber(calSelectedDate)
				&& !calSelectedDate.equals("-1")) {
			date = Integer.valueOf(calSelectedDate);
		}
		if (!type.equals(PlanType.DAY_PLAN.getValue())) {// 不是日计划时,不按照日期查询
			date = 1;
		}
		java.util.Calendar calendar = null;
		if (year == 0) {
			calendar = new GregorianCalendar();
		} else {
			calendar = new GregorianCalendar(year, month - 1, date);
		}

		Date date1 = null;
		Date date2 = null;
		ModelAndView mav = new ModelAndView("plan/listPlan");
		// 判断是否是某一天计划
		if (type.equals(PlanType.DAY_PLAN.getValue())) {// 日计划
			date1 = calendar.getTime();
			date2 = calendar.getTime();
			mav = new ModelAndView("plan/listDayPlan");
		} 
        else if(type.equals(PlanType.WEEK_PLAN.getValue()))//周计划
        {
            date1 = Datetimes.getFirstDayInMonth(calendar.getTime());
            date2 = Datetimes.getLastDayInMonth(calendar.getTime());
        }
        else if(type.equals(PlanType.MONTH_PLAN.getValue()))//月计划
        { 
            date1 = Datetimes.getFirstDayInMonth(calendar.getTime());
			date2 = Datetimes.getLastDayInMonth(calendar.getTime());
        }
        else { //任意期计划
			date1 = calendar.getTime();
			calendar.set(year, month, date);
			date2 = calendar.getTime();
		}
		List<Plan> list = planManager.getMyPlanByTypeAndDateForPage(CurrentUser.get().getId(), type, date1, date2);
		mav.addObject("planList", list);
		mav.addObject("from", request.getParameter("from")) ;
		mav.addObject("id", request.getParameter("id")) ;
		
		return mav;
	}

    /**
     * 根据授权用户列出计划
     */
    public ModelAndView listPlanAuthed(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plan/listPlanAuthed");

        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        String type = RequestUtils.getStringParameter(request, "type");
        Long authedUserId = new Long(request.getParameter("authedUserId"));
        int year = 0;
        int month = 0;
        int date = 1;

        if (calSelectedYear != null && NumberUtils.isNumber(calSelectedYear)
                && !calSelectedYear.equals("-1"))
        {
            year = new Integer(calSelectedYear).intValue();
        }
        if (calSelectedMonth != null && NumberUtils.isNumber(calSelectedMonth)
                && !calSelectedMonth.equals("-1"))
        {
            month = new Integer(calSelectedMonth).intValue();
        }
        if (calSelectedDate != null && NumberUtils.isNumber(calSelectedDate)
                && !calSelectedDate.equals("-1"))
        {
            date = new Integer(calSelectedDate).intValue();
        }
        if (!type.equals(PlanType.DAY_PLAN.getValue()))
        {// 不是日计划时,不按照日期查询
            date = 1;
        }
        java.util.Calendar calendar = null;
        if (year == 0)
        {
            calendar = new GregorianCalendar();
        }
        else
        {
            calendar = new GregorianCalendar(year, month - 1, date);
        }

        Date date1 = null;
        Date date2 = null;
        // 判断是否是某一天计划
        if (type.equals(PlanType.DAY_PLAN.getValue()))
        {// 日计划
            date1 = Datetimes.getFirstDayInWeek(calendar.getTime());
            date2 = Datetimes.getLastDayInWeek(calendar.getTime());
        }
        else
        { // 不是日计划
            date1 = calendar.getTime();
            calendar.set(year, month, date);
            date2 = calendar.getTime();
        }

        List<Plan> list = new ArrayList<Plan>();
        if (request.getParameter("planType").equals("myPlan"))
        {
            list = planManager.getIsDraftsmanPlanForPage(authedUserId, type, date1, date2);
        }
        else
        {
            list = planManager.getNotDraftsmanPlanForPage(authedUserId, type, date1, date2);
        }
        // 将其他单位信息显示
        list = formatPlanList(request, list);
        mav.addObject("planList", list);
        return mav;
    }

    /**
     * 根据用户列出计划
     */
    public ModelAndView listPlanMgr(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plan/listPlanMgr");

        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        String type = RequestUtils.getStringParameter(request, "type");

        // 用户类型
        String userType = request.getParameter("userType");

        // 查询其他用户的计划
        String strUserId = request.getParameter("userId");
        Long userId = null;
        if (strUserId != null && NumberUtils.isNumber(strUserId))
        {
            userId = new Long(strUserId);
        }
        if (userId == null)
        {
            userId = CurrentUser.get().getId();
        }

        int year = 0;
        int month = 0;
        int date = 1;

        if (calSelectedYear != null && NumberUtils.isNumber(calSelectedYear)
                && !calSelectedYear.equals("-1"))
        {
            year = new Integer(calSelectedYear).intValue();
        }
        if (calSelectedMonth != null && NumberUtils.isNumber(calSelectedMonth)
                && !calSelectedMonth.equals("-1"))
        {
            month = new Integer(calSelectedMonth).intValue();
        }
        if (calSelectedDate != null && NumberUtils.isNumber(calSelectedDate)
                && !calSelectedDate.equals("-1"))
        {
            date = new Integer(calSelectedDate).intValue();
        }
        if (!type.equals(PlanType.DAY_PLAN.getValue()))
        {// 不是日计划时,不按照日期查询
            date = 1;
        }
        java.util.Calendar calendar = null;
        if (year == 0)
        {
            calendar = new GregorianCalendar();
        }
        else
        {
            calendar = new GregorianCalendar(year, month - 1, date);
        }

        Date date1 = null;
        Date date2 = null;
        // 判断是否是某一天计划
        if (type.equals(PlanType.DAY_PLAN.getValue()))
        {// 日计划
			date1 = calendar.getTime();
			date2 = calendar.getTime();
            mav = new ModelAndView("plan/listDayPlanMgr");
        }
        else if(type.equals(PlanType.WEEK_PLAN.getValue()))
        {
            date1 = Datetimes.getFirstDayInWeek(calendar.getTime());
            date2 = Datetimes.getLastDayInMonth(calendar.getTime());
        }
        else if(type.equals(PlanType.MONTH_PLAN.getValue()))
        {
  
            date1 = Datetimes.getFirstDayInMonth(calendar.getTime());
			date2 = Datetimes.getLastDayInMonth(calendar.getTime());
        }
        else
        { // 不是日计划
            date1 = calendar.getTime();
            //calendar.set(year, month, date);
            date2 = Datetimes.getLastDayInMonth(calendar.getTime());
        }
        // 返回的计划list
        List<Plan> list = null;
        if (userType == null)
        {// 全部
            list = planManager.getNotDraftsmanPlanForPage(userId, type, date1, date2);
        }
        else
        {
            list = planManager.getPlanForPage(userId, userType, type, date1, date2);
        }
        // 将其他单位信息显示
        list = formatPlanList(request, list);

        mav.addObject("planList", list);
        return mav;
    }

    /**
     * 根据用户列出部门空间更多显示 3
     */
    public ModelAndView listPlanMore(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plan/listPlanMore");
        Date date1 = null;
        date1 = new java.sql.Timestamp(new java.util.Date().getTime());

        Long departmentId = Long.parseLong((String) request.getSession().getAttribute(
                "plan.planDeptId"));
        boolean notFromDeptSpaceMore = true;
        String fromMore = request.getParameter("from");
        if (fromMore != null && !fromMore.equals(""))
        {
            notFromDeptSpaceMore = false;
        }

        Long userId = CurrentUser.get().getId();
        List<Plan> list = null;

        list = planManager.getDeptMorePlanList(departmentId, userId, date1, notFromDeptSpaceMore);

        // 将其他单位信息显示
        list = formatPlanList(request, list);

        mav.addObject("planList", list);
        mav.addObject("from", fromMore);
        return mav;
    }
    public ModelAndView cancelPublish(HttpServletRequest request, HttpServletResponse response)
            throws Exception{
		String ids = request.getParameter("id");
		Set<Long> planIds = new HashSet<Long>() ;
		if(Strings.isNotBlank(ids)){
			for(String id : ids.split(",")){
				planIds.add(Long.parseLong(id));
			}
			planManager.cancelPublishPlan(planIds);
		}
		return new ModelAndView("plan/sPlanMore");
    }
    public ModelAndView listOtherUserPlan(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        ModelAndView mav = new ModelAndView("plan/listPlanMgr");

        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");

        // 查询其他用户的计划
        String strUserId = request.getParameter("userId");
        Long userId = null;
        if (strUserId != null && NumberUtils.isNumber(strUserId))
        {
            userId = new Long(strUserId);
        }
        if (userId == null)
        {
            userId = CurrentUser.get().getId();
        }

        int year = 0;
        int month = 0;
        int date = 1;

        if (calSelectedYear != null && NumberUtils.isNumber(calSelectedYear)
                && !calSelectedYear.equals("-1"))
        {
            year = new Integer(calSelectedYear).intValue();
        }
        if (calSelectedMonth != null && NumberUtils.isNumber(calSelectedMonth)
                && !calSelectedMonth.equals("-1"))
        {
            month = new Integer(calSelectedMonth).intValue();
        }
        if (calSelectedDate != null && NumberUtils.isNumber(calSelectedDate)
                && !calSelectedDate.equals("-1"))
        {
            date = new Integer(calSelectedDate).intValue();
        }
        java.util.Calendar calendar = null;
        if (year == 0)
        {
            calendar = new GregorianCalendar();
        }
        else
        {
            calendar = new GregorianCalendar(year, month - 1, date);
        }

        Date date1 = null;
        Date date2 = null;
        // 判断是否是某一天计划
        if (calSelectedDate != null && NumberUtils.isNumber(calSelectedDate)
                && !calSelectedDate.equals("-1"))
        {// 日计划
            date1 = Datetimes.getFirstDayInWeek(calendar.getTime());
            date2 = Datetimes.getLastDayInWeek(calendar.getTime());
        }
        else
        { // 不是日计划
            date1 = calendar.getTime();
            calendar.set(year, month, date);
            date2 = calendar.getTime();
        }

        List<Plan> list = planManager.getIsDraftsmanPlan(userId, date1, date2);
        mav.addObject("planList", list);
        return mav;
    }

    /**
     * 列出计划详细信息
     */
	public ModelAndView displayDetailPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String strId = request.getParameter("id");
        Boolean isFromDoc = RequestUtils.getBooleanParameter(request, "isFromDoc");
        User user = CurrentUser.get();

        // 判断是否为数字，是数字取Style，不是返回null
        if (NumberUtils.isNumber(strId)) {
            ModelAndView mav = new ModelAndView("plan/content");
            MultiMap planReplyMap = new MultiHashMap();
            Long planId = Long.parseLong(strId);
            Plan plan = planManager.getPlanLoadBodyAndReplyAndSummary(planId, planReplyMap);

            // 我总结的计划自己不可以恢复
            boolean isReplyByMe = false;
            if (plan.getCreateUserId() == user.getId())
                isReplyByMe = true;

            // 判断该用户是否有权限回复总结 是否为主送、抄送、告知
            boolean canReplySummary = false;
            Hibernate.initialize(plan.getAllPlanRefUser());
            List<PlanRelevantUser> allPlanRefUser = plan.getAllPlanRefUser();
            for (PlanRelevantUser ru : allPlanRefUser) {
                if (ru.getRefUserId() == user.getId()) {
                    canReplySummary = true;
                    break;
                }
            }

            mav.addObject("canReplySummary", canReplySummary);
            mav.addObject("isReplyByMe", isReplyByMe);
            mav.addObject("plan", plan);
            mav.addObject("body", plan.getPlanBody());
            
            // 将计划回复排序
			List<PlanReply> replys = (List<PlanReply>)planReplyMap.get(Constant.FIRST_LEVEL_PLANREPLY);
			mav.addObject("reply", replys);
			mav.addObject("opinionSize", CollectionUtils.isEmpty(replys) ? 0 : replys.size());
			mav.addObject("planReplyMap", planReplyMap);
            mav.addObject("loginId", user.getId());
            mav.addObject("loginUserRole", this.planManager.getUserRole(planId, user.getId()));
            mav.addObject("isFromDoc", isFromDoc);
            return mav;
        }
        else
            return null;
    }

    /**
     * 初始化详细信息
     */
    public ModelAndView initDetailHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("plan/home");
        try
        {
        	Long userId = CurrentUser.get().getId();
            Long id = RequestUtils.getLongParameter(request, "id");
            Plan plan = planManager.getPlanByPk(id);
            //yangzd 当删除计划，后查看计划回复不断弹出您已经无权查看了
            if(plan==null) {
            	super.rendJavaScript(response, "alert('" + Constant.getPlanI18NValue("plan.deleted.label") + "');" +
            								   "if(window.dialogArguments) {window.close();} else {parent.parent.location.reload(true);}");
            	return null;
            }
            // 是否还有权查看计划
            int[] counts = planManager.countPlanUser(id, userId);
            boolean isAllowReply = (counts[0] > 0 || counts[1] > 0);
            boolean isAllowView = (isAllowReply || counts[2] > 0 || counts[3] > 0);

            if (!isAllowView){
            	super.rendJavaScript(response, "alert('" + Constant.getPlanI18NValue("plan.noright2view.label") + "');" +
                    						   "if(window.dialogArguments) {window.close();} else {parent.parent.location.reload(true);}");
                return null;
            }
            boolean isFromDoc = false;

            if (request.getParameter("editType").endsWith("doc"))
            {
                mav.addObject("allowReply", Boolean.FALSE);
                isFromDoc = true;
            }
            else if (PublishStatus.SUMMARY.getValue().equals(plan.getPublishStatus()))
            { // TODO kuanghs plan 已总结的计划不可以回复 但还可以总结
                mav.addObject("allowReply", Boolean.FALSE);
                mav.addObject("allowSummary", Boolean.TRUE);
            }
            else if (PublishStatus.DRAFT.getValue().equals(plan.getPublishStatus()))
            { // TODO kuanghs plan 草稿的计划不可以回复 但可以总结
                mav.addObject("allowReply", Boolean.FALSE);
                mav.addObject("allowSummary", Boolean.TRUE);
            }
            else if (PublishStatus.ISSUED.getValue().equals(plan.getPublishStatus()))
            { // TODO kuanghs plan 已发布的计划如果有权限可以回复 可以总结
                mav.addObject("allowReply", isAllowReply || plan.getCreateUserId().equals(userId));
                mav.addObject("allowSummary", Boolean.TRUE);
            }
            mav.addObject("isFromDoc", isFromDoc);
        } catch (Exception e) {
            super.rendJavaScript(response, "alert('" + Constant.getPlanI18NValue("plan.canceled.labe") + "');window.close();");
        }
        return mav;
    }

    /**
     * 初始化被授权用户详细信息
     */
    public ModelAndView initDetailHomeAuthed(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("plan/homeAuthed").addObject("isFromDoc", Boolean.TRUE).addObject("allowReply", Boolean.FALSE);
    }

    /**
     * 添加计划
     */
    public ModelAndView addPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Plan plan = new Plan();
        PlanBody planBody = new PlanBody();
        // 后加字段创建时间
        plan.setCreateTime(new Date());
        setPlanParameter(request, plan, planBody);
        plan.setIdIfNew();
        if (plan.getPublishStatus() == null)
            plan.setPublishStatus(PublishStatus.DRAFT.getValue());
        
        if (plan.getPlanStatus() == null)
            plan.setPlanStatus(PlanStatus.BEFOREBEGINNING.getValue());
        
        plan.setHasAttachments("true".equals(request.getParameter("isHasAtt")));

        planManager.addPlan(plan);
        planManager.addPlanBody(planBody);
        // 保存附件
        attachmentManager.create(ApplicationCategoryEnum.plan, plan.getId(), plan.getId(), request);

        // 发送消息
        User user = CurrentUser.get();
        if (plan.getPublishStatus().equals(PublishStatus.ISSUED.getValue())) {
            List<Long> receiverIds = this.getMsgReceiverIds(plan);
            userMessageManager.sendSystemMessage(MessageContent.get("plan.send",
                    plan.getTitle(), user.getName()).setBody(planBody.getContent(),
                    planBody.getBodyType(), planBody.getCreateDate()),
                    ApplicationCategoryEnum.plan, user.getId(), MessageReceiver
                            .get(new Long(ApplicationCategoryEnum.plan.getKey()), receiverIds,
                                    "message.link.plan.send", String.valueOf(plan.getId())), plan
                            .getType());
        }
        
        //将 add 写入操作日志
        appLogManager.insertLog(user, AppLogAction.Plan_New, user.getName(), plan.getTitle());
        
        //如果是项目计划,存入该项目下当前阶段
        if(plan.getRefProjectId() != null && plan.getRefProjectId() != -1){
        	ProjectSummary projectSummary = projectManager.getProject(plan.getRefProjectId());
        	if(projectSummary != null){
        		if(projectSummary.getPhaseId() != 1){
        			ProjectPhaseEvent projectPhaseEvent = new ProjectPhaseEvent(ApplicationCategoryEnum.plan.key(), plan.getId(), projectSummary.getPhaseId());
        			projectPhaseEventManager.save(projectPhaseEvent);
        		}
        	}
        }
        
        this.handleIndex(plan.getId(), planManager, PlanOperAction.Save, indexManager, updateIndexManager);

        if (plan.getType().equals("0")) {
            super.rendJavaScript(response, "top.contentFrame.topFrame.back();");
            return null;
        } else {
            return super.redirectModelAndView("/planSystemMgr.do?method=myPlanHome&type="+ plan.getType());
        }
    }

    /**
     * 添加部门计划
     */
    public ModelAndView addDeptPlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Plan plan = new Plan();
        PlanBody planBody = new PlanBody();
        plan.setCreateTime(new Date());
        setPlanParameter(request, plan, planBody);
        plan.setIdIfNew();
        plan.setHasAttachments("true".equals(request.getParameter("isHasAtt")));
        planManager.addPlan(plan);
        planManager.addPlanBody(planBody);
        // 保存附件
        attachmentManager.create(ApplicationCategoryEnum.plan, plan.getId(), plan.getId(), request);

        User user = CurrentUser.get();
        Long userId = user.getId();
        if (plan.getPublishStatus().equals(PublishStatus.ISSUED.getValue())) {
            List<Long> receiverIds = getMsgReceiverIds(plan);
            userMessageManager.sendSystemMessage(MessageContent.get("plan.send", plan.getTitle(), user.getName()), 
            		ApplicationCategoryEnum.plan, userId, MessageReceiver.get(plan.getId(), receiverIds, "message.link.plan.send",
                    String.valueOf(plan.getId())), plan.getType());
        }//保存为草稿待发，跳转到我的计划-任意期计划当中去
        else if(plan.getPublishStatus().equals(PublishStatus.DRAFT.getValue())) {
        	return super.redirectModelAndView("/planSystemMgr.do?method=myPlanHome&type=4");
        }
        this.handleIndex(plan.getId(), planManager, PlanOperAction.Save, indexManager, updateIndexManager);
        return super.redirectModelAndView("/planSystemMgr.do?method=planMoreHome&planDeptId=" + plan.getRefDepartmentId() + "&from=more");
        
    }
    
    /**
     * 将<b>主送、抄送</b>人员加入消息接受对象列表
     * @param plan 计划
     * @return 要发送消息的对象
     */
	private List<Long> getMsgReceiverIds(Plan plan) {
		List<Long> receiverIds = new ArrayList<Long>();
		List<PlanRelevantUser> allPru = plan.getAllPlanRefUser();
		if(CollectionUtils.isNotEmpty(allPru)) {
			for (PlanRelevantUser pru : allPru) {
			    if (pru.getType().equals("1") || pru.getType().equals("2")) {
			        receiverIds.add(new Long(pru.getRefUserId()));
			    }
			}
		}
		return receiverIds;
	}

    /**
     * 更新计划
     */
    public ModelAndView updatePlan(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Plan plan = planManager.getPlanByPk(RequestUtils.getLongParameter(request, "id"));

        String oldPublishStatus = plan.getPublishStatus();
        String oldPlanStatus = plan.getPlanStatus();

        // 是否发送更新计划得消息
        boolean isSendUpdateMessage = true;

        planManager.deletePlanRelevantUserByPlanId(plan.getId());

        // TODO kuanghs plan ，将状态复制过来
        plan.setPlanStatus(oldPlanStatus);
        plan.setPublishStatus(oldPublishStatus);

        Hibernate.initialize(plan.getPlanBody());
        PlanBody planBody = plan.getPlanBody();
        if (planBody == null)
            planBody = new PlanBody();
        
        setPlanParameter(request, plan, planBody);

        // 如果页面没有提交状态，则还用以前的状态值
        if (plan.getPublishStatus() == null)
        {
            plan.setPublishStatus(oldPublishStatus);
        }
        if (plan.getPlanStatus() == null)
        {
            plan.setPlanStatus(oldPlanStatus);
        }

        // TODO kuanghs plan 容错处理： 已发送-->草稿 不发送消息
        if (PublishStatus.ISSUED.getValue().equalsIgnoreCase(oldPublishStatus)
                && PublishStatus.DRAFT.getValue().equals(plan.getPublishStatus()))
        {
            plan.setPublishStatus(PublishStatus.ISSUED.getValue());
            isSendUpdateMessage = false;
        }

        // 保存附件
        Long planId = plan.getId();
        attachmentManager.update(ApplicationCategoryEnum.plan, planId, planId, request);
        plan.setHasAttachments("true".equals(request.getParameter("isHasAtt")));

        planManager.updatePlan(plan);
        planManager.updatePlanBody(planBody);

        // edited 2007-04-23
        // 发送消息
        // 如果计划状态由草稿变为发布，则发送发起计划的信息,如果状态始终是已发布的则发送更新计划的消息
        User user = CurrentUser.get();
        if (oldPublishStatus.equalsIgnoreCase(PublishStatus.DRAFT.getValue())
                && plan.getPublishStatus().equals(PublishStatus.ISSUED.getValue())) {
            List<Long> receiverIds = getMsgReceiverIds(plan);

            userMessageManager.sendSystemMessage(MessageContent.get("plan.send", plan.getTitle(),
                    user.getName()), ApplicationCategoryEnum.plan, user.getId(), MessageReceiver.get(new Long(
                    ApplicationCategoryEnum.plan.getKey()), receiverIds, "message.link.plan.send",
                    String.valueOf(plan.getId())), plan.getType());
        }
        else if (isSendUpdateMessage
                && oldPublishStatus.equalsIgnoreCase(PublishStatus.ISSUED.getValue())
                && plan.getPublishStatus().equals(PublishStatus.ISSUED.getValue()))
        {
            List<Long> receiverIds = this.getUserIdsByPlan(request, plan);
            userMessageManager.sendSystemMessage(MessageContent.get("plan.update", plan.getTitle(),
                    user.getName()), ApplicationCategoryEnum.plan, user.getId(), MessageReceiver.get(new Long(
                    ApplicationCategoryEnum.plan.getKey()), receiverIds, "message.link.plan.send",
                    String.valueOf(plan.getId())), plan.getType());
        }
        
       //将 更新操作 写入操作日志
        appLogManager.insertLog(user, AppLogAction.Plan_Update, user.getName(),plan.getTitle());
        
        this.handleIndex(plan.getId(), planManager, PlanOperAction.Update, indexManager, updateIndexManager);
       
        String deptId = request.getParameter("departmentId");
        if (Strings.isNotBlank(deptId))
            return super.redirectModelAndView("/plan.do?method=deptSpaceManageHome&departmentId=" + deptId);
        else
            return super.redirectModelAndView("/planSystemMgr.do?method=myPlanHome&type=" + plan.getType());
    }

    /**
     * 更新部门计划
     */
    public ModelAndView updateDeptPlan(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        Plan plan = planManager.getPlanByPk(RequestUtils.getLongParameter(request, "id"));

        String oldPublishStatus = plan.getPublishStatus();
        String oldPlanStatus = plan.getPlanStatus();

        // 是否发送更新计划得消息
        boolean isSendUpdateMessage = true;

        planManager.deletePlanRelevantUserByPlanId(plan.getId());

        // TODO kuanghs plan ，将状态复制过来
        plan.setPlanStatus(oldPlanStatus);
        plan.setPublishStatus(oldPublishStatus);

        Hibernate.initialize(plan.getPlanBody());
        PlanBody planBody = plan.getPlanBody();
        if (planBody == null)
        {
            planBody = new PlanBody();
        }
        setPlanParameter(request, plan, planBody);

        // 如果页面没有提交状态，则还用以前的状态值
        if (plan.getPublishStatus() == null)
        {
            plan.setPublishStatus(oldPublishStatus);
        }
        if (plan.getPlanStatus() == null)
        {
            plan.setPlanStatus(oldPlanStatus);
        }

        // TODO kuanghs plan 容错处理： 已发送-->草稿 不发送消息
        if (oldPublishStatus.equalsIgnoreCase(PublishStatus.ISSUED.getValue())
                && plan.getPublishStatus().equals(PublishStatus.DRAFT.getValue()))
        {
            plan.setPublishStatus(PublishStatus.ISSUED.getValue());
            isSendUpdateMessage = false;
        }

        // 保存附件
        attachmentManager.update(ApplicationCategoryEnum.plan, plan.getId(), plan.getId(), request);
        plan.setHasAttachments("true".equals(request.getParameter("isHasAtt")));

        planManager.updatePlan(plan);
        planManager.updatePlanBody(planBody);

        // edited 2007-04-23
        // 发送消息
        // 如果计划状态由草稿变为发布，则发送发起计划的信息,如果状态始终是已发布的则发送更新计划的消息
        
        User user = CurrentUser.get();
        Long userId = user.getId();
        String userName = user.getName();
        
        if (oldPublishStatus.equalsIgnoreCase(PublishStatus.DRAFT.getValue())
                && plan.getPublishStatus().equals(PublishStatus.ISSUED.getValue()))
        {
            List<Long> receiverIds = getMsgReceiverIds(plan);
            userMessageManager.sendSystemMessage(MessageContent.get("plan.send", plan.getTitle(),
                    userName), ApplicationCategoryEnum.plan, userId, MessageReceiver.get(new Long(
                    ApplicationCategoryEnum.plan.getKey()), receiverIds, "message.link.plan.send",
                    String.valueOf(plan.getId())), plan.getType());
        }
        else if (isSendUpdateMessage
                && oldPublishStatus.equalsIgnoreCase(PublishStatus.ISSUED.getValue())
                && plan.getPublishStatus().equals(PublishStatus.ISSUED.getValue()))
        {
            List<Long> receiverIds = this.getUserIdsByPlan(request, plan);
            userMessageManager.sendSystemMessage(MessageContent.get("plan.update", plan.getTitle(),
                    userName), ApplicationCategoryEnum.plan, userId, MessageReceiver.get(new Long(
                    ApplicationCategoryEnum.plan.getKey()), receiverIds, "message.link.plan.send",
                    String.valueOf(plan.getId())), plan.getType());
        }
        this.handleIndex(plan.getId(), planManager, PlanOperAction.Update, indexManager, updateIndexManager);
        return super.redirectModelAndView("/plan.do?method=deptSpaceManageHome&departmentId="
                + plan.getRefDepartmentId());
    }

    /**
     * 删除计划
     */
    public ModelAndView deletePlan(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String[] strIds = request.getParameterValues("id");
        User user = CurrentUser.get();
        Long userId = user.getId();
        String userName = user.getName();
        // 发送消息
        for (int i = 0; i < strIds.length; i++)
        {
            Plan plan = planManager.getPlanByPk(new Long(strIds[i]));
            if (plan!=null && plan.getPublishStatus().equals(PublishStatus.ISSUED.getValue()))
            {
                // TODO
                List<Long> receiverIds = this.getUserIdsByPlan(request, plan);
                userMessageManager.sendSystemMessage(MessageContent.get("plan.del",
                        plan.getTitle(), userName),
                        ApplicationCategoryEnum.plan, userId, MessageReceiver
                                .get(new Long(ApplicationCategoryEnum.plan.getKey()), receiverIds),
                        plan.getType());
            }

            // 删除
            if (NumberUtils.isNumber(strIds[i]))
            {
                Long planId = new Long(strIds[i]);
                planManager.deletePlanReplyByPlanId(planId);
                planManager.deletePlanSummaryByPlanId(planId);
                planManager.deletePlanRelevantUserByPlanId(planId);
                planManager.deletePlanBodyByPlanId(planId);
                planManager.deletePlan(planId);
                
                //将 删除操作 写出操作日志
                appLogManager.insertLog(user, AppLogAction.Plan_Delete, user.getName(),plan.getTitle());
                //删除全文检索
                this.handleIndex(planId, planManager, PlanOperAction.Delete, indexManager, updateIndexManager);
            }
        }
        
        InternalResourceView ss = null;

        
        if (request.getParameter("departmentId") != null
                && !request.getParameter("departmentId").equals(""))
        {
            ss = new InternalResourceView("/plan.do?method=deptSpaceManageHome&departmentId="
                    + request.getParameter("departmentId"));
        }
        else
        {
            ss = new InternalResourceView("/plan.do?method=initPlanListHome&type="
                    + request.getParameter("type"));
        }
        ss.render(null, request, response);
        return null;
    }

    /**
     * 初始化计划总结
     */
    public ModelAndView initPlanSummary(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("plan/editPlanState");
    	Plan plan = planManager.getPlanByPk(NumberUtils.toLong(request.getParameter("id")));
		if (plan == null) {
			return null;
		}
		Hibernate.initialize(plan.getAllPlanRefUser());
		List<PlanRelevantUser> allPlanRefUser = plan.getAllPlanRefUser();
		setRelevantUsers(plan, allPlanRefUser);
		mav.addObject("plan", plan);
		if (plan.getRefProjectId() != null && plan.getRefProjectId().intValue() != -1) {
			try {
				ProjectSummary project = projectManager.getProject(plan.getRefProjectId());
				mav.addObject("projectName", project.getProjectName());
			} catch (Exception e) {
					logger.error("没有找到项目信息", e);
			}
		}
		mav.addObject("planRatio", plan.getFinishRatio());
		mav.addObject("planState", plan.getPlanStatus());
		mav.addObject("canSummary", plan.getStartTime().before(new Date(System.currentTimeMillis())));
        return mav;
    }

    public ModelAndView initPlanListHome(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        if (calSelectedYear == null || calSelectedYear.equals("") || calSelectedYear.equals("-1"))
        {
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
            calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
            calSelectedDate = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        }
        ModelAndView mav = new ModelAndView("plan/listHome");
        mav.addObject("calSelectedYear", calSelectedYear);
        mav.addObject("calSelectedMonth", calSelectedMonth);
        mav.addObject("calSelectedDate", calSelectedDate);
        mav.addObject("from", request.getParameter("from")) ;
        mav.addObject("id", request.getParameter("id")) ;
        return mav;
    }

    public ModelAndView initAuthedPlanListHome(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        Long authedUserId = new Long(request.getParameter("authedUserId"));
        String inPlanType = request.getParameter("planType");
        if (calSelectedYear == null || calSelectedYear.equals("") || calSelectedYear.equals("-1"))
        {
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
            calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
            calSelectedDate = String.valueOf(cal.get(Calendar.DAY_OF_MONTH) + 1);
        }
        ModelAndView mav = new ModelAndView("plan/listAuthed");
        mav.addObject("calSelectedYear", calSelectedYear);
        mav.addObject("calSelectedMonth", calSelectedMonth);
        mav.addObject("calSelectedDate", calSelectedDate);
        mav.addObject("authedUserId", authedUserId);
        mav.addObject("planType", inPlanType);
        return mav;
    }

    public ModelAndView initPlanToolBar(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        return new ModelAndView("plan/listToolBar");
    }

    public ModelAndView initPlanToolBarAuthed(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        return new ModelAndView("plan/listToolBarAuthed");
    }

    public ModelAndView initPlanCalendar(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        return new ModelAndView("plan/calendarFrame");
    }

    public ModelAndView initPlanListHomeMgr(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {

        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        if (calSelectedYear == null || calSelectedYear.equals("") || calSelectedYear.equals("-1"))
        {
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
            calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
            calSelectedDate = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        }
        ModelAndView mav = new ModelAndView("plan/listHomeMgr");
        mav.addObject("calSelectedYear", calSelectedYear);
        mav.addObject("calSelectedMonth", calSelectedMonth);
        mav.addObject("calSelectedDate", calSelectedDate);
        return mav;
    }

    // 部门更多显示 3
    public ModelAndView initPlanListHomeMore(HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {

        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        if (calSelectedYear == null || calSelectedYear.equals("") || calSelectedYear.equals("-1"))
        {
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
            calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
            calSelectedDate = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        }
        ModelAndView mav = new ModelAndView("plan/listHomeMore");
        mav.addObject("calSelectedYear", calSelectedYear);
        mav.addObject("calSelectedMonth", calSelectedMonth);
        mav.addObject("calSelectedDate", calSelectedDate);
        return mav;
    }

    public ModelAndView initPlanToolBarMgr(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        return new ModelAndView("plan/listToolBarMgr");
    }

    public ModelAndView initPlanCalendarMgr(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        return new ModelAndView("plan/calendarFrameMgr");
    }

    /**
     * 保存计划状态
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView savePlanStatus(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {

        Plan plan = planManager.getPlanByPk(new Long(request.getParameter("planId")));

        // 如果计划已经删除
        if (plan == null)
        {
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert(v3x.getMessage('PLANLang.plan_has_delete'));");
            out.println("</script>");
            return super.refreshWorkspace();
        }

        // 如果publishStatus没有初始化，初始化为1 即 草稿
        if (plan.getPublishStatus() == null)
        {
            plan.setPublishStatus(PublishStatus.DRAFT.getValue());
        }

        // 如果planStatus没有初始化，初始化为1 即 未开始
        if (plan.getPlanStatus() == null)
        {
            plan.setPlanStatus(PlanStatus.BEFOREBEGINNING.getValue());
        }

        // 如果计划已经总结 ： 提示 “已总结”的事件不能调整状态和完成率！
        // 上层页面已经加了入口的防护，但从安全考虑，还是加了一下防护。
//        if (PublishStatus.SUMMARY.getValue().equals(plan.getPublishStatus()))
//        {
//            PrintWriter out = response.getWriter();
//            out.println("<script>");
//            out.println("alert(v3x.getMessage('PLANLang.plan_has_summary_cannot_modifyStatus'));");
//            out.println("</script>");
//            return super.refreshWorkspace();
//        }

        // Float rate = new Float(request.getParameter("rate"));
        Float rate = RequestUtils.getFloatParameter(request, "rate");
        String planStatus = request.getParameter("planStatus");

        //  不论发布状态如何，若完成率为100，则计划状态只能为“已完成”
        // by Yongzhang 2008-9-18
        if (rate == 100)
        {
            plan.setPlanStatus(PlanStatus.FINISHED.getValue());
        }
        else
        {
            plan.setPlanStatus(planStatus);
        }
        // 新设计约束如下：
        //  不论发布状态如何，若计划状态为“未开始”，则完成率只能为 0
        if (PlanStatus.BEFOREBEGINNING.getValue().equals(plan.getPlanStatus()))
        {
            rate = 0f;
        }
        if (planStatus.equals(PlanStatus.FINISHED.getValue()))
        {
            rate = 100f;
        }

        plan.setFinishRatio(rate);

        planManager.updatePlan(plan);

        ModelAndView mav = new ModelAndView("plan/savePlanSummaryJumpPage");
        mav.addObject("plan", plan);

        String calSelectedYear = "";
        String calSelectedMonth = "";
        String calSelectedDate = "";
        if (calSelectedYear == null || calSelectedYear.equals("") || calSelectedYear.equals("-1"))
        {
            Calendar cal = new GregorianCalendar();
            cal.setTime(plan.getStartTime());
            calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
            calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
            calSelectedDate = String.valueOf(cal.get(Calendar.DAY_OF_MONTH) + 1);
        }
        mav.addObject("calSelectedYear", calSelectedYear);
        mav.addObject("calSelectedMonth", calSelectedMonth);
        mav.addObject("calSelectedDate", calSelectedDate);

        // 发送消息
        User user = CurrentUser.get();
        Long userId = user.getId();
        String userName = user.getName();
        
        List<Long> receiverIds = new ArrayList<Long>();
        List<PlanRelevantUser> allPlanRefUser = plan.getAllPlanRefUser();
        for (PlanRelevantUser refUser : allPlanRefUser)
        {
            if (refUser.getType().equals("1"))
            {
                receiverIds.add(refUser.getRefUserId());
            }
        }
        if (!PublishStatus.DRAFT.getValue().equals(plan.getPublishStatus()))
        { // 如果计划不是草稿状态，则发出消息
            userMessageManager.sendSystemMessage(MessageContent.get("plan.updateplanstatus", plan
                    .getTitle(), userName), ApplicationCategoryEnum.plan,
                    userId, MessageReceiver.get(new Long(
                            ApplicationCategoryEnum.plan.getKey()), receiverIds,
                            "message.link.plan.summary", String.valueOf(plan.getId())), plan
                            .getType());
        }
        this.handleIndex(plan.getId(), planManager, PlanOperAction.Update, indexManager, updateIndexManager);
        return super.refreshWindow("parent");
    }

    /**
     * 保存计划总结
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView savePlanSummary(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        Plan plan = planManager.getPlanByPk(new Long(request.getParameter("planId")));
        User user = CurrentUser.get();
        Long userId = user.getId();
        String userName = user.getName();
        
        PlanSummary planSummary = new PlanSummary();
        // planSummary.setFinishRatio(new Float(request.getParameter("rate")));
        planSummary.setCreateTime(new Date());
        planSummary.setPlan(plan);
        planSummary.setText(request.getParameter("text"));

        planSummary.setRefUserId(userId);
        planSummary.setRefUserName(userName);
        planManager.addPlanSummary(planSummary);
        // 保存附件
        attachmentManager.create(ApplicationCategoryEnum.plan, plan.getId(), planSummary.getId(),
                request);

        // plan.setFinishRatio(planSummary.getFinishRatio());
        /*
         * if (planSummary.getFinishRatio().floatValue() >= 100) {
         * plan.setStatus(PlanStatus.FINISHED.getValue()); }else{
         * plan.setStatus(PlanStatus.ONGOING.getValue()); }
         */
        // 计划总结状态设置为已总结
        plan.setPublishStatus(PublishStatus.SUMMARY.getValue());
        planManager.updatePlan(plan);

        ModelAndView mav = new ModelAndView("plan/savePlanSummaryJumpPage");
        mav.addObject("plan", plan);

        String calSelectedYear = "";
        String calSelectedMonth = "";
        String calSelectedDate = "";
        if (calSelectedYear == null || calSelectedYear.equals("") || calSelectedYear.equals("-1"))
        {
            Calendar cal = new GregorianCalendar();
            cal.setTime(plan.getStartTime());
            calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
            calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
            calSelectedDate = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        }
        mav.addObject("calSelectedYear", calSelectedYear);
        mav.addObject("calSelectedMonth", calSelectedMonth);
        mav.addObject("calSelectedDate", calSelectedDate);

        // edited 2007-04-23
        // 发送消息
        List<Long> receiverIds = new ArrayList<Long>();
        Hibernate.initialize(plan.getAllPlanRefUser());
        List<PlanRelevantUser> allPlanRefUser = plan.getAllPlanRefUser();
        for (PlanRelevantUser refUser : allPlanRefUser)
        {
            if (refUser.getType().equals("1"))
            {
                receiverIds.add(refUser.getRefUserId());
            }
        }
        if (!PublishStatus.DRAFT.getValue().equals(plan.getPublishStatus()))
        { // 如果计划不是草稿状态，则发出消息
            userMessageManager.sendSystemMessage(MessageContent.get("plan.summary",
                    plan.getTitle(), userName), ApplicationCategoryEnum.plan,
                    userId, MessageReceiver.get(new Long(
                            ApplicationCategoryEnum.plan.getKey()), receiverIds,
                            "message.link.plan.summary", String.valueOf(plan.getId())), plan
                            .getType());
        }
        //更新全文检索
        this.handleIndex(plan.getId(), planManager, PlanOperAction.Update, indexManager, updateIndexManager);
        return mav;
    }
    
    /**
     * 初始化计划回复
     */
    public ModelAndView initPlanReply(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView mav = new ModelAndView("plan/editPlanReply");
    	Plan plan = planManager.getPlanByPk(NumberUtils.toLong(request.getParameter("id")));
		if (plan == null) {
			return null;
		}
		Hibernate.initialize(plan.getAllPlanRefUser());
		List<PlanRelevantUser> allPlanRefUser = plan.getAllPlanRefUser();
		setRelevantUsers(plan, allPlanRefUser);
		mav.addObject("plan", plan);
		if (plan.getRefProjectId() != null && plan.getRefProjectId().intValue() != -1) {
			try {
				ProjectSummary project = projectManager.getProject(plan.getRefProjectId());
				mav.addObject("projectName", project.getProjectName());
			} catch (Exception e) {
				logger.error("没有找到项目信息", e);
			}
		}
		//更新全文检索
        this.handleIndex(plan.getId(), planManager, PlanOperAction.Update, indexManager, updateIndexManager);
    	return mav;
    }

    /**
     * 计划回复
     */
    public ModelAndView savePlanReply(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        Plan plan = planManager.getPlanByPk(new Long(request.getParameter("planId")));
        // by Yongzhang 2008-11-19
        if (plan == null)
        {
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.write("alert(v3x.getMessage('PLANLang.plan_has_delete'));");
            out.println("</script>");

            return super.refreshWorkspace();
        }
        PlanReply planReply = new PlanReply();
        planReply.setIdIfNew();
        planReply.setCreateTime(new Date());
        planReply.setPlan(plan);
        planReply.setText(request.getParameter("content"));
        planReply.setIsHidden(request.getParameterValues("isHidden") != null);
        try
        {
            Long refPlanReplyId = RequestUtils.getLongParameter(request, "opinionId");
            planReply.setRefPlanReplyId(refPlanReplyId);
            if (refPlanReplyId == null || refPlanReplyId.intValue() == 0)
            {
                planReply.setRefPlanReplyId(planReply.getId());
            }
        }
        catch (Exception e)
        {
            planReply.setRefPlanReplyId(planReply.getId());
        }
        
        User user = CurrentUser.get();
        Long userId = user.getId();
        String userName = user.getName();
        
        planReply.setRefUserId(userId);
        planReply.setRefUserName(userName);

        planManager.addPlanReply(planReply);
        // 添加附件
        attachmentManager.create(ApplicationCategoryEnum.plan, plan.getId(), planReply.getId(), request);

        String comment = MessageUtil.getComment4Message(planReply.getText());
        // 发送消息
        if (userId.equals(plan.getCreateUserId())) {
            userMessageManager.sendSystemMessage(MessageContent.get("plan.reply", plan.getTitle(), userName, comment), 
            		ApplicationCategoryEnum.plan, 
            		userId, 
            		MessageReceiver.get(plan.getId(), planManager.getPlanReplyByPk(planReply.getRefPlanReplyId()).getRefUserId(), "message.link.plan.reply",
                    String.valueOf(plan.getId()), "reply", planReply.getId()), plan.getType());

        } else {
        	userMessageManager.sendSystemMessage(MessageContent.get("plan.reply", plan.getTitle(),userName, comment), 
        			ApplicationCategoryEnum.plan, 
        			userId, 
        			MessageReceiver.get(plan.getId(), plan.getCreateUserId(), "message.link.plan.reply", String.valueOf(plan.getId()), "summary", planReply.getId()),
                    plan.getType());
        }
        //更新全文检索
        this.handleIndex(plan.getId(), planManager, PlanOperAction.Update, indexManager, updateIndexManager);
        
        String sub = request.getParameter("sub");
		if (StringUtils.isNotBlank(sub) && "parent".equals(sub)) {
			String dialogArguments = request.getParameter("dialogArguments");
			if (StringUtils.isBlank(dialogArguments) || !"true".equals(dialogArguments)) {
				InternalResourceView ss = new InternalResourceView("/plan.do?method=initDetailHome&editType=" + request.getParameter("editType") + "&id=" + plan.getId());
				ss.render(null, request, response);
			}
		} else {
			InternalResourceView ss = new InternalResourceView("/plan.do?method=displayDetailPlan&id=" + plan.getId());
			ss.render(null, request, response);
		}

        return null;
    }

    /**
     * 计划统计
     */
    @SuppressWarnings("rawtypes")
	public ModelAndView countMyPlan(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        int year = 0;
        int month = 0;

        if (calSelectedYear != null && NumberUtils.isNumber(calSelectedYear)
                && !calSelectedMonth.equals("-1"))
        {
            year = new Integer(calSelectedYear).intValue();
        }
        if (calSelectedMonth != null && NumberUtils.isNumber(calSelectedMonth)
                && !calSelectedMonth.equals("-1"))
        {
            month = new Integer(calSelectedMonth).intValue();
        }
        java.util.Calendar calendar = null;

        if (year == 0)
        {
            calendar = new GregorianCalendar();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            calendar.set(year, month - 1, 1, 0, 0, 0);
        }
        else
        {
            calendar = new GregorianCalendar(year, month - 1, 1, 0, 0, 0);
        }

        Date date1 = calendar.getTime();
        calendar.set(year, month, 1, 0, 0, 0);
        Date date2 = calendar.getTime();

        Map map = planManager.countDraftsmanPlan(CurrentUser.get().getId(), date1, date2);

        PlanCountModel pcmDay = (PlanCountModel) map.get(PlanType.DAY_PLAN.getValue());// 日计划统计
        PlanCountModel pcmWeek = (PlanCountModel) map.get(PlanType.WEEK_PLAN.getValue());// 周计划统计
        PlanCountModel pcmMonth = (PlanCountModel) map.get(PlanType.MONTH_PLAN.getValue());// 月计划统计
        PlanCountModel pcmAnyScope = (PlanCountModel) map.get(PlanType.ANY_SCOPE_PLAN.getValue());// 任意期计划统计
        ModelAndView mav = new ModelAndView("plan/countMyPlan");

        mav.addObject("pcmDay", pcmDay);
        mav.addObject("pcmWeek", pcmWeek);
        mav.addObject("pcmMonth", pcmMonth);
        mav.addObject("pcmAnyScope", pcmAnyScope);
        return mav;
    }

    /**
     * 计划管理统计
     */
    @SuppressWarnings("rawtypes")
	public ModelAndView countPlanMgr(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        int year = 0;
        int month = 1;
        int date = 1;

        if (calSelectedYear != null && NumberUtils.isNumber(calSelectedYear)
                && !calSelectedMonth.equals("-1"))
        {
            year = new Integer(calSelectedYear).intValue();
        }
        if (calSelectedMonth != null && NumberUtils.isNumber(calSelectedMonth)
                && !calSelectedMonth.equals("-1"))
        {
            month = new Integer(calSelectedMonth).intValue();
        }
        if (calSelectedDate != null && NumberUtils.isNumber(calSelectedDate)
                && !calSelectedDate.equals("-1"))
        {
            date = new Integer(calSelectedMonth).intValue();
        }
        java.util.Calendar calendar = null;
        if (year == 0)
        {
            calendar = new GregorianCalendar();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            calendar.set(year, month - 1, 1, 0, 0, 0);

        }
        else
        {
            calendar = new GregorianCalendar(year, month - 1, date, 0, 0, 0);
        }
        Date date1 = calendar.getTime();
        calendar.set(year, month, date, 0, 0, 0);
        Date date2 = calendar.getTime();
        // 得到主送、抄送、告知计划的统计结果
        int[][] countMgr = new int[3][4];
        Long userId = CurrentUser.get().getId();
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                List lst = planManager.getPlan(userId, String.valueOf(i + 1),
                        String.valueOf(j + 1), date1, date2);
                if (ListUtils.EMPTY_LIST.equals(lst))
                {
                    countMgr[i][j] = 0;
                }
                else
                {
                    countMgr[i][j] = lst.size();
                }
            }
        }
        // 得到授权用户的计划统计结果
        ModelAndView mav = new ModelAndView("plan/countPlanMgr");
        mav.addObject("countMgr", countMgr);
        Map userScope = planManager.getUserScope(userId);
        if (!MapUtils.EMPTY_MAP.equals(userScope))
        {
            PlanUserScope planUserScope = planUserScopeManager.getPlanUserScopeByRefUser(userId);
            Map userScopeMap = planManager.countUserScopePlan(userId, date1, date2);
            Map userScopeIsDraftMap = planManager.countUserScoupeIsDraftPlan(userId, date1, date2);
            mav.addObject("userScope", userScope);
            mav.addObject("planUserScope", planUserScope);
            mav.addObject("userScopeMap", userScopeMap);
            mav.addObject("userScopeIsDraftMap", userScopeIsDraftMap);
        }
        return mav;
    }

    public ModelAndView searchPlan(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        String type = RequestUtils.getStringParameter(request, "type");
        String dept = request.getParameter("dept");
        List<String> deptList = new ArrayList<String>();
        if (dept != null && !dept.equals(""))
        {
            String[] depts = dept.split(",");
            for (String d : depts)
            {
                deptList.add(d);
            }
        }

        // 查询其他用户的计划
        String strUserId = request.getParameter("userId");
        Long userId = null;
        if (strUserId != null && NumberUtils.isNumber(strUserId))
        {
            userId = new Long(strUserId);
        }
        if (userId == null)
        {
            userId = CurrentUser.get().getId();
        }
        int year = 0;
        int month = 0;
        int date = 1;
        if (calSelectedYear != null && NumberUtils.isNumber(calSelectedYear)
                && !calSelectedYear.equals("-1"))
        {
            year = new Integer(calSelectedYear).intValue();
        }
        if (calSelectedMonth != null && NumberUtils.isNumber(calSelectedMonth)
                && !calSelectedMonth.equals("-1"))
        {
            month = new Integer(calSelectedMonth).intValue();
        }
        if (calSelectedDate != null && NumberUtils.isNumber(calSelectedDate)
                && !calSelectedDate.equals("-1"))
        {
            date = new Integer(calSelectedDate).intValue();
        }
        if (!type.equals(PlanType.DAY_PLAN.getValue()))
        {// 不是日计划时,不按照日期查询
            date = 1;
        }
        java.util.Calendar calendar = null;
        if (year == 0)
        {
            calendar = new GregorianCalendar();
        }
        else
        {
            calendar = new GregorianCalendar(year, month - 1, date);
        }

        ModelAndView mav = new ModelAndView("plan/listPlanMgr");

        Date date1 = null;
        Date date2 = null;
        // 判断是否是某一天计划
        if (type.equals(PlanType.DAY_PLAN.getValue()))
        {// 日计划
            date1 = Datetimes.getFirstDayInWeek(calendar.getTime());
            date2 = Datetimes.getLastDayInWeek(calendar.getTime());
            mav = new ModelAndView("plan/listDayPlanMgr");
        }
        else
        { // 不是日计划
            date1 = calendar.getTime();
            calendar.set(year, month, date);
            date2 = calendar.getTime();
        }
        // 返回的计划list
        List<V3xOrgMember> userList = new ArrayList<V3xOrgMember>();
        for (String deptId : deptList)
        {
        	if(deptId.contains("|1")){
        		String[] deptIdStrs = deptId.split("\\|");
        		getMemberForPlanMag(deptIdStrs[0], userList, userId);
        	}else{
        		getMemberForPlanMag(deptId, userList, userId);
        		List<V3xOrgDepartment> dList = orgManager.getChildDepartments(new Long(deptId), false);
        		for (int i = 0; i < dList.size(); i++) {
                    getMemberForPlanMag(dList.get(i).getId().toString(), userList, userId);
				}
        	}
            
        }
        List<Plan> list = new ArrayList<Plan>();
        list = planManager.getPlan(userId, userList, type, date1, date2);
        mav.addObject("planList", list);
        return mav;
    }
    
    private void getMemberForPlanMag(String deptId,List<V3xOrgMember> userList,Long userId) throws NumberFormatException, BusinessException{
    	List<V3xOrgMember> uList = orgManager.getMembersByDepartment(new Long(deptId), true);
        for (V3xOrgMember member : uList){
        	if(!member.getId().equals(userId)){
        		userList.add(member);
        	}
        }
    }

    @SuppressWarnings("rawtypes")
	public ModelAndView searchPlanCount(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        // String type = RequestUtils.getStringParameter(request, "type");
        int year = 0;
        int month = 1;
        int date = 1;

        if (calSelectedYear != null && NumberUtils.isNumber(calSelectedYear)
                && !calSelectedMonth.equals("-1"))
        {
            year = new Integer(calSelectedYear).intValue();
        }
        if (calSelectedMonth != null && NumberUtils.isNumber(calSelectedMonth)
                && !calSelectedMonth.equals("-1"))
        {
            month = new Integer(calSelectedMonth).intValue();
        }
        if (calSelectedDate != null && NumberUtils.isNumber(calSelectedDate)
                && !calSelectedDate.equals("-1"))
        {
            date = new Integer(calSelectedMonth).intValue();
        }
        java.util.Calendar calendar = null;
        if (year == 0)
        {
            calendar = new GregorianCalendar();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            calendar.set(year, month - 1, 1, 0, 0, 0);

        }
        else
        {
            calendar = new GregorianCalendar(year, month - 1, date, 0, 0, 0);
        }
        // calendar.set(2007, 2, 1);
        Date date1 = calendar.getTime();
        // calendar.set(2008, 1, 1);
        calendar.set(year, month, date, 0, 0, 0);
        Date date2 = calendar.getTime();

        String tempUserName = request.getParameter("searchUser");
        List<V3xOrgMember> memberList = orgManager.getMemberByName(tempUserName);
        // 得到统计结果
        // Map map =
        // planManager.countUserPlan(userId,memberList,
        // date1, date2);
        // midified by paul at 5/17/07 先取出所有统计结果，然后通过filter来判断该用户的统计结果
        Map map = planManager.countUserPlan(CurrentUser.get().getId(), date1, date2);
        Map resultMap = new HashMap();
        if (null != memberList && !memberList.isEmpty())
        {
            for (V3xOrgMember member : memberList)
            {
                if (null != map && !map.isEmpty() && map.containsKey(member.getId()))
                    resultMap.put(member.getId(), map.get(member.getId()));
            }
        }

        ModelAndView mav = new ModelAndView("plan/countPlanMgr");

        // 得到用户名
        Map userMap = planManager.getUserName(resultMap.keySet());
        mav.addObject("countMap", resultMap);
        mav.addObject("userMap", userMap);
        return mav;
    }

    // 计划转发协同
    public ModelAndView planToCol(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String id = request.getParameter("id");
        Plan plan = planManager.getPlanById(new Long(id));
        ModelAndView mav = new ModelAndView("collaboration/newCollaboration");
        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments = attachmentManager.getByReference(plan.getId(), plan.getId());
        mav = collaborationController.appToColl(plan.getTitle(), plan.getPlanBody().getBodyType(),
                plan.getPlanBody().getCreateDate(), plan.getPlanBody().getContent(), attachments,
                true);
        return mav;
    }

    // 计划转发邮件
    public ModelAndView planToMail(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        String id = request.getParameter("id");
        Plan plan = planManager.getPlanById(new Long(id));
        List<Attachment> attachments = new ArrayList<Attachment>();
        PlanBody pbody = plan.getPlanBody();
        String pcontent = pbody.getContent();

        if (!Constants.EDITOR_TYPE_HTML.equals(pbody.getBodyType()))
        {
            pcontent = ResourceBundleUtil.getString(
                    "com.seeyon.v3x.plan.resource.i18n.PlanResources",
                    "plan.contentToAttachment.info");
            InputStream in = null;
            try
            {
                FileManager fileManager = (FileManager) ApplicationContextHolder
                        .getBean("fileManager");
                in = fileManager.getStandardOfficeInputStream(Long.parseLong(pbody.getContent()),
                        pbody.getCreateDate());
                V3XFile f = fileManager.save(in, ApplicationCategoryEnum.plan, plan.getTitle()
                        + EdocUtil.getOfficeFileExt(pbody.getBodyType()), pbody.getCreateDate(),
                        false);
                attachments.add(new Attachment(f, ApplicationCategoryEnum.plan,
                        com.seeyon.v3x.common.filemanager.Constants.ATTACHMENT_TYPE.FILE));
            }
            catch (Exception e)
            {
                log.error("错误 ", e);
            }
            finally
            {
                IOUtils.closeQuietly(in);
            }
        }
        attachments.addAll(attachmentManager.getByReference(plan.getId(), plan.getId()));
        ModelAndView mav = webMailManager.forwordMail(new Long(id), plan.getTitle(), pcontent,
                attachments);

        return mav;
    }

    /**
     * 从request获取计划实体
     * @param request 页面提交的request
     * @param plan    计划实体
     */
    @SuppressWarnings("rawtypes")
	private void setPlanParameter(HttpServletRequest request, Plan plan, PlanBody planBody) throws ServletRequestBindingException {
        try {
        	//id 更新时使用
            plan.setId(RequestUtils.getLongParameter(request, "id"));
        } catch (ServletRequestBindingException e) {
        	
        }     
        //计划基本信息
        setPlanBasicInfo(request, plan);
            
        try {
            //计划相关人员
            Long userId = CurrentUser.get().getId();
            plan.setCreateUserId(userId);
            plan.setAllPlanRefUser(new ArrayList());
            this.setRefUser4Plan(plan, request.getParameter("toUserIds"), PlanRelevantUser.UserType.Leader.getValue(), false);
            this.setRefUser4Plan(plan, request.getParameter("ccUserIds"), PlanRelevantUser.UserType.Copy_To.getValue(), false);
            this.setRefUser4Plan(plan, request.getParameter("apprizeUserIds"), PlanRelevantUser.UserType.Apprize.getValue(), false);
            this.setRefUser4Plan(plan, userId.toString(), PlanRelevantUser.UserType.Creator.getValue(), false);
            
            //计划正文
            planBody.setContent(request.getParameter("content"));
            planBody.setBodyType(request.getParameter("bodyType"));
            Date bodyCreateDate = Datetimes.parseDatetime(request.getParameter("bodyCreateDate"));
            if (bodyCreateDate != null)
                planBody.setCreateDate(new Timestamp(bodyCreateDate.getTime()));
            
            planBody.setPlan(plan);
        } catch (Exception e) {
        	
        }
    }
    
    /**
     * 设定计划的基本信息，便于设定计划、计划格式的两个方法复用
     * @param request
     * @param plan
     * @throws ServletRequestBindingException
     */
	private void setPlanBasicInfo(HttpServletRequest request, Plan plan) {
		try {
			plan.setTitle(RequestUtils.getStringParameter(request, "title"));
		} catch (ServletRequestBindingException e) {
		}
		
		try {
			plan.setStartTime(Datetimes.parse(RequestUtils.getStringParameter(request, "startTime"), "yyyy-MM-dd"));
		} catch (ServletRequestBindingException e) {
		}
		
		try {
			plan.setEndTime(Datetimes.parse(RequestUtils.getStringParameter(request, "endTime"), "yyyy-MM-dd"));
		} catch (ServletRequestBindingException e) {
		}
		
		try {
			plan.setType(RequestUtils.getStringParameter(request, "type"));
		} catch (ServletRequestBindingException e) {
		}
		
		try {
			plan.setPublishStatus(RequestUtils.getStringParameter(request, "publishStatus"));
		} catch (ServletRequestBindingException e) {
		}
		
		try {
			plan.setPlanStatus(RequestUtils.getStringParameter(request, "planStatus"));
		} catch (ServletRequestBindingException e) {
		}
		
		// 关联项目
		String proj = request.getParameter("refProjectId");
		plan.setRefProjectId(Strings.isNotBlank(proj) ? Long.parseLong(proj) : null);
		// 关联部门
		String dept = request.getParameter("refDepartmentId");
		plan.setRefDepartmentId(Strings.isNotBlank(dept) ? Long.parseLong(dept) : null);
		// 单位id
		plan.setRefAccountId(CurrentUser.get().getLoginAccount());
	}
    
    /**
     * 将计划某种类型的相关人员加入到计划全部相关人员集合中去
     * @param plan				计划
     * @param strUserIds		相关人员ID拼串，如主送人员ID拼串、抄送人员拼串等
     * @param userType			类型：主送、抄送、告知、发起人等
     * @param add2OneTypeList	是否同时也将人员加入到这一种人员的集合中去，如：将主送加入到plan.getPlanToLeaderUser()
     */
    private void setRefUser4Plan(Plan plan, String strUserIds, String userType, boolean add2OneTypeList) {
    	if(Strings.isNotBlank(strUserIds)) {
	    	String[] userIds = strUserIds.split(",");
	        if (userIds != null && userIds.length > 0) {
	        	if(add2OneTypeList) {
	        		if(userType.equals(PlanRelevantUser.UserType.Leader.getValue()))
	        			plan.setPlanToLeaderUser(new ArrayList<PlanRelevantUser>());
	        		else if(userType.equals(PlanRelevantUser.UserType.Copy_To.getValue()))
	        			plan.setPlanCcLeaderUser(new ArrayList<PlanRelevantUser>());
	        		else if(userType.equals(PlanRelevantUser.UserType.Apprize.getValue()))
	        			plan.setPlanApprizeUser(new ArrayList<PlanRelevantUser>());
	        	}
	        	
	            for (int i = 0; i < userIds.length; i++) {
	                if (Strings.isNotBlank(userIds[i])) {
	                    PlanRelevantUser pru = new PlanRelevantUser();
	                    pru.setIdIfNew();
	                    pru.setPlan(plan);
	                    pru.setRefUserId(Long.parseLong(userIds[i]));
	                    pru.setType(userType);
	                    pru.setSortId(i);
	                    
	                    if(userType.equals(PlanRelevantUser.UserType.Creator.getValue()))
	                    	pru.setRefUserName(CurrentUser.get().getName());
	                    
	                    if(add2OneTypeList) {
	    	        		if(userType.equals(PlanRelevantUser.UserType.Leader.getValue()))
	    	        			plan.getPlanToLeaderUser().add(pru);
	    	        		else if(userType.equals(PlanRelevantUser.UserType.Copy_To.getValue()))
	    	        			plan.getPlanCcLeaderUser().add(pru);
	    	        		else if(userType.equals(PlanRelevantUser.UserType.Apprize.getValue()))
	    	        			plan.getPlanApprizeUser().add(pru);
	    	        	}
	                    
	                    plan.getAllPlanRefUser().add(pru);
	                }
	            }
	        }
    	}
    }
    
    /**
     * 设置计划格式的基本属性、相关人员
     * @param request 页面提交的request
     * @param plan    计划格式实体
     */
    @SuppressWarnings("rawtypes")
	private void setPlanStyleParameters(HttpServletRequest request, Plan plan) {
    	try {
            setPlanBasicInfo(request, plan);
            
            plan.setAllPlanRefUser(new ArrayList());
            this.setRefUser4Plan(plan, request.getParameter("toUserIds"), PlanRelevantUser.UserType.Leader.getValue(), true);
            this.setRefUser4Plan(plan, request.getParameter("ccUserIds"), PlanRelevantUser.UserType.Copy_To.getValue(), true);
            this.setRefUser4Plan(plan, request.getParameter("apprizeUserIds"), PlanRelevantUser.UserType.Apprize.getValue(), true);
        } catch (Exception e) {
        	
        }
    }
    
    /**
     * 加载样式
     */
    private void loadStyle(HttpServletRequest request, Plan plan) {
        try {
            Long styleId = RequestUtils.getLongParameter(request, "styleId");
            if (styleId != null) {
                MtContentTemplateCAP ps = mtContentTemplateManagerCAP.getById(styleId);// 根据id取的计划
                setPlanStyleParameters(request, plan); // 当改变计划格式时，除了正文，其他内容需要保存
                if (ps != null) {
                    if (plan.getPlanBody() == null)
                        plan.setPlanBody(new PlanBody());
                    
                    plan.getPlanBody().setContent(ps.getContent());
                    plan.getPlanBody().setBodyType(ps.getTemplateFormat());
                    plan.getPlanBody().setCreateDate(ps.getCreateDate());
                }
            }

        } catch (ServletRequestBindingException e) {
        }
    }

    /**
     * 获取计划主送,抄送,告知人员的列表
     */
    private List<Long> getUserIdsByPlan(HttpServletRequest request, Plan plan){
        Hibernate.initialize(plan.getAllPlanRefUser());
        return this.getMsgReceiverIds(plan);
    }

    /**
     * 将计划列表重新封装
     */
    private List<Plan> formatPlanList(HttpServletRequest request, List<Plan> planList) throws BusinessException {
        if (planList.size() != 0) {
        	Long userId = CurrentUser.get().getId();
            Iterator<Plan> itLst = planList.iterator();
            while (itLst.hasNext()) {
                Plan plan = (Plan) itLst.next();
                V3xOrgMember pru = orgManager.getMemberById(plan.getCreateUserId());
                V3xOrgMember loginUser = orgManager.getMemberById(userId);
                if (!(pru.getOrgAccountId().equals(loginUser.getOrgAccountId())))
                    plan.setTitle("(" + orgManager.getAccountById(pru.getOrgAccountId()).getShortname() + ")" + plan.getTitle());
            }
        }
        return planList;
    }

    public ModelAndView showCal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("plan/showCal")
        		.addObject("calSelectedYear", request.getParameter("calSelectedYear"))
        		.addObject("calSelectedMonth", request.getParameter("calSelectedMonth"))
        		.addObject("calSelectedDate", request.getParameter("calSelectedDate"));
    }

    public ModelAndView showCalMgr(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("plan/showCalMgr")
        		.addObject("calSelectedYear", request.getParameter("calSelectedYear"))
				.addObject("calSelectedMonth", request.getParameter("calSelectedMonth"))
				.addObject("calSelectedDate", request.getParameter("calSelectedDate"));
	}

    public ModelAndView deptSpaceManageHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("plan/deptspace/planMoreHomeEntry");
    }

    public ModelAndView deptSpaceManageFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("plan/deptspace/deptSpaceManageFrame");
    }

    public ModelAndView deptSpaceManage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Plan> planList = planManager.getDeptMorePlanList(NumberUtils.toLong(request.getParameter("departmentId")), 
        		CurrentUser.get().getId(), new java.sql.Timestamp(new java.util.Date().getTime()), true);
        return new ModelAndView("plan/deptspace/deptSpaceManage").addObject("planList", planList);
    }

	/**
     * 在由关联项目中新建plan时，自动加入项目组的信息
     * 
     * @param projectId
     *            关联项目ID
     * @param plan
     *            计划
     * @throws Exception
     */
    private void setPlanProjectInfo(Long projectId, Plan plan) throws Exception {
        // 取得信息
        ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, true); 
        List<V3xOrgMember> principal = projectCompose.getPrincipalLists(); // 项目负责人，对应计划主送人
        List<V3xOrgMember> members = projectCompose.getMemberLists(); // 项目成员，对应计划抄送人
        members.addAll(projectCompose.getAssistantLists());//项目助理，对应计划抄送人
        List<V3xOrgMember> fix = projectCompose.getInterfixLists(); // 项目相关人员，对应计划告知人
        List<V3xOrgMember> charges = projectCompose.getChargeLists(); // 项目领导，对应计划告知人

        // 设置到Plan
        plan.setRefProjectId(projectId); // ID
        List<PlanRelevantUser> toLeaderUser = new ArrayList<PlanRelevantUser>();
        List<PlanRelevantUser> toCCUser = new ArrayList<PlanRelevantUser>();
        List<PlanRelevantUser> toAppUser = new ArrayList<PlanRelevantUser>();

        PlanRelevantUser pru = null;
        // 主送
        Long userId = CurrentUser.get().getId();
        if (principal != null && principal.size() != 0){
        	int i = 0;
            for (V3xOrgMember member : principal) {
                pru = new PlanRelevantUser(plan, member.getId(), "1", member.getName(), i++);
                if (!userId.equals(pru.getRefUserId()))
                	toLeaderUser.add(pru);
            }
        }

        // 抄送
        if (members != null && members.size() != 0)
        {
        	int i = 0;
            for (V3xOrgMember member : members) {
                pru = new PlanRelevantUser(plan, member.getId(), "2", member.getName(), i++);
                if (!userId.equals(pru.getRefUserId()))
                    toCCUser.add(pru);
            }
        }

        // 告知
        if (fix != null && fix.size() != 0)
        {
            int i = 0;
        	for (V3xOrgMember member : fix)
            {
                pru = new PlanRelevantUser(plan, member.getId(), "3", member.getName(), i++);
                if (!userId.equals(pru.getRefUserId()))
                    toAppUser.add(pru);
            }
        }
        if (charges != null && charges.size() != 0)
        {
        	int i = 0;
        	for (V3xOrgMember member : charges)
            {
                pru = new PlanRelevantUser(plan, member.getId(), "3", member.getName(), i++);
                if (!userId.equals(pru.getRefUserId()))
                	toAppUser.add(pru);
            }
        }

        plan.setPlanToLeaderUser(toLeaderUser);
        plan.setPlanCcLeaderUser(toCCUser);
        plan.setPlanApprizeUser(toAppUser);

        setStartAndEndTimeAccordingType(plan, new Date());
    }
    /**
     * 对计划进行某一操作时，处理对应的全文检索操作
     * @param planId  计划ID
     * @param planManager
     * @param action  操作
     * @param indexManager
     * @param updateIndexManager
     */
    private void handleIndex(Long planId, PlanManager planManager, PlanOperAction action, 
    		IndexManager indexManager, UpdateIndexManager updateIndexManager){
    	try{
	    	switch(action){
	    	case Save :
	    		IndexInfo indexInfo = ((IndexEnable)planManager).getIndexInfo(planId);
	    		indexManager.index(indexInfo);
	    		break;
	    	case Update :
	    		updateIndexManager.update(planId, ApplicationCategoryEnum.plan.key());
	    		break;
	    	case Delete :
	    		indexManager.deleteFromIndex(ApplicationCategoryEnum.plan, planId);
	    		break;
	    	}
    	} catch (Exception e){
    		log.error("对计划[id=" + planId + "]进行[" + action.name() + "]操作时，处理全文检索出现异常:" + e);
    	}
    }
    /**
     * 根据计划类型设定计划的开始和结束时间
     * @param plan
     */
	private void setStartAndEndTimeAccordingType(Plan plan, Date date) {
		if (PlanType.DAY_PLAN.getValue().equals(plan.getType())) {
            plan.setStartTime(Datetimes.getTodayFirstTime());
            plan.setEndTime(Datetimes.getTodayLastTime());
        } else if (PlanType.WEEK_PLAN.getValue().equals(plan.getType())) {
        	Calendar calendar = Calendar.getInstance();
        	calendar.setTime(Datetimes.getFirstDayInWeek(date));
        	calendar.add(Calendar.DATE, 1);
            plan.setStartTime(calendar.getTime());
            calendar.add(Calendar.DATE, 4);
            plan.setEndTime(calendar.getTime());
        } else if (PlanType.MONTH_PLAN.getValue().equals(plan.getType())) {
        	plan.setStartTime(Datetimes.getFirstDayInMonth(date));
            plan.setEndTime(Datetimes.getLastDayInMonth(date));
        }
	}
    
	/**
	 * 进入关联文档添加页面框架
	 */
	public ModelAndView list4QuoteFrame(HttpServletRequest request, HttpServletResponse response)  {
		return new ModelAndView("collaboration/list4QuoteFrame");
	}

	/**
	 * 首页栏目修改计划完成率、状态
	 */
	public ModelAndView editState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("plan/editState");
		Plan plan = planManager.getPlanByPk(NumberUtils.toLong(request.getParameter("id")));
		if (plan == null) {
			super.rendJavaScript(response, "alert('" + Constant.getPlanI18NValue("plan.canceled.label") + "');window.close(); ");
			return null;
		}

		mav.addObject("plan", plan);
		mav.addObject("planRatio", plan.getFinishRatio());
		mav.addObject("planState", plan.getPlanStatus());
		return mav;
	}
	
	/**
	 * 首页栏目保存计划完成率、状态
	 */
	public ModelAndView updateState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Plan plan = planManager.getPlanByPk(NumberUtils.toLong(request.getParameter("id")));
		String completeRate = request.getParameter("completeRate");
		String states = request.getParameter("states");

		if (plan != null) {
			plan.setFinishRatio(NumberUtils.toFloat(completeRate));
			plan.setPlanStatus(states);
			this.planManager.updatePlan(plan);
		}
		PrintWriter out = response.getWriter();
		out.println("<script type='text/javascript'>");
		out.println("parent.editOk();");
		out.println("</script>");
		return null;
	}
	
	public void setPlanManager(PlanManager planManager) {
		this.planManager = planManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setPlanUserScopeManager(
			PlanUserScopeManager planUserScopeManager) {
		this.planUserScopeManager = planUserScopeManager;
	}
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	public void setCollaborationController(
			CollaborationController collaborationController) {
		this.collaborationController = collaborationController;
	}
	public void setWebMailManager(WebMailManager webMailManager) {
		this.webMailManager = webMailManager;
	}
	public void setMtContentTemplateManagerCAP(MtContentTemplateManagerCAP mtContentTemplateManagerCAP) {
		this.mtContentTemplateManagerCAP = mtContentTemplateManagerCAP;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public IndexManager getIndexManager() {
		return indexManager;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public UpdateIndexManager getUpdateIndexManager() {
		return updateIndexManager;
	}

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	
	public void setProjectPhaseEventManager(ProjectPhaseEventManager projectPhaseEventManager) {
		this.projectPhaseEventManager = projectPhaseEventManager;
	}
}