package com.seeyon.v3x.collaboration.event;


/**
 * 流程处理事件
 * @author dongyj
 *
 */
public class CollaborationProcessEvent extends AbstractCollaborationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6892183650037459267L;

	public CollaborationProcessEvent(Object source) {
		super(source);
	}
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
