package com.seeyon.v3x.bulletin.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the bul_type table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="bul_type"
 */

public abstract class BaseBulType extends BaseModel  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9023645784407832375L;
	public static String REF = "BulType";
	public static String PROP_AUDIT_USER = "auditUser";
	public static String PROP_ACCOUNT_ID = "accountId";
	public static String PROP_CREATE_DATE = "createDate";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_USED_FLAG = "usedFlag";
	public static String PROP_CREATE_USER = "createUser";
	public static String PROP_DESCRIPTION = "description";
	public static String PROP_UPDATE_DATE = "updateDate";
	public static String PROP_TOP_COUNT = "topCount";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_DEFAULT_TEMPLATE = "defaultTemplate";
	public static String PROP_TYPE_NAME = "typeName";
	public static String PROP_SPACE_TYPE = "spaceType";
	public static String PROP_AUDIT_FLAG = "auditFlag";
	public static String PROP_ID = "id";
	public static String PROP_UPDATE_USER = "updateUser";


	// constructors
	public BaseBulType () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseBulType (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseBulType (
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

		this.setId(id);
		this.setTypeName(typeName);
		this.setUsedFlag(usedFlag);
		this.setTopCount(topCount);
		this.setAuditFlag(auditFlag);
		this.setAuditUser(auditUser);
		this.setCreateDate(createDate);
		this.setCreateUser(createUser);
		this.setAccountId(accountId);
		this.setSpaceType(spaceType);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.Long accountId;
	private boolean auditFlag;
	private java.lang.Long auditUser;
	private java.util.Date createDate;
	private java.lang.Long createUser;
	private java.lang.String description;
	private java.lang.String ext1;
	private java.lang.String ext2;
	private java.lang.Integer spaceType;
	private java.lang.Byte topCount;
	private java.lang.String typeName;
	private java.util.Date updateDate;
	private java.lang.Long updateUser;
	private boolean usedFlag;

	// many to one
	private com.seeyon.v3x.bulletin.domain.BulTemplate defaultTemplate;

	// collections
//	private java.util.Set<com.seeyon.v3x.bulletin.domain.BulData> bulDatas;
	private java.util.Set<com.seeyon.v3x.bulletin.domain.BulTypeManagers> bulTypeManagers;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="assigned"
     *  column="id"
     */
	public java.lang.Long getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: accountId
	 */
	public java.lang.Long getAccountId () {
		return accountId;
	}

	/**
	 * Set the value related to the column: accountId
	 * @param accountId the accountId value
	 */
	public void setAccountId (java.lang.Long accountId) {
		this.accountId = accountId;
	}



	/**
	 * Return the value associated with the column: audit_flag
	 */
	public boolean isAuditFlag () {
		return auditFlag;
	}

	/**
	 * Set the value related to the column: audit_flag
	 * @param auditFlag the audit_flag value
	 */
	public void setAuditFlag (boolean auditFlag) {
		this.auditFlag = auditFlag;
	}



	/**
	 * Return the value associated with the column: audit_user
	 */
	public java.lang.Long getAuditUser () {
		return auditUser;
	}

	/**
	 * Set the value related to the column: audit_user
	 * @param auditUser the audit_user value
	 */
	public void setAuditUser (java.lang.Long auditUser) {
		this.auditUser = auditUser;
	}



	/**
	 * Return the value associated with the column: create_date
	 */
	public java.util.Date getCreateDate () {
		return createDate;
	}

	/**
	 * Set the value related to the column: create_date
	 * @param createDate the create_date value
	 */
	public void setCreateDate (java.util.Date createDate) {
		this.createDate = createDate;
	}



	/**
	 * Return the value associated with the column: create_user
	 */
	public java.lang.Long getCreateUser () {
		return createUser;
	}

	/**
	 * Set the value related to the column: create_user
	 * @param createUser the create_user value
	 */
	public void setCreateUser (java.lang.Long createUser) {
		this.createUser = createUser;
	}



	/**
	 * Return the value associated with the column: description
	 */
	public java.lang.String getDescription () {
		return description;
	}

	/**
	 * Set the value related to the column: description
	 * @param description the description value
	 */
	public void setDescription (java.lang.String description) {
		this.description = description;
	}



	/**
	 * 获取板块对应的公告样式，包括三种：标准、正式、正式2
	 * @see com.seeyon.v3x.bulletin.util.Constants.BulStyle
	 */
	public java.lang.String getExt1 () {
		return ext1;
	}

	/**
	 * 设定板块对应的公告样式，包括三种：标准、正式、正式2
	 * @see com.seeyon.v3x.bulletin.util.Constants.BulStyle
	 */
	public void setExt1 (java.lang.String ext1) {
		this.ext1 = ext1;
	}



	/**
	 * Return the value associated with the column: ext2
	 */
	public java.lang.String getExt2 () {
		return ext2;
	}

	/**
	 * Set the value related to the column: ext2
	 * @param ext2 the ext2 value
	 */
	public void setExt2 (java.lang.String ext2) {
		this.ext2 = ext2;
	}



	/**
	 * Return the value associated with the column: spaceType
	 */
	public java.lang.Integer getSpaceType () {
		return spaceType;
	}

	/**
	 * Set the value related to the column: spaceType
	 * @param spaceType the spaceType value
	 */
	public void setSpaceType (java.lang.Integer spaceType) {
		this.spaceType = spaceType;
	}



	/**
	 * Return the value associated with the column: top_count
	 */
	public java.lang.Byte getTopCount () {
		return topCount;
	}

	/**
	 * Set the value related to the column: top_count
	 * @param topCount the top_count value
	 */
	public void setTopCount (java.lang.Byte topCount) {
		this.topCount = topCount;
	}



	/**
	 * Return the value associated with the column: type_name
	 */
	public java.lang.String getTypeName () {
		return typeName;
	}

	/**
	 * Set the value related to the column: type_name
	 * @param typeName the type_name value
	 */
	public void setTypeName (java.lang.String typeName) {
		this.typeName = typeName;
	}



	/**
	 * Return the value associated with the column: update_date
	 */
	public java.util.Date getUpdateDate () {
		return updateDate;
	}

	/**
	 * Set the value related to the column: update_date
	 * @param updateDate the update_date value
	 */
	public void setUpdateDate (java.util.Date updateDate) {
		this.updateDate = updateDate;
	}



	/**
	 * Return the value associated with the column: update_user
	 */
	public java.lang.Long getUpdateUser () {
		return updateUser;
	}

	/**
	 * Set the value related to the column: update_user
	 * @param updateUser the update_user value
	 */
	public void setUpdateUser (java.lang.Long updateUser) {
		this.updateUser = updateUser;
	}



	/**
	 * Return the value associated with the column: used_flag
	 */
	public boolean isUsedFlag () {
		return usedFlag;
	}

	/**
	 * Set the value related to the column: used_flag
	 * @param usedFlag the used_flag value
	 */
	public void setUsedFlag (boolean usedFlag) {
		this.usedFlag = usedFlag;
	}



	/**
	 * Return the value associated with the column: default_template_id
	 */
	public com.seeyon.v3x.bulletin.domain.BulTemplate getDefaultTemplate () {
		return defaultTemplate;
	}

	/**
	 * Set the value related to the column: default_template_id
	 * @param defaultTemplate the default_template_id value
	 */
	public void setDefaultTemplate (com.seeyon.v3x.bulletin.domain.BulTemplate defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}



	/**
	 * Return the value associated with the column: bulDatas
	 */
//	public java.util.Set<com.seeyon.v3x.bulletin.domain.BulData> getBulDatas () {
//		return bulDatas;
//	}
//
//	/**
//	 * Set the value related to the column: bulDatas
//	 * @param bulDatas the bulDatas value
//	 */
//	public void setBulDatas (java.util.Set<com.seeyon.v3x.bulletin.domain.BulData> bulDatas) {
//		this.bulDatas = bulDatas;
//	}

//	public void addTobulDatas (com.seeyon.v3x.bulletin.domain.BulData bulData) {
//		if (null == getBulDatas()) setBulDatas(new java.util.TreeSet<com.seeyon.v3x.bulletin.domain.BulData>());
//		getBulDatas().add(bulData);
//	}



	/**
	 * Return the value associated with the column: bulTypeManagers
	 */
	public java.util.Set<com.seeyon.v3x.bulletin.domain.BulTypeManagers> getBulTypeManagers () {
		return bulTypeManagers;
	}

	/**
	 * Set the value related to the column: bulTypeManagers
	 * @param bulTypeManagers the bulTypeManagers value
	 */
	public void setBulTypeManagers (java.util.Set<com.seeyon.v3x.bulletin.domain.BulTypeManagers> bulTypeManagers) {
		this.bulTypeManagers = bulTypeManagers;
	}

	public void addTobulTypeManagers (com.seeyon.v3x.bulletin.domain.BulTypeManagers bulTypeManagers) {
		if (null == getBulTypeManagers()) setBulTypeManagers(new java.util.TreeSet<com.seeyon.v3x.bulletin.domain.BulTypeManagers>());
		getBulTypeManagers().add(bulTypeManagers);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.bulletin.domain.BulType)) return false;
		else {
			com.seeyon.v3x.bulletin.domain.BulType bulType = (com.seeyon.v3x.bulletin.domain.BulType) obj;
			if (null == this.getId() || null == bulType.getId()) return false;
			else return (this.getId().equals(bulType.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}


}