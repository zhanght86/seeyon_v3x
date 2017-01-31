package com.seeyon.v3x.mobile.menu;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.menu.manager.MenuCheck;

public class EdocMenuChecker implements MenuCheck {

	public boolean check(long memberId, long loginAccountId) {
		return com.seeyon.v3x.common.SystemEnvironment.hasPlugin("edoc") && Functions.isEnableEdoc();
	}

}
