package com.seeyon.v3x.doc.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.doc.util.Constants;

/**
 * 文档权限pojo
 */
public class DocAcl extends BaseModel implements Serializable{
//	default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	// 权限类型 取值 Constansts.XXXPotent
	private int potenttype;
	// 根据userType对应的id
	private long userId;
	// V3xOrgEntiry Type
	private String userType;
	// 共享类型 取值 Constants.SHARETYPE_xxx
	private Byte sharetype;
	// 个人借阅，个人共享时    可空
	private long ownerId;
	// 借阅开始时间 按天计算
	private java.sql.Timestamp sdate;
	// 借阅结束时间
	private java.sql.Timestamp edate;
	// 文档id
	private long docResourceId;
	
	// 是否订阅，默认订阅类型：全部
	private boolean isAlert;
	// 文档订阅的id
	private Long docAlertId;
	// 公文借阅权限类型 取值 Constansts.lenPotent_xxx  zhangh
	private Byte lenPotent=Constants.LENPOTENT_ALL;
	private String lenPotent2;
	
	private int aclOrder;
	
	public int getAclOrder() {
		return aclOrder;
	}
	public void setAclOrder(int aclOrder) {
		this.aclOrder = aclOrder;
	}

	public Long getDocAlertId() {
		return docAlertId;
	}
	public void setDocAlertId(Long docAlertId) {
		this.docAlertId = docAlertId;
	}
	public boolean getIsAlert() {
		return isAlert;
	}
	public void setIsAlert(boolean isAlert) {
		this.isAlert = isAlert;
	}
	public long getDocResourceId() {
		return docResourceId;
	}
	public void setDocResourceId(long docResourceId) {
		this.docResourceId = docResourceId;
	}
	public java.sql.Timestamp getEdate() {
		return edate;
	}
	public void setEdate(java.sql.Timestamp edate) {
		this.edate = edate;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	public int getPotenttype() {
		return potenttype;
	}
	public void setPotenttype(int potenttype) {
		this.potenttype = potenttype;
	}
	public java.sql.Timestamp getSdate() {
		return sdate;
	}
	public void setSdate(java.sql.Timestamp sdate) {
		this.sdate = sdate;
	}
	public Byte getSharetype() {
		return sharetype;
	}
	public void setSharetype(Byte sharetype) {
		this.sharetype = sharetype;
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
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
	public Byte getLenPotent() {
		return lenPotent;
	}
	public void setLenPotent(Byte lenPotent) {
		this.lenPotent = lenPotent;
	}
	public String getLenPotent2() {
		if(lenPotent2==null || "".equals(lenPotent2)){lenPotent2="00";}
		return lenPotent2;
	}
	public void setLenPotent2(String lenPotent2) {
		this.lenPotent2 = lenPotent2;
	}	

}
