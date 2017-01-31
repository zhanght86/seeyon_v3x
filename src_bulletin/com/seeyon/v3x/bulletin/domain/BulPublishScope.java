package com.seeyon.v3x.bulletin.domain;

import com.seeyon.v3x.common.domain.BaseModel;


/**
 * 记录公告的发布范围
 * 
 */
public class BulPublishScope extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 504265606117033427L;
	private long bulDataId;
	private long userId;
	private String userType;
	
	public BulPublishScope() {}
	
	public BulPublishScope(long bulDataId, long userId, String userType) {
		super();
		this.setIdIfNew();
		this.bulDataId = bulDataId;
		this.userId = userId;
		this.userType = userType;
	}
	
	public long getBulDataId() {
		return bulDataId;
	}
	public void setBulDataId(long bulDataId) {
		this.bulDataId = bulDataId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
}