package com.seeyon.v3x.common.comboTree;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * 下拉框树型结构节点
 */
public class ComboTreeNode {

	private String id;
	private String parentId;
	private String text;
	private String iconCls = "icon-children";
	private String state = "closed";
	private List<ComboTreeNode> children = new ArrayList<ComboTreeNode>();
	
	public ComboTreeNode(String id, String parentId, String text) {
		this.id = id;
		this.parentId = parentId;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<ComboTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<ComboTreeNode> children) {
		this.children = children;
	}

	public String toJsonString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		sb.append("\"id\":\"").append(this.getId()).append("\"");
		sb.append(",").append("\"text\":\"").append(this.getText()).append("\"");
		sb.append(",").append("\"iconCls\":\"").append(this.getIconCls()).append("\"");
		
		if (CollectionUtils.isNotEmpty(this.getChildren())) {
			sb.append(",").append("\"state\":\"").append(this.getState()).append("\"");
			
			sb.append(",\"children\":[");
			for (int i = 0; i < this.getChildren().size(); i ++ ) {
				sb.append(this.getChildren().get(i).toJsonString());
				
				if (i != this.getChildren().size() - 1) {
					sb.append(",");
				}
			}
			sb.append("]");
		}
		sb.append("}");
		
		return sb.toString();
	}

}