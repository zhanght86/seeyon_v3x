package com.seeyon.v3x.news.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the news_template table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="news_template"
 */

public abstract class BaseNewsTemplate extends BaseModel  implements Serializable {

	public static String REF = "NewsTemplate";
	public static String PROP_ACCOUNT_ID = "accountId";
	public static String PROP_TEMPLATE_NAME = "templateName";
	public static String PROP_CREATE_DATE = "createDate";
	public static String PROP_TEMPLATE_FORMAT = "templateFormat";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_USED_FLAG = "usedFlag";
	public static String PROP_CREATE_USER = "createUser";
	public static String PROP_DESCRIPTION = "description";
	public static String PROP_UPDATE_DATE = "updateDate";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_CONTENT = "content";
	public static String PROP_UPDATE_USER = "updateUser";
	public static String PROP_ID = "id";


	// constructors
	public BaseNewsTemplate () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseNewsTemplate (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseNewsTemplate (
		java.lang.Long id,
		java.lang.String templateName,
		boolean usedFlag,
		java.lang.String templateFormat,
		java.lang.Long createUser,
		java.util.Date createDate,
		java.lang.Long accountId) {

		this.setId(id);
		this.setTemplateName(templateName);
		this.setUsedFlag(usedFlag);
		this.setTemplateFormat(templateFormat);
		this.setCreateUser(createUser);
		this.setCreateDate(createDate);
		this.setAccountId(accountId);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.String templateName;
	private boolean usedFlag;
	private java.lang.String description;
	private java.lang.String templateFormat;
	private java.lang.String content;
	private java.lang.Long createUser;
	private java.util.Date createDate;
	private java.util.Date updateDate;
	private java.lang.Long updateUser;
	private java.lang.Long accountId;
	private java.lang.String ext1;
	private java.lang.String ext2;

	// collections
	private java.util.Set<com.seeyon.v3x.news.domain.NewsType> newsTypes;



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
	 * Return the value associated with the column: template_name
	 */
	public java.lang.String getTemplateName () {
		return templateName;
	}

	/**
	 * Set the value related to the column: template_name
	 * @param templateName the template_name value
	 */
	public void setTemplateName (java.lang.String templateName) {
		this.templateName = templateName;
	}



	/**
	 * Return the value associated with the column: usedFlag
	 */
	public boolean isUsedFlag () {
		return usedFlag;
	}

	/**
	 * Set the value related to the column: usedFlag
	 * @param usedFlag the usedFlag value
	 */
	public void setUsedFlag (boolean usedFlag) {
		this.usedFlag = usedFlag;
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
	 * Return the value associated with the column: template_format
	 */
	public java.lang.String getTemplateFormat () {
		return templateFormat;
	}

	/**
	 * Set the value related to the column: template_format
	 * @param templateFormat the template_format value
	 */
	public void setTemplateFormat (java.lang.String templateFormat) {
		this.templateFormat = templateFormat;
	}



	/**
	 * Return the value associated with the column: content
	 */
	public java.lang.String getContent () {
		return content;
	}

	/**
	 * Set the value related to the column: content
	 * @param content the content value
	 */
	public void setContent (java.lang.String content) {
		this.content = content;
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
	 * Return the value associated with the column: ext1
	 */
	public java.lang.String getExt1 () {
		return ext1;
	}

	/**
	 * Set the value related to the column: ext1
	 * @param ext1 the ext1 value
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
	 * Return the value associated with the column: newsTypes
	 */
	public java.util.Set<com.seeyon.v3x.news.domain.NewsType> getNewsTypes () {
		return newsTypes;
	}

	/**
	 * Set the value related to the column: newsTypes
	 * @param newsTypes the newsTypes value
	 */
	public void setNewsTypes (java.util.Set<com.seeyon.v3x.news.domain.NewsType> newsTypes) {
		this.newsTypes = newsTypes;
	}

	public void addTonewsTypes (com.seeyon.v3x.news.domain.NewsType newsType) {
		if (null == getNewsTypes()) setNewsTypes(new java.util.TreeSet<com.seeyon.v3x.news.domain.NewsType>());
		getNewsTypes().add(newsType);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.news.domain.NewsTemplate)) return false;
		else {
			com.seeyon.v3x.news.domain.NewsTemplate newsTemplate = (com.seeyon.v3x.news.domain.NewsTemplate) obj;
			if (null == this.getId() || null == newsTemplate.getId()) return false;
			else return (this.getId().equals(newsTemplate.getId()));
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