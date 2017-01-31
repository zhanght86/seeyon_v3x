package com.seeyon.v3x.bulletin.domain;

import com.seeyon.v3x.bulletin.domain.base.BaseBulType;



public class BulType extends BaseBulType implements Comparable<BulType> {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public BulType () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public BulType (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public BulType (
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
	
	public String getBasicInfo() {
		return  "[\n" +
				"是否启用：" + (this.isUsedFlag() ? "启用" : "停用") + "\n" +
				"板块名称：" + this.getTypeName() + "\n" +
				"板块管理员：" + this.getManagerUserNames() + "\n" + 
				"板块审核员：" + (this.isAuditFlag() ? this.getAuditUserName() : "无审核") + "\n" +
				"公告样式：" + ("0".equals(this.getExt1()) ? "标准" : "正式") + "\n" +
				"]";
	}
	
	private Long defaultTemplateId;
	private String managerUserIds;
	private String managerUserNames;
	private String auditUserName;
	private int totalItems;
	
	public Long getDefaultTemplateId() {
		return defaultTemplateId;
	}

	public void setDefaultTemplateId(Long defaultTemplateId) {
		this.defaultTemplateId = defaultTemplateId;
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
	
	private Integer sortNum;

	public Integer getSortNum() {
		return sortNum;
	}

	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}

	public int compareTo(BulType o) {
		if(this.getSortNum().intValue() < o.getSortNum().intValue())
			return -1;
		else if(this.getSortNum().intValue() > o.getSortNum().intValue())
			return 1;
		else
			return this.getCreateDate().getTime() > o.getCreateDate().getTime() ? 1 : -1;
	}
}