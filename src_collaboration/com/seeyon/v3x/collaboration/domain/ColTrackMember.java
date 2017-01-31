package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

public class ColTrackMember extends BaseModel implements Serializable{

	private Long id ;
	//公文或者协同的ID
	private Long objectId;
	//设置跟踪的AffairId
	private Long affairId;
	//设置跟踪的memberId
	private Long memberId;
	//被跟踪的memberId;
	private Long trackMemberId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public Long getAffairId() {
		return affairId;
	}
	public void setAffairId(Long affairId) {
		this.affairId = affairId;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Long getTrackMemberId() {
		return trackMemberId;
	}
	public void setTrackMemberId(Long trackMemberId) {
		this.trackMemberId = trackMemberId;
	}
}
