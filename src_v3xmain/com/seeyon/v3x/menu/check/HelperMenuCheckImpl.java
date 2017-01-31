package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.util.Strings;

public class HelperMenuCheckImpl implements MenuCheck {

	public boolean check(long arg0, long arg1) {
		if(Strings.isBlank((String)SysFlag.NCSuffix.getFlag())){
			return true;
		}
		return false;
	}

}
