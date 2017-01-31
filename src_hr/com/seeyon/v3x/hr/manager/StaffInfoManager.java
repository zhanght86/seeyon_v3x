package com.seeyon.v3x.hr.manager;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.v3x.hr.domain.Assess;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.EduExperience;
import com.seeyon.v3x.hr.domain.PostChange;
import com.seeyon.v3x.hr.domain.Relationship;
import com.seeyon.v3x.hr.domain.RewardsAndPunishment;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.domain.WorkRecord;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public interface StaffInfoManager {
	/**
	 * 方法描述：根据人员ID查询该人员基本信息
	 * 策略：先从缓存中取，如果取不到，再从数据库中取
	 * @return StaffInsfo
	 * @throws Exception
	 *
	 */
	public StaffInfo getStaffInfoById(Long staffid) throws Exception;
	
	/**
	 * 直接从数据库中取对应记录，配合集群同步缓存之用
	 */
	public StaffInfo getStaffInfoByIdFromDB(Long memberId) throws Exception; 
	
	/**
	 * 增删改职员信息时，同步缓存与数据库内容，使之保持一致
	 * @param staffinfo		职员信息，可能为null
	 * @param memberId		职员ID，可能为null
	 * @param action		操作类型：CRUD之一
	 */
	public void syncCache(StaffInfo staffinfo, Long memberId, Constants.ActionType action);
	
	/**
	 * 根据人员ID列表批量取人员基本信息。
	 * @param staffidList 人员ID列表
	 * @return 人员ID-基本信息实体Map。
	 * @throws Exception
	 */
	Map<Long,StaffInfo> getStaffInfos(List<Long> staffidList) throws Exception;
	/**
	 * 根据人员ID查询该人员的联系信息
	 * 
	 */
	public ContactInfo getContactInfoById(Long staffid) throws Exception;
	
	/**
	 * 获取所有联系信息
	 * @return
	 * @throws Exception
	 */
	public Map<Long, ContactInfo> getAllContactInfo() throws Exception;
	
	/**
	 * 根据人员ID查询该人员家庭成员与社会关系信息
	 * 
	 */
	public List<Relationship> getRelationshipByStafferId(Long staffid)throws Exception;
	
	/**
	 * 根据人员ID查询该人员工作履历
	 * 
	 */
	public List<WorkRecord> getWorkRecordByStafferId(Long staffid)throws Exception;
	
	/**
	 * 根据人员ID查询该人员教育培训经历
	 * 
	 */
	public List<EduExperience> getEduExperienceByStafferId(Long staffid)throws Exception;
	
	/**
	 * 根据人员ID查询该人员职务变动
	 * 
	 */
	public List<PostChange> getPostChangeByStafferId(Long staffid)throws Exception;
	
	/**
	 * 根据人员ID查询该人员考核情况
	 * 
	 */
	public List<Assess> getAssessByStafferId(Long staffid)throws Exception;
	
	/**
	 * 根据人员ID查询该人员奖惩档案
	 * 
	 */
	public List<RewardsAndPunishment> getRewardsAndPunishmentByStafferId(Long staffid)throws Exception;
		
	/**
	 * 根据id查询一条家庭成员与社会关系信息记录
	 * 
	 */
	public Relationship getRelationshipById(Long id)throws Exception;
	
	/**
	 * 根据id查询一条工作履历记录
	 * 
	 */
	public WorkRecord getWorkRecordById(Long id)throws Exception;
	
	/**
	 * 根据id查询一条教育培训经历记录
	 * 
	 */
	public EduExperience getEduExperienceById(Long id)throws Exception;
	
	/**
	 * 根据id查询一条职务变动记录
	 * 
	 */
	public PostChange getPostChangeById(Long id)throws Exception;
	
	/**
	 * 根据id查询一条考核情况记录
	 * 
	 */
	public Assess getAssessById(Long id)throws Exception;
	
	/**
	 * 根据id查询一条奖惩档案记录
	 * 
	 */
	public RewardsAndPunishment getRewardsAndPunishmentById(Long id)throws Exception;
	
	/**
	 * 更新人员信息(只在导入时调用,照片、附件信息不更新)
     *   
	 */
	public void updateStaffInfo(StaffInfo staffinfo)throws Exception;
	
	/**
	 * 添加人员信息(只在导入时调用,不保存照片、附件信息)
	 * 
	 */
	public void addStaffInfo(StaffInfo staffinfo)throws Exception;
	
	/**
	 * 添加人员信息(只存于HR人员信息表，组织机构的member表中不加入数据)
	 * 
	 */
	public void addStaffInfo(HttpServletRequest request,StaffInfo staffinfo)throws Exception;
	
	/**
	 * 添加人员信息
	 * 
	 */
	public void addStaffInfo(HttpServletRequest request,StaffInfo staffinfo,V3xOrgMember member)throws Exception;
	
	/**
	 * 更新人员信息(只更新HR人员信息表中的信息)
	 * 
	 */
	public void updateStaffInfo(HttpServletRequest request,StaffInfo staffinfo)throws Exception;

	/**
	 * 更新人员信息
	 * @param isNewStaffer
	 *           为true时，在HR人员信息表中新建一条staffinfo信息,同时更新组织机构member表里的信息;为false时只更新两表信息
	 * 
	 */
	public void updateStaffInfo(HttpServletRequest request,StaffInfo staffinfo,V3xOrgMember member,boolean isNewStaffer)throws Exception;
	
	/**
	 * 更新联系信息
	 * 
	 */
	public void updateContactInfo(ContactInfo contactInfo,V3xOrgMember member)throws Exception;
	
	/**
	 * 添加联系信息
	 * 
	 */
	public void addContactInfo(ContactInfo contactInfo,V3xOrgMember member)throws Exception;
	
	/**
	 * 更新家庭成员与社会关系信息
	 * 
	 */
	public void updateRelationship(Relationship relationship)throws Exception;
	
	/**
	 * 添加家庭成员与社会关系信息
	 * 
	 */
	public void addRelationship(Relationship relationship)throws Exception;
	
	/**
	 * 更新工作履历
	 * 
	 */
	public void updateWorkRecord(WorkRecord workRecord)throws Exception;
	
	/**
	 * 添加工作履历
	 * 
	 */
	public void addWorkRecord(WorkRecord workRecord)throws Exception;
	
	/**
	 * 更新教育培训经历
	 * 
	 */
	public void updateEduExperience(EduExperience eduExperience)throws Exception;
	
	/**
	 * 添加教育培训经历
	 * 
	 */
	public void addEduExperience(EduExperience eduExperience)throws Exception;
	
	/**
	 * 更新职务变动记录
	 * 
	 */
	public void updatePostChange(PostChange postChange)throws Exception;
	
	/**
	 * 添加职务变动记录
	 * 
	 */
	public void addPostChange(PostChange postChange)throws Exception;
	
	/**
	 * 更新考核情况记录
	 * 
	 */
	public void updateAssess(Assess assess)throws Exception;
	
	/**
	 * 添加考核情况记录
	 * 
	 */
	public void addAssess(Assess assess)throws Exception;
	
	/**
	 * 更新奖惩档案
	 * 
	 */
	public void updateRewardsAndPunishment(RewardsAndPunishment rewardsAndPunishment)throws Exception;
	
	/**
	 * 添加奖惩档案
	 * 
	 */
	public void addRewardsAndPunishment(RewardsAndPunishment rewardsAndPunishment)throws Exception;
	
	/**
	 * 删除某人员的信息
	 * 
	 */
	public void deleteStaffInfo(Long staffid)throws Exception;
	
	/**
	 * 删除一条家庭成员与社会关系信息
	 * 
	 */
	public void deleteRelationship(Long id)throws Exception;
	
	
	/**
	 * 删除一条工作履历
	 * 
	 */
	public void deleteWorkRecord(Long id)throws Exception;
	
	/**
	 * 删除一条教育培训经历
	 * 
	 */
	public void deleteEduExperience(Long id)throws Exception;
	
	
	/**
	 * 删除一条职务变动记录
	 * 
	 */
	public void deletePostChange(Long id)throws Exception;
	
	
	/**
	 * 删除一条考核情况记录
	 * 
	 */
	public void deleteAssess(Long id)throws Exception;
	
	
	/**
	 * 删除一条奖惩档案
	 * 
	 */
	public void deleteRewardsAndPunishment(Long id)throws Exception;
	

}
