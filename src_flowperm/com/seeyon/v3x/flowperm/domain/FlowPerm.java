package com.seeyon.v3x.flowperm.domain;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.permission.util.NodePolicy;
import com.seeyon.v3x.common.web.login.CurrentUser;

public class FlowPerm extends BaseModel implements Serializable{
	
	public static final Integer Node_Location_Start = 0;  // 开始节点
	public static final Integer Node_Location_Mid = 1;    // 中间节点
	public static final Integer Node_Location_End = 2;    // 结束节点
	
	public static final Integer Col_Basic_action = 0;     // 基本操作
	public static final Integer Node_Control_action =1;   // 常用操作
	public static final Integer Node_Advanced_action =2;  //高级操作
	
	public static final Integer Node_isActive = 1;        // 启用
	public static final Integer Node_isNotActive = 0;     // 未启用
	
	public static final Integer Node_isRef =1;            //引用
	public static final Integer Node_unRef =0;			  //未引用
	
	public static final Integer Node_Type_System = 0;     // 预置权限
 	public static final Integer Node_Type_Custome = 1;	  // 用户添加权限
	
	private static final long serialVersionUID = 1L;
		
	private Long flowPermId;
	private String name;           //名称
	private String description;	   //描述
	private String category;       //类别
	private Integer type;          //是否为系统或用户定制
	private String label;          //对应的metaItem中标签的名称
	
	private NodePolicy nodePolicy;
	
	public NodePolicy getNodePolicy() {
		return nodePolicy;
	}

	public void setNodePolicy(NodePolicy nodePolicy) {
		this.nodePolicy = nodePolicy;
	}

	public FlowPerm(){
		
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getFlowPermId() {
		return flowPermId;
	}

	public void setFlowPermId(Long flowPermId) {
		this.flowPermId = flowPermId;
	}
	
	public String getAdvancedOperation(){
		return nodePolicy.getAdvancedAction();
	}
	public String getBasicOperation(){
		return nodePolicy.getBaseAction();
	}
	public String getCommonOperation(){
		return nodePolicy.getCommonAction();
	}
	public Integer getLocation(){
		return nodePolicy.getLocation();
	}
	public Integer getIsRef(){
		return nodePolicy.getIsRef();
	}
	public Integer getIsEnabled(){
		return nodePolicy.getIsEnabled();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
