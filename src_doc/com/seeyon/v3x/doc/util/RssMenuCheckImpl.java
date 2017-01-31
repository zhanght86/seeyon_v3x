/**
 * 
 */
package com.seeyon.v3x.doc.util;

import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 知识管理的 RSS模块是否启用
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-12-4
 */
public class RssMenuCheckImpl implements MenuCheck {

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long)
	 */
	public boolean check(long memberId, long loginAccountId) {
		return Constants.rssModuleEnabled();
	}

}
