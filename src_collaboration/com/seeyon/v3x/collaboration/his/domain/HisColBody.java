package com.seeyon.v3x.collaboration.his.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.utils.BeanUtils;

/**
 * The persistent class for the col_body database table.
 * 
 * @author BEA Workshop Studio
 */
public class HisColBody extends BaseModel implements Serializable {

	private static final long serialVersionUID = -8578278590357160726L;

	private String bodyType;

	private String content;
	
	private String contentName;

	private Date createDate;

	private Date updateDate;

	private Long summaryId;

	public HisColBody() {
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

	public void setContent(String content) {
		this.content = content;
	}
	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Long getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}
	
	public void clone(com.seeyon.v3x.collaboration.domain.ColBody b) {
		BeanUtils.convert(this, b);
	}
	
	public com.seeyon.v3x.collaboration.domain.ColBody toColBody(){
		com.seeyon.v3x.collaboration.domain.ColBody body = new com.seeyon.v3x.collaboration.domain.ColBody();
		BeanUtils.convert(body, this);
		
		return body;
	}
}