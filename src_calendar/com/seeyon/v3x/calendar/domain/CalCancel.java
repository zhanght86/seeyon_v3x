package com.seeyon.v3x.calendar.domain;

import com.seeyon.v3x.calendar.domain.base.BaseCalCancel;

public class CalCancel extends BaseCalCancel {
	private static final long serialVersionUID = 1L;

	/* [CONSTRUCTOR MARKER BEGIN] */
	public CalCancel() {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CalCancel(java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CalCancel(java.lang.Long id, java.lang.Long calId,
			java.lang.Long cancelUserId, java.util.Date cancelDate) {

		super(id, calId, cancelUserId, cancelDate);
	}

	/* [CONSTRUCTOR MARKER END] */

}