package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 常用文档
 */
public class DocFavorite extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	// 
	private java.sql.Timestamp createTime;
	private long createUserId;
	private DocResource docResource;
	private int orderNum;
	// 谁的常用文档
	private long orgId;
	private String orgType;

    public DocFavorite() {
    }

	public java.sql.Timestamp getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	public long getCreateUserId() {
		return this.createUserId;
	}
	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}


	public int getOrderNum() {
		return this.orderNum;
	}
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}



	public String toString() {
		return new ToStringBuilder(this)
			.toString();
	}

	public DocResource getDocResource() {
		return docResource;
	}

	public void setDocResource(DocResource docResource) {
		this.docResource = docResource;
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