package com.seeyon.v3x.organization.services;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.*;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;

public interface OrganizationServices {

	public void addAccount(V3xOrgAccount account) throws BusinessException; 
	
	public void addDepartment(V3xOrgDepartment dept, Long parentId) throws BusinessException;  
	
	public void addMember(V3xOrgMember member) throws BusinessException;
	
	public void addPost(V3xOrgPost post) throws BusinessException;
	
	public void addLevel(V3xOrgLevel level) throws BusinessException;
	
	public void delAccount(Long accountId) throws BusinessException;
	
	public void delDepartment(Long deptId) throws BusinessException;
	
	public void delMember(Long memberId) throws BusinessException;
	
	public void delPost(Long postId) throws BusinessException;
	
	public void delLevel(Long levelId) throws BusinessException;
	
	public void updateAccount(V3xOrgAccount account) throws BusinessException;
	
	public void updateDepartment(V3xOrgDepartment dept, Long parentId) throws BusinessException;
	
	public void updateDepartment(V3xOrgDepartment dept) throws BusinessException;
	
	public void updateMember(V3xOrgMember member) throws BusinessException;
	
	public void updatePost(V3xOrgPost post) throws BusinessException;
	
	public void updateLevel(V3xOrgLevel level) throws BusinessException;
	/*
	 * 增加无组织用户
	 */
	public void addUnOrgMember(V3xOrgMember member) throws BusinessException;
	/*
	 * 更新无组织用户
	 */
	public void updateUnOrgMember(V3xOrgMember member) throws BusinessException;
	
	public void addUserCurrentPost(List<ConcurrentPost> currentPosts) throws BusinessException;
	
	public void delUserCurrentPost(Long userId) throws BusinessException;
	
	public void clearAllCurrentPosts() throws BusinessException; 
	
	public boolean isLoadData();
	
	public void setLoadData(boolean isLoadData);
	
	public void reloadOrganizationModel() throws BusinessException;
	
	public void reloadAccountData(Long accountId) throws BusinessException;
	
	public OrgManagerDirect getOrgManagerDirect();
	
	/**
	 * 检查跨单位调动人员是否存在代办事项
	 * 
	 * @param memberId 人员
	 * @return boolean
	 * @throws BusinessException
	 */
	public boolean modifyMemberAccountCheck(Long memberId) throws BusinessException;
	
	/**
	 * 批量同步人员
	 * @param members 人员列表
	 * @param rollback 人员校验出错是否回滚
	 * @param isNeedSecondPost 是否更新人员副岗
	 * @param accountId 同步单位ID
	 * @throws BusinessException
	 */
	public Map<Long,String> synchMember(List<V3xOrgMember> members, boolean rollback, boolean isNeedSecondPost,Long accountId) throws Exception ;
	
	/**
	 * 跨单位调整部门
	 * @param deptId 调整部门ID
	 * @param accountId 调入单位ID
	 * @throws BusinessException
	 */
	public List<String[]> moveDept(Long deptId, Long accountId) throws BusinessException;
	/**
	 * 跨单位调整人员所属部门
	 * @param memberId 调整人员ID
	 * @param DeptId 调入部门ID
	 * @throws BusinessException
	 */
	public void moveMember(Long memberId, Long DeptId) throws BusinessException;
	/**
	 * 添加组
	 * @param team
	 * @throws BusinessException
	 */
	public void addTeam(V3xOrgTeam team) throws BusinessException;
	/**
	 * 更新组
	 * @param team
	 * @throws BusinessException
	 */
	public void updateTeam(V3xOrgTeam team) throws BusinessException;

}
