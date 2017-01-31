package com.seeyon.v3x.flowperm.listener;

import com.seeyon.v3x.flowperm.util.FlowPermHelper;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class FlowOrganizationEventListener {

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		FlowPermHelper.generateFlowPermByAccountId(evt.getAccount().getId());
	}
}