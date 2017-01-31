package com.seeyon.v3x.project.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheMap;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.dao.InvalidEntityDAO;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.dao.ProjectDao;
import com.seeyon.v3x.project.dao.ProjectLogDao;
import com.seeyon.v3x.project.dao.ProjectPhaseDao;
import com.seeyon.v3x.project.domain.ProjectEvolution;
import com.seeyon.v3x.project.domain.ProjectLog;
import com.seeyon.v3x.project.domain.ProjectMember;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.domain.ProjectType;
import com.seeyon.v3x.project.webmodel.EvolutionUserCompose;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.project.webmodel.ProjectLogCompose;

/**
 * @author lin tian 2007-5-16
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-23
 */
public class ProjectManagerImpl implements ProjectManager {
	private static final CacheAccessable cacheFactory = CacheFactory.getInstance(ProjectManagerImpl.class);
	private static CacheMap<Long, ProjectType> projectTypeMap = cacheFactory.createMap("ProjectTypeMap");
	
	public synchronized void init() {
		List<ProjectType> allTypes = this.projectDao.getAllProjectTypes();
		if(CollectionUtils.isNotEmpty(allTypes)) {
			for(ProjectType type : allTypes) {
				projectTypeMap.put(type.getId(), type);
			}
		}
	}

	private OrgManager orgManager;
	
	private UserMessageManager userMessageManager;

	private ProjectDao projectDao;
	private ProjectLogDao projectLogDao;
	private ProjectPhaseDao projectPhaseDao;
	private InvalidEntityDAO  invalidDaoBean;
	
	public void setProjectPhaseDao(ProjectPhaseDao projectPhaseDao) {
		this.projectPhaseDao = projectPhaseDao;
	}
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

	public void setProjectLogDao(ProjectLogDao projectLogDao) {
		this.projectLogDao = projectLogDao;
	}

	/**
	 * @param projectDao
	 *            the projectDao to set
	 */
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	/**
	 * @param orgManager
	 *            the orgManager to set
	 */
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	// public void setProjectLogManager(ProjectLogManager projectLogManager) {
	// this.projectLogManager = projectLogManager;
	// }

	/**
	 * 保存新项目
	 * 
	 * @param projectSummary
	 * @throws Exception
	 */
	public void saveNewProjectSummary(ProjectSummary projectSummary,
			ProjectLog projectLog) throws Exception {
		if (projectSummary == null)
			throw new Exception("新建的项目为空");
		if (projectLog == null)
			throw new Exception("日志为空");
		this.saveProject(projectSummary, projectLog);
	}

	/**
	 * 保存项目
	 * 
	 * @param projectSummary
	 * @throws Exception
	 */
	protected void saveProject(ProjectSummary projectSummary,
			ProjectLog projectLog) throws Exception {
		this.projectLogDao.save(projectLog);
		projectDao.save(projectSummary);
	}
	
	public List<ProjectSummary> getIndexProjectList(long memberid, int maxResult) throws Exception {
		return projectDao.getIndexProjectList(memberid, maxResult, null, null);
	}

	public List<ProjectSummary> getIndexProjectList(long memberid, int maxResult, List<Byte> memberTypeList, List<Long> projectTypeList) throws Exception {
		return projectDao.getIndexProjectList(memberid, maxResult, memberTypeList, projectTypeList);
	}
	
	/**
	 * 获取用户关联项目列表
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getProjectList(User user, Long accountId)throws Exception{
		if(accountId != null){//如果为单位管理员
			return projectDao.getProjectsByAdmin(accountId, null, null, null);
		}
		
		long memberid = user.getId();// 获取当前用户ID
		return projectDao.getProjectList(memberid);
	}
	
	public List<ProjectSummary> getProjectList(User user)throws Exception{
		long memberid = user.getId();// 获取当前用户ID
		return projectDao.getProjectList(memberid);
	}
	/**
	 * 获取当前用户关联项目列表
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public List<ProjectSummary> getProjectList()throws Exception{
		if(CurrentUser.get().isAdministrator()){//如果为单位管理员
			return projectDao.getProjectListByAdmin();
		}
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		return projectDao.getProjectList(memberid);
	}
	/**
	 * 获取当前用户所有项目信息
	 * 
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getAllProjectList(long memberid) throws Exception {
		return projectDao.getAllProjectList(memberid);
	}
	
	public List<ProjectSummary> getAllProjectList(long memberid, boolean filter, boolean pagination) throws Exception {
        return projectDao.getAllProjectList(memberid,filter,pagination);
    }
	
	public List<ProjectSummary> getAllProjects4User(long memberid) throws Exception {
		return projectDao.getAllProjectList(memberid, -1l, true, false);
	}
	
	public List<ProjectSummary> getAllProjects4User(long memberid, long accountId) throws Exception {
		return projectDao.getAllProjectList(memberid, accountId, true, false);
	}
	
	public List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1) throws Exception {
		return projectDao.getAllUserProjectList(memberid, condition, textfield, textfield1);
	}

	public List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1, List<Byte> memberTypeList) throws Exception {
		return projectDao.getAllUserProjectList(memberid, condition, textfield, textfield1, memberTypeList,null);
	}
	
	public List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1, List<Byte> memberTypeList,Map<String,Object> cndMap) throws Exception {
        return projectDao.getAllUserProjectList(memberid, condition, textfield, textfield1, memberTypeList,cndMap);
    }
	
	/**
	 * 根据查询条件获取当前用户所有项目信息
	 * 
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getAllProjectListByCondition(long memberid,String condition,String field,String field1)
			throws Exception {
		return projectDao.getAllProjectListByCondition(memberid,condition,field,field1);
	}
	/**
	 * 获取当前类型下的所有项目信息
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getAllProjectListByProjectTypeName(long memberid, String projectTypeName, String condition, String textfield, String textfield1)
			throws Exception {
		//TODO 性能优化
		return projectDao.getAllProjectListByProjectTypeName(memberid, projectTypeName, condition, textfield, textfield1);
	}
	/**
	 * 根据ID获取项目详细信息
	 * 
	 * @param projectId
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public ProjectCompose getProjectComposeByID(long projectId, boolean b)
			throws Exception {
		ProjectCompose projectCompose = new ProjectCompose();
		ProjectSummary projectSummary = this.getProjectSummary(projectId);
		projectCompose = this.detailProject(projectSummary, b);
		return projectCompose;
	}

	/**
	 * 获取项目信息
	 * 
	 * @param projectId
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public ProjectSummary getProject(long projectId) throws Exception {
		return this.getProjectSummary(projectId);
	}

	/**
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	protected ProjectSummary getProjectSummary(long projectId) throws Exception 
    {
		ProjectSummary projectSummary = projectDao.getProject(projectId);
		if(projectSummary != null){
			Set<ProjectMember> member=projectSummary.getProjectMembers();
			//删除停用人员by Yongzhang 2008-6-17
			Iterator it= member.iterator();
			while(it.hasNext()){
	           ProjectMember member2=(ProjectMember) it.next();           
	           if(member2.getMemberType().byteValue() != ProjectMember.memberType_manager.byteValue()) {
	               V3xOrgMember orgMember= orgManager.getMemberById(member2.getMemberid());//invalidDaoBean.findMemberById(member2.getMemberid()) ;
	               if(orgMember!=null)
	               {                 
	            	   if(!orgMember.getEnabled()||orgMember.getIsDeleted())
	                   {
	                       it.remove();//关键
	                       member.remove(member2);
	                   }
	               }       	   
	           }
	       }
		}
       
		return projectSummary;
	}

	public ProjectCompose detailProject(ProjectSummary projectSummary, boolean b) throws Exception {
		if (projectSummary == null) {
			throw new Exception("无法找到项目信息");
		}
		ProjectCompose projectCompose = new ProjectCompose();
		projectCompose.setProjectSummary(projectSummary);

		V3xOrgDepartment deparment = this.orgManager.getDepartmentById(projectSummary.getDepartment());
		projectCompose.setDeparment(deparment);

		Set<ProjectPhase> projectPhases = projectSummary.getProjectPhases();// 项目阶段
		if (b) {
			projectCompose = this.addProjectEvolutionList(projectCompose, projectPhases);
		}
		
		Set<ProjectMember> projectMembers = projectSummary.getProjectMembers();// 相关人员
		this.addProjectMembers(projectCompose, projectMembers);
		
		return projectCompose;
	}
	
	public ProjectCompose wrapProject(ProjectSummary projectSummary) throws Exception {
		return this.detailProject(projectSummary, false);
	}

	/**
	 * 加载项目阶段进度列表
	 * 
	 * @param projectCompose
	 * @param projectPhases
	 * @return
	 * @throws Exception
	 */
	protected ProjectCompose addProjectEvolutionList(
			ProjectCompose projectCompose, Set<ProjectPhase> projectPhases)
			throws Exception {
		List<EvolutionUserCompose> composelist = new ArrayList<EvolutionUserCompose>();
		for (ProjectPhase phase : projectPhases) {
			@SuppressWarnings("unused")
			Set<ProjectEvolution> projectEvolutions = phase
					.getProjectEvolutions();// 进展状况
			for (ProjectEvolution evolution : projectEvolutions) {
				EvolutionUserCompose compose = new EvolutionUserCompose();
				compose.setProjectEvolution(evolution);
				compose.setMember(this.orgManager.getMemberById(evolution.getUserId()));
				composelist.add(compose);
			}
		}
		projectCompose.setComposeList(composelist);
		return projectCompose;
	}

	/**
	 * 加载项目相关人员
	 * 
	 * @param projectMembers
	 * @return
	 * @throws Exception
	 */
	protected void addProjectMembers(ProjectCompose projectCompose, Set<ProjectMember> projectMembers) throws Exception {
		List<V3xOrgMember> managerList = new ArrayList<V3xOrgMember>();//负责人列表
		List<V3xOrgMember> assistantList = new ArrayList<V3xOrgMember>();//助理列表
		List<V3xOrgMember> chargeList = new ArrayList<V3xOrgMember>();//领导列表
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();//成员列表
		List<V3xOrgMember> interfixList = new ArrayList<V3xOrgMember>();//相关人员列表
		for (ProjectMember member : projectMembers) {
			V3xOrgMember m = this.orgManager.getMemberById(member.getMemberid());
			if(member.getMemberType().equals(ProjectMember.memberType_manager)){
				managerList.add(m);
				projectCompose.setPrincipalLists(managerList);
			}else if(member.getMemberType().equals(ProjectMember.memberType_assistant)){
				assistantList.add(m);
				projectCompose.setAssistantLists(assistantList);
			}else if (member.getMemberType().equals(ProjectMember.memberType_charge)) {
				chargeList.add(m);
				projectCompose.setChargeLists(chargeList);
			} else if (member.getMemberType().equals(ProjectMember.memberType_member)) {
				memberList.add(m);
				projectCompose.setMemberLists(memberList);
			} else if (member.getMemberType().equals(ProjectMember.memberType_interfix)) {
				interfixList.add(m);
				projectCompose.setInterfixLists(interfixList);
			}
		}
	}

	/**
	 * 保存进展信息
	 * 
	 * @param projectEvolution
	 * @throws Exception
	 */
	public void saveProjectEvolution(ProjectEvolution projectEvolution,
			ProjectLog projectLog) throws Exception {
		if (projectEvolution == null) {
			throw new Exception("进展信息为空");
		} else if (projectLog == null) {
			throw new Exception("日志为空");
		} else {
			this.saveEvolution(projectEvolution, projectLog);
		}
	}

	protected void saveEvolution(ProjectEvolution projectEvolution,
			ProjectLog projectLog) throws Exception {
		projectLogDao.save(projectLog);
		projectDao.save(projectEvolution);
	}

	/**
	 * 物理删除项目信息
	 * 
	 * @param projectId
	 * @throws Exception
	 */
	public void removeProjectSummary(long projectId, ProjectLog projectLog)
			throws Exception {
		ProjectSummary projectSummary = projectDao.getProject(projectId);
		if (projectSummary == null) {
			throw new Exception("无法找到项目信息" + projectId);
		}
		if (projectLog == null) {
			throw new Exception("日志为空!");
		}
		this.removeProject(projectSummary, projectLog);
	}

	protected void removeProject(ProjectSummary projectSummary,
			ProjectLog projectLog) throws Exception {
		projectLogDao.save(projectLog);
		projectDao.remove(projectSummary);
	}

	/**
	 * 标记删除项目
	 * 
	 * @param projectId
	 * @throws Exception
	 */
	public void deleteProjectSummary(long projectId, ProjectLog projectLog)
			throws Exception {
		if (projectLog == null) {
			throw new Exception("日志为空!");
		}
		this.deleteProject(projectId, projectLog);
	}

	protected void deleteProject(long projectId, ProjectLog projectLog)
			throws Exception {
		projectLogDao.save(projectLog);
		projectDao.deleteProjectSummary(projectId);
	}

	/**
	 * 判断当前用户是否为当前项目的成员（成员、负责人）
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public boolean isProjectMember(long projectId, long uid) throws Exception {
		try {
			@SuppressWarnings("unused")
			List<Long> memberids = this.getProjectMemberList(projectId);
			for (Long long1 : memberids) {
				if (String.valueOf(long1).equals(String.valueOf(uid))) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 判断用户能否查阅项目内容
	 * @param projectId
	 * @param userId
	 * @return
	 */
	public boolean canUserViewProject(Long projectId, Long userId) {
		return this.projectDao.canUserViewProject(projectId, userId);
	}

	/**
	 * 获取当前项目的成员列表(成员、负责人)
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	protected List<Long> getProjectMemberList(long projectId) throws Exception {
		return projectDao.getProjectMemberList(projectId);
	}

	/**
	 * 更新项目信息
	 * 
	 * @param projectSummary
	 * @param delete_phase：待删除的项目阶段列表
	 * @throws Exception
	 */
	public void updateProjectSummary(ProjectSummary projectSummary,
			String[] projectPhase_delete, ProjectLog projectLog)
			throws Exception {
		if (projectSummary == null) {
			throw new Exception("找不到项目信息");
		}
		if (projectLog == null) {
			throw new Exception("日志为空");
		}
		Set<ProjectPhase> projectPhase = projectSummary.getProjectPhases();
		if (projectPhase_delete != null && projectPhase != null) {
			for (Iterator it = projectPhase.iterator(); it.hasNext();) {
				ProjectPhase phase = (ProjectPhase) it.next();
				for (int j = 0; j < projectPhase_delete.length; j++) {
					if ((phase.getId().toString())
							.equals(projectPhase_delete[j])) {
						it.remove();
					}
				}
			}
		}
		projectLogDao.save(projectLog);
		//projectDao.update(projectSummary);
		projectDao.getHibernateTemplate().merge(projectSummary);
	}
	
	public void updateProjectSummary(ProjectSummary projectSummary,ProjectLog projectLog)throws Exception{
		if (projectSummary == null) {
			throw new Exception("找不到项目信息");
		}
		if (projectLog == null) {
			throw new Exception("日志为空");
		}
		projectLogDao.save(projectLog);
		//projectDao.update(projectSummary);
		projectDao.getHibernateTemplate().merge(projectSummary);
	}
	
	public void updateProjectSummary(ProjectSummary projectSummary)throws Exception {
		
		if (projectSummary == null) {
			throw new Exception("找不到项目信息");
		}
		//projectDao.update(projectSummary);
		projectDao.getHibernateTemplate().merge(projectSummary);
		
	}

	/**
	 * 获取当前项目的操作日志
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<ProjectLogCompose> getProjectLogList(long projectId)
			throws Exception {
		List<ProjectLogCompose> list = new ArrayList<ProjectLogCompose>();
		List<ProjectLog> plist = this.getLogList(projectId);
		for (ProjectLog log : plist) {
			ProjectLogCompose plogCompose = new ProjectLogCompose();
			plogCompose.setProjectLog(log);
			String managerDesc = log.getManagerDesc();
			plogCompose = this.getManagerAdapter(managerDesc, plogCompose);// 项目负责人适配
			String memberDesc = log.getMemberDesc();

			plogCompose = this.getMemberAdapter(memberDesc, plogCompose, 1);// 项目成员适配
			String chargeDesc = log.getChargeDesc();
			plogCompose = this.getMemberAdapter(chargeDesc, plogCompose, 2);// 领导适配
			String interfixDesc = log.getInterfixDesc();
			plogCompose = this.getMemberAdapter(interfixDesc, plogCompose, 3);// 相关人员适配
			plogCompose.setOptionUser(this.orgManager.getMemberById(log.getUserid()));// 操作人
			list.add(plogCompose);
		}
		return list;
	}

	/**
	 * 获取当前项目的操作日志
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public List<ProjectLog> getLogList(long projectId) throws Exception {
		return projectLogDao.getLogList(projectId);
	}

	/**
	 * 设置一个项目负责人修改适配器
	 * 
	 * @param memberDesc 格式为"123@654"(前后最多一个) '@'前为增加的负责人 后为删除的负责人
	 * @return
	 * @throws Exception
	 */
	protected ProjectLogCompose getManagerAdapter(String managerDesc,
			ProjectLogCompose plogCompose) throws Exception {
		if (managerDesc == null || managerDesc.equals("")) {
			return plogCompose;
		}
		String[] managerType = managerDesc.split("@");// managerDesc的格式为"123@654"(前后最多一个)
		// '@'之前的为增加 之后为删除的
		String manager_add = managerType[0];// 增加的在前
		if (manager_add != null && !manager_add.equals("")) {
			V3xOrgMember addManager = this.orgManager.getMemberById(Long.parseLong(manager_add));
			plogCompose.setAddManager(addManager);
		}
		if (managerType.length == 2) {
			String manager_delete = managerType[1];
			if (manager_delete != null && !manager_delete.equals("")) {
				V3xOrgMember deleteManager = this.orgManager.getMemberById(Long.parseLong(manager_delete));
				plogCompose.setDeleteManager(deleteManager);
			}
		}
		return plogCompose;
	}

	/**
	 * 设置一个项目人员修改适配器
	 * 
	 * @param memberDesc：ID串
	 *            格式为"123，456，789@654，987，654"(多个) 以'@'为界 前面为增加的人员ID串 后面的为删除的ID串
	 * @param plogCompose
	 * @param AdapterType：适配类型：
	 *           1 项目成员 2 项目领导 3 相关人员
	 * @return
	 * @throws Exception
	 */
	protected ProjectLogCompose getMemberAdapter(String memberDesc,
			ProjectLogCompose plogCompose, int AdapterType) throws Exception {
		if (memberDesc == null || memberDesc.equals("")) {
			return plogCompose;
		}
		String[] memberType = memberDesc.split("@");// memberDesc的格式为"123，456，789@654，987，654"(多个)
		// '@'之前的为增加 之后为删除的
		String member_add = memberType[0];// 增加的在前
		List<V3xOrgMember> addMember = new ArrayList<V3xOrgMember>();
		if (member_add != null && !member_add.equals("")) {
			String[] m_add = member_add.split(",");
			if (m_add == null || m_add.length == 0)
				return plogCompose;
			for (int j = 0; j < m_add.length; j++) {
				if (m_add[j].equals("")) {
					continue;
				}
				V3xOrgMember add_Member = this.orgManager.getMemberById(Long.parseLong(m_add[j]));
				addMember.add(add_Member);
			}
			switch (AdapterType) {
			     case 1: // 增加的项目成员
				       plogCompose.setAddMember(addMember);
				       break;
			     case 2: // 增加的项目领导
				       plogCompose.setAddCharge(addMember);
				       break;
			     case 3: // 增加的项目相关人员
				       plogCompose.setAddInterfix(addMember);
				       break;
			     default:
				       break;
			}
		}
		if (memberType.length == 2) {
			String member_delete = memberType[1];
			List<V3xOrgMember> deleteMember = new ArrayList<V3xOrgMember>();
			if (member_delete != null && !member_delete.equals("")) {
				String[] m_delete = member_delete.split(",");
				if (m_delete == null || m_delete.length == 0)
					return plogCompose;
				for (int j = 0; j < m_delete.length; j++) {
					if (m_delete[j].equals("")) {
						continue;
					}
					V3xOrgMember delete_Member = this.orgManager.getMemberById(Long.parseLong(m_delete[j]));
					deleteMember.add(delete_Member);
				}
				switch (AdapterType) {
				    case 1: // 删除的项目成员
					    plogCompose.setDeleteMember(deleteMember);
					    break;
				    case 2: // 增加的项目领导
					    plogCompose.setDeleteCharge(deleteMember);
					    break;
				    case 3: // 增加的项目相关人员
					    plogCompose.setDeleteInterfix(deleteMember);
					    break;
				    default:
					    break;
				}
			}
		}
		return plogCompose;
	}

	/**
	 * 获取当前用户创建的项目
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public List<String> getProjectSummaryByUser(long uid) throws Exception {
		return projectDao.getProjectSummaryByUser(uid);
	}
	
	/**
	 * @deprecated
	 * 获取关联项目列表：协同、会议、计划...接口
	 * @param showAll   true 显示全部 false显示当前用户的关联项目
	 * @return
	 * @throws Exception
	 */
 	public List<ProjectSummary> getProjectList(boolean showAll)throws Exception{
 		if(showAll)
 			return projectDao.getProjectListByAdmin();
 		return this.getProjectList();
 			
 	}
 	
 	public List<ProjectSummary> getProjects(long domainId) throws Exception {
 		return projectDao.getProjects(domainId);
 	}
 	
 	@Deprecated
 	public List<ProjectSummary> getProjectsByAdmin(long domainId, String condition, String textfield, String textfield1) throws Exception { 		
 		return projectDao.getProjectsByAdmin(domainId, condition, textfield, textfield1); 		
 	}
 	
 	public List<ProjectSummary> getProjectsOfTypeByAdmin(long accountId, Long projectTypeId, String condition, String textfield, String textfield1) throws Exception {
 		return projectDao.getProjectsOfTypeByAdmin(accountId, projectTypeId, condition, textfield, textfield1); 
 	}

	public void addProjectType(ProjectType projectType) {
		projectType.setIdIfNew();
		this.projectDao.save(projectType);
		
		projectTypeMap.put(projectType.getId(), projectType);
	}

	public void deleteProjectType(Long id) throws Exception {
		//添加删除分类前检测本
        this.projectDao.del(id);
        ProjectType pt = projectTypeMap.get(id);
        if(pt != null) {
        	projectTypeMap.remove(id);
        }
	}

	public ProjectType getProjectTypeById(Long projectId) {
		ProjectType pt = projectTypeMap.get(projectId);
		if(pt == null)
			pt = this.projectDao.findProjectTypeById(projectId);
		return pt;
	}

	public void updateProjectType(ProjectType modifiedPT,Long typeId) {		
		// 批量更新相关联的项目类型
        ProjectType projectType=projectDao.findProjectTypeById(typeId);
        if(projectType!=null)
        {
            List list=projectDao.findProjectByProjectTypeName(projectType.getName());
            if(list!=null&&list.size()>0)
            {
                projectDao.updateProTypeofProjectSummary(modifiedPT.getName(), projectType.getName());
            }
        }
        projectDao.updateProTypeName(modifiedPT);
        
        projectTypeMap.put(modifiedPT.getId(), modifiedPT);
	}

	public void retakeClew(Long ids) throws Exception {
		 List<ProjectSummary> list = projectDao.getManagerProjectList(ids);
		 if(list!=null && list.size()>0){
			 for(ProjectSummary ps : list){
				 List<Long> auth = new ArrayList<Long>();
					auth.add(ps.getProjectCreator());
					Collection<MessageReceiver> receivers = MessageReceiver.get(ps.getId(),auth);
					V3xOrgMember v = orgManager.getMemberById(ids);
					 try {		
						 userMessageManager.sendSystemMessage(MessageContent.get("project.ManagerDel.Clew",v.getName()), ApplicationCategoryEnum.project, CurrentUser.get().getId(), receivers);						 
				        } catch (Exception e) {
				        	e.printStackTrace();
				        }
			 }
		 }
		
	}
	public void updateProjectOrder(String[] proIds,Long userId) throws Exception
	{
		if(proIds==null||proIds.length==0){return;}
		int i=0;
		for(String proId:proIds)
		{
			i++;
			projectDao.updateUserProjectSort(Long.parseLong(proId), userId, i);
		}
	}
 	
	/**
	 * 用于新建单位时初始化项目类型
	 * 
	 * @param accountId
	 */
	public void initProjectType(long accountId) {
		List<ProjectType> types = this.projectDao.getProjectTypeByAccountId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		if (types != null) {
			for (ProjectType type : types) {
				ProjectType newType = new ProjectType();
				newType.setIdIfNew();
				newType.setName(type.getName());
				newType.setAccountId(accountId);
				newType.setMemo(type.getMemo());
				this.projectDao.save(newType);
				projectTypeMap.put(newType.getId(), newType);
			}
		}
	}

    public List getProjectSummaryUseType(Long projectTypeId)
    {
        List list=projectDao.findProjectSummaryUseType(projectTypeId);
        return list;
    }

    public InvalidEntityDAO getInvalidDaoBean()
    {
        return invalidDaoBean;
    }

    public void setInvalidDaoBean(InvalidEntityDAO invalidDaoBean)
    {
        this.invalidDaoBean = invalidDaoBean;
    }

    @SuppressWarnings("deprecation")
    public List checkProjectManager(Long memId) throws Exception
    {
       return projectDao.checkProjectManager(memId);
    }
    public boolean checkExist(Long projectId)throws Exception {
    	
    	boolean existFlag = true;
    	ProjectSummary projectSummary = projectDao.getProject(projectId);
		if (projectSummary.getProjectState() == 4) {
			existFlag = false;
		}
    	
    	return existFlag;
    }

	public List getAllProjectListByMemberId(Long memberId) throws Exception {
		return projectDao.getAllProjectListByMemberId(memberId);
	}
	
	public ProjectPhase getProjectPhase(Long projectPhaseId) {
		return this.projectPhaseDao.get(projectPhaseId);
	}
	
	@SuppressWarnings("unchecked")
	public List<Byte> getProjectRoles(Long userId, Long projectId) {
		String hql = "select distinct memberType from " + ProjectMember.class.getCanonicalName() + " where projectSummary.id=? and memberid=?";
		return this.projectDao.find(hql, -1, -1, null, projectId, userId);
	}
	 
	public boolean isProjectLeaderOrManager(Long userId, Long projectId) {
		List<Byte> roles = this.getProjectRoles(userId, projectId);
		return CollectionUtils.isNotEmpty(roles) && 
			   (roles.contains(ProjectMember.memberType_manager) || 
			    roles.contains(ProjectMember.memberType_charge) ||
			    roles.contains(ProjectMember.memberType_assistant));
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getAllProjectMembers(Long projectId) {
		String hql = "from " + ProjectMember.class.getCanonicalName() + " where projectSummary.id=? order by memberSort asc";
		List<ProjectMember> members = this.projectDao.find(hql, -1, -1, null, projectId);
		List<Long> result = null;
		if(CollectionUtils.isNotEmpty(members)) {
			result = new ArrayList<Long>(members.size());
			for(ProjectMember m : members) {
				if(!result.contains(m.getMemberid())) {
					result.add(m.getMemberid());
				}
			}
		}
		return result;
	}
	
	public List<ProjectType> getProjectTypes(Long accountId) {
//		List<ProjectType> copy = new ArrayList<ProjectType>(projectTypeMap.values());
		Collection<ProjectType> copy = projectTypeMap.values();
		List<ProjectType> ret = new ArrayList<ProjectType>(copy.size());
		for(ProjectType pt : copy) {
			if(pt.getAccountId().equals(accountId)) {
				ret.add(pt);
			}
		}
		return ret;
	}
	
	public void updateProjectFieldById(Long projectId, String field, Object object) {
		Map<String, Object> columns = new HashMap<String, Object>();
		columns.put(field, object);
		this.projectDao.update(projectId, columns);
	}
	
	public List<Object[]> getProjectByPhase(Long phaseId) {
		return projectDao.getProjectByPhase(phaseId);
	}
	
	public List<Object> getProjectMembersByProject(Long projectId) {
		return projectDao.getProjectMembersByProject(projectId);
	}
	
	@SuppressWarnings("unchecked")
	public List<ProjectPhase> getProjectPhases(Collection<Long> phaseIds) {
		return projectPhaseDao.getProjectPhases(phaseIds);
	}
	@Override
	public List<ProjectSummary> getUserManagedProjectsByUserId(String userid) {
		StringBuffer hql = new StringBuffer("select pm.projectSummary from ");
		hql.append(ProjectMember.class.getName()).append(" pm where pm.memberid=? and pm.memberType=?");
		hql.append(" and ");
		hql.append("( pm.projectSummary.projectState = " + ProjectSummary.state_begin );//开始
		hql.append(" or pm.projectSummary.projectState = " + ProjectSummary.state_option );//进行中
		hql.append(" )" );
		return projectDao.find(hql.toString(), -1, -1, null, new Long(userid), ProjectMember.memberType_manager);
	}
}