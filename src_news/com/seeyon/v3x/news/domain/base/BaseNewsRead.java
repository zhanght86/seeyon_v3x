package com.seeyon.v3x.news.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the news_read table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="news_read"
 */

public abstract class BaseNewsRead extends BaseModel  implements Serializable {

	public static String REF = "NewsRead";
	public static String PROP_NEWS = "news";
	public static String PROP_READ_FLAG = "readFlag";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_ACCOUNT_ID = "accountId";
	public static String PROP_MANAGER_ID = "managerId";
	public static String PROP_READ_DATE = "readDate";
	public static String PROP_FEEDBACK = "feedback";
	public static String PROP_ID = "id";


	// constructors
	public BaseNewsRead () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseNewsRead (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseNewsRead (
		java.lang.Long id,
		com.seeyon.v3x.news.domain.NewsData news,
		java.lang.Long managerId,
		boolean readFlag,
		java.lang.Long accountId) {

		this.setId(id);
		this.setNews(news);
		this.setManagerId(managerId);
		this.setReadFlag(readFlag);
		this.setAccountId(accountId);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.Long managerId;
	@Deprecated
	private boolean readFlag;   //只要这里面有记录的表示已读，未读的不写这张表，实际上read_flag已经废弃
	private java.util.Date readDate;
	private java.lang.String feedback;
	private java.lang.Long accountId;
	private java.lang.String ext1;
	private java.lang.String ext2;

	// many to one
	private com.seeyon.v3x.news.domain.NewsData news;



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
	 * Return the value associated with the column: manager_id
	 */
	public java.lang.Long getManagerId () {
		return managerId;
	}

	/**
	 * Set the value related to the column: manager_id
	 * @param managerId the manager_id value
	 */
	public void setManagerId (java.lang.Long managerId) {
		this.managerId = managerId;
	}



	/**
	 * Return the value associated with the column: read_flag
	 */
	@Deprecated    //只要这里面有记录的表示已读，未读的不写这张表，实际上read_flag已经废弃
	public boolean isReadFlag () {
		return readFlag;
	}

	/**
	 * Set the value related to the column: read_flag
	 * @param readFlag the read_flag value
	 */
	@Deprecated    //只要这里面有记录的表示已读，未读的不写这张表，实际上read_flag已经废弃
	public void setReadFlag (boolean readFlag) {
		this.readFlag = readFlag;
	}



	/**
	 * Return the value associated with the column: read_date
	 */
	public java.util.Date getReadDate () {
		return readDate;
	}

	/**
	 * Set the value related to the column: read_date
	 * @param readDate the read_date value
	 */
	public void setReadDate (java.util.Date readDate) {
		this.readDate = readDate;
	}



	/**
	 * Return the value associated with the column: feedback
	 */
	public java.lang.String getFeedback () {
		return feedback;
	}

	/**
	 * Set the value related to the column: feedback
	 * @param feedback the feedback value
	 */
	public void setFeedback (java.lang.String feedback) {
		this.feedback = feedback;
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
	 * Return the value associated with the column: news_id
	 */
	public com.seeyon.v3x.news.domain.NewsData getNews () {
		return news;
	}

	/**
	 * Set the value related to the column: news_id
	 * @param news the news_id value
	 */
	public void setNews (com.seeyon.v3x.news.domain.NewsData news) {
		this.news = news;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.news.domain.NewsRead)) return false;
		else {
			com.seeyon.v3x.news.domain.NewsRead newsRead = (com.seeyon.v3x.news.domain.NewsRead) obj;
			if (null == this.getId() || null == newsRead.getId()) return false;
			else return (this.getId().equals(newsRead.getId()));
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