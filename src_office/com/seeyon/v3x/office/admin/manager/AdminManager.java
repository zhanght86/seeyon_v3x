package com.seeyon.v3x.office.admin.manager;

import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.domain.MAdminSettingId;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public interface AdminManager {
	
	public void saveAdminSetting(MAdminSetting admin);
	
	public void updateAdminSetting(MAdminSetting admin);
	
	public void updateAdminSetting(MAdminSetting admin, MAdminSettingId id)throws Exception;
	
	public void deleteAdminSetting(MAdminSetting admin);
	
	public void deleteAdminSettingForUpdate(MAdminSetting admin);
	
	public int getAdminSettingCount(String sql,Map map);
	
	public MAdminSetting getAdminSettingById(MAdminSettingId id);
	
	public List findAdminSetting(Long domainId,String field,String fieldValue);
	
	public int checkAdmin(long memberId, int model);
	
	public String getDepartAdmins(long accountId, long departmentId, int model)throws Exception;
	
	public String getInfoIds(long memberId, int model);
	
	public List getAdminDepartments(long admin, String adminModel);
	
	/**
	 * 判断管理员能否删除
	 * @param userId
	 * @return
	 */
	public boolean hasAdminInUse(long userId);
	
	/**
	 * 根据 model 和 domainId 得到查询的MAdminSettingId List
	 */
	public List getAdminSettingByModelAdmin(String model,Long domainId);
	
	/**
	 * 移交 管理员权限
	 * @param officeType
	 * @param nowAdminid
	 * @param departId
	 */
	public void handOverOffice(int officeType,Long nowAdminid,Long domainId);
	
	/**
	 * 管理员 得到管理的某项功能的 的单位
	 * @param adminId
	 * @param accountId
	 * @param model
	 * @return
	 * @throws BusinessException 
	 */
	public Object[] getAdminManageDepartment(Long adminId,Long accountId,String model) throws BusinessException;
	
	/**
	 * 管理员 得到管理的某项功能的 的单位
	 * @param adminId
	 * @param accountId
	 * @param model
	 * @return
	 * @throws BusinessException 
	 */
	public List<Long> getAdminManageDepartments(Long adminId,Long accountId,String model) throws BusinessException;
	
	
	public List<V3xOrgMember> getOutCntMemberByDepartment(Object[] depId,Boolean filter,Long accountId);
	/**
	 * 得到 member成员在管理员管理的某项功能model 中  member的部门名称（兼职，副职，原部门）[部门名称,是否兼职]
	 * @param member 成员
	 * @param 管理员单位 id
	 * @param 管理员id
	 * @param 管理模块
	 * @throws BusinessException 
	 */
	public Object[] getMemberDepProxy(V3xOrgMember member,Long accountId,Long adminId,String model,List<Long> belongDep) throws BusinessException;

	public List	getAdminSettingById(Long domainId,Long admin,Long depId,String adminModel,Boolean modelEqual);
	
	/**
	 * 判断是否为会议室管理员
	 */
	public boolean checkAdmin(Long id);
	
	/**
	 * branches_a8_v350sp1_r_gov 向凡，主要是为了修复GOV-4480 当前人在兼职单位有会议室管理员权限，需要传入当前 LoginAccountId
	 * @param userId 当前用户Id
	 * @param loginAccountId 当前登录单位Id
	 * @return
	 */
	public boolean checkAdmin(Long userId, Long loginAccountId);

	/**
	 * 获取我部门的会议室管理员范围
	 */
	public List getMyAdmin(List departmentId);
	
	/**
	 * branches_a8_v350_r_gov 向凡 添加接口，查询具体模块下的信息，（如：政务版的会议室管理只需要查询会议室管理模块）
	 * @param model 模块名称	如：00001:会议室管理 模块
	 * @param domainId 单位名称
	 * @param field 小查询字段
	 * @param keyword 小查询字段
	 * @return
	 */
	public List<MAdminSetting> findAdminSetting(String model, Long domainId, String field, String keyword);

}