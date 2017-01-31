package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 简单的MenuCheck实现，永远返回true，用于所有普通用户都可以访问的菜单
 */
public class MenuForAllUsersChecker implements MenuCheck {

	@Override
	public boolean check(long memberId, long loginAccountId) {
		return true;
	}

}
