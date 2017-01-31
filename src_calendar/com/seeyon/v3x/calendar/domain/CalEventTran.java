package com.seeyon.v3x.calendar.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the cal_event_tran database table.
 * 
 * @author BEA Workshop Studio
 */
public class CalEventTran extends BaseModel implements java.io.Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Long id; // 主键id

	private Long entityId;// 实体的id（接收的部门id，或是人员id）

	private long eventId;// 事件id

	private long sourceRecordId;// 事件创建者的id（多余的）

	private Integer type;// 事件类型（1.安排2.委托3.共享部门4.共享项目）

	private Long receiveId; // 接收者（被委托者和被安排者）的 id

	public Long getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(Long receiveId) {
		this.receiveId = receiveId;
	}

	public CalEventTran() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEntityId() {
		return this.entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public long getEventId() {
		return this.eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getSourceRecordId() {
		return this.sourceRecordId;
	}

	public void setSourceRecordId(long sourceRecordId) {
		this.sourceRecordId = sourceRecordId;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
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