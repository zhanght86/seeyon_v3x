package com.seeyon.v3x.mobile.menu;

import java.io.Serializable;

import com.seeyon.v3x.menu.manager.MenuCheck;



/**
 * 用于移动应用的菜单
 * @author dongyj
 *
 */
public  class BaseMobileMenu implements Comparable<BaseMobileMenu>, Serializable{
	private static final long serialVersionUID = 8261952287056876540L;
	//是否是必选，默认不是必选
	private Boolean forceChecked = false;
	//是否是默认的
	private Boolean isDefaultChecked = false;
	private String action ;
	//是否可用  默认可用
	private MenuCheck menuCheck;
	/**
	 * 排序号
	 */
	private Integer sortId;
	private String id;
	private String name;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getForceChecked() {
		return forceChecked;
	}

	public void setForceChecked(Boolean forceChecked) {
		this.forceChecked = forceChecked;
	}

	public void setSortId(Integer sortId) {
		this.sortId = sortId;
	}
	
	public int compareTo(BaseMobileMenu o) {
		if(this.sortId == null) return 1;
		if(o == null || o.getSortId() == null) return -1;
		return this.getSortId().compareTo(o.getSortId());
	}

	public Integer getSortId() {
		return sortId;
	}

	public Boolean getIsDefaultChecked() {
		return isDefaultChecked;
	}

	public void setIsDefaultChecked(Boolean isDefaultChecked) {
		this.isDefaultChecked = isDefaultChecked;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public MenuCheck getMenuCheck() {
		return menuCheck;
	}

	public void setMenuCheck(MenuCheck menuCheck) {
		this.menuCheck = menuCheck;
	}
}
