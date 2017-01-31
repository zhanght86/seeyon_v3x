package com.seeyon.v3x.space.listener;

import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.organization.event.AddDepartmentEvent;
import com.seeyon.v3x.organization.event.AddMemberEvent;
import com.seeyon.v3x.organization.event.DeleteDepartmentEvent;
import com.seeyon.v3x.organization.event.UpdateDepartmentEvent;
import com.seeyon.v3x.organization.event.UpdateMemberEvent;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class SpaceOrganizationEventListener {

	private SpaceManager spaceManager;

	public SpaceManager getSpaceManager() {
		return spaceManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	@ListenEvent(event = AddDepartmentEvent.class)
	public void onAddDepartment(AddDepartmentEvent evt) throws Exception {
		if (evt.getDept().isCreateDeptSpace()) {
			spaceManager.createDepartmentSpace(evt.getDept().getId(), evt.getDept().getName(),
				evt.getDept().getOrgAccountId());
		}
	}

	@ListenEvent(event = UpdateDepartmentEvent.class)
	public void onUpdateDepartment(UpdateDepartmentEvent evt) throws Exception {
		if (evt.getDept().isCreateDeptSpace()) {
			spaceManager.createDepartmentSpace(evt.getDept().getId(), evt.getDept()
					.getName(), evt.getDept().getOrgAccountId());
		}
		else {
			spaceManager.enableDepartmentSpace(evt.getDept().getId(), false);
		}
	}

	@ListenEvent(event = DeleteDepartmentEvent.class)
	public void onDeleteDepartment(DeleteDepartmentEvent evt) throws Exception {
		spaceManager.deleteDepartmentSpace(evt.getDept().getId());
	}
	
	@ListenEvent(event = AddMemberEvent.class)
	public void onMemberAdd(AddMemberEvent evt){
		spaceManager.putMember2SpaceSecurity(evt.getMember());
	}
	
	@ListenEvent(event = UpdateMemberEvent.class)
	public void onMemberUpdate(UpdateMemberEvent evt){
		spaceManager.putMember2SpaceSecurity(evt.getMember());
	}
	
	@ListenEvent(event = AddAccountEvent.class)
	public void onAccountCreate(AddAccountEvent evt){
		try {
			spaceManager.initAccountSpace(evt.getAccount().getId());
		} catch (SpaceException e) {
		}
	}
}
