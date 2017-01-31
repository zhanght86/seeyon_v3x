package com.seeyon.v3x.blog.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_blog_family_auth database table.
 * 
 * @author BEA Workshop Studio
 */
public class BlogFavorites  extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private Long familyId;
	private Long articleId;
	private BlogFamily BlogFamily;
	private BlogEmployee BlogEmployee;
	private BlogArticle BlogArticle;
	private Long employeeId;
	public Long getEmployeeId() {
		return this.employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}
	public Long getFamilyId() {
		return familyId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}
	public Long getArticleId() {
		return articleId;
	}
	//bi-directional many-to-one association to BlogFamily
	public BlogFamily getBlogFamily() {
		return this.BlogFamily;
	}
	public void setBlogFamily(BlogFamily BlogFamily) {
		this.BlogFamily = BlogFamily;
	}
	//bi-directional many-to-one association to BlogEmployee
	public BlogEmployee getBlogEmployee() {
		return this.BlogEmployee;
	}
	public void setBlogEmployee(BlogEmployee BlogEmployee) {
		this.BlogEmployee = BlogEmployee;
	}
	//bi-directional many-to-one association to BlogArticle
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