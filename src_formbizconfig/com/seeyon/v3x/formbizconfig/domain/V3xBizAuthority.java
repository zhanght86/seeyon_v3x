package com.seeyon.v3x.formbizconfig.domain;

import www.seeyon.com.v3x.form.base.SelectPersonOperation;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 业务生成器配置：共享范围
 * 
 * @author <a href="mailto:wusb@seeyon.com">wusb</a> 2011-12-2
 */
public class V3xBizAuthority extends BaseModel {

	private static final long serialVersionUID = 2981527906592722188L;
	SelectPersonOperation spc = new SelectPersonOperation();
	/**
	 * 业务配置ID
	 */
	private Long bizConfigId;

	/** 共享范围主体ID */
	private Long scopeId;

	private int scopeType;
	private String scopeTypeStr;

	public V3xBizAuthority() {
		super();
	}

	public Long getBizConfigId() {
		return bizConfigId;
	}

	public void setBizConfigId(Long bizConfigId) {
		this.bizConfigId = bizConfigId;
	}

	public Long getScopeId() {
		return scopeId;
	}

	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

	public int getScopeType() {
		return scopeType;
	}

	public void setScopeType(int scopeType) {
		this.scopeType = scopeType;
	}

	public String getScopeTypeStr() {
		scopeTypeStr = spc.getTypeByTypeId(scopeType);
		return scopeTypeStr;
	}

	public void setScopeTypeStr(String scopeTypeStr) {
		this.scopeTypeStr = scopeTypeStr;
	}
	
}