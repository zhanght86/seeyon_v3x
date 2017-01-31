package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.common.authenticate.domain.User;

/**
 * 
 * @author wangwei
 * 验证当前用户是否在单位角色中设置
 * UnitsMeetingAdmin=会议室管理员
 */
public class MeetingAdminMenuCheckerImpl implements MenuCheck {
	/**
	 * 重写父类的方法
	 */
	@Override
	public boolean check(long memberId, long loginAccountId) {
		User user = new User();
		user.setId(memberId);
		user.setLoginAccount(loginAccountId);
		return Functions.isRole("UnitsMeetingAdmin",  user);
	}
}
