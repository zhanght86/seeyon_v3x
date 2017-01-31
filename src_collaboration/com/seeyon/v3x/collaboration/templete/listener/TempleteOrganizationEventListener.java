package com.seeyon.v3x.collaboration.templete.listener;

import java.util.List;

import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.event.AddMemberEvent;
import com.seeyon.v3x.organization.event.UpdateMemberEvent;
import com.seeyon.v3x.organization.event.UpdateTeamEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class TempleteOrganizationEventListener {

	private TempleteConfigManager templeteConfigManager;

	public TempleteConfigManager getTempleteConfigManager() {
		return templeteConfigManager;
	}

	public void setTempleteConfigManager(
			TempleteConfigManager templeteConfigManager) {
		this.templeteConfigManager = templeteConfigManager;
	}

	@ListenEvent(event = AddMemberEvent.class)
	public void onAddMember(AddMemberEvent evt) throws Exception {
		templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER, evt
				.getMember().getId(), evt.getMember().getId());
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onUpdateMember(UpdateMemberEvent evt) throws Exception {
		if(evt.getMember().isChangeOrgInfo()){
			templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER, evt
				.getMember().getId(), evt.getMember().getId());
		}
	}
	
	@ListenEvent(event = UpdateTeamEvent.class)
	public void onUpdateTeam(UpdateTeamEvent evt) throws Exception{
		List<Long>  memberIds = evt.getTeam().getAllMembers();
		for(Long memberId : memberIds){
			templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER, 
					memberId, memberId);
		}
	}
}
