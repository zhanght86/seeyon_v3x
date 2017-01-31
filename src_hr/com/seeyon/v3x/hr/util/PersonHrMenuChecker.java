/**
 * 
 */
package com.seeyon.v3x.hr.util;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-5
 */
public class PersonHrMenuChecker implements MenuCheck {

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long)
	 */
	public boolean check(long memberId, long loginAccountId) {
		return SystemEnvironment.hasPlugin("hr");
	}

}
