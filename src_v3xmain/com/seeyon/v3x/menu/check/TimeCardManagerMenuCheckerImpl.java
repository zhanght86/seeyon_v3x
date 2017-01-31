package com.seeyon.v3x.menu.check;

import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.organization.manager.OrgManager;

public class TimeCardManagerMenuCheckerImpl implements MenuCheck
{

    private SystemConfig systemConfig;
    
    private OrgManager orgManager;
    
    public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public void setOrgManager(OrgManager orgManager) {
        this.orgManager = orgManager;
    }

    /**
     * @return true : 有HR插件，且系统开关启用 
     */
    public boolean check(long memberId, long loginAccountId) {
        //是否包含插件
        boolean flag = com.seeyon.v3x.common.SystemEnvironment.hasPlugin("hr") && MainHelper.isHRAdmin(orgManager);
        if(flag){
            if(systemConfig != null){
                String item = systemConfig.get(com.seeyon.v3x.config.IConfigPublicKey.CARD_ENABLE);
                if(item != null){
                    flag = com.seeyon.v3x.config.IConfigPublicKey.ENABLE.equals(item);
                }
            }
        }
        
        return flag;
    }

}
