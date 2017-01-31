package com.seeyon.v3x.doc.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 文档库管理员表
 */
public class DocLibOwner extends BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2167262058675037606L;
	
	public DocLibOwner() {}
	
	public DocLibOwner(long docLibId, long ownerId, int sortId) {
		super();
		this.setNewId();
		this.docLibId = docLibId;
		this.ownerId = ownerId;
		this.sortId = sortId;
	}
	
	private long docLibId;
	private long ownerId;
	private int sortId;

	public int getSortId() {
		return sortId;
	}
	public void setSortId(int sortId) {
		this.sortId = sortId;
	}
	public long getDocLibId() {
		return this.docLibId;
	}
	public void setDocLibId(long docLibId) {
		this.docLibId = docLibId;
	}

	public long getOwnerId() {
		return this.ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
}