/**
 * 
 */
package com.seeyon.v3x.collaboration.event;


/**
 * 流程终止操作
 * @author dongyj
 *
 */
public class CollaborationStopEvent extends AbstractCollaborationEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5616891024005093615L;

	public CollaborationStopEvent(Object source) {
		super(source);
	}
	private Long userId;

	/*public ColSummary getSummary() {
		return summary;
	}
	public void setSummary(ColSummary summary) {
		this.summary = summary;
	}*/
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
}
