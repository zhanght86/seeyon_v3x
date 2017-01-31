package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;


import com.seeyon.v3x.common.domain.BaseModel;

public class ColSupervisor extends BaseModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long superviseId;
	private Integer permission;
	private Long supervisorId;
	
	public Integer getPermission() {
		return permission;
	}
	public void setPermission(Integer permission) {
		this.permission = permission;
	}
	public Long getSuperviseId() {
		return superviseId;
	}
	public void setSuperviseId(Long superviseId) {
		this.superviseId = superviseId;
	}
	public Long getSupervisorId() {
		return supervisorId;
	}
	public void setSupervisorId(Long supervisorId) {
		this.supervisorId = supervisorId;
	}
}
