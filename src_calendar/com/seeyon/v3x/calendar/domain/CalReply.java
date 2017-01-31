package com.seeyon.v3x.calendar.domain;

import com.seeyon.v3x.calendar.domain.base.BaseCalReply;

public class CalReply extends BaseCalReply {
	private static final long serialVersionUID = 1L;

	/* [CONSTRUCTOR MARKER BEGIN] */
	public CalReply() {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public CalReply(java.lang.Long id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public CalReply(java.lang.Long id, java.lang.Long eventId,
			java.lang.Integer calType, java.lang.Long replyUserId,
			java.util.Date replyDate) {

		super(id, eventId, calType, replyUserId, replyDate);
	}

	/* [CONSTRUCTOR MARKER END] */

	private String replyUserName;

	public String getReplyUserName() {
		return replyUserName;
	}

	public void setReplyUserName(String replyUserName) {
		this.replyUserName = replyUserName;
	}

}