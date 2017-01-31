package com.seeyon.v3x.agent.domain;

import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.utils.UUIDLong;

public class V3xAgentDetail {
	private static final long serialVersionUID = -226129354977492897L;
	
	private Long id;
	private Long agentId;
	private int app;
	private Long entityId;
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	public int getApp() {
		return app;
	}
	public void setApp(int app) {
		this.app = app;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isNew(){
        if(id == null || id == Constants.GLOBAL_NULL_ID) return true;
        return false;
    }

    public void setIdIfNew(){
        if(isNew()){
            id = UUIDLong.longUUID();
        }
    }
}
