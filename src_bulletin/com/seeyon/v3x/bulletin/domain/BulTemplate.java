package com.seeyon.v3x.bulletin.domain;

import com.seeyon.v3x.bulletin.domain.base.BaseBulTemplate;



public class BulTemplate extends BaseBulTemplate {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public BulTemplate () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public BulTemplate (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public BulTemplate (
		java.lang.Long id,
		java.lang.String templateName,
		boolean usedFlag,
		java.lang.String templateFormat,
		java.lang.Long createUser,
		java.util.Date createDate,
		java.lang.Long accountId) {

		super (
			id,
			templateName,
			usedFlag,
			templateFormat,
			createUser,
			createDate,
			accountId);
	}

/*[CONSTRUCTOR MARKER END]*/

	private String createUserName;

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	
	private String typeNames;

	public String getTypeNames() {		
		return typeNames;
	}

	public void setTypeNames(String typeNames) {
		this.typeNames = typeNames;
	}
}