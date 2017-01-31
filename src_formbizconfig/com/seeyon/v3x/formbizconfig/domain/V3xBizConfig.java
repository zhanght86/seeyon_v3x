package com.seeyon.v3x.formbizconfig.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 业务生成器配置
 * @author <a href="mailto:wusb@seeyon.com">wusb</a> 2011-12-2
 */
public class V3xBizConfig extends BaseModel {
	
	private static final long serialVersionUID = 3295937921434203360L;
	/**
	 * 业务配置名称
	 */
	private String name;
	
	/**
	 * 一级菜单ID
	 */
	private Long menuId;
	
	/**
	 * 业务配置创建日期
	 */
	private Timestamp createDate;
	
	/**
	 * 业务配置创建用户
	 */
	private Long createUser;
	
	private List<V3xBizConfigItem> v3xBizConfigItemList;
	
	private List<V3xBizAuthority> v3xBizAuthorityList;
	
	/**
	 * 业务配置修改日期
	 */
	private Timestamp updateDate;
	
	public V3xBizConfig() {
		super();
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Long getCreateUser() {
		return createUser;
	}

	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
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

	public Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	public List<V3xBizConfigItem> getV3xBizConfigItemList() {
		if(v3xBizConfigItemList==null){
			v3xBizConfigItemList = new ArrayList<V3xBizConfigItem>();
		}
		return v3xBizConfigItemList;
	}

	public void setV3xBizConfigItemList(List<V3xBizConfigItem> v3xBizConfigItemList) {
		this.v3xBizConfigItemList = v3xBizConfigItemList;
	}

	public List<V3xBizAuthority> getV3xBizAuthorityList() {
		if(v3xBizAuthorityList==null){
			v3xBizAuthorityList = new ArrayList<V3xBizAuthority>();
		}
		return v3xBizAuthorityList;
	}

	public void setV3xBizAuthorityList(List<V3xBizAuthority> v3xBizAuthorityList) {
		this.v3xBizAuthorityList = v3xBizAuthorityList;
	}
}