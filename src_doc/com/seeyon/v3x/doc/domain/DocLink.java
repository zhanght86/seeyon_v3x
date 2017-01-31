package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 关联文档，已经采用系统的attachment组件来实现
 */
@Deprecated
public class DocLink extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
//	private Long id;
	private java.sql.Timestamp createTime;
	private Long createUserId;
	private long docResourceId1;
	private long docResourceId2;

    public DocLink() {
    }

//	public Long getId() {
//		return this.id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}

	public java.sql.Timestamp getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	public Long getCreateUserId() {
		return this.createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public long getDocResourceId1() {
		return this.docResourceId1;
	}
	public void setDocResourceId1(long docResourceId1) {
		this.docResourceId1 = docResourceId1;
	}

	public long getDocResourceId2() {
		return this.docResourceId2;
	}
	public void setDocResourceId2(long docResourceId2) {
		this.docResourceId2 = docResourceId2;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}