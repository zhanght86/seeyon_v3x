package com.seeyon.v3x.mobile.menu.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

public class MobileMenuSetting extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7340651915570541658L;

	private String menuId;// 菜单id
	
	private Integer sort;//排序号
	
	private Date createDate;//创建日期
	
	private Long userId;//设置人

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
}
