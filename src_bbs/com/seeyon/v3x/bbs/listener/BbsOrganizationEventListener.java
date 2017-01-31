package com.seeyon.v3x.bbs.listener;

import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.organization.event.AddDepartmentEvent;
import com.seeyon.v3x.organization.event.DeleteDepartmentEvent;
import com.seeyon.v3x.organization.event.DeleteMemberEvent;
import com.seeyon.v3x.organization.event.UpdateDepartmentEvent;
import com.seeyon.v3x.organization.event.UpdateMemberEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class BbsOrganizationEventListener {

	private BbsBoardManager bbsBoardManager;

	public BbsBoardManager getBbsBoardManager() {
		return bbsBoardManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	@ListenEvent(event = AddDepartmentEvent.class)
	public void onAddDepartment(AddDepartmentEvent evt) throws Exception {
		if (evt.getDept().isCreateDeptSpace()) {
			bbsBoardManager.createDepartmentBbsBoard(evt.getDept().getId(), evt.getDept().getOrgAccountId(), evt.getDept().getName());
		}
	}

	@ListenEvent(event = UpdateDepartmentEvent.class)
	public void onUpdateDepartment(UpdateDepartmentEvent evt) throws Exception {
		if (evt.getDept().isCreateDeptSpace()) {
			bbsBoardManager.createDepartmentBbsBoard(evt.getDept().getId(), evt.getDept().getOrgAccountId(), evt.getDept().getName());
		}
	}

	@ListenEvent(event = DeleteDepartmentEvent.class)
	public void onDeleteDepartment(DeleteDepartmentEvent evt) throws Exception {
		bbsBoardManager.deleteV3xBbsBoard(evt.getDept().getId());
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		bbsBoardManager.initBbsBoard(evt.getAccount().getId());
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onCancelMember(UpdateMemberEvent evt) throws Exception {
		V3xOrgMember member = evt.getMember();
		// 人员调出
		if (!member.getIsAssigned() && !member.getEnabled()) {
			bbsBoardManager.delMember(member.getId());
		}
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onLeaveMember(UpdateMemberEvent evt) throws Exception {
		V3xOrgMember member = evt.getMember();
		// 人员离职
		if (member.getState() == V3xOrgEntity.MEMBER_STATE_RESIGN) {
			bbsBoardManager.delMember(member.getId());
		}
	}

	@ListenEvent(event = DeleteMemberEvent.class)
	public void onDeleteMember(DeleteMemberEvent evt) throws Exception {
		bbsBoardManager.delMember(evt.getMember().getId());
	}
}
