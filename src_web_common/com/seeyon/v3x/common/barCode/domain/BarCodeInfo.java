package com.seeyon.v3x.common.barCode.domain;

import java.util.Date;

public class BarCodeInfo extends com.seeyon.v3x.common.domain.BaseModel implements java.io.Serializable {
	private static final long serialVersionUID = 202789270616847696L;
	
	private Long id;
	private Integer categoryId;
	private Long objectId;
	private Date createDate;
	private Long fileName;
	private Date updateDate;
	private String fileExt;
	
	public String getFileExt() {
		return fileExt;
	}
	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}
	public Long getFileName() {
		return fileName;
	}
	public void setFileName(Long fileName) {
		this.fileName = fileName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public Long getObjectId() {
		return objectId;
	}
	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
