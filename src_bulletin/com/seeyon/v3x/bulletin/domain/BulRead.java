package com.seeyon.v3x.bulletin.domain;

import com.seeyon.v3x.bulletin.domain.base.BaseBulRead;



public class BulRead extends BaseBulRead {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public BulRead () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public BulRead (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public BulRead (
		java.lang.Long id,
		com.seeyon.v3x.bulletin.domain.BulData bulletin,
		java.lang.Long managerId,
		boolean readFlag,
		java.lang.Long accountId) {

		super (
			id,
			bulletin,
			managerId,
			readFlag,
			accountId);
	}

/*[CONSTRUCTOR MARKER END]*/

	private String managerName;

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
}