package com.seeyon.v3x.blog.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_blog_family database table.
 * 
 * @author BEA Workshop Studio
 */
public class BlogFamily  extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nameFamily;
	private String remark;
	private String type;
	private Integer seqDisplay;
	private Integer articleNumber;
	private Long employeeId;
	private java.sql.Timestamp createDate ;
	
	private BlogEmployee BlogEmployee;
	private java.util.Set<BlogArticle> BlogArticle;
	private java.util.Set<BlogFamily> BlogFamily;

    public BlogFamily() {
    }
	public Long getEmployeeId() {
		return this.employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getNameFamily() {
		return this.nameFamily;
	}
	public void setNameFamily(String nameFamily) {
		this.nameFamily = nameFamily;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}


	public Integer getSeqDisplay() {
		return this.seqDisplay;
	}
	public void setSeqDisplay(Integer seqDisplay) {
		this.seqDisplay = seqDisplay;
	}
	public Integer getArticleNumber() {
		return this.articleNumber;
	}
	public void setArticleNumber(Integer articleNumber) {
		this.articleNumber = articleNumber;
	}
	//bi-directional many-to-one association to blogFamily
	public BlogEmployee getBlogEmployee() {
		return this.BlogEmployee;
	}
	public void setBlogEmployee(BlogEmployee BlogEmployee) {
		this.BlogEmployee = BlogEmployee;
	}

	public java.util.Set<BlogArticle> getBlogArticle() {
		return this.BlogArticle;
	}
	public void setBlogArticle(java.util.Set<BlogArticle> BlogArticle) {
		this.BlogArticle = BlogArticle;
	}

	public java.util.Set<BlogFamily> getBlogFamily() {
		return this.BlogFamily;
	}
	public void setBlogFamily(java.util.Set<BlogFamily> BlogFamily) {
		this.BlogFamily = BlogFamily;
	}
	

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
	
	// 总数
	private int total;

	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public java.sql.Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}
	
	public void  setCreateDate(Date date){
		if(date != null) {
			this.setCreateDate(new Timestamp(date.getTime())) ;
		}
	}

	
}