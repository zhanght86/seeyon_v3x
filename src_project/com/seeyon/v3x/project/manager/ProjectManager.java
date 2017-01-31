package com.seeyon.v3x.project.manager;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.project.domain.ProjectEvolution;
import com.seeyon.v3x.project.domain.ProjectLog;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.domain.ProjectType;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.project.webmodel.ProjectLogCompose;

/**
 * @author lin tian 2007-5-16
 * @author modified by <a href="mailto:zhangyong@seeyon.com">Yong Zhang</a>
 * @version 2008-04-23
 */
public interface ProjectManager {
	/**
	 * 保存新项目
	 * @param projectSummary
	 * @throws Exception
	 */
	public void saveNewProjectSummary(ProjectSummary projectSummary,ProjectLog projectLog)
			throws Exception;
	
	/**
	 * 获取当前用户最新maxResult条项目信息
	 */
	public List<ProjectSummary> getIndexProjectList(long memberid, int maxResult) throws Exception;
	
	/**
	 * 获取当前用户最新maxResult条项目信息
	 */
	public List<ProjectSummary> getIndexProjectList(long memberid, int maxResult, List<Byte> memberTypeList, List<Long> projectTypeList) throws Exception;
	
	/**
	 * 获取普通用户或单位管理员有权查看的关联项目
	 * 
	 * @author jincm 2008-3-24
	 * @param user 当前用户
	 * @param accountId 单位ID
	 * @return	List<ProjectSummary> 关联项目列表
	 */
	public List<ProjectSummary> getProjectList(User user, Long accountId)throws Exception;
	/**
	 * 获取普通用户 有权查看的关联项目```````````````````````
	 * 
	 * @author jincm 2008-3-24
	 * @param user 当前用户
	 * @param accountId 单位ID
	 * @return	List<ProjectSummary> 关联项目列表
	 */
	public List<ProjectSummary> getProjectList(User user)throws Exception;
	/**
	 * 获取当前用户关联项目列表：协同、会议、计划...接口
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getProjectList()throws Exception;	
    
	/**
	 * 获取当前用户所有项目信息
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getAllProjectList(long memberid)throws Exception;
	
	/**
	 * 获取当前用户能访问的全部项目，不分页，只抽取未结束的有效项目
	 * @param memberid	用户ID
	 */
	public List<ProjectSummary> getAllProjects4User(long memberid) throws Exception;
	
	/**
	 * 获取当前用户能访问的<b>指定单位</b>下的全部项目，不分页，只抽取未结束的有效项目
	 * @param memberid	用户ID
	 * @param accountId	单位ID
	 */
	public List<ProjectSummary> getAllProjects4User(long memberid, long accountId) throws Exception;
	
	/**
	 * 获取用户关联项目
	 */
	public List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1) throws Exception;

	/**
	 * 获取用户关联项目（栏目内容过滤）
	 * @param memberid
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param memberTypeList 角色过滤
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1, List<Byte> memberTypeList) throws Exception;
	
	/**
	 * 根据查询条件获取当前用户所有项目信息
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getAllProjectListByCondition(long memberid,String condition,String field,String field1)throws Exception;
	/**
	 * 获取当前类型下的所有项目信息
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	public List<ProjectSummary> getAllProjectListByProjectTypeName(long memberid, String projectTypeName, String condition, String textfield, String textfield1)throws Exception;
	/**
	 * 根据ID获取项目详细信息
	 * @param projectId
	 * @param b: true:需要加入项目进展信息 false：不用加入项目进展信息
	 * @return
	 * @throws Exception
	 */
	public ProjectCompose getProjectComposeByID(long projectId,boolean b)throws Exception;
	
	/**
	 * 将项目信息进行简单包装
	 * @param projectSummary	项目信息
	 */
	public ProjectCompose wrapProject(ProjectSummary projectSummary) throws Exception;
	
	/**
	 * 根据ID获取项目信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public ProjectSummary getProject(long projectId)throws Exception;
     /**
      * 保存进展信息
      * @param projectEvolution
      * @throws Exception
      */
 	public void saveProjectEvolution(ProjectEvolution projectEvolution,ProjectLog projectLog)throws Exception;
 	/**
 	 * 物理删除项目信息
 	 * @param projectId
 	 * @throws Exception
 	 */
 	public void removeProjectSummary(long projectId,ProjectLog plog)throws Exception;
 	/**
 	 * 标记删除项目
 	 * @param projectId
 	 * @throws Exception
 	 */
 	public void deleteProjectSummary(long projectId,ProjectLog plog)throws Exception;
 	/**
 	 * 判断当前用户是否为当前项目的成员（成员、负责人）
 	 * @param projectId
 	 * @return
 	 * @throws Exception
 	 */
 	public boolean isProjectMember(long projectId,long uid)throws Exception;
 	
 	/**
	 * 判断用户能否查阅项目内容
	 * @param projectId
	 * @param userId
	 * @return
	 */
	public boolean canUserViewProject(Long projectId, Long userId);
	
 	/**
 	 * 更新项目信息
 	 * @param projectSummary
 	 * @param delete_phase：待删除的项目阶段列表
 	 * @throws Exception
 	 */
 	public void updateProjectSummary(ProjectSummary projectSummary,String[] projectPhase_delete,ProjectLog projectLog)throws Exception;
 	
 	/**
 	 * 更新项目信息
 	 * @param projectSummary
 	 * @throws Exception
 	 */
 	public void updateProjectSummary(ProjectSummary projectSummary,ProjectLog projectLog)throws Exception;
 	
 	/**
 	 * 更新项目信息
 	 * @param projectSummary
 	 * @throws Exception
 	 */
 	public void updateProjectSummary(ProjectSummary projectSummary)throws Exception;
 	
 	/**
 	 * 获取当前项目的操作日志
 	 * @param projectId
 	 * @return
 	 * @throws Exception
 	 */
 	public List<ProjectLogCompose> getProjectLogList(long projectId)throws Exception;
 	/**
 	 * 获取当前用户创建的项目
 	 * @param uid
 	 * @return
 	 * @throws Exception
 	 */
 	public List<String>  getProjectSummaryByUser(long uid) throws Exception;
 	
 	/**
	 * @deprecated
	 * 获取关联项目列表：协同、会议、计划...接口
	 * @param showAll   true 显示全部 false显示当前用户的关联项目
	 * @return
	 * @throws Exception
	 */
 	public List<ProjectSummary> getProjectList(boolean showAll)throws Exception;
 	
 	/**
 	 * 获取当前单位的所有关联项目列表（项目发起人，包括终止和结束的项目）。
 	 * @param domainId 单位id
 	 * @return List
 	 * @throws Exception
 	 * @deprecated	废弃(随后按照项目类型名称筛选，性能低下)
 	 * @see #getProjectsOfTypeByAdmin(long, Long, String, String, String)
 	 */
 	public List<ProjectSummary> getProjectsByAdmin(long domainId, String condition, String textfield, String textfield1) throws Exception;
 	
 	/**
 	 * 读取本单位所有活动的关联项目（供表单管理员、单位管理员调用）
 	 * @param domainId 单位id
 	 * @return List
 	 * @throws Exception
 	 */
 	public List<ProjectSummary> getProjects(long domainId) throws Exception;
	public void addProjectType(ProjectType project);
	/**
	 * 删除项目分类
	 * 
	 * @param id
	 */
	public void deleteProjectType(Long l) throws Exception;
	public ProjectType getProjectTypeById(Long projectId)throws Exception;
	public void updateProjectType(ProjectType project,Long typeId) throws Exception;
	
	/**
	 * 组织模型删除人时，如果删除的是管理员，就给发起者个提示消息，让他从新设置管理员
	 */
	public void retakeClew(Long ids) throws Exception;
	
	/**
	 * 更新用户的项目排序
	 * @param proIds:项目id
	 * @param userId
	 * @throws Exception
	 */
	public void updateProjectOrder(String[] proIds,Long userId) throws Exception;
 	
	/**
	 * 用于新建单位时初始化关联项目分类
	 * @param accountId
	 */
	public void initProjectType(long accountId);
    /**
     * 删除分类前检测本类型未使用
     * @param accountId
     */
    public  List  getProjectSummaryUseType(Long projectId);
    
    /**
     * @deprecated
     * 检查此人员是否为项目负责人
     * 
     * @param memId
     * @return List
     * @throws Exception
     */
    public List checkProjectManager(Long memId) throws Exception;
    /**
     * 检查项目是否已经被删除
     * @param projectId
     * @throws Exception
     */
    public boolean checkExist(Long projectId)throws Exception ;
    
    
    /**
     * 得到 用户的 所有的关联项目（不分页）
     * @param memberId
     * @return
     * @throws Exception 
     */
    public List getAllProjectListByMemberId(Long memberId) throws Exception;
    
    /**
     * 根据项目阶段ID获取对应的项目阶段信息
     * @param projectPhaseId	项目阶段ID
     */
	public ProjectPhase getProjectPhase(Long projectPhaseId);
	
	/**
	 * 判断当前用户是否为项目的领导或负责人
	 * @param userId		当前用户ID
	 * @param projectId		项目ID
	 * @return	是否为领导或负责人
	 */
	public boolean isProjectLeaderOrManager(Long userId, Long projectId);
	
	/**
	 * 获取用户在项目中的所有角色类型
	 * @param userId		当前用户ID
	 * @param projectId		项目ID
	 */
	public List<Byte> getProjectRoles(Long userId, Long projectId);
	
	/**
	 * 获取当前项目中的所有成员ID集合
	 * @param projectId		项目ID
	 */
	public List<Long> getAllProjectMembers(Long projectId);
	
	/**
	 * 获取某一单位下的全部项目分类
	 * @param accountId		单位ID
	 */
	public List<ProjectType> getProjectTypes(Long accountId);
	
	/**
     * 根据项目阶段ID获取项目信息
     */
    public List<Object[]> getProjectByPhase(Long phaseId);
    
    /**
     * 根据项目ID获取项目负责人、项目助理
     */
    public List<Object> getProjectMembersByProject(Long projectId);
    
    /**
     * 根据ID更新项目某个字段
     */
    public void updateProjectFieldById(Long projectId, String field, Object object);
    
    /**
     * 根据阶段ID集合获取对应的项目阶段集合，按照开始时间升序排列
     * @param phaseIds	项目阶段ID集合
     * @return	排序号的项目阶段集合
     */
	public List<ProjectPhase> getProjectPhases(Collection<Long> phaseIds);
	
	/**
	 * 单位管理员查看项目树时，获取某一项目类型下的关联项目
	 * @param accountId			单位ID
	 * @param projectTypeId		项目类型ID
	 * @param condition			查询条件
	 * @param textfield			查询值1
	 * @param textfield1		查询值2
	 * @return	分页的关联项目集合
	 */
	public List<ProjectSummary> getProjectsOfTypeByAdmin(long accountId, Long projectTypeId, 
			String condition, String textfield, String textfield1) throws Exception;
	
	/**
	 * 根据ID组合项目信息(列出负责人、领导等)
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public ProjectCompose detailProject(ProjectSummary projectSummary, boolean b) throws Exception;
	/**
	 * 
	 * @param userid
	 * @return
	 */
	public List getUserManagedProjectsByUserId(String userid);
	
	List<ProjectSummary> getAllUserProjectList(long memberid, String condition, String textfield, String textfield1, List<Byte> memberTypeList,Map<String,Object> cndMap) throws Exception;
	
	public List<ProjectSummary> getAllProjectList(long memberid, boolean filter, boolean pagination) throws Exception;
}