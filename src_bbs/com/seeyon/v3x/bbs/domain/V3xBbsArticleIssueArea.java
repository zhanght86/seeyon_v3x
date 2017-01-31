package com.seeyon.v3x.bbs.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_bbs_article_issue_area database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xBbsArticleIssueArea extends BaseModel implements Serializable {

	private static final long serialVersionUID = -1443686311764576198L;

	private Long moduleId;

	private String moduleType;

	private Long articleId;
	
	private java.sql.Timestamp issueTime;
	
	public V3xBbsArticleIssueArea(){
		
	}
	
	public V3xBbsArticleIssueArea(Long articleId, String moduleType, Long moduleId) {
		super.setIdIfNew();
		this.articleId = articleId;
		this.moduleType = moduleType;
		this.moduleId = moduleId;
		this.issueTime = new java.sql.Timestamp(System.currentTimeMillis());
	}
	
	public V3xBbsArticleIssueArea(String moduleType, Long moduleId) {
		super.setIdIfNew();
		this.moduleType = moduleType;
		this.moduleId = moduleId;
	}

	public java.sql.Timestamp getIssueTime() {
		return issueTime;
	}

	public void setIssueTime(java.sql.Timestamp issueTime) {
		this.issueTime = issueTime;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
}