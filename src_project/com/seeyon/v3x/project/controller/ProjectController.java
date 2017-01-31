package com.seeyon.v3x.project.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.cap.meeting.domain.MtMeetingCAP;
import com.seeyon.cap.meeting.manager.MtMeetingManagerCAP;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.bbs.domain.BbsConstants;
import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsArticleManager;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bbs.webmodel.ArticleModel;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.manager.DocAclManager;
import com.seeyon.v3x.doc.manager.DocHierarchyManager;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.doc.webmodel.FolderItemDoc;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.guestbook.domain.LeaveWord;
import com.seeyon.v3x.guestbook.manager.GuestbookManager;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.event.UpdateTeamEvent;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.manager.PlanManager;
import com.seeyon.v3x.project.domain.ProjectEvolution;
import com.seeyon.v3x.project.domain.ProjectLog;
import com.seeyon.v3x.project.domain.ProjectMember;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.domain.ProjectType;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.util.ProjectConstants;
import com.seeyon.v3x.project.util.ProjectUtils;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.project.webmodel.ProjectLogCompose;
import com.seeyon.v3x.space.manager.PortletEntityPropertyManager;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.manager.TaskInfoManager;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * @author lin tian 2007-5-16
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-23
 */
public class ProjectController extends BaseController {
	
	private static final Log log = LogFactory.getLog(ProjectController.class);
	
	private ProjectManager projectManager;

	private AttachmentManager attachmentManager;

	private DocHierarchyManager docHierarchyManager;
	
	private DocAclManager docAclManager;
	
	private MtMeetingManagerCAP mtMeetingManagerCAP;
	
	private OrgManagerDirect orgManagerDirect;
	
	private PlanManager planManager;
	
	private CalEventManager calEventManager;
	
	private ColManager colManager;
	
	private TempleteManager templeteManager;
	
	private OrgManager orgManager;
	
	private BbsBoardManager bbsBoardManager;
	
    private BbsArticleManager bbsArticleManager;
    
    private GuestbookManager guestbookManager;
    
    private OperationlogManager operationlogManager;
    
    private AppLogManager appLogManager;
    
    private MetadataManager metadataManager;
    
    private TaskInfoManager taskInfoManager;
    
    private StaffInfoManager staffInfoManager;
    
    private SearchManager searchManager ;
    
    private PortletEntityPropertyManager portletEntityPropertyManager;
    
    public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
		this.taskInfoManager = taskInfoManager;
	}

	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	public void setguestbookManager(GuestbookManager guestbookManager) {
        this.guestbookManager = guestbookManager;
    }

    public void setBbsArticleManager(BbsArticleManager bbsArticleManager) {
        this.bbsArticleManager = bbsArticleManager;
    }
		

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}

	public void setPlanManager(PlanManager planManager) {
		this.planManager = planManager;
	}
  
	public void setCalEventManager(CalEventManager calEventManager){
		this.calEventManager = calEventManager;
	} 
	
	public void setMtMeetingManagerCAP(MtMeetingManagerCAP mtMeetingManagerCAP) {
		this.mtMeetingManagerCAP = mtMeetingManagerCAP;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setDocHierarchyManager(DocHierarchyManager docHierarchyManager) {
		this.docHierarchyManager = docHierarchyManager;
	}
	
	public void setDocAclManager(DocAclManager docAclManager) {
		this.docAclManager = docAclManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}

	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	
	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}
	
	public void setPortletEntityPropertyManager(PortletEntityPropertyManager portletEntityPropertyManager) {
		this.portletEntityPropertyManager = portletEntityPropertyManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView getIndexProjectList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("project/projectBorderFrame");
	}
	
	public ModelAndView myTemplateBorderMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("project/projectFrame");
	}
	
    public ModelAndView getAllProjectListFrame(HttpServletRequest request,
            HttpServletResponse response) throws Exception{
    	ModelAndView mav = new ModelAndView("project/projectList4FormFrame");
		String formRel = request.getParameter("isFormRel");
		mav.addObject("isFormRel",formRel);
    	return mav;
    }
	/**
	 * 获取当前用户所有项目信息
	 */
	public ModelAndView getAllProjectList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView mav;
		String more = request.getParameter("more");
		boolean isMore = Strings.isNotBlank(more);
		String formRel = request.getParameter("isFormRel");
		List<ProjectType> ptList = this.projectManager.getProjectTypes(user.getLoginAccount());
		if(isMore){
			mav = new ModelAndView("project/allProjectMore");//更多			 
		}else if(formRel!=null && !formRel.equals("")){
			mav = new ModelAndView("project/projectList4Form");//表单数据域关联
			mav.addObject("ptList",ptList);
			mav.addObject("isFormRel",formRel);
		}else{
			mav = new ModelAndView("project/projectList");
		}
		
		String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");
		
		//判断是否有项目操作权限
		boolean isProjectBuilder = false;
		
		V3xOrgRole role = new V3xOrgRole();
		try {
			role = orgManager.getRoleByName( V3xOrgEntity.ORGENT_META_KEY_PROJECTBUILD , user.getLoginAccount() );
		} catch (BusinessException e) {
			logger.error("获取角色失败",e);
		}
		
		if(role!=null){
			List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
			try {
				members = orgManager.getMemberByRole(role.getType(), user.getLoginAccount(), role.getId());
			} catch (BusinessException e) {
				logger.error("获取角色人员失败",e);
			}
			if(members.size()>0){
				for(V3xOrgMember member : members){
					if(member.getId().longValue()==user.getId()){
						isProjectBuilder = true;
						break;
					}
				}
			}
		}
		
		List<ProjectSummary> projectSummaryList = new ArrayList<ProjectSummary>();
		
		// 栏目内容来源指定角色或类型，需要过滤
		List<Byte> memberTypeList = null;
		List<Long> projectTypeList = null;
		try {
			String fragmentId = request.getParameter("fragmentId");
			if (Strings.isNotBlank(fragmentId)) {
				String ordinal = request.getParameter("ordinal");
				Map<String, String> preference = portletEntityPropertyManager.getPropertys(Long.parseLong(fragmentId), ordinal);
				String panel = request.getParameter("panel");
				String panelValue = preference.get(panel + "_value");

				if ("designatedRole".equals(panel)) {
					if (Strings.isNotBlank(panelValue)) {
						String[] strs = panelValue.split(",");
						if (strs != null && strs.length > 0) {
							memberTypeList = new ArrayList<Byte>(strs.length);
							for (String str : strs) {
								if (Strings.isNotBlank(str)) {
									memberTypeList.add(Byte.valueOf(str));
								}
							}
						}
					}
				} else if ("designatedType".equals(panel)) {
					projectTypeList = CommonTools.parseStr2Ids(panelValue);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}

        try {
            projectSummaryList = projectManager.getAllUserProjectList(getMemberId(), condition, textfield, textfield1, memberTypeList);
        } catch (Exception e) {
            logger.error("获取项目列表失败：", e);
        }
		
		String state = request.getParameter("state");
		if(StringUtils.isBlank(state)){
			state = "1";
		}
		
        List<ProjectSummary> projectList1=new ArrayList<ProjectSummary>();
        List<ProjectSummary> projectList3=new ArrayList<ProjectSummary>();
		if(isMore){
			if(state.equals("1")){
				for(ProjectSummary dr:projectSummaryList){
					if(dr.getProjectState()<2){
						projectList1.add(dr);
					}
				}
				projectList3.addAll(projectList1);
			}
			else if(state.equals("0")){
				for(ProjectSummary dr:projectSummaryList){
					if(dr.getProjectState()>=2){
						projectList1.add(dr);
					}
				}
				projectList3.addAll(projectList1);
			}	
		}else{
			projectList3.addAll(projectSummaryList) ;
		}
		List<ProjectSummary> tempProjectList=new ArrayList<ProjectSummary>();
		if(isMore){
			List<ProjectType> typeList = new ArrayList<ProjectType>();
			
			List<List<ProjectCompose>> tprojectList = new ArrayList<List<ProjectCompose>>();
			 Map<String, Object> condMap = new HashMap<String, Object>();
             
             if(state.equals("1")){
                 condMap.put("projectState", "projectState_lt"); 
             }else{
                 condMap.put("projectState", "projectState_ge");
             }
             
			for (ProjectType pt : ptList) {
				boolean containsType = true;
				if (projectTypeList != null) {
					containsType = projectTypeList.contains(pt.getId());
				}
				if (containsType) {
					List<ProjectCompose> sprojectList = new ArrayList<ProjectCompose>();
					List<ProjectSummary> psummaryList = projectManager.getAllUserProjectList(getMemberId(), "projectType", pt.getId() + "", textfield1, memberTypeList,condMap);
					for (ProjectSummary dr : psummaryList) {
						if (pt.getId().equals(dr.getProjectTypeId())) {
							tempProjectList.add(dr);
							ProjectCompose projectCompose = projectManager.detailProject(dr, true);
							sprojectList.add(projectCompose);
							if (sprojectList.size() == 5) {
								break;
							}
						}
					}
					typeList.add(pt);
					tprojectList.add(sprojectList);
				}
			}
			
			projectList3.removeAll(tempProjectList);
			Set<ProjectType> tempProTypes = new HashSet<ProjectType>();
			if (projectList3.size() > 0) {
				for (ProjectSummary ps : projectList3) {
					ProjectType projectType = projectManager.getProjectTypeById(ps.getProjectTypeId());
					tempProTypes.add(projectType);
				}
				for(ProjectType pt : tempProTypes){
					// tempProTypes还有自己单位的项目类型，此项目类型已经存到typeList中了
					boolean isHasAdd = false;
					for(ProjectType _tl : typeList) {
						if(_tl.getId().equals(pt.getId())) {
							isHasAdd = true;
							break;
						}
					}
					if(isHasAdd) {
						continue;
					}
					typeList.add(pt);
					List<ProjectCompose> sprojectList=new ArrayList<ProjectCompose>();
					for(ProjectSummary dr : projectList3){
						if(pt.getId().equals(dr.getProjectTypeId())){
							ProjectCompose projectCompose = projectManager.detailProject(dr, true);
							sprojectList.add(projectCompose);
							if(sprojectList.size()==5) {
								break;
							}
						}
					}
					tprojectList.add(sprojectList);
				}
			}
			
			mav.addObject("ptList", typeList);
			mav.addObject("tprojectList", tprojectList);
			mav.addObject("state", state);
		}else{
			List<ProjectCompose> projectComposeList = new ArrayList<ProjectCompose>();
			for(ProjectSummary p : projectList3){
				ProjectCompose projectCompose = projectManager.detailProject(p, true);
				projectComposeList.add(projectCompose);
			}
			mav.addObject("projectComposeList", projectComposeList);
		}
		
		mav.addObject("isProjectBuilder", isProjectBuilder);
		return mav;
	}
	
	//增加[查看项目详情]
	public ModelAndView showprojectDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView modelView = new ModelAndView("project/projectDetail");
		long projectId = Long.parseLong(request.getParameter("projectId"));	
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		List<V3xOrgMember> principal = projectCompose.getPrincipalLists();
		modelView.addObject("principal", principal);
		modelView.addObject("projectCompose", projectCompose);
		return modelView;
	}

	/**
	 * 创建项目
	 * 
	 * @param request
	 * @param response
	 * @return mavs
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes=RoleType.ProjectCreator)
	public ModelAndView createProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			User user = CurrentUser.get();
			ModelAndView mav = new ModelAndView("project/createProject");
			ProjectSummary projectSummary = new ProjectSummary();
			projectSummary.setIdIfNew();
			// ===项目操作日志==
			ProjectLog plog = new ProjectLog();
			plog.setIdIfNew();
			plog.setOptionDate(new Timestamp(System.currentTimeMillis()));
			plog.setProjectId(projectSummary.getId());
			plog.setUserid(this.getMemberId());
			plog.setProjectDesc("add");

			// 设置当前项目状态为开始状态
			String projectState = request.getParameter("projectState");
			projectSummary.setProjectState(Byte.valueOf(projectState));
			// 设置项目名称
			String projectName = request.getParameter("projectName");
			projectSummary.setProjectName(projectName);
			// 设置项目类型
			String projectType = request.getParameter("projectType");
			
			String projectTypeIdStr = request.getParameter("projectTypeId");
			Long projectTypeId = NumberUtils.toLong(projectTypeIdStr);
			boolean exist = this.projectManager.getProjectTypeById(projectTypeId) != null;
			if(exist == false){
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('ProjectLang.project_type_deleted'));");
				out.println("parent.location.reload(true);");
				out.println("</script>");
				return null;
			}
			projectSummary.setProjectTypeName(projectType);
			projectSummary.setProjectTypeId(projectTypeId);
			
			// 开始时间
			String btime = request.getParameter("begintime");
			projectSummary.setBegintime(Datetimes.parseDate(btime));
			// 结束时间
			String etime = request.getParameter("closetime");
			projectSummary.setClosetime(Datetimes.parseDate(etime));
			// 描述
			String projectDesc = request.getParameter("projectDesc");
			projectSummary.setProjectDesc(projectDesc);
			// 创建人
			projectSummary.setProjectCreator(getMemberId());
			// 部门
			String department = request.getParameter("department");
			if(department!=null&&!department.equals("")){
				projectSummary.setDepartment(Long.parseLong(department));
			}
			
			//项目当前阶段
			String currentPhaseId = request.getParameter("currentPhaseId");
			if(StringUtils.isNotBlank(currentPhaseId)){
				projectSummary.setPhaseId(NumberUtils.toLong(currentPhaseId));
			}else{
				projectSummary.setPhaseId(1);
			}
			
			// 公开组
			String publicGroup = request.getParameter("publicGroup");
			projectSummary.setPublicState(new Byte(publicGroup));
			
			// 项目所属单位
			projectSummary.setDomainId(user.getLoginAccount());
			
			//项目模板
			String templates = request.getParameter("templates");
			projectSummary.setTemplates(templates);
			
			//新建组如果设置公开组为是该组为系统组否则为项目组     08-1-2  这个应用是不对的    应该对应访问权限
			V3xOrgTeam team = new V3xOrgTeam();
			//设置组id与项目id相同方便更新
			team.setId(projectSummary.getId());
			team.setName(projectName);
			if(department!=null&&!department.equals("")){
				team.setDepId(Long.parseLong(department));
			}
			team.setOrgAccountId(user.getLoginAccount());
			//从项目建的当然对应的是项目组
			team.setType(V3xOrgEntity.TEAM_TYPE_PROJECT);
			//如果不设置组公开   将组设为私有的
			team.setIsPrivate(false);
			if(new Byte(publicGroup)!=0){
				team.setIsPrivate(true);
			}
			
			//组主管
			List<Long> members1 = FormBizConfigUtils.parseStr2Ids(request, "manager");
			List<String> projectNameList = new ArrayList<String>();
			
			for(Long id : members1){
				List<ProjectSummary> projectList = projectManager.getAllProjectList(id);
				for(ProjectSummary project : projectList){
					projectNameList.add(project.getProjectName());
				}
			}
			
			boolean repeat = false;
			//检查是否重名
			if(projectNameList!=null&&projectNameList.size()!=0){
				for(String name:projectNameList){
					if(name.equals(projectName)){
						repeat = true;
					}
				}
			}
			
			if(repeat==true){
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('ProjectLang.project_name_repeat'));");
				out.println("</script>");
			    out.flush();
				return super.redirectModelAndView("/project.do?method=projectTransfer&&transferId=1");
			}
		
			team.addTeamMember(members1, V3xOrgEntity.ORGREL_TYPE_TEAM_LEADER);
			
			//组成员
			List<Long> members2 = FormBizConfigUtils.parseStr2Ids(request, "member");
			List<Long> members3 = FormBizConfigUtils.parseStr2Ids(request, "assistant");
			if(members2 != null && members2.size() > 0){
				team.addTeamMember(members2, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			}
			if(members3 != null && members3.size() > 0){
				team.addTeamMember(members3, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			}
			
			//组领导
			List<Long> members4 = FormBizConfigUtils.parseStr2Ids(request, "charge");
			if(members4 != null && members4.size() > 0){
				team.addTeamMember(members4, V3xOrgEntity.ORGREL_TYPE_TEAM_SUPERV);
			}
			
			//组关联人员
			List<Long> members5 = FormBizConfigUtils.parseStr2Ids(request, "interfix");
			if(members5 != null && members5.size() > 0){
				team.addTeamMember(members5, V3xOrgEntity.ORGREL_TYPE_TEAM_RELATIVE);
			}
			
			orgManagerDirect.addTeam(team);
			appLogManager.insertLog(user, AppLogAction.Project_New,user.getName(),projectName);
			
			//增加项目讨论
			V3xBbsBoard bbsBoard = new V3xBbsBoard();				
			bbsBoard.setId(projectSummary.getId());
			bbsBoard.setName(projectName);
			bbsBoard.setAffiliateroomFlag(BbsConstants.BBS_BOARD_AFFILITER.PROJECT.ordinal());
			bbsBoard.setAnonymousFlag((byte)BbsConstants.BBS_BOARD_ANONYONMOUS_NO);
			bbsBoard.setTopNumber(BbsConstants.BBS_BOARD_PUTTER_THREE);
			bbsBoard.setDescription(projectName);
			bbsBoard.setAccountId(user.getLoginAccount());
			bbsBoard.setAnonymousReplyFlag((byte)BbsConstants.BBS_BOARD_ANONYONMOUS_REPLY_NO);
			bbsBoard.setBoardTime(new java.sql.Timestamp(new java.util.Date().getTime()));
			this.bbsBoardManager.createV3xBbsBoard(bbsBoard, members1);
			
			//加入相关人员
			Set<ProjectMember> projectMembers = this.setMembers(request, projectSummary, plog);
			if (projectMembers != null) {
				projectSummary.setProjectMembers(projectMembers);
			}
			
			//加入项目阶段
			Set<ProjectPhase> projectPhaseSet = new HashSet<ProjectPhase>();
			this.handleProjectPhase(request, "addProjectPhases", projectSummary, projectPhaseSet);
			this.handleProjectPhase(request, "updateProjectPhases", projectSummary, projectPhaseSet);
			if(projectPhaseSet != null && projectPhaseSet.size() > 0) {
				projectSummary.setProjectPhases(projectPhaseSet);
			}
			
			projectManager.saveNewProjectSummary(projectSummary, plog);//保存项目
			docHierarchyManager.createNewProject(projectSummary, getMemberId());//知识管理接口
			attachmentManager.create(ApplicationCategoryEnum.project, projectSummary.getId(), projectSummary.getId(), request);//保存附件
			
			//为项目阶段增加提醒任务调度
			for(ProjectPhase projectPhase : projectPhaseSet){
				ProjectUtils.remind4Create(projectPhase);
			}
			
			mav.addObject("create", "create");
			return mav;
		} catch (Exception e) {
			logger.error("创建项目异常", e);
			return null;
		}
	}

	/**
	 * 获取项目成员并设置及记录日志
	 */
	protected Set<ProjectMember> setMembers(HttpServletRequest request, ProjectSummary projectSummary, ProjectLog plog) throws Exception {
		Set<ProjectMember> projectMembers = new HashSet<ProjectMember>();
		
		// 负责人
		String memberType_manager = request.getParameter("manager");
		plog.setManagerDesc(memberType_manager);
		projectMembers = this.setProjectMember(projectMembers, memberType_manager, ProjectMember.memberType_manager, projectSummary);
		
		// 助理
		String memberType_assistant = request.getParameter("assistant");
		plog.setAssistantDesc(memberType_assistant);
		projectMembers = this.setProjectMember(projectMembers, memberType_assistant, ProjectMember.memberType_assistant, projectSummary);
		
		
		// 领导
		String memberType_charge = request.getParameter("charge");
		plog.setChargeDesc(memberType_charge);
		projectMembers = this.setProjectMember(projectMembers, memberType_charge, ProjectMember.memberType_charge, projectSummary);
		
		// 项目成员
		String memberType_member = request.getParameter("member");
		plog.setMemberDesc(memberType_member);
		projectMembers = this.setProjectMember(projectMembers, memberType_member, ProjectMember.memberType_member, projectSummary);
		
		// 相关人员
		String memberType_interfix = request.getParameter("interfix");
		plog.setInterfixDesc(memberType_interfix);
		projectMembers = this.setProjectMember(projectMembers, memberType_interfix, ProjectMember.memberType_interfix, projectSummary);
		
		return projectMembers;
	}

	/**
	 * 设置项目的相关人员
	 * @param set：将所有相关人员加入到set集合中
	 * @param memberlist："1234567,9876543"
	 * @param memberType: "0：负责人 1：领导 2：成员 3：相关人 5：助理"
	 * @param projectId：项目ID
	 */
	protected Set<ProjectMember> setProjectMember(Set<ProjectMember> projectMembers, String memberlist, Byte memberType, ProjectSummary projectSummary)
			throws Exception {
		if (null == memberlist || "".equals(memberlist) || null == memberType  ) {
			return projectMembers;
		}
		String[] member = memberlist.split(",");
		for (int j = 0; j < member.length; j++) {
			ProjectMember projectMember = this.setNewProjectMember(Long .parseLong(member[j]), memberType, projectSummary);
			projectMembers.add(projectMember);
		}
		return projectMembers;
	}

	/**
	 * 设置关联人员
	 */
	protected ProjectMember setNewProjectMember(long memberid, Byte type, ProjectSummary projectSummary) throws Exception {
		ProjectMember projectMember = new ProjectMember();
		projectMember.setIdIfNew();
		projectMember.setMemberid(memberid);
		projectMember.setMemberType(type);
		projectMember.setProjectSummary(projectSummary);
		projectMember.setMemberSort(ProjectUtils.getInstance().getTheSort());
		return projectMember;
	}

	/**
	 * 设置项目阶段
	 */
	private void handleProjectPhase(HttpServletRequest request, String handleType, ProjectSummary projectSummary, Set<ProjectPhase> projectPhaseSet) throws Exception {
		String[] projectPhases = request.getParameterValues(handleType);
		if (projectPhases != null && projectPhases.length > 0) {
			for (int k = 0; k < projectPhases.length; k++) {
				String[] phaseParameter = projectPhases[k].split("-phaseSplit-");
				String phaseId = phaseParameter[0];
				String phaseName = phaseParameter[1];
				String phaseBegintime = phaseParameter[2];
				String phaseClosetime = phaseParameter[3];
				String beforeAlarmDate = phaseParameter[4];
				String endAlarmDate = phaseParameter[5];
				String phaseDesc = "";
				if (phaseParameter.length > 6) {
					phaseDesc = phaseParameter[6];
				}

				ProjectPhase projectPhase = new ProjectPhase(NumberUtils.toLong(phaseId), phaseName, 
						Datetimes.parseDate(phaseBegintime), Datetimes.parseDate(phaseClosetime), 
						NumberUtils.toLong(beforeAlarmDate), NumberUtils.toLong(endAlarmDate), phaseDesc);

				projectPhase.setProjectSummary(projectSummary);
				projectPhaseSet.add(projectPhase);
			}
		}
	}
	
	/**
	 * 获取当前用户的ID号
	 * 
	 * @return
	 */
	public long getMemberId() {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		return memberid;
	}

	/**
	 * 更新项目信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView updateProject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String from = request.getParameter("from");
		String showModalWindows = request.getParameter("showModalWindows");
		try {
			User user = CurrentUser.get();
			PrintWriter out = response.getWriter();
			long projectId = Long.parseLong(request.getParameter("projectId"));
			String projectName = request.getParameter("projectName");
			// 项目信息 需要加载项目进度 级联删除阶段下的进展
			ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, true);
			ProjectSummary projectSummary = projectCompose.getProjectSummary();
			
			if(!this.isManager(projectCompose, null) && !user.isAdministrator()) {
				out.println("<script>");
				out.println("parent.location.reload(true);");
				out.println("</script>");	
				return null ;
			}
			
			//项目操作日志
			ProjectLog plog = new ProjectLog();
			plog.setIdIfNew();
			plog.setOptionDate(new Timestamp(System.currentTimeMillis()));
			plog.setProjectId(projectSummary.getId());
			plog.setUserid(this.getMemberId());
			plog.setProjectDesc("update");
			
			//根据项目负责人检查重名情况
			List<Long> members1 = FormBizConfigUtils.parseStr2Ids(request, "manager");
			List<String> projectNameList = new ArrayList<String>();
			for(Long id : members1){
				List<ProjectSummary> projectList = projectManager.getAllProjectList(id);
				for(ProjectSummary project : projectList){
					if(!projectNameList.contains(project.getProjectName())){
						projectNameList.add(project.getProjectName());
					}
				}
			}
			
			boolean repeat = false;
			//检查是否重名
			if(projectNameList!=null&&projectNameList.size()!=0&&!user.isAdministrator()){
				projectNameList.remove(projectSummary.getProjectName());
				for(String name:projectNameList){
					if(name.equals(projectName)){
						repeat = true;
						break;
					}
				}
			}
			
			if(repeat==true){
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('ProjectLang.project_name_repeat'));");
				out.println("</script>");
				return super.redirectModelAndView("/project.do?method=detailProject&&projectId=" + projectId + "&update=update");
			}
			
			// 设置项目名称
			if(projectName!=null && !projectName.equals("")){
				projectSummary.setProjectName(projectName);
			}
			// 设置项目类型
			String projectType = request.getParameter("projectType");
			if(projectType!=null && !projectType.equals("")){
				String projectTypeIdStr = request.getParameter("projectTypeId");
				Long projectTypeId = NumberUtils.toLong(projectTypeIdStr);
				if(projectTypeId.equals(0L) || projectTypeId == null) {
					log.info("项目类型是:"+projectType+"的id为0了！！！"+"\n"
				+ "此用户是："+CurrentUser.get().getName()+";所用的浏览器为:"+request.getHeader("user-agent"));
				} else {
					projectSummary.setProjectTypeId(projectTypeId);
					projectSummary.setProjectTypeName(projectType);
				}				
			}
			// 开始时间
			String btime = request.getParameter("begintime");
			if(btime!=null && !btime.equals("")){
				projectSummary.setBegintime(Datetimes.parseDate(btime));
			}
			// 结束时间
			String etime = request.getParameter("closetime");
			if(etime!=null && !etime.equals("")){
				projectSummary.setClosetime(Datetimes.parseDate(etime));
			}
			// 描述
			String projectDesc = request.getParameter("projectDesc");
			if(projectDesc!=null && !projectDesc.equals("")){
//                if(projectDesc.length()>255)
//                {
//                    projectDesc=projectDesc.substring(0,80);
//                    projectDesc+="...";
//				    projectSummary.setProjectDesc(projectDesc);
//                }
                projectSummary.setProjectDesc(projectDesc);
			}
			// 部门
			String department = request.getParameter("department");
			if(department!=null&&!department.equals("")){
				projectSummary.setDepartment(Long.parseLong(department));
			}
			// 公开组
			String publicGroup = request.getParameter("publicGroup");
			if(publicGroup!=null && !publicGroup.equals("")){
				projectSummary.setPublicState(new Byte(publicGroup));
			}
			
			
			//项目模板
			String templates = request.getParameter("templates");
			projectSummary.setTemplates(templates);
			
			V3xOrgTeam team = orgManagerDirect.getTeamById(projectId);
			team.setLeaders(new ArrayList<Long>());
			team.setMembers(new ArrayList<Long>());
			team.setRelatives(new ArrayList<Long>());
			team.setSupervisors(new ArrayList<Long>());
			
			if(projectName!=null && !projectName.equals("")){
				team.setName(projectName);
			}else{
				team.setName(projectSummary.getProjectName());
			}
			if(department!=null&&!department.equals("")){
				team.setDepId(Long.parseLong(department));
			}
			team.setOrgAccountId(user.getLoginAccount());
			team.setType(V3xOrgEntity.TEAM_TYPE_PROJECT);
			team.setIsPrivate(false);
			if(publicGroup!=null && !publicGroup.equals("")){
				if(new Byte(publicGroup)!=0){
					team.setIsPrivate(true);
				}
			}
			
			// 组主管
			team.addTeamMember(members1, V3xOrgEntity.ORGREL_TYPE_TEAM_LEADER);
			
			//组成员
			List<Long> members2 = FormBizConfigUtils.parseStr2Ids(request, "member");
			List<Long> members3 = FormBizConfigUtils.parseStr2Ids(request, "assistant");
			if(members2 != null && members2.size() > 0){
				team.addTeamMember(members2, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			}
			if(members3 != null && members3.size() > 0){
				team.addTeamMember(members3, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			}
			//组领导
			List<Long> members4 = FormBizConfigUtils.parseStr2Ids(request, "charge");
			if(members4 != null && members4.size() > 0){
				team.addTeamMember(members4, V3xOrgEntity.ORGREL_TYPE_TEAM_SUPERV);
			}
			
			//组关联人员
			List<Long> members5 = FormBizConfigUtils.parseStr2Ids(request, "interfix");
			if(members5 != null && members5.size() > 0){
				team.addTeamMember(members5, V3xOrgEntity.ORGREL_TYPE_TEAM_RELATIVE);
			}
			
			orgManagerDirect.updateEntity(team);
			//推送模板
		     UpdateTeamEvent event = new UpdateTeamEvent(this);
		     event.setTeam(team);
		     EventDispatcher.fireEvent(event);
			//单位管理员修改项目负责人,其它不修改
			if(Strings.isNotBlank(from)){
				List<Long> oldManagers = new ArrayList<Long>();
				//清空项目负责人
				Set<ProjectMember> projectMembers = projectSummary.getProjectMembers();
				Iterator<ProjectMember> it = projectMembers.iterator();
				while (it.hasNext()) {
					ProjectMember m = it.next();
					if(m.getMemberType().equals(ProjectMember.memberType_manager)){
						oldManagers.add(m.getMemberid());
						it.remove();
					}
				}
				
				//重新设置项目负责人
				String memberType_manager = request.getParameter("manager");
				List<Long> newManagers = FormBizConfigUtils.parseStr2Ids(memberType_manager);
				projectMembers = this.setProjectMember(projectMembers, memberType_manager, ProjectMember.memberType_manager, projectSummary);
				projectSummary.setProjectMembers(projectMembers);
				
				try {
					projectManager.updateProjectSummary(projectSummary);
					this.docHierarchyManager.updateProjectManagerAuth4ProjectFolder(projectId, oldManagers, newManagers);
				} catch (RuntimeException e) {
					logger.error("清空项目负责人出错", e);
				}
				
				out.println("<script>");
				out.println("window.returnValue = 'true' ;") ;
				out.println("window.close();") ;
				out.println("parent.location.reload(true);");
				out.println("</script>");	
				return null;
			}else{
				appLogManager.insertLog(user, AppLogAction.Project_Update, user.getName(), projectName);
				//项目状态
				String projectState = request.getParameter("projectState");
	            //组结束
				if(projectState!=null && !projectState.equals("")){
					projectSummary.setProjectState(Byte.valueOf(projectState));
	                if(Integer.valueOf(projectState)==2){
	                	orgManagerDirect.deleteEntity(team);
	                }
				}
				
				//项目阶段集合
				Set<ProjectPhase> projectPhaseSet = projectSummary.getProjectPhases();
				
				//先清空项目阶段
				if(projectPhaseSet != null && projectPhaseSet.size() > 0) {
					Iterator<ProjectPhase> it = projectPhaseSet.iterator();
					while(it.hasNext()){
						it.next();
						it.remove();
					}
				}
				
				projectSummary.setProjectPhases(projectPhaseSet);
				
				//清空项目人员
				Set<ProjectMember> projectMembers = projectSummary.getProjectMembers();
				List<Long> oldProjectMemberIds = FormBizConfigUtils.collectProperty(projectMembers, "memberid");
				
				Iterator<ProjectMember> it = projectMembers.iterator();
				while (it.hasNext()) {
					it.next();
					it.remove();
				}
				
				projectSummary.setProjectMembers(projectMembers);
				
				try {
					projectManager.updateProjectSummary(projectSummary);
				} catch (RuntimeException e) {
					logger.error("清空项目人员是出错", e);
				}
				
				//新增的项目阶段
				Set<ProjectPhase> addProjectPhases = new HashSet<ProjectPhase>();
				this.handleProjectPhase(request, "addProjectPhases", projectSummary, addProjectPhases);
				
				//更新的项目阶段
				Set<ProjectPhase> updateProjectPhases = new HashSet<ProjectPhase>();
				this.handleProjectPhase(request, "updateProjectPhases", projectSummary, updateProjectPhases);
				
				projectPhaseSet.addAll(addProjectPhases);
				projectPhaseSet.addAll(updateProjectPhases);
				
				//删除的项目阶段id
				String[] deleteProjectPhases = request.getParameterValues("deleteProjectPhases");
				
				//加入项目阶段
				if (projectPhaseSet != null && projectPhaseSet.size() > 0) {
					projectSummary.setProjectPhases(projectPhaseSet);
				}

				//项目当前阶段
				String currentPhaseId = request.getParameter("currentPhaseId");
				if(StringUtils.isNotBlank(currentPhaseId)){
					boolean phflag = true;
					if(projectPhaseSet != null && projectPhaseSet.size() > 0) {
						Iterator<ProjectPhase> i = projectPhaseSet.iterator();
						while(i.hasNext()){
							ProjectPhase pp = i.next();
							if(pp.getId()==NumberUtils.toLong(currentPhaseId)){
								phflag = false;
								projectSummary.setPhaseId(NumberUtils.toLong(currentPhaseId));
							}
						}
					}
					if(phflag)
						projectSummary.setPhaseId(1);
				}else{
					projectSummary.setPhaseId(1);
				}
				//更新项目人员
				this.updateProjectMember(request, projectMembers, projectSummary, plog);
				projectManager.updateProjectSummary(projectSummary, plog);
				
				//为项目阶段更新提醒任务调度
				for(ProjectPhase projectPhase : projectPhaseSet){
					ProjectUtils.remind4Update(projectPhase);
				}
				
				//知识管理接口
				projectSummary.setOldMemberIds(oldProjectMemberIds);
				docHierarchyManager.updateProject(projectSummary, addProjectPhases, updateProjectPhases, deleteProjectPhases, getMemberId());
				
				//保存附件
				attachmentManager.update(ApplicationCategoryEnum.project, projectSummary.getId() , projectSummary.getId() , request);
				
				//修改项目讨论
				V3xBbsBoard bbsBoard = bbsBoardManager.getBoardById(projectSummary.getId());
				bbsBoard.setName(projectName);
				bbsBoard.setDescription(projectName);
				bbsBoard.setAccountId(user.getLoginAccount());
				bbsBoard.setBoardTime(new java.sql.Timestamp(new java.util.Date().getTime()));
				this.bbsBoardManager.updateV3xBbsBoard(bbsBoard, members1);
	
				out.println("<script>");
				if(Strings.isNotBlank(showModalWindows)&& showModalWindows.equals("showWindows")){
					out.println("parent.listFrame.location.href = parent.listFrame.location.href;");
	            }else{
	            	out.println("window.returnValue = 'true' ;") ;
					out.println("window.close();") ;
					out.println("parent.location.reload(true);");
	            }
				out.println("</script>");			
				return null ;
			}
		} catch (Exception e) {
			logger.error("更新项目异常", e);
			return null;
		}
	}

	/**
	 * 更新项目成员 并记录日志
	 */
	protected void updateProjectMember(HttpServletRequest request, Set<ProjectMember> projectMembers, ProjectSummary projectSummary, ProjectLog plog) throws Exception {
		// 负责人
		String memberType_manager = request.getParameter("manager");
		String memberType_manager_back = request.getParameter("manager_back");
		if (!memberType_manager.equals(memberType_manager_back)) {
			String managerDesc = this.setUpdateLog(memberType_manager_back, memberType_manager);
			plog.setManagerDesc(managerDesc);
		}
		projectMembers = this.setProjectMember(projectMembers, memberType_manager, ProjectMember.memberType_manager, projectSummary);
		
		// 领导
		String memberType_assistant = request.getParameter("assistant");
		String memberType_assistant_back = request.getParameter("assistant_back");
		if (!memberType_assistant.equals(memberType_assistant_back)) {
			String chargeDesc = this.setUpdateLog(memberType_assistant_back, memberType_assistant);
			plog.setChargeDesc(chargeDesc);
		}
		projectMembers = this.setProjectMember(projectMembers, memberType_assistant, ProjectMember.memberType_assistant, projectSummary);
		
		// 领导
		String memberType_charge = request.getParameter("charge");
		String memberType_charge_back = request.getParameter("charge_back");
		if (!memberType_charge.equals(memberType_charge_back)) {
			String chargeDesc = this.setUpdateLog(memberType_charge_back, memberType_charge);
			plog.setChargeDesc(chargeDesc);
		}
		projectMembers = this.setProjectMember(projectMembers, memberType_charge, ProjectMember.memberType_charge, projectSummary);
		
		// 项目成员
		String memberType_member = request.getParameter("member");
		String memberType_member_back = request.getParameter("member_back");
		if (!memberType_member.equals(memberType_member_back)) {
			String memberDesc = this.setUpdateLog(memberType_member_back, memberType_member);
			plog.setMemberDesc(memberDesc);
		}
		projectMembers = this.setProjectMember(projectMembers, memberType_member, ProjectMember.memberType_member, projectSummary);
		
		// 相关人员
		String memberType_interfix = request.getParameter("interfix");
		String memberType_interfix_back = request.getParameter("interfix_back");
		if (!memberType_interfix.equals(memberType_interfix_back)) {
			String interfixDesc = this.setUpdateLog(memberType_interfix_back, memberType_interfix);
			plog.setInterfixDesc(interfixDesc);
		}
		projectMembers = this.setProjectMember(projectMembers, memberType_interfix, ProjectMember.memberType_interfix, projectSummary);
	}

	/**
	 * 设置更新时的增加以及删除的人员日志
	 * 
	 * @param _backs
	 * @param _news
	 * @return
	 * @throws Exception
	 */
	protected String setUpdateLog(String _backs, String _news) throws Exception {
		try {
			String[] members_new = _news.split(",");
			String[] members_back = _backs.split(",");
			if (members_new == null) {
				return "@" + _backs;
			}
			if (members_back == null) {
				return _news;
			}
			String _add = "";
			String _delete = "";
			for (int j = 0; j < members_back.length; j++) {
				boolean d = false;
				for (int k = 0; k < members_new.length; k++) {
					if (members_back[j].equals(members_new[k]))
						d = true;
				}
				if (!d) {
					_delete += members_back[j] + ",";
				}
			}
			for (int j = 0; j < members_new.length; j++) {
				boolean a = false;
				for (int k = 0; k < members_back.length; k++) {
					if (members_back[k].equals(members_new[j]))
						a = true;
				}
				if (!a) {
					_add += members_new[j] + ",";
				}
			}
			return _add + "@" + _delete;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 查看项目信息(不包括进展信息)『更新前操作』
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView detailProject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
        String showModalWindows = request.getParameter("showModalWindows");
        long projectId = Long.parseLong(request.getParameter("projectId"));
		String update = request.getParameter("update");
		// 查看项目信息 不需要加载项目进度
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		if (update != null && update.equals("update")) {
			mav = new ModelAndView("project/updateProject");
            if(Strings.isNotBlank(showModalWindows)&& showModalWindows.equals("showWindows")){
                mav.addObject("showModalWindows", showModalWindows);
            }
		} else {
			mav = new ModelAndView("project/detailProject");
		}
		mav.addObject("isManager", this.isManager(projectCompose, null));
		mav.addObject("projectCompose", projectCompose);
		List<ProjectType> pType = this.projectManager.getProjectTypes(CurrentUser.get().getLoginAccount());
		mav.addObject("pTypeList", pType);
		return mav;
	}

	/**
	 * 查看项目信息(包括进展信息)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.NeedNoCheck})
	public ModelAndView getProject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
		
		String from = request.getParameter("from");
		if(from != null){
			//项目首页查看项目详情
			mav = new ModelAndView("project/projectView");	
		}else{
			//个人或单位管理员查看项目详情
			mav = new ModelAndView("project/allProject");
		}
		
		long projectId = Long.parseLong(request.getParameter("projectId"));
		
		//查看项目信息 需要加载项目进度列表
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, true);
		
		mav.addObject("projectCompose", projectCompose);
		
		String readOnly = request.getParameter("readonly");
		if(Strings.isBlank(readOnly)){
			//普通人员查看
			mav.addObject("readnOnly",true);
		}else{
			//单位管理员查看或修改项目负责人
			mav.addObject("memberList",projectCompose.getAllProjectMembers(ProjectConstants.MEMBERTYPE_ASSISTANT, ProjectConstants.MEMBERTYPE_MEMBER, ProjectConstants.MEMBERTYPE_CHARGE, ProjectConstants.MEMBERTYPE_INTERFIX));
			mav.addObject("readnOnly", "1".equals(readOnly) ? true : "0".equals(readOnly) ? false : true);
		}
		
		return mav;
	}
	public String ajaxGetProjectName(long idStr) throws Exception{
		ProjectSummary p = projectManager.getProject(idStr);
		return p.getProjectName();
	}
	public String ajaxGetDepartmentName(long idStr) throws Exception{
		V3xOrgDepartment d = orgManager.getDepartmentById(idStr);
		return d.getName();
	}
	public ModelAndView orderProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/orderProject");
		List<ProjectSummary> projectSummaryList = new ArrayList<ProjectSummary>();
		try {
			projectSummaryList = projectManager.getAllProjectList(getMemberId());
		} catch (Exception e) {
			logger.error("获取项目列表失败",e);
		}
		List<ProjectSummary> projectList2=new ArrayList<ProjectSummary>();
		if(projectSummaryList!=null&&projectSummaryList.size()>0){
			List<Long> ids = new ArrayList<Long>(projectSummaryList.size());
			for(ProjectSummary dr:projectSummaryList){
				if(dr.getProjectState()<=2) {
					projectList2.add(dr);
					ids.add(dr.getId());
				}
			}
			mav.addObject("oldProjects", StringUtils.join(ids, ';'));
		}
		mav.addObject("projectSummaryList", projectSummaryList);
		mav.addObject("projectList2", projectList2);
		return mav;
	}
	
	public ModelAndView saveOrderProject(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String projectIdStr = request.getParameter("projects");
		String oldProjects = request.getParameter("oldProjects");
		if(Strings.isNotBlank(projectIdStr) && !projectIdStr.equals(oldProjects)) {
			String [] projectIds = projectIdStr.split(";");
			projectManager.updateProjectOrder(projectIds, CurrentUser.get().getId());
			super.rendJavaScript(response, "parent.endOrderSave()");
		}
		else {
			super.rendJavaScript(response, "parent.window.close();");
		}
		return null;
	}

	/**
	 * 添加项目进展
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addProjectEvolution(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ProjectEvolution projectEvolution = new ProjectEvolution();
		projectEvolution.setIdIfNew();
		long projectId = Long.parseLong(request.getParameter("projectId"));
		// ===项目操作日志==
		ProjectLog plog = new ProjectLog();
		plog.setIdIfNew();
		plog.setOptionDate(new Timestamp(System.currentTimeMillis()));
		plog.setProjectId(projectId);
		plog.setUserid(this.getMemberId());
		plog.setProjectDesc("addEvolution");// 加入进度

		String projectPhase = request.getParameter("projectPhase");
		ProjectPhase phase = new ProjectPhase();
		phase.setId(Long.parseLong(projectPhase));
		projectEvolution.setProjectPhase(phase);
		String evolutionPercent = request.getParameter("evolutionPercent");
		projectEvolution
				.setEvolutionPercent(Float.parseFloat(evolutionPercent));
		String evolutionState = request.getParameter("evolutionState");
		projectEvolution.setEvolutionState(Byte.valueOf(evolutionState));
		String evolutionDesc = request.getParameter("evolutionDesc");
		projectEvolution.setEvolutionDesc(evolutionDesc);
		projectEvolution.setEvolutionDate(new Date());
		projectEvolution.setUserId(getMemberId());
		projectManager.saveProjectEvolution(projectEvolution, plog);
		// 保存附件
		attachmentManager.create(ApplicationCategoryEnum.project, projectId,
				projectEvolution.getId(), request);
		return super
				.redirectModelAndView("/project.do?method=getProject&projectId="
						+ projectId);
	}

	/**
	 * 删除项目(标记删除)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView removeProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
//		long projectId = Long.parseLong(request.getParameter("projectId"));
		String projectIds = request.getParameter("projectId");
		String[] projectIdsArray = projectIds.split(",");
		User user = CurrentUser.get();
		List<String[]> labelsList = new ArrayList<String[]>();
		for(String projectId : projectIdsArray){
			// ===项目操作日志==
			ProjectLog plog = new ProjectLog();
			plog.setIdIfNew();
			plog.setOptionDate(new Timestamp(System.currentTimeMillis()));
			plog.setProjectId(new Long(projectId));
			plog.setUserid(this.getMemberId());
			plog.setProjectDesc("delete");// 加入操作描述
			
			ProjectSummary project = projectManager.getProject(new Long(projectId));
			
		//先注释掉-暂没验证--删除项目时候同时判断项目下是否有文档代码	
//			删除项目-条件是项目文档为空/项目阶段文档为空才能删除
			
//			项目阶段集合
			Set<ProjectPhase> projectPhaseSet = project.getProjectPhases();
			
//			判断项目阶段下是否有文档 //项目阶段文档为空返回false,不空为true
			if(projectPhaseSet != null && projectPhaseSet.size() > 0) {
				for(ProjectPhase pp : projectPhaseSet){
					if(docHierarchyManager.hasDocsInProject(pp.getId())){
						PrintWriter out = response.getWriter();				
						out.println("<script>");
						out.println("alert(parent.v3x.getMessage('ProjectLang.project_Doc_del'));");
						out.println("self.history.back();");
						out.println("</script>");
						return  null;
					}
				}
				
			}					
			boolean projectDoc = docHierarchyManager.hasDocsInProject(Long.valueOf(projectId));//项目文档为空返回false,不空为true
			
			if(projectDoc){
				PrintWriter out = response.getWriter();				
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('ProjectLang.project_Doc_del'));");
				out.println("self.history.back();");
				out.println("</script>");
				return  null;
			}else{
				if(projectPhaseSet != null && projectPhaseSet.size() > 0){
					for(ProjectPhase pp : projectPhaseSet){
						docHierarchyManager.removeProjectFolderWithoutAcl(pp.getId());//删除项目阶段文档夹项目阶段文档夹
					}
				}				
				docHierarchyManager.removeProjectFolderWithoutAcl(Long.valueOf(projectId));//删除项目文档夹项目阶段文档夹
			}
					
			if (project.getProjectState() != null && project.getProjectState()==ProjectSummary.state_create) {// 当前的项目处于刚创建阶段时作物理删除
				projectManager.removeProjectSummary(new Long(projectId), plog);
			}
			projectManager.deleteProjectSummary(new Long(projectId), plog);// 标记删除
			docHierarchyManager.deleteProject(new Long(projectId), getMemberId());// 知识管理接口
//			删除组
			V3xOrgTeam team = orgManagerDirect.getTeamById(new Long(projectId));
			if (team!=null){
			orgManagerDirect.deleteEntity(team);}
			
			String[] label = {user.getName(),project.getProjectName()};
			labelsList.add(label);
		}
		appLogManager.insertLogs(CurrentUser.get(), AppLogAction.Project_Delete, labelsList);
		return super.refreshWorkspace();
	}

	/**
	 * 获取当前项目的日志文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getProjectLog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/projectLog");
		long projectId = Long.parseLong(request.getParameter("projectId"));
		List<ProjectLogCompose> logList = projectManager
				.getProjectLogList(projectId);		
		mav.addObject("logList", logList);
		mav.addObject("projectName", request.getParameter("projectName"));
		return mav;
	}

	/**
	 * 页面中转
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes=RoleType.ProjectCreator)
	public ModelAndView projectTransfer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/createProject");
		List<String> projectNameList = new ArrayList<String>();
		List<ProjectSummary> projectList = projectManager.getAllProjectList(getMemberId());
		for( ProjectSummary project :  projectList){
			projectNameList.add(project.getProjectName());
		}
		mav.addObject("projectList", projectNameList);
		List<ProjectType> pType = this.projectManager.getProjectTypes(CurrentUser.get().getLoginAccount());
		Date init = new Date();
		mav.addObject("init", init);
		mav.addObject("pTypeList", pType);
		return mav;
	}
	
	public ModelAndView addProjectPhase(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Metadata remindTimeMetaData = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
		return new ModelAndView("project/addPhase", "remindTimeMetaData", remindTimeMetaData);
	}
	
	/**
	 * 项目详细信息
	 */
	public ModelAndView projectInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/projectInfo");
		long projectId = Long.parseLong(request.getParameter("projectId"));
		boolean hasAuthForNew = false;
		boolean hasAuthForDoc = false;
		boolean flag = false;
		boolean relat = false;
		boolean state = true ;  //判断是不是结束
		ProjectSummary projectSummary = projectManager.getProject(projectId) ;
		boolean exist = !projectSummary.getProjectState().equals(ProjectSummary.state_delete);
		if(projectSummary.getProjectState().intValue() >= 2){ 
		     state = false ;
		}
		User user = CurrentUser.get();
		boolean isStillInProject = projectManager.canUserViewProject(projectId, user.getId());
		
		boolean fromSpaceMenu = "spaceMenu".equals(request.getParameter("spaceFlag"));
		//关联项目作为空间导航配置时，必须为已启用、进行中，否则点击导航菜单时，进行防护并刷新空间导航菜单显示
		if(fromSpaceMenu) {
			if(!exist || !state || !isStillInProject) {
				PrintWriter out = response.getWriter();
				super.printV3XJS(out);
				out.println("<script>");
				out.println("	alert('" + ResourceBundleUtil.getString(ProjectUtils.PROJECT_RESOUCE, "project.alert.deletedorclosed") + "');");
				out.println("	getA8Top().contentFrame.topFrame.realignSpaceMenu('" + user.getLoginAccount() + "');");
				out.println("	getA8Top().contentFrame.topFrame.backToPersonalSpace();");
				out.println("</script>");
				out.close();
				return null;
			}
		} else {
			//首页关联项目栏目中查看时，只作项目是否已被取消的判断防护
			if(!exist || !isStillInProject) {
				PrintWriter out = response.getWriter();
				super.printV3XJS(out);
				out.println("<script>");
				out.println("	alert('" + ResourceBundleUtil.getString(ProjectUtils.PROJECT_RESOUCE, "project.alert.deleted") + "');");
				out.println("	getA8Top().contentFrame.topFrame.backToPersonalSpace();");
				out.println("</script>");
				out.close();
				return null;
			}
		}
		
		mav.addObject("projectState", state) ;
	
		// 查看项目信息 需要加载项目进度列表
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		
		mav.addObject("exist", exist);
		List<V3xOrgMember> leader = projectCompose.getPrincipalLists();
		List<V3xOrgMember> members = projectCompose.getMemberLists();
		List<V3xOrgMember> relateMem= projectCompose.getInterfixLists();
		if(CollectionUtils.isNotEmpty(members)) {
		      for(V3xOrgMember mem : members){
		    	  if(mem != null && mem.getId().longValue()== user.getId()) {
		    		  flag = true;
		    		  break;
		    	  }
		      }
		}
		
		for(V3xOrgMember member : leader){
			if(member.getId().longValue() == user.getId()){
				hasAuthForNew = true;
				break;
			}
		}
		if(flag || hasAuthForNew){
			hasAuthForDoc= true;
		}
		
		if(!hasAuthForNew){
			hasAuthForNew = flag;
		}
		
		if(CollectionUtils.isNotEmpty(relateMem)) {
			for(V3xOrgMember member : relateMem){
				if(member != null && member.getId().longValue()==user.getId()) {
					relat = true;
					break;
				}
			}
		}
		
		//某个阶段|当前阶段|所有阶段
		String phaseIds = request.getParameter("phaseId");
		Long phaseId = null;
		if(StringUtils.isNotBlank(phaseIds)){
			phaseId = NumberUtils.toLong(phaseIds);
		}else{
			phaseId = projectSummary.getPhaseId();
		}
		
		//项目协同，抽取所有与项目相关的事项   流程中有我的  显示有效的协同  已发  已办  待办
		List<Affair> colList = colManager.getColSummaryByProjectId(projectId, 8, phaseId);
		mav.addObject("colList", colList);
		
		//项目任务
		boolean leaderOrManager = this.isInRoleTypes(projectCompose, user.getId(), ProjectMember.memberType_charge, 
				ProjectMember.memberType_manager, ProjectMember.memberType_assistant);
		List<TaskInfo> projectTasks = this.taskInfoManager.getProjectTasks4Section(projectId, phaseId, user.getId(), leaderOrManager);
		mav.addObject("projectTasks", projectTasks);
		TaskUtils.renderMetadatas4Task(mav, metadataManager);
		
		//项目计划/会议/日程
		List<Affair> mtAndPlanCalList = this.setProjectMtAndPlanAndCal(projectId, phaseId);
		
		if(mtAndPlanCalList!=null&&mtAndPlanCalList.size()>9){
			mtAndPlanCalList = mtAndPlanCalList.subList(0, 9);
		}
		mav.addObject("mtAndPlanList", mtAndPlanCalList);
		
		this.renderProjectDocs(projectId, phaseId, mav, true);
		
		//项目讨论
		List<V3xBbsArticle> v3xBbsArticles = bbsArticleManager.ProjectqueryArticleList(projectId, 8, phaseId, null, null, null);
		List<ArticleModel> bbsMode = null;
		if(CollectionUtils.isNotEmpty(v3xBbsArticles)) {
			bbsMode = new ArrayList<ArticleModel>();
			for(V3xBbsArticle ba:v3xBbsArticles) {
				bbsMode.add(new ArticleModel(ba));			
			}
		}
		mav.addObject("bbsList", bbsMode);
		
		//项目留言
		String flagStr = String.valueOf(UUIDLong.longUUID());
		List<LeaveWord> leaveWordList = this.setProjectLeaveWord(projectId, phaseId, flagStr);
		mav.addObject("flagStr", flagStr);
		mav.addObject("leaveList", leaveWordList);
		
		//添加项目进度
		ProjectUtils.addProjectProcess(projectCompose, mav);
		
		//获取创建者是自己或自己包括在4种人员中的相关项目
		List<ProjectSummary> projectSummaryList = projectManager.getAllProjectList(getMemberId(),false,false);
		mav.addObject("projectSummaryList", projectSummaryList);
		mav.addObject("projectCompose", projectCompose);
		
		mav.addObject("hasAuthForNew", hasAuthForNew);
		mav.addObject("hasAuthForDoc", hasAuthForDoc);
		mav.addObject("relat", relat);
		
		mav.addObject("isManager", this.isManager(projectCompose, user.getId()));//项目负责人或项目助理
		mav.addObject("phaseId", phaseId);
		
		return mav;
	}

	/**
	 * 显示项目文档
	 * @param homePage 是否在项目首页(只显示9条文档，如果是在更多页面，则按照分页显示)
	 */
	private void renderProjectDocs(long projectId, Long phaseId, ModelAndView mav, boolean homePage) throws DocException {
		List<FolderItemDoc> docList = null;
		boolean hasAcl = false;
		boolean addAcl = false;
		String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
		Set<Integer> sets = docAclManager.getDocResourceAclList(projectId, orgIds);
		
		boolean all = sets != null && sets.contains(Constants.ALLPOTENT);
		boolean edit = sets != null && sets.contains(Constants.EDITPOTENT);
		boolean add = sets != null && sets.contains(Constants.ADDPOTENT);
		boolean readonly = sets != null && sets.contains(Constants.READONLYPOTENT);
		boolean browse = sets != null && sets.contains(Constants.BROWSEPOTENT);
		boolean list = sets != null && sets.contains(Constants.LISTPOTENT);
		if(all || edit || add || readonly || browse || list) {
			hasAcl = true;
			if(homePage) {
				Pagination.setNeedCount(false);
				Pagination.setFirstResult(0);
				Pagination.setMaxResults(9);
			}
		} 
		docList = docHierarchyManager.getLatestDocsOfProject(projectId, phaseId,orgIds, hasAcl);
		if(all || edit || add) {
			addAcl = true;
		}
		mav.addObject("docList", docList);
		mav.addObject("hasAcl", hasAcl);
		mav.addObject("addAcl", addAcl);
		mav.addObject("all", all);
		mav.addObject("edit", edit);
		mav.addObject("add", add);
		mav.addObject("readonly", readonly);
		mav.addObject("browse", browse);
		mav.addObject("list", list);
	}
	/**
	 * 查询更多
	 * @param homePage 是否在项目首页(只显示9条文档，如果是在更多页面，则按照分页显示)
	 */
	private void renderProjectDocsByCondition(String condition,long projectId, Long phaseId, ModelAndView mav, boolean homePage, Map<String,String> paramMap) throws DocException {
		List<FolderItemDoc> docList = new ArrayList<FolderItemDoc>() ;
		boolean hasAcl = false;
		boolean addAcl = false;
		String orgIds = Constants.getOrgIdsOfUser(CurrentUser.get().getId());
		Set<Integer> sets = docAclManager.getDocResourceAclList(projectId, orgIds);
		
		boolean all = sets != null && sets.contains(Constants.ALLPOTENT);
		boolean edit = sets != null && sets.contains(Constants.EDITPOTENT);
		boolean add = sets != null && sets.contains(Constants.ADDPOTENT);
		boolean readonly = sets != null && sets.contains(Constants.READONLYPOTENT);
		boolean browse = sets != null && sets.contains(Constants.BROWSEPOTENT);
		boolean list = sets != null && sets.contains(Constants.LISTPOTENT);
		if(all || edit || add || readonly || browse || list) {
			hasAcl = true;
			if(homePage) {
				Pagination.setNeedCount(false);
				Pagination.setFirstResult(0);
				Pagination.setMaxResults(9);
			}
		}
		docList = docHierarchyManager.getLatestDocsOfProjectByCondition(condition,projectId, phaseId, paramMap, orgIds, hasAcl);
		if(all || edit || add) {
			addAcl = true;
		}
		mav.addObject("docList", docList);
		mav.addObject("hasAcl", hasAcl);
		mav.addObject("addAcl", addAcl);
		mav.addObject("all", all);
		mav.addObject("edit", edit);
		mav.addObject("add", add);
		mav.addObject("readonly", readonly);
		mav.addObject("browse", browse);
		mav.addObject("list", list);
	}
	
	/**
	 * 更多项目文档
	 */
	public ModelAndView moreProjectDoc(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/moreProjectDoc");
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		Long phaseId = NumberUtils.toLong(request.getParameter("phaseId"));
		this.renderProjectDocs(projectId, phaseId, mav, false);
		
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		ProjectUtils.addProjectProcess(projectCompose, mav);
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("isManager", this.isManager(projectCompose, null));
		mav.addObject("morePro", ApplicationCategoryEnum.doc);
		mav.addObject("projectId", projectId);
		mav.addObject("phaseId", phaseId);
		mav.addObject("condition", "choice");
		return mav;
	}
	
	/**
	 * 条件查询更多项目文档
	 */
	public ModelAndView queryMoreProjectDocByCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/moreProjectDoc");
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		Long phaseId = NumberUtils.toLong(request.getParameter("phaseId"));
		
		String condition = request.getParameter("condition") ;
		String name = request.getParameter("name") ;
		String modifyDate = request.getParameter("modifyDate") ;
		Map<String,String> param = new HashMap<String,String>() ;
		param.put("name", name);
		param.put("modifyDate", modifyDate);
		
		this.renderProjectDocsByCondition(condition, projectId, phaseId, mav, false, param) ;
		
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		ProjectUtils.addProjectProcess(projectCompose, mav);
		
		mav.addObject("condition", condition);
		if ("name".equals(condition))
			mav.addObject("name", name);
		else if("modifyDate".equals(condition))
			mav.addObject("modifyDate", modifyDate);
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("isManager", this.isManager(projectCompose, null));
		mav.addObject("morePro", ApplicationCategoryEnum.doc);
		mav.addObject("projectId", projectId);
		mav.addObject("phaseId", phaseId);
		return mav;
	}
	
	
	/**
	 * 更多项目计划/会议/日程
	 */
	public ModelAndView moreProjectPlanAndMeeting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/moreProjectPlanAndMeeting");
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		
		//某个阶段|当前阶段|所有阶段
		String phaseIds = request.getParameter("phaseId");
		Long phaseId = null;
		if(StringUtils.isNotBlank(phaseIds)){
			phaseId = NumberUtils.toLong(phaseIds);
		}else{
			phaseId = projectCompose.getProjectSummary().getPhaseId();
		}
		
		List<Affair> mtAndPlanList = this.setProjectMtAndPlanAndCal(projectId, phaseId);
		
		//处理分页
		Pagination.setRowCount(mtAndPlanList.size());
		int first = Pagination.getFirstResult();
		int pageSize = Pagination.getMaxResults();
		int end1 = first + pageSize;
		int end2 = mtAndPlanList.size();
		int end = 0;
		if (end1 > end2)
			end = end2;
		else
			end = end1;
		mtAndPlanList = mtAndPlanList.subList(first, end);

		//添加项目进度
		ProjectUtils.addProjectProcess(projectCompose, mav);
		
		mav.addObject("projectId", projectId);
		mav.addObject("phaseId", phaseId);
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("mtAndPlanList", mtAndPlanList);
		mav.addObject("isManager", this.isManager(projectCompose, null));
		mav.addObject("morePro", ApplicationCategoryEnum.calendar);
		return mav;
	}
	
	/**
	 * 条件查询更多项目计划/会议/日程
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public ModelAndView queryMoreProjectPlanAndMeetingByCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/moreProjectPlanAndMeeting");
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		
		//某个阶段|当前阶段|所有阶段
		String phaseIds = request.getParameter("phaseId");
		Long phaseId = null;
		if(StringUtils.isNotBlank(phaseIds)){
			phaseId = NumberUtils.toLong(phaseIds);
		}else{
			phaseId = projectCompose.getProjectSummary().getPhaseId();
		}
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		String condition = request.getParameter("condition") ;
		
		String title = request.getParameter("title");
		String author = request.getParameter("author");
		String newDate = request.getParameter("newDate");
		paramMap.put("title", title);
		paramMap.put("author", author);
		paramMap.put("newDate", newDate);
		
		List<Affair> mtAndPlanList = new ArrayList<Affair>() ;
		if ("title".equals(condition) && title != null && !"".equals(title.trim())) {
			mtAndPlanList = this.setProjectMtAndPlanAndCalByCondition(condition,projectId, phaseId, paramMap); 
		} else if ("author".equals(condition) && author != null && !"".equals(author.trim())) {
			Map<String, Object> param = new HashMap<String, Object>();
			String hql = "select m.id from " + V3xOrgMember.class.getName() + " m where m.name like :name and m.isAdmin != 1";
			param.put("name", "%" + author + "%");
			List<Long> ids = searchManager.searchByHql(hql, -1, -1, param);
			if (CollectionUtils.isNotEmpty(ids)) {
				paramMap.put("author", ids);
				mtAndPlanList = this.setProjectMtAndPlanAndCalByCondition(condition, projectId, phaseId, paramMap);
			}
		} else if ("newDate".equals(condition) && newDate != null && !"".equals(newDate.trim())) {
			mtAndPlanList = this.setProjectMtAndPlanAndCalByCondition(condition,projectId, phaseId, paramMap); 
		} else {
			mtAndPlanList = this.setProjectMtAndPlanAndCal(projectId, phaseId);
		}

		//处理分页
		Pagination.setRowCount(mtAndPlanList.size());
		int first = Pagination.getFirstResult();
		int pageSize = Pagination.getMaxResults();
		int end1 = first + pageSize;
		int end2 = mtAndPlanList.size();
		int end = 0;
		if (end1 > end2)
			end = end2;
		else
			end = end1;
		mtAndPlanList = mtAndPlanList.subList(first, end);

		//添加项目进度
		ProjectUtils.addProjectProcess(projectCompose, mav);
		
		mav.addObject("condition", condition) ;
		if ("title".equals(condition))
			mav.addObject("title", title) ;
		if ("author".equals(condition))
			mav.addObject("author", author) ;
		if ("newDate".equals(condition))
			mav.addObject("newDate", newDate) ;
		mav.addObject("projectId", projectId);
		mav.addObject("phaseId", phaseId);
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("mtAndPlanList", mtAndPlanList);
		mav.addObject("isManager", this.isManager(projectCompose, null));
		mav.addObject("morePro", ApplicationCategoryEnum.calendar);
		return mav;
	}
	
	/**
	 * 更多项目协同
	 */
	public ModelAndView moreProjectCol(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("project/moreProjectCol");
		
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		
		//某个阶段|当前阶段|所有阶段
		String phaseIds = request.getParameter("phaseId");
		Long phaseId = null;
		if(StringUtils.isNotBlank(phaseIds)){
			phaseId = NumberUtils.toLong(phaseIds);
		}else{
			phaseId = projectCompose.getProjectSummary().getPhaseId();
		}
		List<Affair> colList = colManager.getColSummaryByProjectId(projectId, -1, phaseId);
		
		//添加项目进度
		ProjectUtils.addProjectProcess(projectCompose, mav);
		
		mav.addObject("projectId", projectId);
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("colList", colList);
		mav.addObject("isManager", this.isManager(projectCompose, null));
		mav.addObject("morePro", ApplicationCategoryEnum.collaboration);
		return mav;
	}
	
	/**
	 * 项目协同更多条件查询
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public ModelAndView queryMoreProjectColByCondition(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/moreProjectCol");
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));

		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);

		// 某个阶段|当前阶段|所有阶段
		String phaseIds = request.getParameter("phaseId");
		Long phaseId = null;
		if (StringUtils.isNotBlank(phaseIds)) {
			phaseId = NumberUtils.toLong(phaseIds);
		} else {
			phaseId = projectCompose.getProjectSummary().getPhaseId();
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String condition = request.getParameter("condition");

		// 发起人
		String author = request.getParameter("author");
		if (Strings.isNotBlank(author)) {
			Map<String, Object> param = new HashMap<String, Object>();
			String hql = "select m.id from " + V3xOrgMember.class.getName() + " m where m.name like :name and m.isAdmin != 1";
			param.put("name", "%" + SQLWildcardUtil.escape(author) + "%");
			List<Long> ids = searchManager.searchByHql(hql, param);
			paramMap.put("author", ids); // 此处为 "1,2,3" 格式
		}
		String title = request.getParameter("title") == null ? "" : request.getParameter("title");
		String newDate = request.getParameter("newDate") == null ? "" : request.getParameter("newDate");
		paramMap.put("title", title);
		paramMap.put("newDate", newDate);
		List<Affair> colList = colManager.getColSummaryByCondition(condition, projectId, -1, phaseId, paramMap);

		// 添加项目进度
		ProjectUtils.addProjectProcess(projectCompose, mav);

		if ("title".equals(condition))
			mav.addObject("title", title);
		if ("author".equals(condition))
			mav.addObject("author", author);
		if ("newDate".equals(condition))
			mav.addObject("newDate", newDate);
		mav.addObject("condition", condition);
		mav.addObject("projectId", projectId);
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("colList", colList);
		mav.addObject("isManager", this.isManager(projectCompose, null));
		mav.addObject("morePro", ApplicationCategoryEnum.collaboration);
		return mav;
	}
	
	/**
	 * 更多项目模板
	 */
	public ModelAndView moreProjectTemplete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/moreProjectTemplete");
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, false);
		
		//项目模板
		List<Long> idList = FormBizConfigUtils.parseStr2Ids(projectCompose.getProjectSummary().getTemplates());
		List<Templete> templeteList = templeteManager.getListByIds(idList);
		
		//项目协同模板
		List<Templete> colTempleteList = templeteManager.getTempleteByPropectId(projectId, 100);
		
		for(Templete t : colTempleteList){
			if(idList!=null && !idList.contains(t.getId())){
				templeteList.add(t);
			}
		}
		
		//添加项目进度
		ProjectUtils.addProjectProcess(projectCompose, mav);
		
		mav.addObject("projectCompose", projectCompose);
		mav.addObject("templeteList", templeteList);
		mav.addObject("isManager", this.isManager(projectCompose, null));
		
		return mav;
	}
	
	/**
	 *  项目分类设置进入方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView systemFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("project/systemFrame");
	}
	
	
	/**
	 * 显示 项目应用的菜单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView showToolBar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("project/systemToolBar");
	}
	
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView showDetali(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("project/systemDetailHtml");
	}
	
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView showListFrame(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		return new ModelAndView("project/showListFrame");
	}
	
	/**
	 * 显示 项目应用类型
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView showSystemTree(HttpServletRequest request, HttpServletResponse response)throws Exception {
		User user = CurrentUser.get();
		List<ProjectType> ptList = this.projectManager.getProjectTypes(user.getLoginAccount());
		return new ModelAndView("project/systemTree", "ptList", ptList);
	}
	
	/**
	 * 项目分类设置的显示数据方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView systemList(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView result = new ModelAndView("project/systemList");
		
		String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1");
		
		Long projectTypeId = NumberUtils.toLong(request.getParameter("projectTypeId"), -1l);
		
		List<ProjectSummary> ptList = new ArrayList<ProjectSummary>();
		if(projectTypeId == -1l) {
			List<ProjectSummary> temp = projectManager.getProjectList(null, CurrentUser.get().getAccountId());
			ptList = FormBizConfigUtils.pagenate(temp);
		}
		else {
			ptList = projectManager.getProjectsOfTypeByAdmin(CurrentUser.get().getAccountId(), projectTypeId, condition, textfield, textfield1);
		}
		
		List<ProjectCompose> projectComposeList = new ArrayList<ProjectCompose>();
		if(CollectionUtils.isNotEmpty(ptList)) {
			for(ProjectSummary p : ptList){
				ProjectCompose projectCompose = projectManager.detailProject(p, true);
				projectComposeList.add(projectCompose);
			}
		}
		result.addObject("ptList", projectComposeList);
		return result;
	}
	
	/**
	 * 进入项目分类设置的添加方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView addType(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("project/systemEdit");
		//获取全部的类型--前台判断名称重复
		List<ProjectType> temp = this.projectManager.getProjectTypes(CurrentUser.get().getLoginAccount());
		result.addObject("projectList", temp);
		result.addObject("systemEumitosis",1);//前台用来判断显示标签的内容1.添加2.修改3.预览
		return result;
	}
	//保存项目分类 create and modify 方法
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView saveProjectType(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long accountId	= CurrentUser.get().getLoginAccount();
		String pTypeId = request.getParameter("id");
		
		ProjectType project = new ProjectType();
		bind(request,project);
		project.setAccountId(accountId);
		if (Strings.isBlank(pTypeId)) {
			this.projectManager.addProjectType(project);
		}else{
			this.projectManager.updateProjectType(project,new Long(pTypeId));
		}
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("var rv = [\"" + project.getId() + "\", \"" + Functions.toHTMLWithoutSpaceEscapeQuote(project.getName())+"\"];");
		out.println("parent.window.returnValue = rv;");
		out.println("parent.window.close();");
		out.println("</script>");

		return null;
	}
	//删除项目分类
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView projectDelete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
        String alerString=null;
		try {
			String[] ids = request.getParameterValues("id");
            
           
			Long l = null;
            List list=null;
			PrintWriter out = response.getWriter();
			for (int i = 1; i < ids.length; i++) {
				l = Long.valueOf(ids[i]);
                list=projectManager.getProjectSummaryUseType(l);
                if(list.size()>0){
                    ProjectSummary ps=(ProjectSummary)list.get(0);
                    alerString= "\""+ResourceBundleUtil.getString(ProjectUtils.PROJECT_RESOUCE, "project.alert.delte.useprompt", ps.getProjectTypeName())+"\"";
                    break;
                }
                else{
                    this.projectManager.deleteProjectType(l);
                    alerString ="parent.v3x.getMessage('ProjectLang.project_option_ok')";
                }
			}
			out.println("<script>");
			out.println("alert("+alerString+");");
		
			out.println("parent.location.reload(true);");
			out.println("</script>");
            return null;
			//return super.refreshWindow("parent");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//修改项目分类
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView modifyProject(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("project/systemEdit");
		Long projectId = Long.valueOf(request.getParameter("id"));
		ProjectType pj = this.projectManager.getProjectTypeById(projectId);
		
		result.addObject("pType", pj);
		
		String view = request.getParameter("view");//预览
		
		boolean readOnly = false;
		if (null != view && view.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("systemEumitosis",3);
		}else{
			//获取全部的类型--前台判断名称重复,修改时候，不包括本身
			List<ProjectType> temp = this.projectManager.getProjectTypes(CurrentUser.get().getLoginAccount());
			for(ProjectType pt:temp)
			{
				if(pt.getId().longValue()==projectId.longValue())
				{
					temp.remove(pt);
					break;
				}
			}
			result.addObject("projectList", temp);
			result.addObject("systemEumitosis",2);
		}
		
		
		return result;
	}
	
	/**
	 * 新建 项目类型
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView doActionProjectType(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("project/systemType");
		List<ProjectType> temp = this.projectManager.getProjectTypes(CurrentUser.get().getLoginAccount());
		mav.addObject("projectList", temp);
		String projectId = request.getParameter("id");
		
		if(Strings.isNotBlank(projectId)){
			Long id = Long.valueOf(projectId);
			ProjectType pj = this.projectManager.getProjectTypeById(id);
			mav.addObject("pType", pj);
		}
		return mav;
	}
	
	public ModelAndView judgeProjectTypeIsContainProjectSummary(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		PrintWriter out = response.getWriter();
		boolean returnstr = false;
		String projectId = request.getParameter("id");
		if(Strings.isNotBlank(projectId)){
			Long id = Long.valueOf(projectId);
			ProjectType pj = this.projectManager.getProjectTypeById(id);
			List<ProjectSummary>  list = projectManager.getProjects(CurrentUser.get().getAccountId());
			if(list!=null){
				for(ProjectSummary s : list){
					if(s!=null&&pj!=null&&s.getProjectTypeName().equals(pj.getName())){
						returnstr = true;
						out.write(String.valueOf(returnstr));
						break;
					}
				}
				if(!returnstr){
					out.write(String.valueOf(returnstr));
				}
			}
			//out.close();
		}
		return null;
	}
	
	@CheckRoleAccess(roleTypes={RoleType.Administrator})
	public ModelAndView deleteProjectType(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		String id = request.getParameter("id");
		if(Strings.isNotBlank(id)){
			Long l = Long.parseLong(id);
			PrintWriter out = response.getWriter();
			out.println("<script>");
			try {
				this.projectManager.deleteProjectType(l);
				out.println("endDeleteCategory(true);");
			}
			catch (BusinessException e) {
				out.println("endDeleteCategory(false);");
			}		
			
			out.println("</script>");
			
		}
		
		return null;
	}
	
//	查看项目操作日志
	public ModelAndView viewLog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/log");
		String projectId = request.getParameter("projectId");				
		mav.addObject("projectId", projectId);
		return mav;
	}
	
	/**
	 * 配置项目模板
	 */
	public ModelAndView setTemplete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		String templates = request.getParameter("templates");
		
		projectManager.updateProjectFieldById(projectId, "templates", templates);
		
    	return null;
	}
	
	/**
	 * 修改项目进度
	 */
	public ModelAndView setProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		float num = NumberUtils.toFloat(request.getParameter("process"));
		
		projectManager.updateProjectFieldById(projectId, "projectProcess", num);
		
		String jsContent = "parent.window.returnValue = \"success\"; parent.window.close();";
    	super.rendJavaScript(response, jsContent);
    	return null;
	}
	
	/**
	 * 修改项目当前阶段
	 */
	public ModelAndView setPhase(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/setPhase");
		
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		ProjectSummary projectSummary = projectManager.getProject(projectId);

		mav.addObject("currentPhase", projectSummary.getPhaseId());
		mav.addObject("phaseList", projectSummary.getProjectPhases());
		return mav;
	}
	
	/**
	 * 修改项目当前阶段
	 */
	public ModelAndView savePhase(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		Long phaseId = NumberUtils.toLong(request.getParameter("phaseId"));
		
		projectManager.updateProjectFieldById(projectId, "phaseId", phaseId);
		
		String jsContent = "parent.window.returnValue = \"success\"; parent.window.close();";
    	super.rendJavaScript(response, jsContent);
    	return null;
	}
	
	/**
	 * 获取当前用户所有项目信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView projectSearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav;
	    mav = new ModelAndView("project/allProjectMore");//更多			 
	    //判断是否有项目操作权限
		boolean isProjectBuilder = false;
		User user = CurrentUser.get();
		V3xOrgRole role = new V3xOrgRole();
		try {
			role = orgManager.getRoleByName( V3xOrgEntity.ORGENT_META_KEY_PROJECTBUILD , user.getLoginAccount() );
		} catch (BusinessException e) {
			logger.error("获取角色失败",e);
		}
		if(role!=null){
			List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
			try {
				members = orgManager.getMemberByRole(role.getType(), user.getLoginAccount(), role.getId());
			} catch (BusinessException e) {
				logger.error("获取角色人员失败",e);
			}
			if(members.size()>0){
				for(V3xOrgMember member : members){
					if(member.getId().longValue()==user.getId()){
						isProjectBuilder = true;
						break;
					}
				}
			}
		}
		String condition=request.getParameter("condition");
		String field=request.getParameter("textfield");
		String field1=request.getParameter("textfield1");
		String managerID=request.getParameter("managerID");
		if(field==null){
			field="";
		}
		if(field1==null){
			field1="";
		}
		if(managerID==null){
			managerID="";
		}
		List<ProjectSummary> projectSummaryList = new ArrayList<ProjectSummary>();
		try {
            //根据搜索条件抽取创建者是自己或自己包括在4种人员中的
			if(condition!=null&&!condition.equals("")){
				if(condition.equals("projectName"))
					projectSummaryList = projectManager.getAllProjectListByCondition(getMemberId(),"projectName",field,"");
				else if(condition.equals("projectManager"))
					projectSummaryList = projectManager.getAllProjectListByCondition(getMemberId(),"projectManager",managerID,"");
				else if(condition.equals("projectDate"))
					projectSummaryList = projectManager.getAllProjectListByCondition(getMemberId(),condition,field,field1);
			}
			else
				projectSummaryList = projectManager.getAllProjectList(getMemberId());

		} catch (Exception e) {
			logger.error("获取项目列表失败",e);
		}
		List<ProjectSummary> projectList1=new ArrayList<ProjectSummary>();
		List<ProjectSummary> projectList2=new ArrayList<ProjectSummary>();
		List<ProjectSummary> projectList3=new ArrayList<ProjectSummary>();
		for(ProjectSummary dr:projectSummaryList){
			if(dr.getProjectState()<2){
				projectList1.add(dr);
			}else 
				projectList2.add(dr);
			
		}
		projectList3.addAll(projectList1);
		projectList3.addAll(projectList2);
		
		mav.addObject("isProjectBuilder", isProjectBuilder);	
		List<ProjectType> ptList = this.projectManager.getProjectTypes(user.getLoginAccount());
		List<List<ProjectCompose>> tprojectList=new ArrayList<List<ProjectCompose>>();
		List<ProjectCompose> sprojectList;
			for(ProjectType pt:ptList){
				sprojectList=new ArrayList<ProjectCompose>();
				for(ProjectSummary dr:projectList3){
					if(pt.getName().equals(dr.getProjectTypeName())){
						ProjectCompose projectCompose = projectManager.getProjectComposeByID(dr.getId(), true);
						sprojectList.add(projectCompose);
					}
				}
				sprojectList.size();
				tprojectList.add(sprojectList);
			}
			mav.addObject("ptList", ptList);
			mav.addObject("tprojectList", tprojectList);
			mav.addObject("condition",condition);
			mav.addObject("managerID",managerID);
			mav.addObject("field",field);
			mav.addObject("field1",field1);
		return mav;
	}
	/**
	 * 获取当前类型下的所有项目信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView projectInfoMore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String projectTypeName=request.getParameter("projectTypeName");
		String projectTypeId=request.getParameter("projectTypeId");
		String state=request.getParameter("state");
		ModelAndView mav;
		mav = new ModelAndView("project/allProject2More");//更多
		
		String condition = request.getParameter("condition");
        String textfield = request.getParameter("textfield");
        String textfield1 = request.getParameter("textfield1"); 
        if (condition == null && textfield == null && state != null) {//点击更多
            condition = "projectState";
            if ("0".equals(state)) {
                textfield = "2";
            } else {
                textfield = "0";
            }
        }
		
		//判断是否有项目操作权限
		boolean isProjectBuilder = false;
		User user = CurrentUser.get();
		V3xOrgRole role = new V3xOrgRole();
		
		try {
			role = orgManager.getRoleByName( V3xOrgEntity.ORGENT_META_KEY_PROJECTBUILD , user.getLoginAccount() );
		} catch (BusinessException e) {
			logger.error("获取角色失败",e);
		}
		
		if(role!=null){
			List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
			try {
				members = orgManager.getMemberByRole(role.getType(), user.getLoginAccount(), role.getId());
			} catch (BusinessException e) {
				logger.error("获取角色人员失败",e);
			}
			if(members.size()>0){
				for(V3xOrgMember member : members){
					if(member.getId().longValue()==user.getId()){
						isProjectBuilder = true;
						break;
					}
				}
			}
		}
		
		List<ProjectSummary> projectSummaryList = new ArrayList<ProjectSummary>();
		try {
			//抽取创建者是自己或自己包括在4种人员中的
			projectSummaryList = projectManager.getAllProjectListByProjectTypeName(getMemberId(), projectTypeId, condition, textfield, textfield1);
		} catch (Exception e) {
			logger.error("获取项目列表失败",e);
		}
		
		List<ProjectCompose> projectComposeList = new ArrayList<ProjectCompose>();
		for(ProjectSummary p : projectSummaryList){
			ProjectCompose projectCompose = projectManager.detailProject(p, true);
			projectComposeList.add(projectCompose);
		}
		
		mav.addObject("projectComposeList", projectComposeList);
		
		mav.addObject("isProjectBuilder", isProjectBuilder);
		mav.addObject("projectTypeName", projectTypeName);
		mav.addObject("state", state);
		return mav;
		
	}
	public ModelAndView getFlag(HttpServletRequest request,
			HttpServletResponse response){
		try {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("var rv = 'true'");
			out.println("parent.window.returnValue = rv;");
			out.println("parent.window.close();");
			out.println("</script>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 判断是否为项目的负责人或助理(项目助理和项目负责人权限相同)
	 */
	private boolean isManager(ProjectCompose projectCompose, Long memberId) {
		return this.isInRoleTypes(projectCompose, memberId, ProjectMember.memberType_manager, ProjectMember.memberType_assistant);
	}
	
	private boolean isInRoleTypes(ProjectCompose projectCompose, Long memberId, Byte... memberTypes) {
		Long userId = memberId == null ? CurrentUser.get().getId() : memberId;
		if(projectCompose != null) {
			List<V3xOrgMember> members = this.getMembersOfRoleType(projectCompose, memberTypes);
			if(CollectionUtils.isNotEmpty(members)) {
				for(V3xOrgMember member : members) {
					if(member.getId().equals(userId)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private List<V3xOrgMember> getMembersOfRoleType(ProjectCompose projectCompose, Byte... memberTypes) {
		List<V3xOrgMember> ret = new ArrayList<V3xOrgMember>();
		if(memberTypes != null && memberTypes.length > 0) {
			for(Byte type : memberTypes) {
				if(type.equals(ProjectMember.memberType_charge)) {
					FormBizConfigUtils.addAllIgnoreEmpty(ret, projectCompose.getChargeLists());
				}
				else if(type.equals(ProjectMember.memberType_manager)) {
					FormBizConfigUtils.addAllIgnoreEmpty(ret, projectCompose.getPrincipalLists());
				}
				else if(type.equals(ProjectMember.memberType_assistant)) {
					FormBizConfigUtils.addAllIgnoreEmpty(ret, projectCompose.getAssistantLists());
				}
				else if(type.equals(ProjectMember.memberType_member)) {
					FormBizConfigUtils.addAllIgnoreEmpty(ret, projectCompose.getMemberLists());
				}
				else if(type.equals(ProjectMember.memberType_interfix)) {
					FormBizConfigUtils.addAllIgnoreEmpty(ret, projectCompose.getInterfixLists());
				}
			}
		}
		return ret;
	}

	/**
	 * 设置项目计划/会议/日程
	 */
	private List<Affair> setProjectMtAndPlanAndCal(Long projectId, Long phaseId) throws Exception {
		User user = CurrentUser.get();
		
		List<Affair> mtAndPlanCalList = new ArrayList<Affair>();
		List<MtMeetingCAP> mtList = mtMeetingManagerCAP.getProjectMeeting(projectId, phaseId, user.getId());
		List<Plan> planList = planManager.getProjectPlan(projectId, phaseId);
		List<CalEvent> calList = calEventManager.getItemEventListByUserId(user, projectId, phaseId);

		if (CollectionUtils.isNotEmpty(planList)) {
			for (Plan plan : planList) {
				Affair affair = new Affair();
				affair.setIdIfNew();
				affair.setObjectId(plan.getId());
				affair.setSubject(plan.getTitle());
				affair.setApp(ApplicationCategoryEnum.plan.getKey());
				affair.setMemberId(plan.getCreateUserId());
				affair.setCreateDate(new Timestamp(plan.getCreateTime().getTime()));
				affair.setBodyType(plan.getPlanBody().getBodyType());
				affair.setHasAttachments(plan.isHasAttachments());
				affair.setState(Integer.parseInt(plan.getPlanStatus()));
				mtAndPlanCalList.add(affair);
			}
		}

		if (CollectionUtils.isNotEmpty(mtList)) {
			for (MtMeetingCAP mt : mtList) {
				Affair affair = new Affair();
				affair.setIdIfNew();
				affair.setObjectId(mt.getId());
				affair.setSubject(mt.getTitle());
				affair.setApp(ApplicationCategoryEnum.meeting.getKey());
				affair.setMemberId(mt.getCreateUser());
				affair.setCreateDate(new Timestamp(mt.getCreateDate().getTime()));
				affair.setBodyType(mt.getDataFormat());
				affair.setHasAttachments(mt.isHasAttachments());
				affair.setState(mt.getState());
				mtAndPlanCalList.add(affair);
			}
		}

		if (CollectionUtils.isNotEmpty(calList)) {
			for (CalEvent cal : calList) {
				Affair affair = new Affair();
				affair.setIdIfNew();
				affair.setObjectId(cal.getId());
				affair.setSubject(cal.getSubject());
				affair.setApp(ApplicationCategoryEnum.calendar.getKey());
				affair.setMemberId(cal.getCreateUserId());
				if (cal.getEventType() == 1) {
					affair.setAddition(cal.getCreateUserName());
				} else {
					affair.setAddition(cal.getReceiveMemberName());
				}
				affair.setCreateDate(new Timestamp(cal.getBeginDate() == null ? System.currentTimeMillis() : cal.getBeginDate().getTime()));
				affair.setHasAttachments(cal.getAttachmentsFlag());
				affair.setState(cal.getStates());
				mtAndPlanCalList.add(affair);
			}
		}

		Collections.sort(mtAndPlanCalList);
		
		return mtAndPlanCalList;
	}
	
	/**
	 * 条件查询 设置项目计划/会议/日程
	 */
	private List<Affair> setProjectMtAndPlanAndCalByCondition(String condition,Long projectId, Long phaseId,Map<String,Object> paramMap) throws Exception {
		User user = CurrentUser.get();  
		List<Affair> mtAndPlanCalList = new ArrayList<Affair>();
		List<MtMeetingCAP> mtList = mtMeetingManagerCAP.getProjectMeetingByCondition(condition,projectId, phaseId, user.getId(),paramMap);
		List<Plan> planList = planManager.getProjectPlanByCondition(condition, projectId, phaseId, paramMap);
		List<CalEvent> calList = calEventManager.getItemEventListByCondition(condition, user, projectId, phaseId, paramMap);

		if (CollectionUtils.isNotEmpty(planList)) {
			for (Plan plan : planList) {
				Affair affair = new Affair();
				affair.setIdIfNew();
				affair.setObjectId(plan.getId());
				affair.setSubject(plan.getTitle());
				affair.setApp(ApplicationCategoryEnum.plan.getKey());
				affair.setMemberId(plan.getCreateUserId());
				affair.setCreateDate(new Timestamp(plan.getCreateTime().getTime()));
				affair.setBodyType(plan.getPlanBody().getBodyType());
				affair.setHasAttachments(plan.isHasAttachments());
				affair.setState(Integer.parseInt(plan.getPlanStatus()));
				mtAndPlanCalList.add(affair);
			}
		}

		if (CollectionUtils.isNotEmpty(mtList)) {
			for (MtMeetingCAP mt : mtList) {
				Affair affair = new Affair();
				affair.setIdIfNew();
				affair.setObjectId(mt.getId());
				affair.setSubject(mt.getTitle());
				affair.setApp(ApplicationCategoryEnum.meeting.getKey());
				affair.setMemberId(mt.getCreateUser());
				affair.setCreateDate(new Timestamp(mt.getCreateDate().getTime()));
				affair.setBodyType(mt.getDataFormat());
				affair.setHasAttachments(mt.isHasAttachments());
				affair.setState(mt.getState());
				mtAndPlanCalList.add(affair);
			}
		}

		if (CollectionUtils.isNotEmpty(calList)) {
			for (CalEvent cal : calList) {
				Affair affair = new Affair();
				affair.setIdIfNew();
				affair.setObjectId(cal.getId());
				affair.setSubject(cal.getSubject());
				affair.setApp(ApplicationCategoryEnum.calendar.getKey());
				affair.setMemberId(cal.getCreateUserId());
				if (cal.getEventType() == 1) {
					affair.setAddition(cal.getCreateUserName());
				} else {
					affair.setAddition(cal.getReceiveMemberName());
				}
				affair.setCreateDate(new Timestamp(cal.getBeginDate() == null ? System.currentTimeMillis() : cal.getBeginDate().getTime()));
				affair.setHasAttachments(cal.getAttachmentsFlag());
				affair.setState(cal.getStates());
				mtAndPlanCalList.add(affair);
			}
		}

		Collections.sort(mtAndPlanCalList);
		
		return mtAndPlanCalList;
	}
	
	/**
	 * 设置项目留言
	 */
	private List<LeaveWord> setProjectLeaveWord(Long projectId, Long phaseId, String flagStr) throws Exception {
		List<LeaveWord> leaveWordList = guestbookManager.getLeaveWords4Project(projectId, 10, phaseId);
		if (CollectionUtils.isNotEmpty(leaveWordList)) {
			for (int i = 0; i < leaveWordList.size(); i++) {
				LeaveWord leaveWord = leaveWordList.get(i);
				if (leaveWord != null) {
					leaveWord.setIdflag(flagStr);
					leaveWord.setIndexShow(i);
					String urlStr = SystemEnvironment.getA8ContextPath() + "/apps_res/v3xmain/images/personal/pic.gif";
					StaffInfo staff = staffInfoManager.getStaffInfoById(leaveWord.getCreatorId());
					if (staff != null) {
						String issuerImage = staff.getSelf_image_name();
						if (StringUtils.isNotBlank(issuerImage)) {
							if (issuerImage.startsWith("fileId")) {
								urlStr = SystemEnvironment.getA8ContextPath() + "/fileUpload.do?method=showRTE&" + issuerImage + "&type=image";
							} else {
								urlStr = SystemEnvironment.getA8ContextPath() + "/apps_res/v3xmain/images/personal/" + issuerImage;
							}
						}
					}
					leaveWord.setUrlImage(urlStr);
				}
			}
		}
		return leaveWordList;
	}
	
	/**
	 * 项目空间新增留言后局部刷新
	 */
	public String refreshProjectLeaveword(Long projectId, Long projectPhaseId, String flagStr) throws Exception {
		List<LeaveWord> leaveWordList = this.setProjectLeaveWord(projectId, projectPhaseId, flagStr);
		StringBuilder sb = new StringBuilder();
		for(LeaveWord leaveWord : leaveWordList){
			if(leaveWord.getIndexShow() == 0){
				sb.append("<div class='messageDivFirst'>");
			}else if(leaveWord.getIndexShow() <= 4){
				sb.append("<div class='messageDiv'>");
			}else{
				sb.append("<div class='messageDivHidden'>");
			}
			sb.append("<table cellpadding='0' cellspacing='0' width='100%' style='table-layout:fixed'>");
			sb.append("<tr>");
			
			sb.append("<td class='phtoImgTD'>");
			sb.append("<div class='phtoImg'><img src='" + leaveWord.getUrlImage() + "' width='40' height='40'/></div>");
			sb.append("</td>");
			
			sb.append("<td>");
			sb.append("<div class='messageContent'>");
			sb.append("<span class='peopleName'>" + Functions.showMemberName(leaveWord.getCreatorId()));
			if(leaveWord.getReplyerId() != null && leaveWord.getReplyerId() != leaveWord.getCreatorId()){
				sb.append("<span class='replySay'>" + ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "guestbook.leaveword.reply", null) + ":</span>" + Functions.showMemberName(leaveWord.getReplyerId()));
			}
			sb.append("</span>");
			sb.append("<span class='peopleSay'>" + ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "guestbook.leaveword.speak", null) + ":</span>");
			sb.append("<span class='peopleMessage'>" + leaveWord.getContent() + "</span>");
			sb.append("</div>");
			sb.append("<div class='messageTime'>");
			sb.append("<span class='reply'>");
			sb.append("<a href='javascript:replyMessage(\"" + (leaveWord.getReplyId() != null ? leaveWord.getReplyId() : leaveWord.getId())  + "\", \"" + leaveWord.getDepartmentId() 
					+ "\", \"" + leaveWord.getCreatorId() + "\", \"" + leaveWord.getIdflag() + "\")'>" 
					+ ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "guestbook.leaveword.reply", null) 
					+ "</a>");
			sb.append("</span>");
			sb.append("<span class='meaageTime'>" + Datetimes.format(leaveWord.getCreateTime(), "MM-dd HH:mm") + "</span>");
			sb.append("</div>");
			sb.append("</td>");
			
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("</div>");
		}
		return sb.toString();
	}
	
	/**
	 * portal显示角色
	 */
	public ModelAndView showDesignatedRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/showDesignatedRole");
		return mav;
	}
	
	/**
	 * portal显示类型
	 */
	public ModelAndView showDesignatedType(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("project/showDesignatedType");
		List<ProjectType> typeList = this.projectManager.getProjectTypes(CurrentUser.get().getLoginAccount());
		mav.addObject("typeList", typeList);
		return mav;
	}
	
}