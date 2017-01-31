package com.seeyon.v3x.office.admin.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;

import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.domain.MAdminSettingId;

public interface AdminSettingDAO {

	public void save(MAdminSetting admin);

	public void update(MAdminSetting admin);

	public void delete(MAdminSetting admin);
	
	public void deleteForUpdate(final MAdminSetting admin);

	public SQLQuery find(String sql, Map map);

	public int getCount(String sql, Map map);

	public MAdminSetting load(MAdminSettingId id);

	public List findAdminSettingByModel(String model, Long domainId);

	public void updateAssetManager(Long adminId, Long domainId);

	public void updateStockManager(Long adminId, Long domainId);

	public void updateBookManager(Long adminId, Long domainId);

	public void updateMeetingManager(Long adminId, Long domainId);

	public void updateAutoManager(Long adminId, Long domainId);

	public List findAdminManageDepartment(Long adminId, Long accountId, String model);

	public List listAdminSetting(Long domainId, String fieldValue, String keyword);

	public List listAdminSettingById(Long domainId, Long admin, Long depId, String adminModel, Boolean modelEqual);

	public boolean checkAdmin(Long id);
	
	/**
	 * branches_a8_v350sp1_r_gov 向凡，主要是为了修复GOV-4480 当前人在兼职单位有会议室管理员权限，需要传入当前 LoginAccountId
	 * @param userId 当前用户Id
	 * @param loginAccountId 当前登录单位Id
	 * @return
	 */
	public boolean checkAdmin(Long userId, Long loginAccountId);

	public List getMyAdmin(List departmentId);
	
	/**
	 * branches_a8_v350_r_gov 向凡 添加接口，查询具体模块下的信息，（如：政务版的会议室管理只需要查询会议室管理模块）
	 * @param model 模块名称	如：00001:会议室管理 模块
	 * @param domainId 单位名称
	 * @param field 小查询字段
	 * @param keyword 小查询字段
	 * @return
	 */
	public List<MAdminSetting> listAdminSetting(String model, Long domainId, String field, String keyword);

}