package com.seeyon.v3x.doc.webmodel;

import java.sql.Timestamp;

import com.seeyon.v3x.doc.domain.DocResource;

/**
 * 迁出（被锁定）文档的vo
 */
public class DocCheckOutVO extends FolderItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3303394975520719136L;

	public DocCheckOutVO(DocResource docResource) {
		super(docResource);
		this.setDocCheckOutVOProperties(docResource);
	}

	/** 文档锁定人 */
	private String checkOutUserName;

	private Timestamp checkOutTime;

	public Timestamp getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(Timestamp checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public String getCheckOutUserName() {
		return checkOutUserName;
	}

	public void setCheckOutUserName(String checkOutUserName) {
		this.checkOutUserName = checkOutUserName;
	}

	private void setDocCheckOutVOProperties(DocResource dr) {
		this.checkOutTime = dr.getCheckOutTime();
	}

	@Override
	public void setDocResource(DocResource docResource) {
		super.setDocResource(docResource);
		this.setDocCheckOutVOProperties(docResource);
	}

}
