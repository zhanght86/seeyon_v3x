package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * HR管理页签
 */
public class Page extends BaseModel implements java.io.Serializable {

	private static final long serialVersionUID = 8429129539530477847L;
	private String pageName;
	private int pageNo;
	private int pageDisplay;
	private int repair;
	private String modelName;
	private String memo;
	private int remove;
	private Long accountId;
	private int sort;
	private boolean sysFlag;
	
	public Page() {
	}
	
	public Page(int pageDisplay, boolean sysFlag, boolean changeFlag) {
		this.pageDisplay = pageDisplay;
		this.sysFlag = sysFlag;
	}

	public int getRemove() {
		return remove;
	}

	public void setRemove(int remove) {
		this.remove = remove;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public int getPageDisplay() {
		return pageDisplay;
	}

	public void setPageDisplay(int pageDisplay) {
		this.pageDisplay = pageDisplay;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public int getRepair() {
		return repair;
	}

	public void setRepair(int repair) {
		this.repair = repair;
	}
	
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public boolean isSysFlag() {
		return sysFlag;
	}

	public void setSysFlag(boolean sysFlag) {
		this.sysFlag = sysFlag;
	}

}