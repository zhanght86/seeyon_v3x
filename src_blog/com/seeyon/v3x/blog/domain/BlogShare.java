package com.seeyon.v3x.blog.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_blog_family_auth database table.
 * 
 * @author BEA Workshop Studio
 */
public class BlogShare  extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String type;
	private BlogEmployee BlogEmployee;
	private Long employeeId;
	private Long shareId;
	public Long getEmployeeId() {
		return this.employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getShareId() {
		return this.shareId;
	}
	public void setShareId(Long shareId) {
		this.shareId = shareId;
	}

	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}

	//bi-directional many-to-one association to BlogFamily
	public BlogEmployee getBlogEmployee() {
		return this.BlogEmployee;
	}
	public void setBlogEmployee(BlogEmployee BlogEmployee) {
		this.BlogEmployee = BlogEmployee;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}