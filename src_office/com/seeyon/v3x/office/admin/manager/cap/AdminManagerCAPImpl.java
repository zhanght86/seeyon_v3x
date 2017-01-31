package com.seeyon.v3x.office.admin.manager.cap;

import java.util.List;
import java.util.Map;

import com.seeyon.cap.office.admin.domain.MAdminSettingCAP;
import com.seeyon.cap.office.admin.manager.AdminManagerCAP;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.office.admin.domain.MAdminSetting;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public class AdminManagerCAPImpl implements AdminManagerCAP {

	private AdminManager officeAdminManager;

	public void setOfficeAdminManager(AdminManager officeAdminManager) {
		this.officeAdminManager = officeAdminManager;
	}

	@Override
	public boolean checkAdmin(Long id) {
		return officeAdminManager.checkAdmin(id);
	}
	
	@Override
	public boolean checkAdmin(Long userId, Long loginAccountId){
		return officeAdminManager.checkAdmin(userId, loginAccountId);
	}

	@Override
	public List<Long> getAdminManageDepartments(Long adminId, Long accountId, String model) throws BusinessException {
		return officeAdminManager.getAdminManageDepartments(adminId, accountId, model);
	}

	@Override
	public Object[] getMemberDepProxy(V3xOrgMember member, Long accountId, Long adminId, String model, List<Long> belongDep) throws BusinessException {
		return officeAdminManager.getMemberDepProxy(member, accountId, adminId, model, belongDep);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getMyAdmin(List<Long> departmentId) {
		return officeAdminManager.getMyAdmin(departmentId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MAdminSettingCAP> findAdminSetting(Long domainId, String field, String fieldValue) {
		List<MAdminSetting> list = officeAdminManager.findAdminSetting(domainId, field, fieldValue);
		if (list == null) {
			return null;
		}
		return (List<MAdminSettingCAP>) BeanUtils.converts(MAdminSettingCAP.class, list);
	}

	//branches_a8_v350_r_gov 向凡 添加方法，查询具体模块下的信息
	@SuppressWarnings("unchecked")
	@Override
	public List<MAdminSettingCAP> findAdminSetting(String model, Long domainId, String field, String fieldValue){
		List<MAdminSetting> list = officeAdminManager.findAdminSetting(model, domainId, field, fieldValue);
		if (list == null) {
			return null;
		}
		return (List<MAdminSettingCAP>) BeanUtils.converts(MAdminSettingCAP.class, list);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MAdminSettingCAP> getAdminSettingById(Long domainId, Long admin, Long depId, String adminModel, Boolean modelEqual) {
		List<MAdminSetting> list = officeAdminManager.getAdminSettingById(domainId, admin, depId, adminModel, modelEqual);
		if (list == null) {
			return null;
		}
		return (List<MAdminSettingCAP>) BeanUtils.converts(MAdminSettingCAP.class, list);
	}

	@Override
	public int getAdminSettingCount(String sql, Map<String, Object> map) {
		return officeAdminManager.getAdminSettingCount(sql, map);
	}

	@Override
	public void deleteAdminSetting(MAdminSettingCAP admin) {
		MAdminSetting mAdminSetting = new MAdminSetting();
		BeanUtils.convert(mAdminSetting, admin);
		officeAdminManager.deleteAdminSetting(mAdminSetting);		
	}

	//branches_a8_v350_r_gov GOV-1617 王为 修改会议室管理员 Start
	@Override
	public void deleteAdminSettingForUpdate(MAdminSettingCAP admin) {
		MAdminSetting mAdminSetting = officeAdminManager.getAdminSettingById(admin.getId());
		//BeanUtils.convert(mAdminSetting, admin); xiangfan 注释，此处不需要拷贝，修复 修改时报错的问题
		officeAdminManager.deleteAdminSettingForUpdate(mAdminSetting);		
	}
	//branches_a8_v350_r_gov GOV-1617 王为 修改会议室管理员 End
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAdminDepartments(long admin, String adminModel) {
		return officeAdminManager.getAdminDepartments(admin, adminModel);
	}

	@Override
	public void handOverOffice(int officeType, Long nowAdminid, Long domainId) {
		officeAdminManager.handOverOffice(officeType, nowAdminid, domainId);
	}

	@Override
	public void saveAdminSetting(MAdminSettingCAP admin) {
		MAdminSetting mAdminSetting = new MAdminSetting();
		BeanUtils.convert(mAdminSetting, admin);
		officeAdminManager.saveAdminSetting(mAdminSetting);		
	}

	@Override
	public void updateAdminSetting(MAdminSettingCAP admin) {
		MAdminSetting mAdminSetting = new MAdminSetting();
		BeanUtils.convert(mAdminSetting, admin);
		officeAdminManager.updateAdminSetting(mAdminSetting);		
	}

}