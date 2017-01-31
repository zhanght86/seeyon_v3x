package com.seeyon.v3x.news.domain;

import com.seeyon.v3x.news.domain.base.BaseNewsTemplate;



public class NewsTemplate extends BaseNewsTemplate {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public NewsTemplate () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public NewsTemplate (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public NewsTemplate (
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