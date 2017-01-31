/**
 * 
 */
package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 类描述：用于保存督办模板的角色
 * 创建日期：
 *
 * @author mercury
 * @version 1.0 
 * @since JDK 5.0
 */
public class SuperviseTemplateRole  extends BaseModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long superviseTemplateId ;
	private String role;
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}
	/**
	 * @return the superviseTemplateId
	 */
	public Long getSuperviseTemplateId() {
		return superviseTemplateId;
	}
	/**
	 * @param superviseTemplateId the superviseTemplateId to set
	 */
	public void setSuperviseTemplateId(Long superviseTemplateId) {
		this.superviseTemplateId = superviseTemplateId;
	}
}
