package com.seeyon.v3x.blog.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_blog_article_reply database table.
 * 
 * @author BEA Workshop Studio
 */
public class BlogReply  extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String subject;
	private String content;
	private java.sql.Timestamp issueTime;
	private Long parentId;
	private BlogEmployee BlogEmployee;
	private BlogArticle BlogArticle;
	private Long employeeId;
	private Long articleId;

    public BlogReply() {
    	
    }
	public Long getEmployeeId() {
		return this.employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getArticleId() {
		return this.articleId;
	}
	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	public String getSubject() {
		return this.subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public java.sql.Timestamp getIssueTime() {
		return this.issueTime;
	}
	public void setIssueTime(java.sql.Timestamp issueTime) {
		this.issueTime = issueTime;
	}
	public Long getParentId() {
		return this.parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	//bi-directional many-to-one association to BlogEmployee
	public BlogEmployee getBlogEmployee() {
		return this.BlogEmployee;
	}
	public void setBlogEmployee(BlogEmployee BlogEmployee) {
		this.BlogEmployee = BlogEmployee;
	}

	//bi-directional many-to-one association to V3xBlogArticle
	public BlogArticle getBlogArticle() {
		return this.BlogArticle;
	}
	public void setBlogArticle(BlogArticle BlogArticle) {
		this.BlogArticle = BlogArticle;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}