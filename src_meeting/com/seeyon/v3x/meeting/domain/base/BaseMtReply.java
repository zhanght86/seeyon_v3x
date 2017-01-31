package com.seeyon.v3x.meeting.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the mt_reply table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="mt_reply"
 */

public abstract class BaseMtReply extends BaseModel  implements Serializable {

	public static String REF = "MtReply";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_FEEDBACK_FLAG = "feedbackFlag";
	public static String PROP_USER_ID = "userId";
	public static String PROP_READ_DATE = "readDate";
	public static String PROP_FEEDBACK = "feedback";
	public static String PROP_ID = "id";
	public static String PROP_MEETING_ID = "meetingId";


	// constructors
	public BaseMtReply () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMtReply (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMtReply (
		java.lang.Long id,
		java.lang.Long userId,
		java.lang.Long meetingId,
		java.lang.Integer feedbackFlag) {

		this.setId(id);
		this.setUserId(userId);
		this.setMeetingId(meetingId);
		this.setFeedbackFlag(feedbackFlag);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.Long userId;
	private java.lang.Long meetingId;
	private java.lang.Integer feedbackFlag;
	private java.lang.String feedback;
	private java.util.Date readDate;
	private java.lang.String ext1;
	private java.lang.String ext2;



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



	/**
	 * Return the value associated with the column: meeting_id
	 */
	public java.lang.Long getMeetingId () {
		return meetingId;
	}

	/**
	 * Set the value related to the column: meeting_id
	 * @param meetingId the meeting_id value
	 */
	public void setMeetingId (java.lang.Long meetingId) {
		this.meetingId = meetingId;
	}



	/**
	 * Return the value associated with the column: feedback_flag
	 */
	public java.lang.Integer getFeedbackFlag () {
		return feedbackFlag;
	}

	/**
	 * Set the value related to the column: feedback_flag
	 * @param feedbackFlag the feedback_flag value
	 */
	public void setFeedbackFlag (java.lang.Integer feedbackFlag) {
		this.feedbackFlag = feedbackFlag;
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




	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof com.seeyon.v3x.meeting.domain.MtReply)) return false;
		else {
			com.seeyon.v3x.meeting.domain.MtReply mtReply = (com.seeyon.v3x.meeting.domain.MtReply) obj;
			if (null == this.getId() || null == mtReply.getId()) return false;
			else return (this.getId().equals(mtReply.getId()));
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