package com.seeyon.v3x.collaboration.event;


/**
 * 流程发起事件
 * @author <a href="mailto:dongyj@seeyon.com">dongyj</a>
 *
 */
public class CollaborationStartEvent extends AbstractCollaborationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8411990532000425368L;
	private Long affairId;
	/**
	 * 发起人的affair Id。
	 * @return
	 */
	public Long getAffairId() {
		return affairId;
	}
	public void setAffairId(Long affairId) {
		this.affairId = affairId;
	}
	public CollaborationStartEvent(Object source) {
		super(source);
	}
	
	//private ColSummary summary;
	/**
	 *  发起协同的地方,<br>
	 *  pc:A8浏览器<br>
	 *  mobile:移动应用<br>
	 *  interface:a8接口<br>
	 *  ...其他
	 */
	private String from;

	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
