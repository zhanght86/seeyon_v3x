/**
 * 
 */
package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

/**
 * 类描述：
 * 创建日期：
 *
 * @author Mercurial_lin
 * @version 1.0 
 * @since JDK 5.0
 */
public class ManagementSet extends BaseModel implements Serializable {
	
	private static final long serialVersionUID = -7624521242306112221L;

	private String memberId;

	private String manageRange;
	
	private int showContent;

	private int extConfigure;

	private long createUserId;

	private java.sql.Timestamp createTime;

	private long lastUserId;

	private java.sql.Timestamp lastUpdate;

	private long domainId;
	
	private Set<ManagementSetAcl> managementSetAcls;
	
	private String typeNames; //用于前段现实

	/**
	 * @return the typeNames
	 */
	public String getTypeNames() {
		return typeNames;
	}

	/**
	 * @param typeNames the typeNames to set
	 */
	public void setTypeNames(String typeNames) {
		this.typeNames = typeNames;
	}

	/**
	 * @return the createTime
	 */
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the createUserId
	 */
	public long getCreateUserId() {
		return createUserId;
	}

	/**
	 * @param createUserId the createUserId to set
	 */
	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	/**
	 * @return the domainId
	 */
	public long getDomainId() {
		return domainId;
	}

	/**
	 * @param domainId the domainId to set
	 */
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	/**
	 * @return the extConfigure
	 */
	public int getExtConfigure() {
		return extConfigure;
	}

	/**
	 * @param extConfigure the extConfigure to set
	 */
	public void setExtConfigure(int extConfigure) {
		this.extConfigure = extConfigure;
	}

	/**
	 * @return the lastUpdate
	 */
	public java.sql.Timestamp getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(java.sql.Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the lastUserId
	 */
	public long getLastUserId() {
		return lastUserId;
	}

	/**
	 * @param lastUserId the lastUserId to set
	 */
	public void setLastUserId(long lastUserId) {
		this.lastUserId = lastUserId;
	}

	/**
	 * @return the manageRange
	 */
	public String getManageRange() {
		return manageRange;
	}

	/**
	 * @param manageRange the manageRange to set
	 */
	public void setManageRange(String manageRange) {
		this.manageRange = manageRange;
	}

	/**
	 * @return the memberId
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * @param memberId the memberId to set
	 */
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}


	/**
	 * @return the managementSetAcls
	 */
	public Set<ManagementSetAcl> getManagementSetAcls() {
		return managementSetAcls;
	}

	/**
	 * @param managementSetAcls the managementSetAcls to set
	 */
	public void setManagementSetAcls(Set<ManagementSetAcl> managementSetAcls) {
		this.managementSetAcls = managementSetAcls;
	}

	/**
	 * @return the showContent
	 */
	public int getShowContent() {
		return showContent;
	}

	/**
	 * @param showContent the showContent to set
	 */
	public void setShowContent(int showContent) {
		this.showContent = showContent;
	}
	
}
