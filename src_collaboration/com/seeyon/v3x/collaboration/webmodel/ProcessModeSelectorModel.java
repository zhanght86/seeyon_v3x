/**
 * 
 */
package com.seeyon.v3x.collaboration.webmodel;

import com.seeyon.v3x.workflow.event.WorkflowEventListener;

/**
 * 
 * 流程选人信息：分支条件 + 节点匹配
 * 
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class ProcessModeSelectorModel {

	/**
	 * 节点匹配信息
	 */
	private WorkflowEventListener.NodeAddition addition;
	
	/**
	 * 节点id
	 */
	private String nodeId;
	
	/**
	 * 节点名称
	 */
	private String nodeName;
	
	/**
	 * 分支条件
	 */
	private String condition;
	
	/**
	 * 是否是强制分支
	 */
	private String force;
	
	/**
	 * 分支描述
	 */
	private String link;
	
	/**
	 * 执行模式
	 */
	private String processMode;
	
	/**
	 * 是否来自知会
	 */
	private boolean fromIsInform = false;
	
	
	/**
	 * 分支类型1、自动条件分支2、手动选择分支
	 */ 
	private Integer conditionType;

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getForce() {
		return force;
	}

	public void setForce(String force) {
		this.force = force;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public WorkflowEventListener.NodeAddition getAddition() {
		return addition;
	}

	public void setAddition(WorkflowEventListener.NodeAddition addition) {
		this.addition = addition;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Integer getConditionType() {
		return conditionType;
	}

	public void setConditionType(Integer conditionType) {
		this.conditionType = conditionType;
	}

	public String getProcessMode() {
		return processMode;
	}

	public void setProcessMode(String processMode) {
		this.processMode = processMode;
	}

	public boolean isFromIsInform() {
		return fromIsInform;
	}

	public void setFromIsInform(boolean fromIsInform) {
		this.fromIsInform = fromIsInform;
	}
	
}