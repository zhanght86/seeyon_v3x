package com.seeyon.v3x.news.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the doc_body database table.
 * 
 * @author BEA Workshop Studio
 */
public class NewsBody extends BaseModel implements Serializable {

	private Long newsDataId;
	private String bodyType;
	private String content;
	private Date createDate;

    public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public NewsBody() {
    }

	public NewsBody(long newsDataId){
		this.newsDataId = newsDataId;
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


	public Long getNewsDataId() {
		return newsDataId;
	}

	public void setNewsDataId(Long newsDataId) {
		this.newsDataId = newsDataId;
	}

	public void setContent(String content) {
		this.content = content;
	}


}