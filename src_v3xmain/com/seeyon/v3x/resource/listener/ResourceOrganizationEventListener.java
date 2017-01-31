package com.seeyon.v3x.resource.listener;

import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.resource.manager.ResourceManager;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class ResourceOrganizationEventListener {

	private ResourceManager resourceManager;

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		resourceManager.generateResource(evt.getAccount().getId());
	}
}
