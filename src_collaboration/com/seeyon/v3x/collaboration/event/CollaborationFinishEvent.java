package com.seeyon.v3x.collaboration.event;


public class CollaborationFinishEvent extends AbstractCollaborationEvent {

	public CollaborationFinishEvent(Object source) {
		super(source);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5861283716411567704L;
	private Long affairId;
	/**
	 * 处理人的affair Id。
	 * @return
	 */
	public Long getAffairId() {
		return affairId;
	}
	public void setAffairId(Long affairId) {
		this.affairId = affairId;
	}
}
