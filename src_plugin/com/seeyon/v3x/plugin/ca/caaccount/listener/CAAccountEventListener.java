package com.seeyon.v3x.plugin.ca.caaccount.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.plugin.ca.caaccount.manager.CAAccountManager;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.event.UpdateMemberEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class CAAccountEventListener {
    private static Log log = LogFactory.getLog(CAAccountEventListener.class);
	private CAAccountManager caAccountManager;
		
    public CAAccountManager getCaAccountManager() {
        return caAccountManager;
    }
    
    public void setCaAccountManager(CAAccountManager caAccountManager) {
        this.caAccountManager = caAccountManager;
    }

    /**
     * 修改人员时被调用
     */
    @ListenEvent(event = UpdateMemberEvent.class)
    public void onUpdateMember(UpdateMemberEvent evt) throws Exception {
        V3xOrgMember oldMember = evt.getOldMember();
        V3xOrgMember newMember = evt.getMember();
        if (oldMember == null) {
            log.error("修改人员时出错：oldMember=null");
            return;
        }
        if (newMember == null) {
            log.error("修改人员时出错：newMember=null");
            return;
        }
        String oldLoginName = oldMember.getLoginName();
        String newLoginName = newMember.getLoginName();
        if(!oldLoginName.equals(newLoginName)){
            //caAccountManager.updateLoginName(oldLoginName, newLoginName);
        }
    }
}
