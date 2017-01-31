package com.seeyon.v3x.system.signet.listener;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.event.AbstractOrganizationSyncEventListener;
import com.seeyon.v3x.organization.event.OrganizationEventException;
import com.seeyon.v3x.system.signet.manager.SignetManager;

public class SignetOrganizationSyncEventListener extends AbstractOrganizationSyncEventListener{
	private SignetManager signetManager;

	public void setSignetManager(SignetManager signetManager) {
		this.signetManager = signetManager;
	}
	/**
	 * 删除人员之前的判断操作
	 * 
	 * @param member
	 * @return int 1:成功  	0:失败
	 * @throws OrganizationEventException
	 */
	public int beforeDeleteMember(V3xOrgMember member) throws OrganizationEventException{
		return signetManager.hasSignet(member.getId()) ? 0 : 1;
	}
	
}
