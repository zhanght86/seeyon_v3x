package com.seeyon.v3x.doc.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

public class DocFromPotent extends BaseModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private Long formid;
	
	private Long operationid;
	
	private Long docresid;
	
	private Long affairid;
	
	private String extend;

	public String getExtend() {
		return extend;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public Long getFormid() {
		return formid;
	}

	public void setFormid(Long formid) {
		this.formid = formid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOperationid() {
		return operationid;
	}

	public void setOperationid(Long operationid) {
		this.operationid = operationid;
	}

	public Long getAffairid() {
		return affairid;
	}

	public void setAffairid(Long affairid) {
		this.affairid = affairid;
	}

	public Long getDocresid() {
		return docresid;
	}

	public void setDocresid(Long docresid) {
		this.docresid = docresid;
	}
}
