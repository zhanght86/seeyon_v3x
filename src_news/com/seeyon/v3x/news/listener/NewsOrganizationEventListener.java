package com.seeyon.v3x.news.listener;

import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.organization.event.DeleteMemberEvent;
import com.seeyon.v3x.organization.event.UpdateMemberEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class NewsOrganizationEventListener {

	private NewsTypeManager newsTypeManager;

	public NewsTypeManager getNewsTypeManager() {
		return newsTypeManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		newsTypeManager.initNewsType(evt.getAccount().getId());
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onCancelMember(UpdateMemberEvent evt) throws Exception {
		V3xOrgMember member = evt.getMember();
		// 人员调出
		if (!member.getIsAssigned() && !member.getEnabled()) {
			newsTypeManager.delMember(member.getId());
		}
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onLeaveMember(UpdateMemberEvent evt) throws Exception {
		V3xOrgMember member = evt.getMember();
		// 人员离职
		if (member.getState() == V3xOrgEntity.MEMBER_STATE_RESIGN) {
			newsTypeManager.delMember(member.getId());
		}
	}

	@ListenEvent(event = DeleteMemberEvent.class)
	public void onDeleteMember(DeleteMemberEvent evt) throws Exception {
		newsTypeManager.delMember(evt.getMember().getId());
	}
}
