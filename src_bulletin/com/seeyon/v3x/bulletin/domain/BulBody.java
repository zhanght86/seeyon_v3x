package com.seeyon.v3x.bulletin.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the doc_body database table.
 * 
 * @author BEA Workshop Studio
 */
public class BulBody extends BaseModel implements Serializable {

	private Long bulDataId;
	private String bodyType;
	private String content;
	private Date createDate;
	private String contentName; 

    public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public BulBody() {
    }

	public BulBody(long bulDataId){
		this.bulDataId = bulDataId;
		this.content = "";
	}
	
	public String getBodyType() {
		return this.bodyType;
	}
	
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public String getContent() {
		return this.content;
	}
	public Long getBulDataId() {
		return bulDataId;
	}

	public void setBulDataId(Long bulDataId) {
		this.bulDataId = bulDataId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}


}