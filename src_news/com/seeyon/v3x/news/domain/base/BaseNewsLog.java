package com.seeyon.v3x.news.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the news_log table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="news_log"
 */

public abstract class BaseNewsLog extends BaseModel  implements Serializable {

	public static String REF = "NewsLog";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_TABLE_NAME = "tableName";
	public static String PROP_RESULT = "result";
	public static String PROP_LOG_LEVEL = "logLevel";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_RECORD_ID = "recordId";
	public static String PROP_RECORD_DATE = "recordDate";
	public static String PROP_USER_ID = "userId";
	public static String PROP_LOG_TYPE = "logType";
	public static String PROP_ID = "id";
	public static String PROP_OPER_TYPE = "operType";


	// constructors
	public BaseNewsLog () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseNewsLog (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseNewsLog (
		java.lang.Long id,
		java.util.Date recordDate,
		java.lang.Long userId) {

		this.setId(id);
		this.setRecordDate(recordDate);
		this.setUserId(userId);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.String ext1;
	private java.lang.String ext2;
	private java.lang.Integer logLevel;
	private java.lang.String logType;
	private java.lang.String operType;
	private java.util.Date recordDate;
	private java.lang.Long recordId;
	private java.lang.String result;
	private java.lang.String tableName;
	private java.lang.Long userId;



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
	 * Return the value associated with the column: log_level
	 */
	public java.lang.Integer getLogLevel () {
		return logLevel;
	}

	/**
	 * Set the value related to the column: log_level
	 * @param logLevel the log_level value
	 */
	public void setLogLevel (java.lang.Integer logLevel) {
		this.logLevel = logLevel;
	}



	/**
	 * Return the value associated with the column: log_type
	 */
	public java.lang.String getLogType () {
		return logType;
	}

	/**
	 * Set the value related to the column: log_type
	 * @param logType the log_type value
	 */
	public void setLogType (java.lang.String logType) {
		this.logType = logType;
	}



	/**
	 * Return the value associated with the column: oper_type
	 */
	public java.lang.String getOperType () {
		return operType;
	}

	/**
	 * Set the value related to the column: oper_type
	 * @param operType the oper_type value
	 */
	public void setOperType (java.lang.String operType) {
		this.operType = operType;
	}



	/**
	 * Return the value associated with the column: record_date
	 */
	public java.util.Date getRecordDate () {
		return recordDate;
	}

	/**
	 * Set the value related to the column: record_date
	 * @param recordDate the record_date value
	 */
	public void setRecordDate (java.util.Date recordDate) {
		this.recordDate = recordDate;
	}



	/**
	 * Return the value associated with the column: record_id
	 */
	public java.lang.Long getRecordId () {
		return recordId;
	}

	/**
	 * Set the value related to the column: record_id
	 * @param recordId the record_id value
	 */
	public void setRecordId (java.lang.Long recordId) {
		this.recordId = recordId;
	}



	/**
	 * Return the value associated with the column: result
	 */
	public java.lang.String getResult () {
		return result;
	}

	/**
	 * Set the value related to the column: result
	 * @param result the result value
	 */
	public void setResult (java.lang.String result) {
		this.result = result;
	}



	/**
	 * Return the value associated with the column: table_name
	 */
	public java.lang.String getTableName () {
		return tableName;
	}

	/**
	 * Set the value related to the column: table_name
	 * @param tableName the table_name value
	 */
	public void setTableName (java.lang.String tableName) {
		this.tableName = tableName;
	}



	/**
	 * Return the value associated with the column: user_id
	 */
	public java.lang.Long getUserId () {
		return userId;
	}

	/**
	 * Set the value related to the column: user_id
	 * @param userId the user_id value
	 */
	public void setUserId (java.lang.Long userId) {
		this.userId = userId;
	}




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.news.domain.NewsLog)) return false;
		else {
			com.seeyon.v3x.news.domain.NewsLog newsLog = (com.seeyon.v3x.news.domain.NewsLog) obj;
			if (null == this.getId() || null == newsLog.getId()) return false;
			else return (this.getId().equals(newsLog.getId()));
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