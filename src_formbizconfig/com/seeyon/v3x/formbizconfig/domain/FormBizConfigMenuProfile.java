package com.seeyon.v3x.formbizconfig.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 表单业务配置：菜单挂接项与表单业务配置的中间关系
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfigMenuProfile extends BaseModel{
	private static final long serialVersionUID = 3984826601410249578L;
	/** 菜单ID  */
	private Long menuId;
	/** 表单业务配置ID */
	private Long formBizConfigId;
	
	public FormBizConfigMenuProfile() {
		super();
	}
	
	/** 
	 * 定义构造方法
	 * @param menuId            菜单ID
	 * @param formBizConfigId   表单业务配置ID
	 */
	public FormBizConfigMenuProfile(Long menuId, Long formBizConfigId) {
		this.setIdIfNew();
		this.setMenuId(menuId);
		this.setFormBizConfigId(formBizConfigId);
	}
	
	public Long getFormBizConfigId() {
		return formBizConfigId;
	}

	public void setFormBizConfigId(Long formBizConfigId) {
		this.formBizConfigId = formBizConfigId;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	
}
