package com.seeyon.v3x.menu.check;

import java.util.List;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.product.ProductInfo;

public class MenuCheckHelper {
	
	/**
	 * 判断是否为部门管理员
	 * @param orgManager
	 * @param memberId
	 * @param loginAccountId
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isDepartmentManager(OrgManager orgManager, Long memberId, Long loginAccountId) throws BusinessException{
		V3xOrgRole deptAdminRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN, loginAccountId);
		if(deptAdminRole!=null){
			List<Long> deptIds = orgManager.getDomainByRole(deptAdminRole.getId(), memberId);
			if(deptIds != null&&deptIds.size() > 0){
				for(Long deptId:deptIds){
					V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
					if(dept.getOrgAccountId().longValue() == loginAccountId){
						return true;
					}
				}
			}			
		}
		return false;
	}
	
	public static boolean isHRAdmin(OrgManager orgManager, Long memberId, Long loginAccountId) throws BusinessException{
		if(SystemEnvironment.hasPlugin(ProductInfo.PluginNoMapper.hr.name())){
			V3xOrgRole hrAdminRole = orgManager.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_HRADMIN, loginAccountId);
			if(hrAdminRole!=null){
				List<Long> accountIds = orgManager.getDomainByRole(hrAdminRole.getId(), memberId);
				if(accountIds!=null && accountIds.size() > 0){
					for(Long accountId:accountIds){
						if(accountId.longValue()==(loginAccountId)){
							return true;
						}
					}				
				}				
			}
		}
		return false;
	}
}
