package com.seeyon.v3x.meeting.util;
/**
 * 在为代理人员发送系统消息时，需传入被代理人ID作为参数及代理人ID作为消息接受者<br>
 * 为发送消息时区分代理还是非代理，增加此POJO，简易版的AgentModel<br>
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-5-7
 */
public class MeetingAgent {

	public MeetingAgent(Long agentId, Long agentToId) {
		super();
		this.agentId = agentId;
		this.agentToId = agentToId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public Long getAgentToId() {
		return agentToId;
	}

	public void setAgentToId(Long agentToId) {
		this.agentToId = agentToId;
	}

	private Long agentId;
	
	private Long agentToId;
	
	public MeetingAgent() {}
	
}
