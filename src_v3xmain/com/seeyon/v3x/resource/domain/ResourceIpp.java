package com.seeyon.v3x.resource.domain;

import java.io.Serializable;
import java.util.Date;


import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the notepage database table.
 * 
 * @author BEA Workshop Studio
 */
public class ResourceIpp extends BaseModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 资源
	 */
	private Resource resource;
	
	/**
	 * 开始时间
	 */
	private Date startTime;

	/**
	 * 结束时间
	 */
	private Date endTime;
	
	/**
	 * 占用者id
	 */
	private Long refAppId;

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Long getRefAppId() {
		return refAppId;
	}

	public void setRefAppId(Long refAppId) {
		this.refAppId = refAppId;
	}

}