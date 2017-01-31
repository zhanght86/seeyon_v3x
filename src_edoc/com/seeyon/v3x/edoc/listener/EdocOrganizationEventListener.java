package com.seeyon.v3x.edoc.listener;

import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class EdocOrganizationEventListener {

	@ListenEvent(event=AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt)throws Exception{
		EdocHelper.generateZipperFleet(evt.getAccount().getId());
	}	
}
