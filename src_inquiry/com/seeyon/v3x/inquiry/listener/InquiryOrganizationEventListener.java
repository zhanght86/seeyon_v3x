package com.seeyon.v3x.inquiry.listener;

import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.organization.event.DeleteMemberEvent;
import com.seeyon.v3x.organization.event.UpdateMemberEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class InquiryOrganizationEventListener {

	private InquiryManager inquiryManager;

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		inquiryManager.initInquiryType(evt.getAccount().getId());
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onCancelMember(UpdateMemberEvent evt) throws Exception {
		V3xOrgMember member = evt.getMember();
		// 人员调出
		if (!member.getIsAssigned() && !member.getEnabled()) {
			inquiryManager.delMember(member.getId());
		}
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onLeaveMember(UpdateMemberEvent evt) throws Exception {
		V3xOrgMember member = evt.getMember();
		// 人员离职
		if (member.getState() == V3xOrgEntity.MEMBER_STATE_RESIGN) {
			inquiryManager.delMember(member.getId());
		}
	}

	@ListenEvent(event = DeleteMemberEvent.class)
	public void onDeleteMember(DeleteMemberEvent evt) throws Exception {
		inquiryManager.delMember(evt.getMember().getId());
	}
}
