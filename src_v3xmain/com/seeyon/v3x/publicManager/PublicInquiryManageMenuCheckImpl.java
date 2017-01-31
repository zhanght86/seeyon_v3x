/**
 * 
 */
package com.seeyon.v3x.publicManager;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicInquiryManageMenuCheckImpl implements MenuCheck {
	private InquiryManager inquiryManager;

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
	public boolean check(long memberId, long loginAccountId) {
		/*// 调查
		try {			
			boolean inq = inquiryManager.hasManageAuthForAccountSpace(memberId, loginAccountId);
			if (inq)
				return true;
		} catch (Exception e) {
		}*/

		return false;
	}

}
