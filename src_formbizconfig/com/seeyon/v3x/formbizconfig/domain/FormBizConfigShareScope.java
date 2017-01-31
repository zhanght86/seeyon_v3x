package com.seeyon.v3x.formbizconfig.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 表单业务配置：共享范围
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigShareScope extends BaseModel {

	private static final long serialVersionUID = 6326482285075163218L;
	
	/** 共享范围主体ID */
	private Long scopeId;
	/** 共享范围主体类型，如Member,Department,Post等 */
	private String scopeType;
	/** 排序号 */
	private int sortId;
	/** 所共享的表单业务配置对象ID */
	private Long formBizConfigId;
	
	/**
	 * 定义构造方法
	 * @param scopeId 			共享范围主体ID
	 * @param scopeType			共享范围主体类型，如Member,Department,Post等
	 * @param sortId			排序号
	 * @param formBizConfigId	所共享的表单业务配置对象ID
	 */
	public FormBizConfigShareScope(Long scopeId, String scopeType, int sortId, Long formBizConfigId) {
		this.setIdIfNew();
		this.setScopeId(scopeId);
		this.setScopeType(scopeType);
		this.setSortId(sortId);
		this.setFormBizConfigId(formBizConfigId);
	}
	
	public FormBizConfigShareScope() {
		
	}
	
	public Long getScopeId() {
		return scopeId;
	}
	
	public void setScopeId(Long scopeId) {
		this.scopeId = scopeId;
	}

	public Long getFormBizConfigId() {
		return formBizConfigId;
	}

	public void setFormBizConfigId(Long formBizConfigId) {
		this.formBizConfigId = formBizConfigId;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public String getScopeType() {
		return scopeType;
	}

	public void setScopeType(String scopeType) {
		this.scopeType = scopeType;
	}

}
