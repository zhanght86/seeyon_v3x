package com.seeyon.v3x.collaboration.event;

import com.seeyon.v3x.event.Event;

public abstract class AbstractCollaborationEvent  extends Event{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9009449975423422403L;
	public AbstractCollaborationEvent(Object source) {
		super(source);
	}
	private Long summaryId;
	/**
	 * 取得流程Id。
	 * 
	 * @return 唯一标识流程的Id。
	 */
	public Long getSummaryId() {
		return summaryId;
	}
	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}
}
