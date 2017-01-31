package com.seeyon.v3x.menu.check;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.organization.manager.OrgManager;

public class DepartmentAdminMenuCheckerAppLogImpl implements MenuCheck {
	private static final Log log = LogFactory.getLog(DepartmentAdminMenuCheckerAppLogImpl.class);
	private OrgManager orgManager;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public boolean check(long memberId, long loginAccountId) {
		try {
			boolean isDeptAdmin = MenuCheckHelper.isDepartmentManager(orgManager, memberId, loginAccountId);
			if(isDeptAdmin){
				boolean isHrAdmin = MenuCheckHelper.isHRAdmin(orgManager, memberId, loginAccountId);
				if(isHrAdmin){
					return true;
				}
			}
		}catch (BusinessException e) {
			log.error(e);
		}
		return false;
	}
}
