package com.seeyon.v3x.doc.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.util.Datetimes;

/**
 * 文档正文
 */
public class DocBody extends BaseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3514008029298391724L;


	/** 文档id */
	private Long docResourceId;
	/** 正文类型 */
	private String bodyType;
	/** 正文  */
	private String content;
	/** 创建时间 */
	private Date createDate;

    public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public DocBody() {
    }

	public Long getDocResourceId() {
		return this.docResourceId;
	}
	
	public void setDocResourceId(Long docResourceId) {
		this.docResourceId = docResourceId;
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

	@Override
	public String toString() {
		return "DocBody[" + this.docResourceId + ", " + this.bodyType + "," + Datetimes.formatDatetime(this.createDate) + "]";	
	}
	

}