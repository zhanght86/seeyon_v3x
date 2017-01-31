/**
 * 
 */
package com.seeyon.v3x.publicManager;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicGroupMenuCheckImpl implements MenuCheck {
    
    private OrgManager orgManager;
    
    public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }
	/* (non-Javadoc)
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long)
	 * 公共信息的权限判断
	 */
	public boolean check(long memberId, long loginAccountId) {		
		// 公告
		boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag());//判断是否为集团版
		if(isGroupVer){
		    try {
                V3xOrgMember m = orgManager.getMemberById(memberId);
                return (m != null && m.getIsInternal());
            }
            catch (BusinessException e) {
                e.printStackTrace();
            }
        }
		return false;
	}


}
