package com.seeyon.v3x.mobile.webmodel;


/*
 * 该对象还需要扩展一些方法
 * 
 * 
 */

public class FlowChart {
	private Nodes nodes; // 发起人 Nodes::parent==NULL
	// private int templateId;
	 private int type;
	public Nodes getNodes() {
		return nodes;
	}
	public void setNodes(Nodes nodes) {
		this.nodes = nodes;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	} 

}
