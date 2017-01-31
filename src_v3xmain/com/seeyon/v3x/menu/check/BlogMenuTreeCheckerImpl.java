package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.menu.manager.MenuTreeCheck;

public class BlogMenuTreeCheckerImpl implements MenuTreeCheck {

	private SystemConfig systemConfig; 

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public boolean check() {
		return "enable".equals(systemConfig.get(IConfigPublicKey.BLOG_ENABLE));
	}

}
