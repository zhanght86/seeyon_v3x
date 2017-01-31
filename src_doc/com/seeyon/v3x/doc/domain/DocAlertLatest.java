package com.seeyon.v3x.doc.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 最新订阅
 */
public class DocAlertLatest extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2707742765324754979L;
	// 文档id
	private long docResourceId;
	// 文档名称
	private String docResourceName;
	// 文档格式id
	private long mimeTypeId;
	// 操作类型
	private byte changeType;
	// 谁的订阅
	private long alertUserId;

	// 谁做的操作
	private long lastUserId;
	// 操作时间
	private java.sql.Timestamp lastUpdate;
	// 状态，暂未使用
	private byte status;

	public long getAlertUserId() {
		return alertUserId;
	}

	public void setAlertUserId(long alertUserId) {
		this.alertUserId = alertUserId;
	}
	public byte getChangeType() {
		return changeType;
	}

	public void setChangeType(byte changeType) {
		this.changeType = changeType;
	}

	public String getDocResourceName() {
		return docResourceName;
	}

	public void setDocResourceName(String docResourceName) {
		this.docResourceName = docResourceName;
	}

	public long getMimeTypeId() {
		return mimeTypeId;
	}

	public void setMimeTypeId(long mimeTypeId) {
		this.mimeTypeId = mimeTypeId;
	}

	public long getDocResourceId() {
		return docResourceId;
	}

	public void setDocResourceId(long docResourceId) {
		this.docResourceId = docResourceId;
	}

	public java.sql.Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(java.sql.Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public long getLastUserId() {
		return lastUserId;
	}

	public void setLastUserId(long lastUserId) {
		this.lastUserId = lastUserId;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

}
