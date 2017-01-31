package com.seeyon.v3x.calendar.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the cal_content database table.
 * 
 * @author BEA Workshop Studio
 */
public class CalContent extends BaseModel implements java.io.Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Long id;

	private String content;

	private String contentType;

	private java.util.Date createDate;

	private Long eventId;

	public CalContent() {
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public java.util.Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}

	public long getEventId() {
		return this.eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	private CalEvent event;

	public CalEvent getEvent() {
		return event;
	}

	public void setEvent(CalEvent event) {
		this.event = event;
	}

	public boolean isNew() {
		if (id == null || id == Constants.GLOBAL_NULL_ID) {
			return true;
		}

		return false;
	}
}