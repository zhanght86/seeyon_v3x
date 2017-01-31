package com.seeyon.v3x.meeting.domain;

import com.seeyon.v3x.meeting.domain.base.BaseMtSummaryTemplate;



public class MtSummaryTemplate extends BaseMtSummaryTemplate {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MtSummaryTemplate () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MtSummaryTemplate (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MtSummaryTemplate (
		java.lang.Long id,
		java.lang.String templateName,
		java.lang.Long meetingId,
		java.lang.String templateFormat,
		java.lang.Long createUser,
		java.util.Date createDate) {

		super (
			id,
			templateName,
			meetingId,
			templateFormat,
			createUser,
			createDate);
	}

/*[CONSTRUCTOR MARKER END]*/

	private String createUserName;

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
}