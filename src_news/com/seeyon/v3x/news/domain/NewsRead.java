package com.seeyon.v3x.news.domain;

import com.seeyon.v3x.news.domain.base.BaseNewsRead;



public class NewsRead extends BaseNewsRead {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public NewsRead () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public NewsRead (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public NewsRead (
		java.lang.Long id,
		com.seeyon.v3x.news.domain.NewsData news,
		java.lang.Long managerId,
		boolean readFlag,
		java.lang.Long accountId) {

		super (
			id,
			news,
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