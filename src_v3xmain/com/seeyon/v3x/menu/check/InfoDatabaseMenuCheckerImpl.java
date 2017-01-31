package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.menu.manager.MenuTreeCheck;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.manager.OrgManager;

public class InfoDatabaseMenuCheckerImpl implements MenuTreeCheck {

	/** 信息管理员 */
	public final static String ACCOUNT_INFO_ADMIN = "AccountInfoAdmin";

	private OrgManager orgManager;
	
	@Override
	public boolean check() {
		boolean hasInfoPlugin = (Boolean)SysFlag.is_gov_only.getFlag() && SystemEnvironment.hasPlugin("govInfoPlugin");
		if(hasInfoPlugin) {
			try {
				return isAccountInfoAdmin(CurrentUser.get().getAccountId(), CurrentUser.get().getId(), ACCOUNT_INFO_ADMIN);
			} catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 是否是信息报送管理员
	 * @param accountId
	 * @param userId
	 * @param roleName
	 * @return
	 */
	private boolean isAccountInfoAdmin(Long accountId, Long userId, String roleName) {
		try {
		     V3xOrgRole accountInfoAdminRole = orgManager.getRoleByName(roleName, accountId);
		     if(accountInfoAdminRole != null) {
		    	 return orgManager.isInDomain(accountId, accountInfoAdminRole.getId(), userId);
		     }
		} catch(Exception e) {
			return false;
		}		 
		return false;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
}
