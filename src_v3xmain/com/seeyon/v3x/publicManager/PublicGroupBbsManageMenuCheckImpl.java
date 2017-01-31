/**
 * 
 */
package com.seeyon.v3x.publicManager;
import java.util.List;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicGroupBbsManageMenuCheckImpl implements MenuCheck {
    
    private BbsBoardManager bbsBoardManager;

	public BbsBoardManager getBbsBoardManager() {
		return bbsBoardManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long)
	 * 公共信息的权限判断
	 */
	public boolean check(long memberId, long loginAccountId) {	
		/*boolean bbs = false;
		List<V3xBbsBoard> v3xBbsBoardList =  bbsBoardManager.getAllGroupBbsBoard();
		for(V3xBbsBoard board : v3xBbsBoardList){
			if(bbsBoardManager.validUserIsAdmin(board.getId(), memberId)){
				bbs = true;
				break;
			}
		}
		return bbs;*/
		return false;
	}


}
