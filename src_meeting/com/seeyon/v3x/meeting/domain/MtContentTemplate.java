package com.seeyon.v3x.meeting.domain;

import com.seeyon.v3x.meeting.domain.base.BaseMtContentTemplate;



public class MtContentTemplate extends BaseMtContentTemplate {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public MtContentTemplate () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public MtContentTemplate (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public MtContentTemplate (
		java.lang.Long id,
		java.lang.String templateName,
		boolean usedFlag,
		java.lang.String templateFormat,
		java.lang.Long accountId,
		java.lang.Long createUser,
		java.util.Date createDate) {

		super (
			id,
			templateName,
			usedFlag,
			templateFormat,
			accountId,
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