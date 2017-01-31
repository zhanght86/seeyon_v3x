package com.seeyon.v3x.menu.check;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.organization.manager.OrgManager;

public class DepartmentAdminMenuCheckerImpl implements MenuCheck {
	private static final Log log = LogFactory.getLog(DepartmentAdminMenuCheckerImpl.class);
	
	private OrgManager orgManager;
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public boolean check(long memberId, long loginAccountId) {
		// 当前人员是本单位的部门管理员而不是hr管理员时返回真，否则返回假
		try {
			boolean isDeptAdmin = MenuCheckHelper.isDepartmentManager(orgManager, memberId, loginAccountId);
			if(isDeptAdmin){
				boolean isHrAdmin = MenuCheckHelper.isHRAdmin(orgManager, memberId, loginAccountId);
				if(!isHrAdmin){
					return true;
				}
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		return false;
	}


}
