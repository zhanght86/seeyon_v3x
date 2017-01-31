package com.seeyon.v3x.meeting.domain;

import com.seeyon.v3x.meeting.domain.base.BaseMtReply;



public class MtReply extends BaseMtReply {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MtReply () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MtReply (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MtReply (
		java.lang.Long id,
		java.lang.Long userId,
		java.lang.Long meetingId,
		java.lang.Integer feedbackFlag) {

		super (
			id,
			userId,
			meetingId,
			feedbackFlag);
	}

/*[CONSTRUCTOR MARKER END]*/

	private String userName;
	private String userAccountName;

	public String getUserAccountName() {
		return userAccountName;
	}

	public void setUserAccountName(String userAccountName) {
		this.userAccountName = userAccountName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}