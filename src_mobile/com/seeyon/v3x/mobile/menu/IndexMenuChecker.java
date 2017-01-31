package com.seeyon.v3x.mobile.menu;

import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.menu.manager.MenuCheck;

public class IndexMenuChecker implements MenuCheck {

	public boolean check(long memberId, long loginAccountId) {
		return IndexInitConfig.hasLuncenePlugIn();
	}

}
