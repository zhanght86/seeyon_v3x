/**
 * 
 */
package com.seeyon.v3x.publicManager;

import java.util.List;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicBbsManageMenuCheckImpl implements MenuCheck {

	private BbsBoardManager bbsBoardManager;

	public BbsBoardManager getBbsBoardManager() {
		return bbsBoardManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public boolean check(long memberId, long loginAccountId) {
		/*List<V3xBbsBoard> v3xBbsBoardList = this.bbsBoardManager
				.getAllCorporationBbsBoard();
		for (V3xBbsBoard board : v3xBbsBoardList) {
			if (bbsBoardManager.validUserIsAdmin(board.getId(), memberId)) {
				return true;
			}
		}*/

		return false;
	}

}
