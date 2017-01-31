package com.seeyon.v3x.blog.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_blog_family database table.
 * 
 * @author BEA Workshop Studio
 */
public class BlogEmployee  extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String introduce;
	private Integer articleNumber;
	private String image;
	private Long idCompany;
	private Byte flagStart;
	private Byte flagShare;
	
	private java.util.Set<BlogAttention> BlogAttention;
	private java.util.Set<BlogShare> BlogShare;
	private java.util.Set<BlogFamily> BlogFamily;
	private java.util.Set<BlogReply> BlogReply;
	private java.util.Set<BlogArticle> BlogArticle;
	private java.util.Set<BlogFavorites> BlogFavorites;

    public BlogEmployee() {
    }

	public String getIntroduce() {
		return this.introduce;
	}
	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}
	
	public Integer getArticleNumber() {
		return this.articleNumber;
	}
	public void setArticleNumber(Integer articleNumber) {
		this.articleNumber = articleNumber;
	}
	public String getImage() {
		return this.image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Long getIdCompany() {
		return this.idCompany;
	}
	public void setIdCompany(Long idCompany) {
		this.idCompany = idCompany;
	}

	public Byte getFlagStart() {
		return flagStart;
	}
	public void setFlagStart(Byte flagStart) {
		this.flagStart = flagStart;
	}
	public Byte getFlagShare() {
		return flagShare;
	}
	public void setFlagShare(Byte flagShare) {
		this.flagShare = flagShare;
	}

	public java.util.Set<BlogAttention> getBlogAttention() {
		return this.BlogAttention;
	}
	public void setBlogAttention(java.util.Set<BlogAttention> BlogAttention) {
		this.BlogAttention = BlogAttention;
	}
	public java.util.Set<BlogShare> getBlogShare() {
		return this.BlogShare;
	}
	public void setBlogShare(java.util.Set<BlogShare> BlogShare) {
		this.BlogShare = BlogShare;
	}
	public java.util.Set<BlogFamily> getBlogFamily() {
		return this.BlogFamily;
	}
	public void setBlogFamily(java.util.Set<BlogFamily> BlogFamily) {
		this.BlogFamily = BlogFamily;
	}
	public java.util.Set<BlogReply> getBlogReply() {
		return this.BlogReply;
	}
	public void setBlogReply(java.util.Set<BlogReply> BlogReply) {
		this.BlogReply = BlogReply;
	}
	public java.util.Set<BlogArticle> getBlogArticle() {
		return this.BlogArticle;
	}
	public void setBlogArticle(java.util.Set<BlogArticle> BlogArticle) {
		this.BlogArticle = BlogArticle;
	}

	public java.util.Set<BlogFavorites> getBlogFavorites() {
		return this.BlogFavorites;
	}
	public void setBlogFavorites(java.util.Set<BlogFavorites> BlogFavorites) {
		this.BlogFavorites = BlogFavorites;
	}
	

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
	

}