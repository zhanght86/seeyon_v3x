package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * HR管理信息项类别
 */
public class PropertyCategory extends BaseModel implements java.io.Serializable {
	private static final long serialVersionUID = -994563509753155331L;

	private String name;
	private String memo;
	private Long accountId;
	private int remove;
	private boolean sysFlag;

	public int getRemove() {
		return remove;
	}

	public void setRemove(int remove) {
		this.remove = remove;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSysFlag() {
		return sysFlag;
	}

	public void setSysFlag(boolean sysFlag) {
		this.sysFlag = sysFlag;
	}

}