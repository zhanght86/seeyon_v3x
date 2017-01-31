package com.seeyon.v3x.collaboration.event;


/**
 * 流程撤销事件
 * @author dongyj
 *
 */
public class CollaborationCancelEvent extends AbstractCollaborationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2711337304700880102L;

	public CollaborationCancelEvent(Object source) {
		super(source);
	}
	
	private Long userId;
	private String message;
//
//	private ColSummary summary;
//	public String getMessage() {
//		return message;
//	}
//	public void setMessage(String message) {
//		this.message = message;
//	}
//	public ColSummary getSummary() {
//		return summary;
//	}
//	public void setSummary(ColSummary summary) {
//		this.summary = summary;
//	}
//	public Long getUserId() {
//		return userId;
//	}
//	public void setUserId(Long userId) {
//		this.userId = userId;
//	}
//	

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
