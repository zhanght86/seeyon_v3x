package com.seeyon.v3x.collaboration.event;

/**
 * 流程取回事件。
 * @author wangwenyou
 *
 */
public class CollaborationTakeBackEvent extends AbstractCollaborationEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3366535438985279095L;

	public CollaborationTakeBackEvent(Object source) {
		super(source);
	}
	private Long userId;
	/*private Affair affiar;
	private ColSummary summary;

	public Affair getAffiar() {
		return affiar;
	}
	public void setAffiar(Affair affiar) {
		this.affiar = affiar;
	}
	public ColSummary getSummary() {
		return summary;
	}
	public void setSummary(ColSummary summary) {
		this.summary = summary;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	*/
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
