package com.seeyon.v3x.news.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the news_data table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="news_data"
 */

public abstract class BaseNewsData extends BaseModel  implements Serializable {

	public static String REF = "NewsData";
	public static String PROP_TYPE = "type";
	public static String PROP_PUBLISH_DEPARTMENT_ID = "publishDepartmentId";
	public static String PROP_KEYWORDS = "keywords";
	public static String PROP_CREATE_DATE = "createDate";
	public static String PROP_EXT5 = "ext5";
	public static String PROP_PIGEONHOLE_PATH = "pigeonholePath";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_CREATE_USER = "createUser";
	public static String PROP_UPDATE_DATE = "updateDate";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_DELETED_FLAG = "deletedFlag";
	public static String PROP_AUDIT_DATE = "auditDate";
	public static String PROP_EXT3 = "ext3";
	public static String PROP_BRIEF = "brief";
	public static String PROP_CONTENT = "content";
	public static String PROP_PUBLISH_SCOPE = "publishScope";
	public static String PROP_READ_COUNT = "readCount";
	public static String PROP_EXT4 = "ext4";
	public static String PROP_PUBLISH_USER_ID = "publishUserId";
	public static String PROP_PIGEONHOLE_USER_ID = "pigeonholeUserId";
	public static String PROP_STATE = "state";
	public static String PROP_PIGEONHOLE_DATE = "pigeonholeDate";
	public static String PROP_ACCOUNT_ID = "accountId";
	public static String PROP_DATA_FORMAT = "dataFormat";
	public static String PROP_PUBLISH_DATE = "publishDate";
	public static String PROP_AUDIT_ADVICE = "auditAdvice";
	public static String PROP_TITLE = "title";
	public static String PROP_TOP_ORDER = "topOrder";
	public static String PROP_ID = "id";
	public static String PROP_UPDATE_USER = "updateUser";
	public static String PROP_AUDIT_USER_ID = "auditUserId";


	// constructors
	public BaseNewsData () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseNewsData (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseNewsData (
		java.lang.Long id,
		com.seeyon.v3x.news.domain.NewsType type,
		java.lang.String title,
		java.lang.String publishScope,
		java.lang.String keywords,
		java.lang.String brief,
		java.util.Date createDate,
		java.lang.Long createUser,
		java.lang.Byte topOrder,
		java.lang.Integer state,
		boolean deletedFlag,
		java.lang.Long accountId) {

		this.setId(id);
		this.setType(type);
		this.setTitle(title);
		this.setPublishScope(publishScope);
		this.setKeywords(keywords);
		this.setBrief(brief);
		this.setCreateDate(createDate);
		this.setCreateUser(createUser);
		this.setTopOrder(topOrder);
		this.setState(state);
		this.setDeletedFlag(deletedFlag);
		this.setAccountId(accountId);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.String title;
	private java.lang.String publishScope;
	private java.lang.Long publishDepartmentId;
	private java.lang.String brief;
	private java.lang.String keywords;
	private java.lang.String dataFormat;
	private java.lang.String content;
	private java.util.Date createDate;
	private java.lang.Long createUser;
	private java.util.Date auditDate;
	private java.lang.Long auditUserId;
	private java.lang.String auditAdvice;
	private java.util.Date publishDate;
	private java.lang.Long publishUserId;
	private java.util.Date pigeonholeDate;
	private java.lang.Long pigeonholeUserId;
	private java.lang.String pigeonholePath;
	private java.util.Date updateDate;
	private java.lang.Long updateUser;
	private java.lang.Integer readCount;
	private java.lang.Byte topOrder;
	private java.lang.Integer state;
	private boolean deletedFlag;
	private java.lang.Long accountId;
	private java.lang.String ext1;
	private java.lang.String ext2;
	private java.lang.String ext3;
	private java.lang.String ext4;
	private java.lang.String ext5;

	// many to one
	private com.seeyon.v3x.news.domain.NewsType type;

	// collections
	private java.util.Set<com.seeyon.v3x.news.domain.NewsRead> newsReads;



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
	 * Return the value associated with the column: title
	 */
	public java.lang.String getTitle () {
		return title;
	}

	/**
	 * Set the value related to the column: title
	 * @param title the title value
	 */
	public void setTitle (java.lang.String title) {
		this.title = title;
	}



	/**
	 * Return the value associated with the column: publish_scope
	 */
	public java.lang.String getPublishScope () {
		return publishScope;
	}

	/**
	 * Set the value related to the column: publish_scope
	 * @param publishScope the publish_scope value
	 */
	public void setPublishScope (java.lang.String publishScope) {
		this.publishScope = publishScope;
	}



	/**
	 * Return the value associated with the column: publish_department_id
	 */
	public java.lang.Long getPublishDepartmentId () {
		return publishDepartmentId;
	}

	/**
	 * Set the value related to the column: publish_department_id
	 * @param publishDepartmentId the publish_department_id value
	 */
	public void setPublishDepartmentId (java.lang.Long publishDepartmentId) {
		this.publishDepartmentId = publishDepartmentId;
	}



	/**
	 * Return the value associated with the column: brief
	 */
	public java.lang.String getBrief () {
		return brief;
	}

	/**
	 * Set the value related to the column: brief
	 * @param brief the brief value
	 */
	public void setBrief (java.lang.String brief) {
		this.brief = brief;
	}



	/**
	 * Return the value associated with the column: keywords
	 */
	public java.lang.String getKeywords () {
		return keywords;
	}

	/**
	 * Set the value related to the column: keywords
	 * @param keywords the keywords value
	 */
	public void setKeywords (java.lang.String keywords) {
		this.keywords = keywords;
	}



	/**
	 * Return the value associated with the column: data_format
	 */
	public java.lang.String getDataFormat () {
		return dataFormat;
	}

	/**
	 * Set the value related to the column: data_format
	 * @param dataFormat the data_format value
	 */
	public void setDataFormat (java.lang.String dataFormat) {
		this.dataFormat = dataFormat;
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
	 * Return the value associated with the column: audit_date
	 */
	public java.util.Date getAuditDate () {
		return auditDate;
	}

	/**
	 * Set the value related to the column: audit_date
	 * @param auditDate the audit_date value
	 */
	public void setAuditDate (java.util.Date auditDate) {
		this.auditDate = auditDate;
	}



	/**
	 * Return the value associated with the column: audit_user_id
	 */
	public java.lang.Long getAuditUserId () {
		return auditUserId;
	}

	/**
	 * Set the value related to the column: audit_user_id
	 * @param auditUserId the audit_user_id value
	 */
	public void setAuditUserId (java.lang.Long auditUserId) {
		this.auditUserId = auditUserId;
	}



	/**
	 * Return the value associated with the column: audit_advice
	 */
	public java.lang.String getAuditAdvice () {
		return auditAdvice;
	}

	/**
	 * Set the value related to the column: audit_advice
	 * @param auditAdvice the audit_advice value
	 */
	public void setAuditAdvice (java.lang.String auditAdvice) {
		this.auditAdvice = auditAdvice;
	}



	/**
	 * Return the value associated with the column: publish_date
	 */
	public java.util.Date getPublishDate () {
		return publishDate;
	}

	/**
	 * Set the value related to the column: publish_date
	 * @param publishDate the publish_date value
	 */
	public void setPublishDate (java.util.Date publishDate) {
		this.publishDate = publishDate;
	}



	/**
	 * Return the value associated with the column: publish_user_id
	 */
	public java.lang.Long getPublishUserId () {
		return publishUserId;
	}

	/**
	 * Set the value related to the column: publish_user_id
	 * @param publishUserId the publish_user_id value
	 */
	public void setPublishUserId (java.lang.Long publishUserId) {
		this.publishUserId = publishUserId;
	}



	/**
	 * Return the value associated with the column: pigeonhole_date
	 */
	public java.util.Date getPigeonholeDate () {
		return pigeonholeDate;
	}

	/**
	 * Set the value related to the column: pigeonhole_date
	 * @param pigeonholeDate the pigeonhole_date value
	 */
	public void setPigeonholeDate (java.util.Date pigeonholeDate) {
		this.pigeonholeDate = pigeonholeDate;
	}



	/**
	 * Return the value associated with the column: pigeonhole_user_id
	 */
	public java.lang.Long getPigeonholeUserId () {
		return pigeonholeUserId;
	}

	/**
	 * Set the value related to the column: pigeonhole_user_id
	 * @param pigeonholeUserId the pigeonhole_user_id value
	 */
	public void setPigeonholeUserId (java.lang.Long pigeonholeUserId) {
		this.pigeonholeUserId = pigeonholeUserId;
	}



	/**
	 * Return the value associated with the column: pigeonhole_path
	 */
	public java.lang.String getPigeonholePath () {
		return pigeonholePath;
	}

	/**
	 * Set the value related to the column: pigeonhole_path
	 * @param pigeonholePath the pigeonhole_path value
	 */
	public void setPigeonholePath (java.lang.String pigeonholePath) {
		this.pigeonholePath = pigeonholePath;
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
	 * Return the value associated with the column: read_count
	 */
	public java.lang.Integer getReadCount () {
		return readCount;
	}

	/**
	 * Set the value related to the column: read_count
	 * @param readCount the read_count value
	 */
	public void setReadCount (java.lang.Integer readCount) {
		this.readCount = readCount;
	}



	/**
	 * Return the value associated with the column: top_order
	 */
	public java.lang.Byte getTopOrder () {
		return topOrder;
	}

	/**
	 * Set the value related to the column: top_order
	 * @param topOrder the top_order value
	 */
	public void setTopOrder (java.lang.Byte topOrder) {
		this.topOrder = topOrder;
	}



	/**
	 * Return the value associated with the column: state
	 */
	public java.lang.Integer getState () {
		return state;
	}

	/**
	 * Set the value related to the column: state
	 * @param state the state value
	 */
	public void setState (java.lang.Integer state) {
		this.state = state;
	}



	/**
	 * Return the value associated with the column: deleted_flag
	 */
	public boolean isDeletedFlag () {
		return deletedFlag;
	}

	/**
	 * Set the value related to the column: deleted_flag
	 * @param deletedFlag the deleted_flag value
	 */
	public void setDeletedFlag (boolean deletedFlag) {
		this.deletedFlag = deletedFlag;
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
	 * Return the value associated with the column: ext3
	 */
	public java.lang.String getExt3 () {
		return ext3;
	}

	/**
	 * Set the value related to the column: ext3
	 * @param ext3 the ext3 value
	 */
	public void setExt3 (java.lang.String ext3) {
		this.ext3 = ext3;
	}



	/**
	 * Return the value associated with the column: ext4
	 */
	public java.lang.String getExt4 () {
		return ext4;
	}

	/**
	 * Set the value related to the column: ext4
	 * @param ext4 the ext4 value
	 */
	public void setExt4 (java.lang.String ext4) {
		this.ext4 = ext4;
	}



	/**
	 * Return the value associated with the column: ext5
	 */
	public java.lang.String getExt5 () {
		return ext5;
	}

	/**
	 * Set the value related to the column: ext5
	 * @param ext5 the ext5 value
	 */
	public void setExt5 (java.lang.String ext5) {
		this.ext5 = ext5;
	}



	/**
	 * Return the value associated with the column: type_id
	 */
	public com.seeyon.v3x.news.domain.NewsType getType () {
		return type;
	}

	/**
	 * Set the value related to the column: type_id
	 * @param type the type_id value
	 */
	public void setType (com.seeyon.v3x.news.domain.NewsType type) {
		this.type = type;
	}



	/**
	 * Return the value associated with the column: newsReads
	 */
	public java.util.Set<com.seeyon.v3x.news.domain.NewsRead> getNewsReads () {
		return newsReads;
	}

	/**
	 * Set the value related to the column: newsReads
	 * @param newsReads the newsReads value
	 */
	public void setNewsReads (java.util.Set<com.seeyon.v3x.news.domain.NewsRead> newsReads) {
		this.newsReads = newsReads;
	}

	public void addTonewsReads (com.seeyon.v3x.news.domain.NewsRead newsRead) {
		if (null == getNewsReads()) setNewsReads(new java.util.TreeSet<com.seeyon.v3x.news.domain.NewsRead>());
		getNewsReads().add(newsRead);
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.news.domain.NewsData)) return false;
		else {
			com.seeyon.v3x.news.domain.NewsData newsData = (com.seeyon.v3x.news.domain.NewsData) obj;
			if (null == this.getId() || null == newsData.getId()) return false;
			else return (this.getId().equals(newsData.getId()));
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