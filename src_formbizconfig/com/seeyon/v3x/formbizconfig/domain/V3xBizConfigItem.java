package com.seeyon.v3x.formbizconfig.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 业务生成器配置明细
 * 
 * @author <a href="mailto:wusb@seeyon.com">wusb</a> 2011-12-2
 */
public class V3xBizConfigItem extends BaseModel {

	private static final long serialVersionUID = 2684292491363961196L;

	/**
	 * 业务配置名称
	 */
	private String name;

	/**
	 * 二级菜单ID
	 */
	private Long menuId;

	/**
	 * 业务配置ID
	 */
	private Long bizConfigId;

	/**
	 * 来源ID
	 */
	private Long sourceId;
	private int sourceType;

	/**
	 * 排序ID
	 */
	private int sortId;

	/**
	 * 表单ID
	 */
	private Long formAppmainId;

	/**
	 * 流程模块菜单类型
	 */
	private Integer flowMenuType;

	public V3xBizConfigItem() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public Long getBizConfigId() {
		return bizConfigId;
	}

	public void setBizConfigId(Long bizConfigId) {
		this.bizConfigId = bizConfigId;
	}
	
	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public Long getFormAppmainId() {
		return formAppmainId;
	}

	public void setFormAppmainId(Long formAppmainId) {
		this.formAppmainId = formAppmainId;
	}

	public Integer getFlowMenuType() {
		return flowMenuType;
	}

	public void setFlowMenuType(Integer flowMenuType) {
		this.flowMenuType = flowMenuType;
	}
}