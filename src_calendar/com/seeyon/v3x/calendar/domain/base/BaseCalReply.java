package com.seeyon.v3x.calendar.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * This is an object that contains data related to the cal_reply table. Do not
 * modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 * 
 * @hibernate.class table="cal_reply"
 */

public abstract class BaseCalReply extends BaseModel implements Serializable {

	public static String REF = "CalReply";

	public static String PROP_EXT1 = "ext1";

	public static String PROP_REPLY_USER_ID = "replyUserId";

	public static String PROP_CAL_ID = "eventId";

	public static String PROP_REPLY_INFO = "replyInfo";

	public static String PROP_EXT2 = "ext2";

	public static String PROP_CAL_TYPE = "calType";

	public static String PROP_REPLY_DATE = "replyDate";

	public static String PROP_ID = "id";

	public static String PROP_REPLY_OPTION = "replyOption";

	// constructors
	public BaseCalReply() {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCalReply(java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCalReply(java.lang.Long id, java.lang.Long eventId,
			java.lang.Integer calType, java.lang.Long replyUserId,
			java.util.Date replyDate) {

		this.setId(id);
		this.setEventId(eventId);
		this.setCalType(calType);
		this.setReplyUserId(replyUserId);
		this.setReplyDate(replyDate);
		initialize();
	}

	protected void initialize() {
	}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	// //private java.lang.Long id;

	// fields
	private java.lang.Long eventId;

	private java.lang.Integer calType;

	private java.lang.String replyInfo;

	private java.lang.Long replyUserId;

	private java.lang.String replyOption;

	private java.util.Date replyDate;

	private java.lang.String ext1;

	private java.lang.String ext2;

	/**
	 * Return the unique identifier of this class
	 * 
	 * @hibernate.id generator-class="assigned" column="id"
	 */
	public java.lang.Long getId() {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * 
	 * @param id
	 *            the new ID
	 */
	public void setId(java.lang.Long id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}

	public java.lang.Long getEventId() {
		return eventId;
	}

	public void setEventId(java.lang.Long eventId) {
		this.eventId = eventId;
	}

	/**
	 * Return the value associated with the column: cal_type
	 */
	public java.lang.Integer getCalType() {
		return calType;
	}

	/**
	 * Set the value related to the column: cal_type
	 * 
	 * @param calType
	 *            the cal_type value
	 */
	public void setCalType(java.lang.Integer calType) {
		this.calType = calType;
	}

	/**
	 * Return the value associated with the column: reply_info
	 */
	public java.lang.String getReplyInfo() {
		return replyInfo;
	}

	/**
	 * Set the value related to the column: reply_info
	 * 
	 * @param replyInfo
	 *            the reply_info value
	 */
	public void setReplyInfo(java.lang.String replyInfo) {
		this.replyInfo = replyInfo;
	}

	/**
	 * Return the value associated with the column: reply_user_id
	 */
	public java.lang.Long getReplyUserId() {
		return replyUserId;
	}

	/**
	 * Set the value related to the column: reply_user_id
	 * 
	 * @param replyUserId
	 *            the reply_user_id value
	 */
	public void setReplyUserId(java.lang.Long replyUserId) {
		this.replyUserId = replyUserId;
	}

	/**
	 * Return the value associated with the column: reply_option
	 */
	public java.lang.String getReplyOption() {
		return replyOption;
	}

	/**
	 * Set the value related to the column: reply_option
	 * 
	 * @param replyOption
	 *            the reply_option value
	 */
	public void setReplyOption(java.lang.String replyOption) {
		this.replyOption = replyOption;
	}

	/**
	 * Return the value associated with the column: reply_date
	 */
	public java.util.Date getReplyDate() {
		return replyDate;
	}

	/**
	 * Set the value related to the column: reply_date
	 * 
	 * @param replyDate
	 *            the reply_date value
	 */
	public void setReplyDate(java.util.Date replyDate) {
		this.replyDate = replyDate;
	}

	/**
	 * Return the value associated with the column: ext1
	 */
	public java.lang.String getExt1() {
		return ext1;
	}

	/**
	 * Set the value related to the column: ext1
	 * 
	 * @param ext1
	 *            the ext1 value
	 */
	public void setExt1(java.lang.String ext1) {
		this.ext1 = ext1;
	}

	/**
	 * Return the value associated with the column: ext2
	 */
	public java.lang.String getExt2() {
		return ext2;
	}

	/**
	 * Set the value related to the column: ext2
	 * 
	 * @param ext2
	 *            the ext2 value
	 */
	public void setExt2(java.lang.String ext2) {
		this.ext2 = ext2;
	}

	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		if (!(obj instanceof com.seeyon.v3x.calendar.domain.CalReply))
			return false;
		else {
			com.seeyon.v3x.calendar.domain.CalReply calReply = (com.seeyon.v3x.calendar.domain.CalReply) obj;
			if (null == this.getId() || null == calReply.getId())
				return false;
			else
				return (this.getId().equals(calReply.getId()));
		}
	}

	public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId())
				return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":"
						+ this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public String toString() {
		return super.toString();
	}

}