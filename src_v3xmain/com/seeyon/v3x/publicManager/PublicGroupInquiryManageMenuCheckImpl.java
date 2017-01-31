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
public class PublicGroupInquiryManageMenuCheckImpl implements MenuCheck {
    private InquiryManager inquiryManager;
    
	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long)
	 * 公共信息的权限判断
	 */
	public boolean check(long memberId, long loginAccountId) {		
		/*boolean inquiry = false;
		try {
		inquiry = inquiryManager.hasManageAuthForGroupSpace();
		} catch (Exception e) {
		}
		return inquiry;*/
		return false;
	}

}
