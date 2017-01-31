package com.seeyon.v3x.publicManager;

import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.menu.manager.MenuCheck;

public class PublicEnterpriseMenuCheckImpl implements MenuCheck{

	public boolean check(long memberId, long loginAccountId) {
		boolean isEnterPriseVer = (Boolean)(SysFlag.sys_isEnterpriseVer.getFlag());	//判断是否为企业版
		return isEnterPriseVer;
	}
}
