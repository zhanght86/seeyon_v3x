package com.seeyon.v3x.news.domain;

import com.seeyon.v3x.news.domain.base.BaseNewsLog;



public class NewsLog extends BaseNewsLog {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public NewsLog () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public NewsLog (java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public NewsLog (
		java.lang.Long id,
		java.util.Date recordDate,
		java.lang.Long userId) {

		super (
			id,
			recordDate,
			userId);
	}

/*[CONSTRUCTOR MARKER END]*/

	private String userName;
	private String recordTitle;

	public String getRecordTitle() {
		return recordTitle;
	}

	public void setRecordTitle(String recordTitle) {
		this.recordTitle = recordTitle;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}