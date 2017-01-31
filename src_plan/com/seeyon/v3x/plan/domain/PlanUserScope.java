package com.seeyon.v3x.plan.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:计划用户查看范围
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Feb 10, 2007 8:46:35 PM
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class PlanUserScope extends BaseModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 565415647854055472L;

	// Fields

	/**
	 * 用户id
	 */
	private Long refUserId;
	
	/**
	 * 单位id
	 */
	private Long refAccountId;

	/**
	 * 用户名
	 */
	private String refUserName;

	/**
	 * 用户查看范围id集合（每个可被查看的用户id用“,”分割）
	 */
	private String scopeUserIds;

	/**
	 * 用户查看范围用户名集合（每个可被查看的用户名用“,”分割）
	 */
	private String scopeUserNames;
	
	/**
	 * 是否可以查看用户的详细计划
	 */
	private Boolean isSeeDetail;

	public Boolean getIsSeeDetail() {
		return isSeeDetail;
	}

	public void setIsSeeDetail(Boolean isSeeDetail) {
		this.isSeeDetail = isSeeDetail;
	}

	public Long getRefUserId() {
		return refUserId;
	}

	public void setRefUserId(Long refUserId) {
		this.refUserId = refUserId;
	}

	public String getRefUserName() {
		return refUserName;
	}

	public void setRefUserName(String refUserName) {
		this.refUserName = refUserName;
	}

	public String getScopeUserIds() {
		return scopeUserIds;
	}

	public void setScopeUserIds(String scopeUserIds) {
		this.scopeUserIds = scopeUserIds;
	}

	public String getScopeUserNames() {
		return scopeUserNames;
	}

	public void setScopeUserNames(String scopeUserNames) {
		this.scopeUserNames = scopeUserNames;
	}

	public Long getRefAccountId() {
		return refAccountId;
	}

	public void setRefAccountId(Long refAccountId) {
		this.refAccountId = refAccountId;
	}

}