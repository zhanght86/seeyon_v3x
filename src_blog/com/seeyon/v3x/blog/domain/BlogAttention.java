package com.seeyon.v3x.blog.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the v3x_blog_family_auth database table.
 * 
 * @author BEA Workshop Studio
 */
public class BlogAttention  extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private BlogEmployee BlogEmployee;
	private Long employeeId;
	private Long attentionId;
    private String typeProperty = V3xOrgMember.ORGENT_TYPE_MEMBER;	//parseElements（）这个方法中用到
	public Long getEmployeeId() {
		return this.employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getAttentionId() {
		return this.attentionId;
	}
	public void setAttentionId(Long attentionId) {
		this.attentionId = attentionId;
	}

	//bi-directional many-to-one association to BlogFamily
	public BlogEmployee getBlogEmployee() {
		return this.BlogEmployee;
	}
	public void setBlogEmployee(BlogEmployee BlogEmployee) {
		this.BlogEmployee = BlogEmployee;
	}
	public String getTypeProperty() {
		return typeProperty;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}