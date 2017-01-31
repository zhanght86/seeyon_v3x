package com.seeyon.v3x.meeting.domain.base;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;


/**
 * This is an object that contains data related to the mt_resources table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="mt_resources"
 */

public abstract class BaseMtResources extends BaseModel  implements Serializable {

	public static String REF = "MtResources";
	public static String PROP_EXT1 = "ext1";
	public static String PROP_END_DATE = "endDate";
	public static String PROP_DESCRIPTION = "description";
	public static String PROP_STATE = "state";
	public static String PROP_EXT2 = "ext2";
	public static String PROP_RESERVE_FLAG = "reserveFlag";
	public static String PROP_USER_ID = "userId";
	public static String PROP_BEGIN_DATE = "beginDate";
	public static String PROP_RESOURCE_ID = "resourceId";
	public static String PROP_ID = "id";
	public static String PROP_MEETING_ID = "meetingId";


	// constructors
	public BaseMtResources () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMtResources (java.lang.Long id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMtResources (
		java.lang.Long id,
		java.lang.Long resourceId,
		java.lang.Long userId,
		boolean reserveFlag,
		java.util.Date beginDate,
		java.util.Date endDate) {

		this.setId(id);
		this.setResourceId(resourceId);
		this.setUserId(userId);
		this.setReserveFlag(reserveFlag);
		this.setBeginDate(beginDate);
		this.setEndDate(endDate);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	////private java.lang.Long id;

	// fields
	private java.lang.Long meetingId;
	private java.lang.Long resourceId;
	private java.lang.Long userId;
	private boolean reserveFlag;
	private java.util.Date beginDate;
	private java.util.Date endDate;
	private java.lang.String description;
	private java.lang.String state;
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
	 * Return the value associated with the column: resource_id
	 */
	public java.lang.Long getResourceId () {
		return resourceId;
	}

	/**
	 * Set the value related to the column: resource_id
	 * @param resourceId the resource_id value
	 */
	public void setResourceId (java.lang.Long resourceId) {
		this.resourceId = resourceId;
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
	 * Return the value associated with the column: reserve_flag
	 */
	public boolean isReserveFlag () {
		return reserveFlag;
	}

	/**
	 * Set the value related to the column: reserve_flag
	 * @param reserveFlag the reserve_flag value
	 */
	public void setReserveFlag (boolean reserveFlag) {
		this.reserveFlag = reserveFlag;
	}



	/**
	 * Return the value associated with the column: begin_date
	 */
	public java.util.Date getBeginDate () {
		return beginDate;
	}

	/**
	 * Set the value related to the column: begin_date
	 * @param beginDate the begin_date value
	 */
	public void setBeginDate (java.util.Date beginDate) {
		this.beginDate = beginDate;
	}



	/**
	 * Return the value associated with the column: end_date
	 */
	public java.util.Date getEndDate () {
		return endDate;
	}

	/**
	 * Set the value related to the column: end_date
	 * @param endDate the end_date value
	 */
	public void setEndDate (java.util.Date endDate) {
		this.endDate = endDate;
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
	 * Return the value associated with the column: state
	 */
	public java.lang.String getState () {
		return state;
	}

	/**
	 * Set the value related to the column: state
	 * @param state the state value
	 */
	public void setState (java.lang.String state) {
		this.state = state;
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
		if (!(obj instanceof com.seeyon.v3x.meeting.domain.MtResources)) return false;
		else {
			com.seeyon.v3x.meeting.domain.MtResources mtResources = (com.seeyon.v3x.meeting.domain.MtResources) obj;
			if (null == this.getId() || null == mtResources.getId()) return false;
			else return (this.getId().equals(mtResources.getId()));
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