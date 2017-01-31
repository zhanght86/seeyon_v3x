package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.menu.manager.MenuCheck;

public class BlogMenuCheckerImpl implements MenuCheck {

	private SystemConfig systemConfig; 

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public boolean check(long memberId, long loginAccountId) {
		return "enable".equals(systemConfig.get(IConfigPublicKey.BLOG_ENABLE));
	}

}
