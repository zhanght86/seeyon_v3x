package com.seeyon.v3x.blog.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_blog_article database table.
 * 
 * @author BEA Workshop Studio
 */
public class BlogArticle  extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String subject;
	private String content;
	private java.sql.Timestamp issueTime;
	private java.sql.Timestamp modifyTime;
	private Integer clickNumber;
	private Integer replyNumber;
	private Byte state;
	private Long idCompany;
	private Long employeeId;
	private Long familyId;
	private Integer y;
	private Integer m;
	private Byte attachmentFlag;
//	private Long favoritesId;
	private Long articleSize;

	private BlogEmployee BlogEmployee;
	private BlogFamily BlogFamily;
	private java.util.Set<BlogFavorites> BlogFavorites;
	private java.util.Set<BlogReply> BlogReply;

    public BlogArticle() {
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
	public java.sql.Timestamp getModifyTime() {
		return this.modifyTime;
	}
	public void setModifyTime(java.sql.Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Integer getClickNumber() {
		return this.clickNumber;
	}
	public void setClickNumber(Integer clickNumber) {
		this.clickNumber = clickNumber;
	}


	public Integer getReplyNumber() {
		return this.replyNumber;
	}
	public void setReplyNumber(Integer replyNumber) {
		this.replyNumber = replyNumber;
	}
	public Long getIdCompany() {
		return this.idCompany;
	}
	public void setIdCompany(Long idCompany) {
		this.idCompany = idCompany;
	}

	public Long getEmployeeId() {
		return this.employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getFamilyId() {
		return this.familyId;
	}
	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}
//	public Long getFavoritesId() {
//		return this.favoritesId;
//	}
//	public void setFavoritesId(Long favoritesId) {
//		this.favoritesId = favoritesId;
//	}
	public Long getArticleSize() {
		return this.articleSize;
	}
	public void setArticleSize(Long articleSize) {
		this.articleSize = articleSize;
	}

	public Integer getY() {
		return this.y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public Integer getM() {
		return this.m;
	}
	public void setM(Integer m) {
		this.m = m;
	}
	//bi-directional many-to-one association to BlogEmployee
	public BlogEmployee getBlogEmployee() {
		return this.BlogEmployee;
	}
	public void setBlogEmployee(BlogEmployee BlogEmployee) {
		this.BlogEmployee = BlogEmployee;
	}

	//bi-directional many-to-one association to blogFamily
	public BlogFamily getBlogFamily() {
		return this.BlogFamily;
	}
	public void setBlogFamily(BlogFamily BlogFamily) {
		this.BlogFamily = BlogFamily;
	}
	//bi-directional many-to-one association to BlogArticleIssueArea
	public java.util.Set<BlogFavorites> getBlogFavorites() {
		return this.BlogFavorites;
	}
	public void setBlogFavorites(java.util.Set<BlogFavorites> BlogFavorites) {
		this.BlogFavorites = BlogFavorites;
	}

	//bi-directional many-to-one association to BlogArticleReply
	public java.util.Set<BlogReply> getBlogReply() {
		return this.BlogReply;
	}
	public void setBlogReply(java.util.Set<BlogReply> BlogReply) {
		this.BlogReply = BlogReply;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public Byte getState() {
		return state;
	}

	public void setState(Byte state) {
		this.state = state;
	}
	/**
	 * @return the attachmentFlag
	 */
	public Byte getAttachmentFlag() {
		return attachmentFlag;
	}

	/**
	 * @param attachmentFlag the attachmentFlag to set
	 */
	public void setAttachmentFlag(Byte attachmentFlag) {
		this.attachmentFlag = attachmentFlag;
	}

}