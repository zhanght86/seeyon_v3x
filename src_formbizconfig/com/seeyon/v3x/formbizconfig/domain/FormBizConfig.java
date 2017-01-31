package com.seeyon.v3x.formbizconfig.domain;

import java.sql.Timestamp;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;

/**
 * 表单业务配置
 * @author <a href="mailto:yangm@seeyon.com">Yangm</a> 2009-08-12
 */
public class FormBizConfig extends BaseModel {

	private static final long serialVersionUID = 1709137071732671011L;
	/**
	 * 表单业务配置名称
	 */
	private String name;
	/**
	 * 表单业务配置创建日期
	 */
	private Timestamp createDate;
	/**
	 * 表单业务配置创建用户
	 */
	private Long createUser;
	/**
	 * 表单业务配置修改日期
	 */
	private Timestamp updateDate;
	/**
	 * 表单业务配置对应栏目是否已经发布到首页
	 * @deprecated 由于业务配置栏目可以共享，因而对创建者及每个共享者而言，是否发布到首页个人空间状态都不同，此字段实际无法起到预期作用
	 * 改为获取首页个人空间栏目信息并解析当前业务配置是否在其中，以判定其是否已经发布到首页
	 * @see com.seeyon.v3x.formbizconfig.controller.FormBizConfigController#getPublishInfo
	 */
	private boolean publishFlag;
	/**
	 * 表单业务配置挂接类型，分为：栏目挂接、菜单挂接、栏目挂接和菜单挂接、未挂接
	 */
	private int bizConfigType;
	
	public FormBizConfig() {
		super();
	}
	
	/**
	 * 表单业务配置构造方法，主键id使用setNewId()为其设定
	 * @param name			表单业务配置名称
	 * @param createDate	表单业务配置创建日期
	 * @param createUser	表单业务配置创建用户
	 * @param updateDate	表单业务配置修改日期
	 * @param bizConfigType	表单业务配置挂接类型，分为：栏目挂接、菜单挂接、栏目挂接和菜单挂接、未挂接
	 */
	public FormBizConfig(String name, Timestamp createDate, Long createUser, Timestamp updateDate, int bizConfigType) {
		super();
		this.setNewId();
		this.name = name;
		this.createDate = createDate;
		this.createUser = createUser;
		this.updateDate = updateDate;
		this.bizConfigType = bizConfigType;
	}
	
	/**
	 * 判断当前表单业务配置是否具备栏目挂接
	 */
	public boolean hasColumnConfig() {
		return this.bizConfigType == FormBizConfigConstants.CONFIG_TYPE_COLUMN || 
			   this.bizConfigType == FormBizConfigConstants.CONFIG_TYPE_COLUMN_MENU;
	}
	
	/**
	 * 判断当前表单业务配置是否具备菜单挂接
	 */
	public boolean hasMenuConfig() {
		return this.bizConfigType == FormBizConfigConstants.CONFIG_TYPE_MENU || 
		   	   this.bizConfigType == FormBizConfigConstants.CONFIG_TYPE_COLUMN_MENU;
	}
	
	public int getBizConfigType() {
		return bizConfigType;
	}
	
	public void setBizConfigType(int bizConfigType) {
		this.bizConfigType = bizConfigType;
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

	public boolean isPublishFlag() {
		return publishFlag;
	}

	public void setPublishFlag(boolean publishFlag) {
		this.publishFlag = publishFlag;
	}

	public Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
}
