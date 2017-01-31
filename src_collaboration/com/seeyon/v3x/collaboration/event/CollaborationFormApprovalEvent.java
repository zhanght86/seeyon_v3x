package com.seeyon.v3x.collaboration.event;


/**
 * 表单审批事件。监听表单是否审批通过。
 * 
 * @author wangwy
 * 
 */
public class CollaborationFormApprovalEvent extends AbstractCollaborationEvent {

	private static final long serialVersionUID = 4213533021358051189L;
	private String templateCode;
	private int state=-1;
	private boolean isFinished = false;
	private long userId;
	private long affairId;
	/**
	 * 取得当前审批人的affair Id。
	 * @return
	 */
	public long getAffairId() {
		return affairId;
	}
	public void setAffairId(long affairId) {
		this.affairId = affairId;
	}
	public CollaborationFormApprovalEvent(Object source) {
		super(source);
	}	
	/**
	 * 判断流程是否已结束。
	 * @return 流程结束返回true，否则返回false。
	 */
	public boolean isFinished() {
		return isFinished;
	}
	public void setIsFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	/**
	 * 取得审批人的Id。
	 * @return 审批人的人员Id。
	 */
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * 表单模板编号。亦即建立表单时用户录入的用于外部系统调用的“模板编号”。
	 * 
	 * @return 表单模板编号。
	 */
	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	/**
	 * 取得审批状态。
	 * 
	 * @return 草稿0----未审批1----审批通过2----审批不通过3
	 */
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
