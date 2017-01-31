package com.seeyon.v3x.doc.domain;

import java.sql.Timestamp;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 学习文档
 */
public class DocLearning extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1534833888815611498L;
	private DocResource docResource;
	// 谁的学习文档
	private long orgId;
	private String orgType;
	private int orderNum;
	private long createUserId;
	private Timestamp createTime;
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}
	public DocResource getDocResource() {
		return docResource;
	}
	public void setDocResource(DocResource docResource) {
		this.docResource = docResource;
	}
	public int getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
}
