package com.seeyon.v3x.bulletin.listener;

import com.seeyon.v3x.bulletin.manager.BaseBulletinManager;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.organization.event.AddDepartmentEvent;
import com.seeyon.v3x.organization.event.DeleteDepartmentEvent;
import com.seeyon.v3x.organization.event.DeleteMemberEvent;
import com.seeyon.v3x.organization.event.UpdateMemberEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class BulOrganizationEventListener {

	private BulDataManager bulDataManager;
	private BulTypeManager bulTypeManager;

	public BulDataManager getBulDataManager() {
		return bulDataManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public BulTypeManager getBulTypeManager() {
		return bulTypeManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	@ListenEvent(event = AddDepartmentEvent.class)
	public void onAddDepartment(AddDepartmentEvent evt) throws Exception {
		if (evt.getDept().isCreateDeptSpace()) {
			((BaseBulletinManager) bulDataManager).getBulletinUtils().createBulTypeByDept(
					evt.getDept().getName() + ResourceBundleUtil.getString(Constants.BUL_RESOURCE_BASENAME,"bul.more"), 
					evt.getDept().getId(), evt.getDept().getOrgAccountId());
		}
	}

	@ListenEvent(event = DeleteDepartmentEvent.class)
	public void onDeleteDepartment(DeleteDepartmentEvent evt) throws Exception {
		bulTypeManager.delDept(evt.getDept().getId());
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		bulTypeManager.initBulType(evt.getAccount().getId());
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onCancelMember(UpdateMemberEvent evt) throws Exception {
		V3xOrgMember member = evt.getMember();
		// 人员调出
		if (!member.getIsAssigned() && !member.getEnabled()) {
			bulTypeManager.delMember(member.getId());
		}
	}

	@ListenEvent(event = UpdateMemberEvent.class)
	public void onLeaveMember(UpdateMemberEvent evt) throws Exception {
		V3xOrgMember member = evt.getMember();
		// 人员离职
		if (member.getState() == V3xOrgEntity.MEMBER_STATE_RESIGN) {
			bulTypeManager.delMember(member.getId());
		}
	}

	@ListenEvent(event = DeleteMemberEvent.class)
	public void onDeleteMember(DeleteMemberEvent evt) throws Exception {
		bulTypeManager.delMember(evt.getMember().getId());
	}
}