/**
 * 
 */
package com.seeyon.v3x.publicManager;

import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicBullitenMangerMenuCheckImpl implements MenuCheck {
	private BulDataManager bulDataManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long) 公共信息的权限判断
	 */
	public boolean check(long memberId, long loginAccountId) {
		// 公告
		/*boolean bul = bulDataManager.showManagerMenu(memberId);
		if (bul)
			return true;*/
		return false;
	}

	public BulDataManager getBulDataManager() {
		return bulDataManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}
}
