package com.seeyon.v3x.collaboration.event;

/**
 * 协同表单核定事件。
 * 
 * @author wangwenyou
 * 
 */
public class CollaborationFormVouchEvent extends AbstractCollaborationEvent {
	private static final long serialVersionUID = 3112956430095491211L;
	
	public CollaborationFormVouchEvent(Object source) {
		super(source);
	}
	private long affairId;
	// 是否核定0：默认值;1核定通过;2核定不能过
	private Integer isVouch = 0;
	private Integer oldIsVouch = 0;

	public long getAffairId() {
		return affairId;
	}

	public void setAffairId(long affairId) {
		this.affairId = affairId;
	}

	public Integer getIsVouch() {
		return isVouch;
	}

	public void setIsVouch(Integer isVouch) {
		this.isVouch = isVouch;
	}

	public Integer getOldIsVouch() {
		return oldIsVouch;
	}

	public void setOldIsVouch(Integer oldIsVouch) {
		this.oldIsVouch = oldIsVouch;
	}
}
