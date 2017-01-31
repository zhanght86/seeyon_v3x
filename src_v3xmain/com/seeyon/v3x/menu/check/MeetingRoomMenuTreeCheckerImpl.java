package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.menu.manager.MenuTreeCheck;

public class MeetingRoomMenuTreeCheckerImpl implements MenuTreeCheck {

	@Override
	public boolean check() {
		return !(Boolean)SysFlag.is_gov_only.getFlag();
	}

}
