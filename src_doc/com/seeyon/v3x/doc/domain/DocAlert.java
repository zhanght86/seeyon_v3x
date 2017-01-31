package com.seeyon.v3x.doc.domain;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 文档订阅
 */
public class DocAlert  extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3266606676270272882L;
	
	// 暂未使用
	private byte alertType;
	// 是否文档夹订阅
	private boolean isFolder;
	// 订阅用户id
	private long alertUserId;
	// 生成该条订阅记录的用户
	private long createUserId;
	// 订阅类型 Constants.ALERT_OPR_TYPE_xxx
	private byte changeType;
	// 该条记录的生成时间
	private java.sql.Timestamp createTime;
	// 文档id
	private long docResourceId;
	// 最近修改时间
	private java.sql.Timestamp lastUpdate;
	// 最近修改人
	private long lastUserId;
	// 状态，暂未使用
	private byte status;
	// alertUserType 取值同 组织模型用户类型
	private String alertUserType;
	// 是否发送在线消息
	private boolean sendMessage;
	// 是否影响到子文档夹
	private boolean setSubFolder;
	// 共享时产生的订阅
	private boolean isFromAcl;

    public boolean getIsFromAcl() {
		return isFromAcl;
	}

	public void setIsFromAcl(boolean isFromAcl) {
		this.isFromAcl = isFromAcl;
	}

	public boolean getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(boolean sendMessage) {
		this.sendMessage = sendMessage;
	}

	public boolean getSetSubFolder() {
		return setSubFolder;
	}

	public void setSetSubFolder(boolean setSubFolder) {
		this.setSubFolder = setSubFolder;
	}
	public DocAlert() {
    }

	public byte getAlertType() {
		return this.alertType;
	}
	public void setAlertType(byte alertType) {
		this.alertType = alertType;
	}

	public long getAlertUserId() {
		return this.alertUserId;
	}
	public void setAlertUserId(long alertUserId) {
		this.alertUserId = alertUserId;
	}

	public byte getChangeType() {
		return this.changeType;
	}
	public void setChangeType(byte changeType) {
		this.changeType = changeType;
	}

	public java.sql.Timestamp getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	public long getDocResourceId() {
		return this.docResourceId;
	}
	public void setDocResourceId(long docResourceId) {
		this.docResourceId = docResourceId;
	}

	public java.sql.Timestamp getLastUpdate() {
		return this.lastUpdate;
	}
	public void setLastUpdate(java.sql.Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public byte getStatus() {
		return this.status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public boolean getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public long getLastUserId() {
		return lastUserId;
	}

	public void setLastUserId(long lastUserId) {
		this.lastUserId = lastUserId;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public String getAlertUserType() {
		return alertUserType;
	}

	public void setAlertUserType(String alertUserType) {
		this.alertUserType = alertUserType;
	}
}