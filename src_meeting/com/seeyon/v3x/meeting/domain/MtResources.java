package com.seeyon.v3x.meeting.domain;

import com.seeyon.v3x.meeting.domain.base.BaseMtResources;



public class MtResources extends BaseMtResources {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MtResources () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MtResources (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MtResources (
		java.lang.Long id,
		java.lang.Long resourceId,
		java.lang.Long userId,
		boolean reserveFlag,
		java.util.Date beginDate,
		java.util.Date endDate) {

		super (
			id,
			resourceId,
			userId,
			reserveFlag,
			beginDate,
			endDate);
	}

/*[CONSTRUCTOR MARKER END]*/

	private String resourceName;
	private String meetingName;
	private String userName;

	public String getMeetingName() {
		return meetingName;
	}

	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}