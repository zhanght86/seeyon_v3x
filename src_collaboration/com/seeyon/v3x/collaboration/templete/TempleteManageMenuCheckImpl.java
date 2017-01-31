package com.seeyon.v3x.collaboration.templete;

import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 协同模板管理菜单检查实现类
 *
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 *
 */
public class TempleteManageMenuCheckImpl implements MenuCheck
{

    private TempleteCategoryManager templeteCategoryManager;
    
    public void setTempleteCategoryManager(
            TempleteCategoryManager templeteCategoryManager) {
        this.templeteCategoryManager = templeteCategoryManager;
    }
    
    public boolean check(long memberId, long loginAccountId) {
        return templeteCategoryManager.isTempleteManager(memberId, loginAccountId);
    }
}
