package com.seeyon.v3x.hr.util.listener;

import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class HrOrganizationEventListener {

	private UserDefinedManager userDefinedManager;

	public void setUserDefinedManager(UserDefinedManager userDefinedManager) {
		this.userDefinedManager = userDefinedManager;
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		this.userDefinedManager.initHrData(evt.getAccount().getId());
	}

}