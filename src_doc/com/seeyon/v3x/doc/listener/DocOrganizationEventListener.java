package com.seeyon.v3x.doc.listener;

import com.seeyon.v3x.doc.exception.DocException;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.organization.event.AddMemberEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class DocOrganizationEventListener {

	private DocLibManager docLibManager;

	public DocLibManager getDocLibManager() {
		return docLibManager;
	}

	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}

	@ListenEvent(event = AddMemberEvent.class)
	public void onAddMember(AddMemberEvent evt) throws Exception {
		docLibManager.addDocLib(evt.getMember().getId());
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws DocException {
		docLibManager.addSysDocLibs(evt.getAccount().getId());
	}
}
