package com.seeyon.v3x.agent.listener;

import com.seeyon.v3x.agent.manager.AgentIntercalateManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.event.DeleteMemberEvent;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class AgentOrganizationEventListener {

	private AgentIntercalateManager agentIntercalateManager;

	public AgentIntercalateManager getAgentIntercalateManager() {
		return agentIntercalateManager;
	}

	public void setAgentIntercalateManager(
			AgentIntercalateManager agentIntercalateManager) {
		this.agentIntercalateManager = agentIntercalateManager;
	}

	@ListenEvent(event = DeleteMemberEvent.class)
	public void onDeleteMember(DeleteMemberEvent evt) throws Exception {
		agentIntercalateManager.cancelUserAgent(evt.getMember().getId(), CurrentUser.get());
	}
}
