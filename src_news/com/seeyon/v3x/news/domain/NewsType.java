package com.seeyon.v3x.news.domain;

import com.seeyon.v3x.news.domain.base.BaseNewsType;



public class NewsType extends BaseNewsType implements Comparable<NewsType>{
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public NewsType () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public NewsType (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public NewsType (
		java.lang.Long id,
		java.lang.String typeName,
		boolean usedFlag,
		java.lang.Byte topCount,
		boolean auditFlag,
		java.lang.Long auditUser,
		java.util.Date createDate,
		java.lang.Long createUser,
		java.lang.Long accountId,
		java.lang.Integer spaceType) {

		super (
			id,
			typeName,
			usedFlag,
			topCount,
			auditFlag,
			auditUser,
			createDate,
			createUser,
			accountId,
			spaceType);
	}

/*[CONSTRUCTOR MARKER END]*/
	private Long defaultTemplateId;
	private String createUserName;
	private String managerUserIds;
	private String managerUserNames;
	private String writeUserIds;
	private String writeUserNames;
	private String auditUserName;
	private int totalItems;
	
	private boolean isShowManageButton;

	public Long getDefaultTemplateId() {
		return defaultTemplateId;
	}

	public void setDefaultTemplateId(Long defaultTemplateId) {
		this.defaultTemplateId = defaultTemplateId;
	}
	
	

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	
	
	

	public String getManagerUserIds() {
		return managerUserIds;
	}

	public void setManagerUserIds(String managerUserIds) {
		this.managerUserIds = managerUserIds;
	}

	public String getAuditUserName() {
		return auditUserName;
	}

	public void setAuditUserName(String auditUserName) {
		this.auditUserName = auditUserName;
	}

	public String getManagerUserNames() {
		return managerUserNames;
	}

	public void setManagerUserNames(String managerUserNames) {
		this.managerUserNames = managerUserNames;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public String getWriteUserIds() {
		return writeUserIds;
	}

	public void setWriteUserIds(String writeUserIds) {
		this.writeUserIds = writeUserIds;
	}

	public String getWriteUserNames() {
		return writeUserNames;
	}

	public void setWriteUserNames(String writeUserNames) {
		this.writeUserNames = writeUserNames;
	}

	public boolean getIsShowManageButton() {
		return isShowManageButton;
	}

//	public void setIsShowManageButton(boolean isShowManageButton) {
//		this.isShowManageButton = isShowManageButton;
//	}
	
	//
	private boolean canNewOfCurrent;   // 当前用户是否有新建权限
	private boolean canAdminOfCurrent;   // 当前用户是否可以管理
	private boolean canAuditOfCurrent;   // 当前用户是否审核员
	private int auditPending;         // 如果是审核员 待办条数

	public int getAuditPending() {
		return auditPending;
	}

	public void setAuditPending(int auditPending) {
		this.auditPending = auditPending;
	}

	public boolean getCanAdminOfCurrent() {
		return canAdminOfCurrent;
	}

	public void setCanAdminOfCurrent(boolean canAdminOfCurrent) {
		this.canAdminOfCurrent = canAdminOfCurrent;
	}

	public boolean getCanAuditOfCurrent() {
		return canAuditOfCurrent;
	}

	public void setCanAuditOfCurrent(boolean canAuditOfCurrent) {
		this.canAuditOfCurrent = canAuditOfCurrent;
	}

	public boolean getCanNewOfCurrent() {
		return canNewOfCurrent;
	}

	public void setCanNewOfCurrent(boolean canNewOfCurrent) {
		this.canNewOfCurrent = canNewOfCurrent;
	}
	
	
	//
	private Integer sortNum;
	private Boolean outterPermit;

	public Boolean getOutterPermit() {
		return outterPermit;
	}

	public void setOutterPermit(Boolean outterPermit) {
		this.outterPermit = outterPermit;
	}

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public int compareTo(NewsType o) {
		if(this.getSortNum().intValue() < o.getSortNum().intValue())
			return -1;
		else if(this.getSortNum().intValue() > o.getSortNum().intValue())
			return 1;
		else
			return this.getCreateDate().getTime() > o.getCreateDate().getTime() ? 1 : -1;
	}
}