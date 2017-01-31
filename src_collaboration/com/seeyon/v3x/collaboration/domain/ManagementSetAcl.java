/**
 * 
 */
package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 类描述：
 * 创建日期：
 *
 * @author Mercurial_lin
 * @version 1.0 
 * @since JDK 5.0
 */
public class ManagementSetAcl extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;
    
	private Long managementSetId;
	private Long aclId;
	private String aclType;
	/**
	 * @return the aclId
	 */
	public Long getAclId() {
		return aclId;
	}
	/**
	 * @param aclId the aclId to set
	 */
	public void setAclId(Long aclId) {
		this.aclId = aclId;
	}
	/**
	 * @return the aclType
	 */
	public String getAclType() {
		return aclType;
	}
	/**
	 * @param aclType the aclType to set
	 */
	public void setAclType(String aclType) {
		this.aclType = aclType;
	}
	/**
	 * @return the managementSetId
	 */
	public Long getManagementSetId() {
		return managementSetId;
	}
	/**
	 * @param managementSetId the managementSetId to set
	 */
	public void setManagementSetId(Long managementSetId) {
		this.managementSetId = managementSetId;
	}
	
	
}
